package cn.hu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Collectors;

import cn.hu.MetaChr.OperationType;

/**
 * Jregex
 */
public class Jregex {
    private String raw;
    private List<Token2> tokens;
    private NFA NFA;
    private DFA DFA;

    public Jregex(String raw) {
        this.raw = raw;
        try {
            this.tokens = tokenize2(raw);    
        } catch (Exception e) {
            //TODO: handle exception
            e.printStackTrace();
        }

        this.NFA = new NFA(tokens);
        //this.NFA = toNFA(this.raw);
        this.DFA = toDFA(this.NFA);
    }

    public List<Token> tokenize(String s) throws TokenizeFailedException{
        List<Token> l = new ArrayList<>();
        int bracketsPattern = 0;
        for (int i = 0; i < s.length();) {
            String value = String.valueOf(s.charAt(i)), tmp;
            MetaChr mc = MetaChr.map(value), mcc;
            if (mc == null) {
                l.add(new Token(value, MetaChr.OPERAND));
                l.add(new Token("_", MetaChr.CONCAT));
                i++;
            } else {
                switch (mc.optType()) {
                    case DELIMITER:
                        if (i == 0) {
                            l.add(new Token(value, mc));
                            i++;
                        } else if (i == s.length() - 1) {
                            l.remove(l.size() - 1);
                            l.add(new Token(value, mc));
                            i++;
                        } else {
                            throw new TokenizeFailedException();
                        }
                        break;
                    case PREFIX:
                        switch (mc.value()) {
                            case "\\":
                                tmp = String.valueOf(s.charAt(i + 1));
                                mcc =  MetaChr.map(value + tmp);
                                if ( MetaChr.map(tmp) != null ) {
                                    l.add(new Token(tmp, MetaChr.OPERAND));
                                    i += 2;
                                } else if ( mcc != null ) {
                                    l.add(new Token(value + tmp, mcc));
                                    i += 2;
                                } else {
                                    throw new TokenizeFailedException();
                                }
                                l.add(new Token("_", MetaChr.CONCAT));
                                break;
                            case "^":
                                break;
                        }
                        break;
                    case GROUP:
                        switch (mc.value()) {
                            case "(":
                                tmp = s.substring(i, i + 3);
                                System.out.println(tmp);
                                mcc = MetaChr.map(tmp);
                                if (mcc != null) {
                                    l.add(new Token(tmp, mcc));
                                    i += 3;
                                } else {
                                    l.add(new Token(value, mc));
                                    i++;
                                }
                                bracketsPattern += 1;
                                break;
                            case ")":
                                l.add(l.size() - 1, new Token(value, mc));
                                bracketsPattern -= 1;
                                i++;
                                break;
                            case "{":
                                String acc = "{";
                                char c;
                                i++;
                                for (; ( c = s.charAt(i) ) != '}'; i++) {
                                    if ( c != ',' && (c < 48 || c > 57 || i == s.length() - 1)) {
                                        throw new TokenizeFailedException();
                                    }
                                    acc += c;
                                }
                                acc += "}";
                                l.add(l.size() - 1, new Token(acc, MetaChr.LEFT_BRACE));
                                i++;
                                break;
                            case "[":
                                break;
                        }
                        break;
                    case SUFFIX:
                        l.add(l.size() - 1, new Token(value, mc));
                        i++;
                        break;
                    case CONJUNCTION:
                        l.remove(l.size() - 1);
                        l.add(new Token(value, mc));
                        i++;
                        break;
                    default:
                        l.add(new Token(value, mc));
                        i++;
                        break;
                }
            }

        }
        if (bracketsPattern != 0) {
            throw new TokenizeFailedException();
        }
        return l;
    }

    public List<Token2> tokenize2(String s) throws TokenizeFailedException {
        List<Token2> l = new ArrayList<>();
        final String NONE = "_";
        for (int i = 0; i < s.length();) {
            String c = String.valueOf(s.charAt(i));
            Meta meta = Meta.map(c);
            if (null == meta) {
                l.add(new Token2(Meta.OPERAND, c));
                l.add(new Token2(Meta.CONCAT, NONE));
                i++;
                continue;
            }

            switch (meta) {
                case SLASH:
                    if (i == 0) {
                        l.add(new Token2(meta, c));
                        i++;
                    } else if (i == s.length() - 1) {
                        l.remove(l.size() - 1);
                        l.add(new Token2(meta, c));
                        i++;
                    } else {
                        throw new TokenizeFailedException();
                    }
                    break;
                case BACKSLASH:
                    String next = String.valueOf(s.charAt(i + 1));
                    Meta tmp =  Meta.map(c + next);
                    if ( Meta.map(next) != null ) {
                        l.add(new Token2(Meta.OPERAND, next));
                        i += 2;
                    } else if ( tmp != null ) {
                        l.add(new Token2(tmp, c + next));
                        i += 2;
                    } else {
                        throw new TokenizeFailedException();
                    }
                    l.add(new Token2(Meta.CONCAT, NONE));
                    break;
                case LEFT_PARENTHESIS:
                    int bracketsPattern = 0;
                    String acc = "";
                    String prefix = s.substring(i, i + 3);
                    Meta group = Meta.map(prefix);
                    if (null == group) {
                        group = meta;
                    }
                    for (; i < s.length();) {
                        acc += s.charAt(i);
                        if (s.charAt(i) == ')') {
                            bracketsPattern--;
                        } else if (s.charAt(i) == '(') {
                            bracketsPattern++;
                        }
                        i++;
                        if (bracketsPattern == 0) break;
                    }
                    if (i < s.length()) {
                        l.add(new Token2(group, acc));
                        l.add(new Token2(Meta.CONCAT, NONE));
                    } else {
                        throw new TokenizeFailedException();
                    }
                    break;
                case LEFT_BRACE:
                    acc = "{";
                    char ch;
                    i++;
                    for (; ( ch = s.charAt(i) ) != '}'; i++) {
                        if ( ch != ',' && (ch < 48 || ch > 57 || i == s.length() - 1)) {
                            throw new TokenizeFailedException();
                        }
                        acc += ch;
                    }
                    acc += "}";
                    l.add(l.size() - 1, new Token2(Meta.REPEAT, acc));
                    i++;
                    break;
                case LEFT_BRACKET:
                    acc = "[";
                    i++;
                    for (; ( ch = s.charAt(i) ) != ']'; i++) {
                        acc += ch;
                    }
                    acc += "]";
                    l.add(new Token2(meta, acc));
                    l.add(new Token2(Meta.CONCAT, NONE));
                    i++;
                    break;
                case UNION:
                    l.remove(l.size() - 1);
                    l.add(new Token2(meta, c));
                    i++;
                    break;
                case STAR:
                    l.add(l.size() - 1, new Token2(meta, c));
                    i++;
                    break;
                case PLUS:
                    l.add(l.size() - 1, new Token2(meta, c));
                    i++;
                    break;
                case QUES_MARK:
                    l.add(l.size() - 1, new Token2(meta, c));
                    i++;
                    break;
                case POINT:
                    l.add(new Token2(meta, c));
                    l.add(new Token2(Meta.CONCAT, NONE));
                    break;
                default:
                    l.add(new Token2(meta, c));
                    i++;
            }
        }
        return l;
    }

    /**
     * @return to NFA
     */
    public NFA toNFA(String s) {
        Stack<NFA> operandStack = new Stack<>();
        Stack<MetaChr> operatorStack = new Stack<>();
        int combo = 0;
        for (int i = 0; i < s.length();) {
            String p = String.valueOf(s.charAt(i));
            MetaChr mc = MetaChr.map(p);
            if (null != mc) {
                if (i == 0 && mc.optType() == MetaChr.OperationType.DELIMITER) {
                    operatorStack.push(mc);
                    i++;
                    continue;
                }
                if (operatorStack.peek().priority() > mc.priority()) {
                    operatorStack.push(mc);
                    if (mc.optType().paramSize() > 1) {
                        combo = 0;
                    }
                    i++;
                } else {
                    MetaChr tmp = operatorStack.pop();
                    switch (tmp.optType()) {
                    case PREFIX:
                        break;
                    case SUFFIX:
                        operandStack.push(tmp.opt().exe(operandStack.pop()));
                        break;
                    case CONJUNCTION:
                        NFA r = operandStack.pop(), l = operandStack.pop();
                        operandStack.push(tmp.opt().exe(l, r));
                        break;
                    case CHARCLASS:
                        break;
                    case GROUP:
                        break;
                    case DELIMITER:
                        i++;
                        break;
                    default:
                        break;
                    }
                }
            } else {
                combo += 1;
                if (combo < 2) {
                    operandStack.push(new NFA(p));
                    i++;
                } else {
                    if (operatorStack.peek().priority() > MetaChr.CONCAT.priority()) {
                        operatorStack.push(MetaChr.CONCAT);
                        operandStack.push(new NFA(p));
                        combo = 1;
                        i++;
                    } else {
                        MetaChr tmp = operatorStack.pop();
                        switch (tmp.optType()) {
                        case PREFIX:
                            break;
                        case SUFFIX:
                            operandStack.push(tmp.opt().exe(operandStack.pop()));
                            operatorStack.push(MetaChr.CONCAT);
                            operandStack.push(new NFA(p));
                            combo = 1;
                            i++;
                            break;
                        case CONJUNCTION:
                            NFA r = operandStack.pop(), l = operandStack.pop();
                            operandStack.push(tmp.opt().exe(l, r));
                            break;
                        case CHARCLASS:
                            break;
                        case GROUP:
                            
                            break;
                        case DELIMITER:
                            break;
                        default:
                            break;
                        }
                    }
                }
            }
        }
        return operandStack.pop();
    }

    public NFA toNFA2(List<Token> l) {
        System.out.println(l);
        Stack<NFA> operandStack = new Stack<>();
        Stack<MetaChr> operatorStack = new Stack<>();
        for (int i = 0; i < l.size();) {
            Token token = l.get(i);
            if (token.getName() == MetaChr.OPERAND) {
                operandStack.push(new NFA(token.getValue()));
                i++;
            } else {
                if (token.getName() == MetaChr.LIMIT && i == 0) {
                    operatorStack.push(MetaChr.LIMIT);
                    i++;
                    continue;
                }
                if (token.getName().optType() == OperationType.CHARCLASS) {
                    operandStack.push(token.getName().opt().exe(new NFA()));
                    i++;
                    continue;
                }
                if (token.getName().priority() < operatorStack.peek().priority()) {
                    operatorStack.push(token.getName());
                    i++;
                } else {
                    MetaChr mc = operatorStack.pop();
                    switch (mc.optType()) {
                        case SUFFIX:
                            operandStack.push(mc.opt().exe(operandStack.pop()));
                            break;
                        case CONJUNCTION:
                            NFA n = operandStack.pop(), m = operandStack.pop();
                            operandStack.push(mc.opt().exe(m, n));
                            break;
                        case DELIMITER:
                            i++;
                            break;
                        default:
                            break;
                    }
                }

            }
        }
        return operandStack.pop();
    }

    public DFA toDFA(NFA n) {
        class Anonymous {
            private DFA d = new DFA();

            Anonymous() {
                Set<State> initState = move(new HashSet<State>(Arrays.asList(n.getInitState())));
                d.setInitState(initState);
                d.setOperands(n.getOperands());
                d.getStates().add(new HashSet<State>(initState));
                epsilonClosure(Arrays.asList(initState));
                d.setAcceptStates(d.getStates().stream().filter(state -> state.contains(n.getEndState())).collect(Collectors.toSet()));
            }

            Set<State> move(Set<State> l) {
                Set<State> set = new HashSet<>();
                Set<State> r = l.stream().map(state -> {
                    return state.getTransitions().keySet().stream().filter(pairs -> pairs.getString() == null)
                            .map(pairs -> state.getTransitions().get(pairs)).collect(Collectors.toSet());
                }).reduce(set, (acc, item) -> {
                    acc.addAll(item);
                    return acc;
                });
                //System.out.println(r);
                if (!r.isEmpty()) {
                    l.addAll(move(r));
                }
                return l;
            }

            List<Set<State>> epsilonClosure(List<Set<State>> states) {
                if (states.isEmpty()) {
                    return null;
                }
                List<Set<State>> r = new ArrayList<>();
                states.stream().forEach(state -> {
                    for (int i = 0; i < n.getOperands().length(); i++) {
                        String s = String.valueOf(n.getOperands().charAt(i));
                        Set<State> set = new HashSet<>();
                        Set<State> l = state.stream().map(x -> {
                            return x.getTransitions().keySet().stream().filter(pairs -> {
                                if (null == pairs.getString())
                                    return false;
                                return pairs.getString().equals(s);
                            }).map(pairs -> x.getTransitions().get(pairs)).collect(Collectors.toSet());
                        }).reduce(set, (acc, item) -> {
                            acc.addAll(item);
                            return acc;
                        });
                        l = move(l);
                        if (l.equals(state)) {
                            d.getMap().put(new DFA.Pairs(state, s), l);
                        }
                        if ( !(l.equals(state) || l.isEmpty()) ) {
                            r.add(l);
                            d.getStates().add(l);
                            d.getMap().put(new DFA.Pairs(state, s), l);
                        }
                    }
                });
                return epsilonClosure(r);
            }
        }

        return new Anonymous().d;
    }

    public boolean matches(String s) {
        if (null == s)
            return false;
        Map<DFA.Pairs, Set<State>> m = this.DFA.getMap();
        Set<State> currentState = this.DFA.getInitState();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            Set<State> nextState = m.get(new DFA.Pairs(currentState, String.valueOf(c)));
            if (null == nextState) {
                return false;
            } else {
                currentState = nextState;
            }
        }
        return this.DFA.getAcceptStates().contains(currentState) ? true : false;
    }

    /**
     * @return the DFA
     */
    public DFA getDFA() {
        return DFA;
    }

    /**
     * @return the NFA
     */
    public NFA getNFA() {
        return NFA;
    }

    /**
     * @return the tokens
     */
    public List<Token2> getTokens() {
        return tokens;
    }

    /**
     * @return the raw
     */
    public String getRaw() {
        return raw;
    }
}

class TokenizeFailedException extends Exception {

	private static final long serialVersionUID = 1L;

}
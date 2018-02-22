package cn.hu;

import java.util.*;

/**
 * Jregex
 */
public class Jregex {
    private String raw;
    private List<Token> tokens;
    private NFA NFA;
    private DFA DFA;

    Jregex(String raw) {
        this.raw = raw;
        try {
            this.tokens = tokenize(raw);    
        } catch (Exception e) {
            //TODO: handle exception
            e.printStackTrace();
        }

        this.NFA = new NFA(tokens);
        this.DFA = new DFA(NFA);
    }
    /**
     * tokenize
     * @param s string
     */
    public static List<Token> tokenize(String s) throws TokenizeFailedException {
        List<Token> l = new ArrayList<>();
        final String NONE = "_";
        for (int i = 0; i < s.length();) {
            String c = String.valueOf(s.charAt(i));
            Meta meta = Meta.map(c);
            if (null == meta) {
                l.add(new Token(Meta.OPERAND, c));
                l.add(new Token(Meta.CONCAT, NONE));
                i++;
                continue;
            }

            switch (meta) {
                case SLASH:
                    if (i == 0) {
                        l.add(new Token(meta, c));
                        i++;
                    } else if (i == s.length() - 1) {
                        if (l.get(l.size() - 1).getName() == Meta.CONCAT) {
                            l.remove(l.size() - 1);
                        }
                        l.add(new Token(meta, c));
                        i++;
                    } else {
                        throw new TokenizeFailedException();
                    }
                    break;
                case BACKSLASH:
                    char chr = s.charAt(i + 1);
                    String next = String.valueOf(chr);
                    Meta tmp =  Meta.map(c + next);
                    if (Meta.map(next) != null) {
                        l.add(new Token(Meta.OPERAND, next));
                        i += 2;
                    } else if (tmp != null) {
                        l.add(new Token(tmp, c + next));
                        i += 2;
                    } else if (chr > 48 && chr < 58) {
                        l.add(new Token(Meta.REFERENCE, c + next));
                        i += 2;
                    } else {
                        throw new TokenizeFailedException();
                    }
                    l.add(new Token(Meta.CONCAT, NONE));
                    break;
                case LEFT_PARENTHESIS:
                    int bracketsPattern = 0;
                    StringBuilder acc = new StringBuilder();
                    String prefix = s.substring(i, i + 3);
                    Meta group = Meta.map(prefix);
                    if (null == group) {
                        group = meta;
                    }
                    for (; i < s.length();) {
                        acc.append(s.charAt(i));
                        if (s.charAt(i) == ')') {
                            bracketsPattern--;
                        } else if (s.charAt(i) == '(') {
                            bracketsPattern++;
                        }
                        i++;
                        if (bracketsPattern == 0) break;
                    }
                    if (i < s.length()) {
                        l.add(new Token(group, acc.toString()));
                        l.add(new Token(Meta.CONCAT, NONE));
                    } else {
                        throw new TokenizeFailedException();
                    }
                    break;
                case LEFT_BRACE:
                    acc = new StringBuilder("{");
                    char ch;
                    i++;
                    for (; ( ch = s.charAt(i) ) != '}'; i++) {
                        if ( ch != ',' && (ch < 48 || ch > 57 || i == s.length() - 1)) {
                            throw new TokenizeFailedException();
                        }
                        acc.append(ch);
                    }
                    acc.append("}");
                    l.add(l.size() - 1, new Token(Meta.LEFT_BRACE, acc.toString()));
                    i++;
                    break;
                case LEFT_BRACKET:
                    acc = new StringBuilder("[");
                    i++;
                    for (; ( ch = s.charAt(i) ) != ']'; i++) {
                        acc.append(ch);
                    }
                    acc.append("]");
                    l.add(new Token(meta, acc.toString()));
                    l.add(new Token(Meta.CONCAT, NONE));
                    i++;
                    break;
                case UNION:
                    l.remove(l.size() - 1);
                    l.add(new Token(meta, c));
                    i++;
                    break;
                case STAR:
                    l.add(l.size() - 1, new Token(meta, c));
                    i++;
                    break;
                case PLUS:
                    l.add(l.size() - 1, new Token(meta, c));
                    i++;
                    break;
                case OPTIONAL:
                    l.add(l.size() - 1, new Token(meta, c));
                    i++;
                    break;
                case POINT:
                    l.add(new Token(meta, c));
                    l.add(new Token(Meta.CONCAT, NONE));
                    i++;
                    break;
                default:
                    l.add(new Token(meta, c));
                    i++;
            }
        }
        return l;
    }

    /**
     *
     */
    private class Machine {
        private DFA dfa;
        private Set<State> currentState;
        private Map<Set<State>, int[]> loops;
        private StringBuffer operands;
        Machine(DFA dfa) {
            this.dfa = dfa;
            this.currentState = dfa.getInitState();
            this.loops = new HashMap<>();
            this.operands = new StringBuffer();
        }
        void push(char ch) {
            String s = String.valueOf(ch);
            this.operands.append(s);
            Map<DFA.Pairs, Set<State>> m = dfa.getMap();
            Set<State> nextState = m.get(new DFA.Pairs(currentState, s));
            nextState = nextState != null ? nextState : m.get(new DFA.Pairs(currentState, "ANY"));
            if (null == nextState) {
                Map<DFA.Pairs, Set<State>> unfiniteTransition = DFA.getUnfiniteTransition();
                for (DFA.Pairs pairs : unfiniteTransition.keySet()) {
                    String str = pairs.getString();
                    str = str.substring(3, str.length() - 1);
                    if (pairs.getState().equals(currentState) && !str.contains(s)) {
                        nextState = unfiniteTransition.get(pairs);
                        break;
                    }
                }
            }
            Set<State> currentLoop = DFA.isLoop(nextState);
            if (null != currentLoop) {
                int[] scaleAndRepeat = loops.get(currentLoop);
                if (null != scaleAndRepeat) {
                    loops.put(currentLoop, new int[]{scaleAndRepeat[0], scaleAndRepeat[1], scaleAndRepeat[2] + 1});
                } else {
                    int[] scale = DFA.getLoopState().get(currentLoop);
                    if (null != scale) {
                        loops.put(currentLoop, new int[]{scale[0], scale[1], 1});
                    }
                }
            }
            currentState = nextState;
        }

        void reset() {
            currentState = dfa.getInitState();
            loops = new HashMap<>();
            operands = new StringBuffer();
        }

        boolean pack() {
            return null != currentState;
        }

        boolean stop() {
            for (int[] status : loops.values()) {
                if (status[2] < status[0] || status[2] < status[1])
                    return false;
            }
            return dfa.getAcceptStates().contains(currentState);
        }

        boolean isHead(char ch) {
            return null != dfa.getMap().get(new DFA.Pairs(dfa.getInitState(), String.valueOf(ch)));
        }

    }

    /**
     * matches OLD
     * @param s string
     */
    public boolean matches(String s) {
        if (null == s)
            return false;
        Map<Set<State>, int[]> loopStatus = new HashMap<>();
        Map<DFA.Pairs, Set<State>> m = this.DFA.getMap();
        Set<State> currentState = this.DFA.getInitState();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            Set<State> nextState = m.get(new DFA.Pairs(currentState, String.valueOf(c)));
            nextState = nextState != null ? nextState : m.get(new DFA.Pairs(currentState, "ANY"));
            if (null == nextState) {
                Map<DFA.Pairs, Set<State>> unfiniteTransition = this.DFA.getUnfiniteTransition();
                for (DFA.Pairs pairs : unfiniteTransition.keySet()) {
                    String str = pairs.getString();
                    str = str.substring(3, str.length() - 1);
                    if (pairs.getState().equals(currentState) && str.indexOf(c) == -1) {
                        nextState = unfiniteTransition.get(pairs);
                        break;
                    }
                }
            }
            if (null == nextState) {
                return false;
            } else {
                currentState = nextState;
            }
            Set<State> currentLoop = DFA.isLoop(nextState);
            if (null != currentLoop) {
                int[] scaleAndRepeat = loopStatus.get(currentLoop);
                if (null != scaleAndRepeat) {
                    switch (scaleAndRepeat[1]) {
                        case 0:
                            if (scaleAndRepeat[2] + 1 > scaleAndRepeat[0])
                                return false;
                            break;
                        case -1:
                            break;
                        default:
                            if (scaleAndRepeat[2] + 1 > scaleAndRepeat[1])
                                return false;
                            break;
                    }
                    loopStatus.put(currentLoop, new int[]{scaleAndRepeat[0], scaleAndRepeat[1], scaleAndRepeat[2] + 1});
                } else {

                    int[] scale = DFA.getLoopState().get(currentLoop);
                    if (null != scale) {
                        loopStatus.put(currentLoop, new int[]{scale[0], scale[1], 1});
                    }
                }
            }

        }
        for (int[] status : loopStatus.values()) {
            if (status[2] < status[0])
                return false;
        }
        return this.DFA.getAcceptStates().contains(currentState);
    }

    /**
     * NEW
     * @param s string
     * @return _
     */
    public boolean _matches(String s) {
        Machine m = new Machine(this.DFA);
        for (int i = 0; i < s.length(); i++) {
            m.push(s.charAt(i));
            if (!m.pack())
                return false;
        }
        return m.stop();
    }

    /**
     * @param s string
     * @return string list accc
     */
    public List<String> pattern(String s) {
        List<String> result = new ArrayList<>();
        Machine m = new Machine(DFA);
        int privousIndex = 0;
        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);
            boolean isValid = m.stop();
            m.push(ch);
            if (!m.pack()) {
                m.reset();
                if (isValid && privousIndex != i) {
                    result.add("(" + privousIndex + "): " + s.substring(privousIndex, i));
                }
                if (m.isHead(ch)) {
                    m.push(ch);
                    privousIndex = i;
                } else {
                    privousIndex = i + 1;
                }
            }
        }
        if (m.stop()) {
            result.add("(" + privousIndex + "): " + s.substring(privousIndex));
        }
        return result;
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
    public List<Token> getTokens() {
        return tokens;
    }

    /**
     * @return the raw
     */
    public String getRaw() {
        return raw;
    }

    @Override
    public String toString() {
        return "cn.hu.Jregex\n{"
                + "\n\traw: " + raw
                + "\n\ttokens: " + tokens
                + "\n\tNFA: " + NFA
                + "\n\tDFA: " + DFA
                + "\n}";
    }
}

class TokenizeFailedException extends Exception {

	private static final long serialVersionUID = 1L;

}
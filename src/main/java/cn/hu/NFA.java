package cn.hu;

import java.util.Map;
import java.util.Stack;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * NFA
 */
public class NFA {
    private Map<Pairs, State> map;
    private List<State> states;
    private State initState;
    private State endState;
    private StringBuffer operands;
    private static int count = 0;
    
    /**
     * NFA.Pairs as the key of map which records the relations of NFA 
     */
    public static class Pairs {
        private State state;
        private String string;

        Pairs(State state, String string) {
            this.state = state;
            this.string = string;
        }

        /**
         * @return the string
         */
        public String getString() {
            return string;
        }
        @Override
        public String toString() {
            return "{state: " + state + ", string: " + string + "}";
        }
    }
    /**
     * constructor with no argument.
     */
    NFA() {
        this.map = new HashMap<>();
        this.states = new ArrayList<>();
        this.operands = new StringBuffer();
    }

    /**
     * constructor with a String from a char. 
     * @param c length 1 String
     */
    NFA(String c) {
        this.map = new HashMap<>();
        this.states = new ArrayList<>();
        this.operands = new StringBuffer();

        if (c.length() > 1)
            return;
        State x = new State(count++), y = new State(count++);
        x.addTransitions(c, y);
        this.initState = x;
        this.endState = y;
        this.operands.append(c);
        this.states.add(x);
        this.states.add(y);
        this.map.put(new Pairs(x, c), y);
    }


    /**
     * constructor
     * @param l token list
     */
    NFA(List<Token> l) {
        Stack<NFA> operandStack = new Stack<>();
        Stack<Token> operatorStack = new Stack<>();
        for (int i = 0; i < l.size();) {
            Token token = l.get(i);
            if (token.getName() == Meta.OPERAND) {
                operandStack.push(new NFA(token.getValue()));
                i++;
            } else {
                switch (token.getName()) {
                    case SLASH:
                        if (i == 0) {
                            operatorStack.push(token);
                            i++;
                            continue;
                        }
                        break;
                    case LEFT_PARENTHESIS:
                        String s = token.getValue();
                        s = "/" + s.substring(1, s.length() - 1) + "/";
                        try {
                            NFA n = new NFA(Jregex.tokenize(s));
                            operandStack.push(n);
                            i++;
                            continue;
                        } catch (Exception e) {
                            //TODO: handle exception
                            e.printStackTrace();
                        }
                        break;
                    case LEFT_BRACKET:
                        operandStack.push(charClass(token));
                        i++;
                        continue;
                    default:
                        break;
                }

                if (token.getName().priority() < operatorStack.peek().getName().priority()) {
                    operatorStack.push(token);
                    i++;
                } else {
                    Token t = operatorStack.pop();
                    switch (t.getName()) {
                        case STAR:
                            operandStack.push(star(operandStack.pop()));
                            break;
                        case PLUS:
                            operandStack.push(plus(operandStack.pop()));
                            break;
                        case OPTIONAL:
                            operandStack.push(optional(operandStack.pop()));
                            break;
                        case CONCAT:
                            NFA n = operandStack.pop();
                            NFA m = operandStack.pop();
                            operandStack.push(concat(m, n));
                            break;
                        case UNION:
                            n = operandStack.pop();
                            m = operandStack.pop();
                            operandStack.push(union(m, n));
                            break;
                        case REPEAT:
                            String s = token.getValue();
                            int min = Integer.valueOf(s.substring(1, s.indexOf(',')));
                            int max = Integer.valueOf(s.substring(s.indexOf(",") + 1, s.length() - 1));
                            operandStack.push(repeat(operandStack.pop(), min, max));
                            break;
                        case SLASH:
                            i++;
                            break;
                        default:
                            break;
                    }
                }

            }
        }
        NFA r = operandStack.pop();
        this.map = r.map;
        this.states = r.states;
        this.initState = r.initState;
        this.endState = r.endState;
        this.operands = r.operands;
    }
    /**
     * repeat UNFINISHED
     * @param n NFA
     * @param min specified minimum repeat times
     * @param max specified maximum repeat times
     */
    private NFA repeat(NFA n, int min, int max) {

        return n;
    }
    /**
     * star
     * @param n the operand
     */
    private NFA star(NFA n) {
        State s = new State(NFA.count++);
        State e = new State(NFA.count++);
        n.states.addAll(Arrays.asList(s, e));
        s.addTransitions(null, n.initState);
        s.addTransitions(null, e);
        n.endState.addTransitions(null, e);
        n.endState.addTransitions(null, n.initState);
        n.map.putAll(s.getTransitions());
        n.map.putAll(n.endState.getTransitions());
        n.initState = s;
        n.endState = e;
        return n;
    }
    /**
     * concat
     * @param l left operand
     * @param r right operand
     */
    private NFA concat(NFA l, NFA r) {
        l.map.putAll(r.map);
        l.map.put(new NFA.Pairs(l.endState, null), r.initState);
        l.states.addAll(r.states);
        l.operands.append(r.operands);
        l.endState.addTransitions(null, r.initState);
        l.endState = r.endState;
        return l;
    }
    /**
     * union
     * @param l left operand
     * @param r right operand
     */
    private NFA union(NFA l, NFA r) {
        State s = new State(NFA.count++);
        State e = new State(NFA.count++);
        s.addTransitions(null, l.getInitState());
        s.addTransitions(null, r.getInitState());
        l.endState.addTransitions(null, e);
        r.endState.addTransitions(null, e);
        l.map.putAll(r.getMap());
        l.states.addAll(r.getStates());
        l.operands.append(r.getOperands());
        l.map.putAll(s.getTransitions());
        l.map.putAll(l.endState.getTransitions());
        l.map.putAll(r.endState.getTransitions());
        l.states.addAll(Arrays.asList(s, e));
        l.initState = s;
        l.endState = e;
        return l;
    }

    private NFA plus(NFA n) {
        n.map.put(new NFA.Pairs(n.endState, null), n.initState);
        n.endState.addTransitions(null, n.initState);
        return n;
    }

    private NFA optional(NFA n) {
        State s = new State(NFA.count++);
        State e = new State(NFA.count++);
        n.states.addAll(Arrays.asList(s, e));
        s.addTransitions(null, n.initState);
        s.addTransitions(null, e);
        n.endState.addTransitions(null, e);
        n.map.putAll(s.getTransitions());
        n.map.putAll(n.endState.getTransitions());
        n.initState = s;
        n.endState = e;
        return n;
    }

    private NFA charClass(Token t) {
        NFA n = new NFA();
        String str = t.getValue();
        System.out.println(str);
        str = str.substring(1, str.length() - 1);
        State s = new State(NFA.count++);
        State e = new State(NFA.count++);
        n.states.addAll(Arrays.asList(s, e));
        n.initState = s;
        n.endState = e;
        for (int i = 0; i < str.length();) {
            if (i > str.length() - 3 || str.charAt(i + 1) != '-') {
                State l = new State(NFA.count++);
                State r = new State(NFA.count++);
                l.addTransitions(String.valueOf(str.charAt(i)), r);
                s.addTransitions(null, l);
                r.addTransitions(null, e);
                n.states.addAll(Arrays.asList(l, r));
                n.map.putAll(l.getTransitions());
                n.map.putAll(r.getTransitions());
                n.operands.append(str.charAt(i));
                i++;
            } else {
                char min = str.charAt(i), max = str.charAt(i + 2);
                for (char c = min; c <= max; c = (char) (c + 1)) {
                    State l = new State(NFA.count++);
                    State r = new State(NFA.count++);
                    l.addTransitions(String.valueOf(c), r);
                    s.addTransitions(null, l);
                    r.addTransitions(null, e);
                    n.states.addAll(Arrays.asList(l, r));
                    n.map.putAll(l.getTransitions());
                    n.map.putAll(r.getTransitions());
                    n.operands.append(c);
                }
                i += 3;
            }
        }
        n.map.putAll(s.getTransitions());
        return n;
    }

    /**
     * @return the initState
     */
    public State getInitState() {
        return initState;
    }

    /**
     * @return the endState
     */
    public State getEndState() {
        return endState;
    }


    /**
     * @return the map
     */
    public Map<Pairs, State> getMap() {
        return map;
    }

    /**
     * @return the states
     */
    public List<State> getStates() {
        return states;
    }

    /**
     * @return the operands
     */
    public StringBuffer getOperands() {
        return operands;
    }
}
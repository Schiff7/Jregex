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
    public static int count = 0;
    
    /**
     * NFA.Pairs as the key of map which records the relations of NFA 
     */
    public static class Pairs {
        private State state;
        private String string;

        public Pairs(State state, String string) {
            this.state = state;
            this.string = string;
        }

        /**
         * @return the state
         */
        public State getState() {
            return state;
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
    public NFA() {
        this.map = new HashMap<>();
        this.states = new ArrayList<>();
        this.operands = new StringBuffer();
    }

    /**
     * constructor with a String from a char. 
     * @param c length 1 String
     */
    public NFA(String c) {
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
    public NFA(List<Token> l) {
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
     * @param n
     * @param min
     * @param max
     */
    public NFA repeat(NFA n, int min, int max) {
        State s = new State(NFA.count++);
        State e = new State(NFA.count++);
        n.getStates().addAll(Arrays.asList(s, e));
        s.addTransitions(null, n.getInitState());
        s.addTransitions(null, e);
        n.getEndState().addTransitions(null, e);
        n.getEndState().addTransitions(null, n.getInitState());
        n.getMap().putAll(s.getTransitions());
        n.getMap().putAll(n.getEndState().getTransitions());
        n.setInitState(s);
        n.setEndState(e);
        return n;
    }
    /**
     * star
     * @param n
     */
    public NFA star(NFA n) {
        State s = new State(NFA.count++);
        State e = new State(NFA.count++);
        n.getStates().addAll(Arrays.asList(s, e));
        s.addTransitions(null, n.getInitState());
        s.addTransitions(null, e);
        n.getEndState().addTransitions(null, e);
        n.getEndState().addTransitions(null, n.getInitState());
        n.getMap().putAll(s.getTransitions());
        n.getMap().putAll(n.getEndState().getTransitions());
        n.setInitState(s);
        n.setEndState(e);
        return n;
    }
    /**
     * concat
     * @param l
     * @param r
     */
    public NFA concat(NFA l, NFA r) {
        l.getMap().putAll(r.getMap());
        l.getMap().put(new NFA.Pairs(l.getEndState(), null), r.getInitState());
        l.getStates().addAll(r.getStates());
        l.getOperands().append(r.getOperands());
        l.getEndState().addTransitions(null, r.getInitState());
        l.setEndState(r.getEndState());
        return l;
    }
    /**
     * union
     * @param l
     * @param r
     */
    public NFA union(NFA l, NFA r) {
        State s = new State(NFA.count++);
        State e = new State(NFA.count++);
        s.addTransitions(null, l.getInitState());
        s.addTransitions(null, r.getInitState());
        l.getEndState().addTransitions(null, e);
        r.getEndState().addTransitions(null, e);
        l.getMap().putAll(r.getMap());
        l.getStates().addAll(r.getStates());
        l.getOperands().append(r.getOperands());
        l.getMap().putAll(s.getTransitions());
        l.getMap().putAll(l.getEndState().getTransitions());
        l.getMap().putAll(r.getEndState().getTransitions());
        l.getStates().addAll(Arrays.asList(s, e));
        l.setInitState(s);
        l.setEndState(e);
        return l;
    }

    /**
     * @return the initState
     */
    public State getInitState() {
        return initState;
    }

    /**
     * @param initState the initState to set
     */
    public void setInitState(State initState) {
        this.initState = initState;
    }

    /**
     * @return the endState
     */
    public State getEndState() {
        return endState;
    }

    /**
     * @param endState the endState to set
     */
    public void setEndState(State endState) {
        this.endState = endState;
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
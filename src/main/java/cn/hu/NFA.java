package cn.hu;

import java.util.Map;
import java.util.ArrayList;
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
    }

    public NFA() {
        this.map = new HashMap<>();
        this.states = new ArrayList<>();
        this.operands = new StringBuffer();
    }

    /**
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
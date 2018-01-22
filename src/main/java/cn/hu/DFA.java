package cn.hu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DFA
 */
class DFA {
    private Map<Pairs, List<State>> map;
    private List<List<State>> states;
    private List<State> initState;
    private StringBuffer operands;

    public DFA() {
        this.map = new HashMap<>();
        this.states = new ArrayList<>();
        this.initState = new ArrayList<>();
        this.operands = new StringBuffer();
    }

    public static class Pairs {
        private List<State> state;
        private String string;
        
        public Pairs(List<State> state, String string) {
            this.state = state;
            this.string = string;
        }
        /**
         * @return the state
         */
        public List<State> getState() {
            return state;
        }
        /**
         * @return the string
         */
        public String getString() {
            return string;
        }
    }

    /**
     * @return the map
     */
    public Map<Pairs, List<State>> getMap() {
        return map;
    }
    /**
     * @return the states
     */
    public List<List<State>> getStates() {
        return states;
    }
    /**
     * @return the initState
     */
    public List<State> getInitState() {
        return initState;
    }
    /**
     * @param initState the initState to set
     */
    public void setInitState(List<State> initState) {
        this.initState = initState;
    }
    /**
     * @return the operands
     */
    public StringBuffer getOperands() {
        return operands;
    }
}
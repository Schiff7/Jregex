package cn.hu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * DFA
 */
class DFA {
    private Map<Pairs, Set<State>> map;
    private List<Set<State>> states;
    private Set<State> initState;
    private StringBuffer operands;

    public DFA() {
        this.map = new HashMap<>();
        this.states = new ArrayList<>();
        this.initState = new HashSet<>();
        this.operands = new StringBuffer();
    }

    public static class Pairs {
        private Set<State> state;
        private String string;

        public Pairs(Set<State> state, String string) {
            this.state = state;
            this.string = string;
        }

        /**
         * @return the state
         */
        public Set<State> getState() {
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
    public Map<Pairs, Set<State>> getMap() {
        return map;
    }

    /**
     * @return the states
     */
    public List<Set<State>> getStates() {
        return states;
    }

    /**
     * @return the initState
     */
    public Set<State> getInitState() {
        return initState;
    }

    /**
     * @param initState the initState to set
     */
    public void setInitState(Set<State> initState) {
        this.initState = initState;
    }

    /**
     * @return the operands
     */
    public StringBuffer getOperands() {
        return operands;
    }
    /**
     * @param operands the operands to set
     */
    public void setOperands(StringBuffer operands) {
        this.operands = operands;
    }
}
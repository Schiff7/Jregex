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
    private Set<Set<State>> acceptStates;
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

        @Override
        public String toString() {
            return "{state: " + state + ", string: " + string + "}";
        }
        @Override
        public boolean equals(Object obj) {
            if (obj == this)
                return true;
            return state.equals( ((Pairs)obj).state ) && string.equals( ((Pairs)obj).string );
        }
        @Override
        public int hashCode() {
            int h = 0;
            for (int i = 0; i < string.length(); i++) {
                h += string.charAt(i);
            }
            h += string.length();
            return state.stream().map(x -> x.getId()).reduce(0, (acc, item) -> acc + item) + h;
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
    /**
     * @return the acceptStates
     */
    public Set<Set<State>> getAcceptStates() {
        return acceptStates;
    }
    /**
     * @param acceptStates the acceptStates to set
     */
    public void setAcceptStates(Set<Set<State>> acceptStates) {
        this.acceptStates = acceptStates;
    }
}
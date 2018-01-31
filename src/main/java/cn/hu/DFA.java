package cn.hu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * DFA
 */
class DFA {
    private Map<Pairs, Set<State>> map;
    private List<Set<State>> states;
    private Set<Set<State>> acceptStates;
    private Set<State> initState;
    private StringBuffer operands;

    /**
     * DFA.Pairs as the key of map which records the relations of DFA 
     */
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
     * constructor with no argument
     */
    public DFA() {
        this.map = new HashMap<>();
        this.states = new ArrayList<>();
        this.initState = new HashSet<>();
        this.operands = new StringBuffer();
    }
    /**
     * constructe from a NFA
     * @param n NFA
     */
    public DFA(NFA n) {
        this.map = new HashMap<>();
        this.states = new ArrayList<>();
        Set<State> initState = epsilonClosure(new HashSet<State>(Arrays.asList(n.getInitState())));
        this.initState = initState;
        this.operands = n.getOperands();
        this.states.add(new HashSet<State>(initState));
        move(n, Arrays.asList(initState));
        this.acceptStates = this.states.stream().filter(state -> state.contains(n.getEndState())).collect(Collectors.toSet());
    }
    /**
     * move
     * @param n
     * @param states
     */
    List<Set<State>> move(NFA n, List<Set<State>> states) {
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
                l = epsilonClosure(l);
                if (l.equals(state)) {
                    this.map.put(new DFA.Pairs(state, s), l);
                }
                if ( !(l.equals(state) || l.isEmpty()) ) {
                    r.add(l);
                    this.states.add(l);
                    this.map.put(new DFA.Pairs(state, s), l);
                }
            }
        });
        return move(n, r);
    }

    /**
     * epsilonClosure
     * @param l
     */
    public Set<State> epsilonClosure(Set<State> l) {
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
            l.addAll(epsilonClosure(r));
        }
        return l;
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
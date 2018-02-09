package cn.hu;

import java.util.*;
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

        Pairs(Set<State> state, String string) {
            this.state = state;
            this.string = string;
        }

        @Override
        public String toString() {
            return "{state: " + state + ", string: " + string + "}";
        }
        @Override
        public boolean equals(Object obj) {
            return (obj != null)
                    && (obj.getClass() == this.getClass())
                    && (obj == this || state.equals(((Pairs) obj).state)
                    && string.equals(((Pairs) obj).string));
        }
        @Override
        public int hashCode() {
            int h = 0;
            for (int i = 0; i < string.length(); i++) {
                h += string.charAt(i);
            }
            h += string.length();
            return state.stream().map(State::getId).reduce(0, (acc, item) -> acc + item) + h;
        }
    }
    /**
     * constructor with no argument
     */
    DFA() {
        this.map = new HashMap<>();
        this.states = new ArrayList<>();
        this.initState = new HashSet<>();
        this.operands = new StringBuffer();
    }
    /**
     * constructed from a NFA
     * @param n NFA
     */
    DFA(NFA n) {
        this.map = new HashMap<>();
        this.states = new ArrayList<>();
        Set<State> initState = epsilonClosure(new HashSet<>(Collections.singletonList(n.getInitState())));
        this.initState = initState;
        this.operands = n.getOperands();
        this.states.add(new HashSet<>(initState));
        move(n, Collections.singletonList(initState));
        this.acceptStates = this.states.stream().filter(state -> state.contains(n.getEndState())).collect(Collectors.toSet());
    }
    /**
     * move
     * @param n target NFA
     * @param states states
     */
    private List<Set<State>> move(NFA n, List<Set<State>> states) {
        if (states.isEmpty()) {
            return null;
        }
        List<Set<State>> r = new ArrayList<>();
        states.forEach(state -> {
            for (int i = 0; i < n.getOperands().length(); i++) {
                String s = String.valueOf(n.getOperands().charAt(i));
                Set<State> set = new HashSet<>();
                Set<State> l = state.stream()
                        .map(x -> x.getTransitions()
                                .keySet()
                                .stream()
                                .filter(pairs -> null != pairs.getString() && pairs.getString().equals(s))
                                .map(pairs -> x.getTransitions().get(pairs)
                        ).collect(Collectors.toSet()))
                        .reduce(set, (acc, item) -> {
                            acc.addAll(item);
                            return acc;
                        });
                l = epsilonClosure(l);
                if (this.states.contains(l)) {
                    this.map.put(new DFA.Pairs(state, s), l);
                }
                if ( !(this.states.contains(l) || l.isEmpty()) ) {
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
     * @param l state set
     */
    private Set<State> epsilonClosure(Set<State> l) {
        Set<State> set = new HashSet<>();
        Set<State> r = l.stream()
                .map(
                        state -> state.getTransitions().keySet().stream()
                                .filter(pairs -> pairs.getString() == null)
                                .map(pairs -> state.getTransitions().get(pairs))
                                .collect(Collectors.toSet())
                ).reduce(set, (acc, item) -> {
                    acc.addAll(item);
                    return acc;
                });
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
     * @return the initState
     */
    public Set<State> getInitState() {
        return initState;
    }

    /**
     * @return the acceptStates
     */
    public Set<Set<State>> getAcceptStates() {
        return acceptStates;
    }

    /**
     * @return the operands
     */
    public StringBuffer getOperands() {
        return operands;
    }
}
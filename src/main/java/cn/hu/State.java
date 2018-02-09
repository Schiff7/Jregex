package cn.hu;

import java.util.HashMap;
import java.util.Map;

/**
 * State
 */
public class State {
    private int id;
    private Map<NFA.Pairs, State> transitions;

    State(int id) {
        this.id = id;
        this.transitions = new HashMap<>();
    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @return the transitions
     */
    public Map<NFA.Pairs, State> getTransitions() {
        return transitions;
    }

    /**
     * the transitions to set
     * @param c length 1 string
     * @param s state
     */
    public void addTransitions(String c, State s) {
        this.transitions.put(new NFA.Pairs(this, c), s);
    }

    @Override
    public boolean equals(Object obj) {
        return (obj != null)
                && (obj.getClass() == this.getClass())
                && (obj == this || this.id == ((State) obj).id);

    }
    @Override
    public String toString() {
        return "{id: " + id + "}";
    }
}
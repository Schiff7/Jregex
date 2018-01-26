package cn.hu;

import java.util.HashMap;
import java.util.Map;

/**
 * State
 */
public class State {
    private int id;
    private Map<NFA.Pairs, State> transitions;

    public State(int id) {
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
     * @param transitions the transitions to set
     */
    public void addTransitions(String c, State s) {
        this.transitions.put(new NFA.Pairs(this, c), s);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        
        return this.id == ((State)obj).id;
    }
}
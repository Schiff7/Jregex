package cn.hu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Collectors;

/**
 * Jregex
 */
public class Jregex {
    private String raw;
    private NFA NFA;
    private DFA DFA;

    public Jregex(String raw) {
        this.raw = raw;
        this.NFA = toNFA(this.raw);
        this.DFA = toDFA(this.NFA);
    }

    /**
     * @return to NFA
     */
    public NFA toNFA(String s) {
        Stack<NFA> operandStack = new Stack<>();
        Stack<MetaChr> operatorStack = new Stack<>();
        int combo = 0;
        for(int i = 0; i < s.length();) {
            String p = String.valueOf(s.charAt(i));
            MetaChr mc = MetaChr.map(p);
            if(null != mc) {
                if(i == 0 && mc.optType() == MetaChr.OperationType.DELIMITER) {
                    operatorStack.push(mc);
                    i++;
                    continue;
                }
                if(operatorStack.peek().priority() > mc.priority()) {
                    operatorStack.push(mc);
                    if(mc.optType().paramSize() > 1) {
                        combo = 0;
                    }
                    i++;
                } else {
                    MetaChr tmp = operatorStack.pop();
                    switch(tmp.optType()) {
                        case PREFIX:
                            break;
                        case SUFFIX:
                            operandStack.push(tmp.opt().exe(operandStack.pop()));
                            break;
                        case CONJUNCTION:
                            NFA r = operandStack.pop(), l = operandStack.pop();
                            operandStack.push(tmp.opt().exe(l, r));
                            break;
                        case CHARCLASS:
                            break;
                        case GROUP:
                            break;
                        case DELIMITER:
                            i++;
                            break;
                        default:
                            break;
                    }
                }
            } else {
                combo += 1;
                if(combo < 2) {
                    operandStack.push(new NFA(p));
                    i++;
                } else {
                    if(operatorStack.peek().priority() > MetaChr.CONCAT.priority()) {
                        operatorStack.push(MetaChr.CONCAT);
                        operandStack.push(new NFA(p));
                        combo = 1;
                        i++;
                    } else {
                        MetaChr tmp = operatorStack.pop();
                        System.out.println(tmp);
                        switch(tmp.optType()) {
                            case PREFIX:
                                break;
                            case SUFFIX:
                                operandStack.push(tmp.opt().exe(operandStack.pop()));
                                operatorStack.push(MetaChr.CONCAT);
                                operandStack.push(new NFA(p));
                                combo = 1;
                                i++;
                                break;
                            case CONJUNCTION:
                                NFA r = operandStack.pop(), l = operandStack.pop();
                                operandStack.push(tmp.opt().exe(l, r));
                                break;
                            case CHARCLASS:
                                break;
                            case GROUP:
                                break;
                            case DELIMITER:
                                break;
                            default:
                                break;
                        }
                    }
                }
            }
        }
        return operandStack.pop();
    }

    public DFA toDFA(NFA n) {
        class Anonymous {
            private DFA DFA;
            Anonymous() {
                this.DFA = new DFA();
                Map<NFA.Pairs, State> initTransitions = n.getInitState().getTransitions(); 
                List<State> initState = initTransitions.keySet()
                    .stream()
                    .filter(x -> x.getString() == null)
                    .map(y -> initTransitions.get(y))
                    .collect(Collectors.toList());
                this.DFA.setInitState(initState);
                epsilonClosure(Arrays.asList(initState));
            }
            List<State> move(List<State> l) {
                Set<State> set = new HashSet<>();
                List<State> r = new ArrayList<>( l.stream()
                    .map(x -> {
                        return x.getTransitions().keySet()
                            .stream()
                            .filter(y -> y.getString() == null)
                            .map(z -> x.getTransitions().get(z))
                            .collect(Collectors.toSet());
                    })
                    .reduce(set, (acc, item) -> {
                        acc.addAll(item);
                        return acc;
                    })
                );
                return r;
            }
            List<List<State>> epsilonClosure(List<List<State>> states) {
                List<List<State>> r = new ArrayList<>();
                states.stream().map(state -> {
                    for(int i = 0; i < n.getOperands().length(); i++) {
                        String s = String.valueOf(n.getOperands().charAt(i));
                        Set<State> set = new HashSet<>();
                        List<State> l = new ArrayList<>( state.stream()
                            .map(x -> {
                                return x.getTransitions().keySet()
                                    .stream()
                                    .filter(y -> y.getString().equals(s))
                                    .map(z -> x.getTransitions().get(z))
                                    .collect(Collectors.toSet());
                            })
                            .reduce(set, (acc, item) -> {
                                acc.addAll(item);
                                return acc;
                            })
                        );
                        l = this.move(l);
                        r.add(l);
                        this.DFA.getStates().add(l);
                        this.DFA.getMap().put(new DFA.Pairs(state, s), l);
                    }
                    return r;
                });
                return epsilonClosure(r);
            }
        }

        return new Anonymous().DFA;
    }
    /**
     * @return to DFA
     */
    public DFA toDFA() {
        return null;
    }

    /**
     * @return the DFA
     */
    public DFA getDFA() {
        return DFA;
    }

    /**
     * @return the NFA
     */
    public NFA getNFA() {
        return NFA;
    }

    /**
     * @return the raw
     */
    public String getRaw() {
        return raw;
    }
}
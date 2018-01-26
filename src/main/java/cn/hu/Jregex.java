package cn.hu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
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
        for (int i = 0; i < s.length();) {
            String p = String.valueOf(s.charAt(i));
            MetaChr mc = MetaChr.map(p);
            if (null != mc) {
                if (i == 0 && mc.optType() == MetaChr.OperationType.DELIMITER) {
                    operatorStack.push(mc);
                    i++;
                    continue;
                }
                if (operatorStack.peek().priority() > mc.priority()) {
                    operatorStack.push(mc);
                    if (mc.optType().paramSize() > 1) {
                        combo = 0;
                    }
                    i++;
                } else {
                    MetaChr tmp = operatorStack.pop();
                    switch (tmp.optType()) {
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
                if (combo < 2) {
                    operandStack.push(new NFA(p));
                    i++;
                } else {
                    if (operatorStack.peek().priority() > MetaChr.CONCAT.priority()) {
                        operatorStack.push(MetaChr.CONCAT);
                        operandStack.push(new NFA(p));
                        combo = 1;
                        i++;
                    } else {
                        MetaChr tmp = operatorStack.pop();
                        switch (tmp.optType()) {
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
            private DFA d = new DFA();

            Anonymous() {
                Set<State> initState = move(new HashSet<State>(Arrays.asList(n.getInitState())));
                d.setInitState(initState);
                d.setOperands(n.getOperands());
                d.getStates().add(new HashSet<State>(initState));
                epsilonClosure(Arrays.asList(initState));
            }

            Set<State> move(Set<State> l) {
                Set<State> set = new HashSet<>();
                Set<State> r = l.stream().map(state -> {
                    return state.getTransitions().keySet().stream().filter(pairs -> pairs.getString() == null)
                            .map(pairs -> state.getTransitions().get(pairs)).collect(Collectors.toSet());
                }).reduce(set, (acc, item) -> {
                    acc.addAll(item);
                    return acc;
                });
                System.out.println(r);
                if (!r.isEmpty()) {
                    l.addAll(move(r));
                }
                return l;
            }

            List<Set<State>> epsilonClosure(List<Set<State>> states) {
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
                        l = move(l);
                        if ((!l.equals(state)) && (!l.isEmpty())) {
                            r.add(l);
                            d.getStates().add(l);
                            d.getMap().put(new DFA.Pairs(state, s), l);
                        }
                    }
                });
                return epsilonClosure(r);
            }
        }

        return new Anonymous().d;
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
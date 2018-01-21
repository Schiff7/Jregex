package cn.hu;

import java.util.Stack;

/**
 * Jregex
 */
public class Jregex {
    private String raw;
    private NFA NFA;
    private DFA DFA;

    public Jregex(String raw) {
        this.raw = raw;
        this.NFA = toNFA();
    }

    /**
     * @return to NFA
     */
    public NFA toNFA() {
        Stack<NFA> operandStack = new Stack<>();
        Stack<MetaChr> operatorStack = new Stack<>();
        int combo = 0;
        for(int i = 0; i < this.raw.length();) {
            String p = String.valueOf(this.raw.charAt(i));
            MetaChr mc = MetaChr.map(p);
            System.out.println(operatorStack);
            System.out.println(operandStack);
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
                    System.out.println(tmp);
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
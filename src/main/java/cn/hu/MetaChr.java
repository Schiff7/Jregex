package cn.hu;

import java.util.List;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * MetaChr
 */
public enum MetaChr {
    LIMIT ("/", OperationType.DELIMITER, 5, (n) -> {
        return null;
    }),
    ESCAPE ("\\", OperationType.PREFIX, 0, (n) -> {
        return null;
    }),
    CONCAT ("", OperationType.CONJUNCTION, 3, (n) -> {
        n[0].getMap().putAll(n[1].getMap());
        n[0].getMap().put(new NFA.Pairs(n[0].getEndState(), null), n[1].getInitState());
        n[0].getStates().addAll(n[1].getStates());
        n[0].getOperands().append(n[1].getOperands());
        n[0].getEndState().addTransitions(null, n[1].getInitState());
        n[0].setEndState(n[1].getEndState());
        return n[0];
    }),
    POINT (".",OperationType.CHARCLASS , 3, (n) -> {
        return null;
    }),
    START ("^", OperationType.PREFIX, 3, (n) -> {
        return null;
    }),
    END ("$", OperationType.SUFFIX, 3, (n) -> {
        return null;
    }),
    UNION ("|", OperationType.CONJUNCTION, 4, (n) -> {
        State s = new State(NFA.count++);
        State e = new State(NFA.count++);
        s.addTransitions(null, n[0].getInitState());
        s.addTransitions(null, n[1].getInitState());
        n[0].getEndState().addTransitions(null, e);
        n[1].getEndState().addTransitions(null, e);
        n[0].getMap().putAll(n[1].getMap());
        n[0].getStates().addAll(n[1].getStates());
        n[0].getOperands().append(n[1].getOperands());
        n[0].getMap().putAll(s.getTransitions());
        n[0].getMap().putAll(n[0].getEndState().getTransitions());
        n[0].getMap().putAll(n[1].getEndState().getTransitions());
        n[0].getStates().addAll(Arrays.asList(s, e));
        n[0].setInitState(s);
        n[0].setEndState(e);
        return n[0];
    }),
    STAR ("*", OperationType.SUFFIX, 2, (n) -> {
        State s = new State(NFA.count++);
        State e = new State(NFA.count++);
        n[0].getStates().addAll(Arrays.asList(s, e));
        s.addTransitions(null, n[0].getInitState());
        s.addTransitions(null, e);
        n[0].getEndState().addTransitions(null, e);
        n[0].getEndState().addTransitions(null, n[0].getInitState());
        n[0].getMap().putAll(s.getTransitions());
        n[0].getMap().putAll(n[0].getEndState().getTransitions());
        n[0].setInitState(s);
        n[0].setEndState(e);
        return n[0];
    }),
    PLUS ("+", OperationType.SUFFIX, 2, (n) -> {
        return null;
    }),
    QUESTION_MARK ("?", OperationType.SUFFIX, 2, (n) -> {
        return null;
    }),
    LEFT_PARENTHESIS ("(", OperationType.GROUP, 1, (n) -> {
        return null;
    }),
    NON_CAPTURING_GROUP ("(?:", OperationType.GROUP, 1, (n) -> {
        return null;
    }),
    POSITIVE_LOOKAHEAD ("(?=", OperationType.GROUP, 1, (n) -> {
        return null;
    }),
    NEGATIVE_LOOKAHEAD ("(?!", OperationType.GROUP, 1, (n) -> {
        return null;
    }),
    RIGHT_PARENTHESIS (")", OperationType.GROUP, 1, (n) -> {
        return null;
    }),
    LEFT_BRACKET ("[", OperationType.GROUP, 1, (n) -> {
        return null;
    }),
    RIGHT_BRACKET ("]", OperationType.GROUP, 1, (n) -> {
        return null;
    }),
    LEFT_BRACE ("{", OperationType.GROUP, 2, (n) -> {
        return null;
    }),
    RIGHT_BRACE ("}", OperationType.GROUP, 2, (n) -> {
        return null;
    });

    private final String value;
    private final OperationType optType;
    private final int priority;
    private final Operation opt;

    static enum OperationType {
        PREFIX (1),
        SUFFIX (1),
        CONJUNCTION (2),
        CHARCLASS (1),
        GROUP (1),
        DELIMITER (1);

        private final int paramSize;
        
        OperationType(int paramSize) {
            this.paramSize = paramSize;
        }

        public int paramSize() { return paramSize; }
    }

    MetaChr(String value, OperationType optType, int priority, Operation opt) {
        this.value = value;
        this.optType = optType;
        this.opt = opt;
        this.priority = priority;
    }
    public String value() { return value; }
    public OperationType optType() { return optType; }
    public int priority() { return priority; }
    public Operation opt() { return opt; }
    public static MetaChr map(String s) {
        List<MetaChr> resultList = Arrays.asList(MetaChr.values())
            .stream()
            .filter(x -> x.value().equals(s))
            .collect(Collectors.toList());
        return resultList.isEmpty() ? null : resultList.get(0);
    }
}



/**
 * Operate
 */
@FunctionalInterface
interface Operation {
    public abstract NFA exe(NFA...n);
}
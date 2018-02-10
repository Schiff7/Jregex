package cn.hu;

/**
 * Meta
 */
public enum Meta {
    /**
     * Virtual Mark.
     */
    OPERAND ("_", 4),
    CONCAT ("_", 4),


    /**
     * Real Mark.
     */
    BACKSLASH ("\\", 1),
    SLASH ("/", 6),
    STAR ("*", 3),
    UNION ("|", 5),
    DOLLAR ("$", 4),
    CARET ("^", 4),
    PLUS ("+", 3),
    OPTIONAL ("?", 3),
    LEFT_PARENTHESIS ("(", 4),
    NON_CAPTURING_GROUP ("(?:", 4),
    POSITIVE_LOOKAHEAD ("(?=", 4),
    NEGATIVE_LOOKAHEAD ("(?!", 4),
    RIGHT_PARENTHESIS (")", 4),
    LEFT_BRACKET ("[", 4),
    RIGHT_BRACKET ("]", 4),
    LEFT_BRACE ("{", 3),
    RIGHT_BRACE ("}", 3),
    POINT (".", 4),
    DIGITAL ("\\d", 4),
    LINEFEED ("\n", 4);

    private final String value;
    private final int priority;

    Meta(String value, int priority) {
        this.value = value;
        this.priority = priority;
    }

    public String value() {
        return this.value;
    }

    public int priority() {
        return this.priority;
    }

    public static Meta map(String s) {
        if (s.equals("_"))
            return null;
        for (Meta m : Meta.values()) {
            if (m.value.equals(s) )
                return m;
        }
        return null;
    }
}
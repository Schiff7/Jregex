package cn.hu;

/**
 * Meta
 */
public enum Meta {
    /**
     * Virtual Mark.
     */
    OPERAND ("_", 4),
    REPEAT ("_", 3),
    CONCAT ("_", 4),
    CHARCLASS("_", 4),


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
    QUES_MARK ("?", 3),
    LEFT_PARENTHESIS ("(", 2),
    NON_CAPTURING_GROUP ("(?:", 2),
    POSITIVE_LOOKAHEAD ("(?=", 2),
    NEGATIVE_LOOKAHEAD ("(?!", 2),
    RIGHT_PARENTHESIS (")", 2),
    LEFT_BRACKET ("[", 2),
    RIGHT_BRACKET ("]", 2),
    LEFT_BRACE ("{", 2),
    RIGHT_BRACE ("}", 2),
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
package cn.hu;

/**
 * Token
 */
public class Token {
    private Meta name;
    private String value;

    Token(Meta name, String value) {
        this.name = name;
        this.value = value;
    }

    /**
     * @return the type
     */
    public Meta getName() {
        return name;
    }
    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "{value: " + value + ", name: " + name + "}";
    }
}
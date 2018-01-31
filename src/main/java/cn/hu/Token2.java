package cn.hu;

/**
 * Token2
 */
public class Token2 {
    private Meta name;
    private String value;

    public Token2(Meta name, String value) {
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
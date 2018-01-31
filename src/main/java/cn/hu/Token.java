package cn.hu;

public class Token {
  private String value;
  private MetaChr name;

  public Token(String value, MetaChr name) {
    this.value = value;
    this.name = name;
  }

  /**
   * @return the value
   */
  public String getValue() {
    return value;
  }
  /**
   * @return the name
   */
  public MetaChr getName() {
    return name;
  }

  @Override
  public String toString() {
    return "{value: " + this.value + ", name: " + this.name + "}";
  }
}
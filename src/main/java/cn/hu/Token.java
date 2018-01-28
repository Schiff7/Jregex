package cn.hu;

import java.util.Stack;

public class Token {
  String value;
  MetaChr name;

  public Token(String value, MetaChr name) {
    this.value = value;
    this.name = name;
  }

  public void exe(Stack<NFA> operandStack, Stack<MetaChr> operatorStack) {
    this.name.opt().exe(n);
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
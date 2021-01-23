package com.github.samyadaleh.cltoolbox.common.lag;

import java.util.List;

public class LagState {

  private String[] rulePackage;
  private String[] category;

  public LagState(String[] rulePackage, String[] category) {
    this.rulePackage = rulePackage;
    this.category = category;
  }

  public String[] getRulePackage() {
    return rulePackage;
  }

  public void setRulePackage(String[] rulePackage) {
    this.rulePackage = rulePackage;
  }

  public String[] getCategory() {
    return category;
  }

  public void setCategory(String[] category) {
    this.category = category;
  }

  @Override public String toString() {
    StringBuilder repr = new StringBuilder();
    repr.append("[{").append(String.join(", ", rulePackage)).append("} ");
    if (category.length == 0) {
      repr.append("ε");
    } else {
      repr.append("(").append(String.join(" ", category)).append(")");
    }
    repr.append("]");
    return repr.toString();
  }
}

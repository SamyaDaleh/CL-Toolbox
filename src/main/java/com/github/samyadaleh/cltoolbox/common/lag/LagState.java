package com.github.samyadaleh.cltoolbox.common.lag;

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
}

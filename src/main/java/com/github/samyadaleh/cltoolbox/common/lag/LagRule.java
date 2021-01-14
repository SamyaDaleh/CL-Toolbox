package com.github.samyadaleh.cltoolbox.common.lag;

public class LagRule {
  private String[] cat1;
  private String[] cat2;
  private LagState state;

  public LagRule(String[] cat1, String[] cat2, LagState state) {
    this.cat1 = cat1;
    this.cat2 = cat2;
    this.state = state;
  }

  public String[] getCat1() {
    return cat1;
  }

  public void setCat1(String[] cat1) {
    this.cat1 = cat1;
  }

  public String[] getCat2() {
    return cat2;
  }

  public void setCat2(String[] cat2) {
    this.cat2 = cat2;
  }

  public LagState getState() {
    return state;
  }

  public void setState(LagState state) {
    this.state = state;
  }
}

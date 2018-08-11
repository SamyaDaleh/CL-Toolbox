package com.github.samyadaleh.cltoolbox.chartparsing;

import java.util.ArrayList;
import java.util.List;

/** Class to hold the methods commonly used by all DeductionRules. */
public abstract class AbstractDynamicDeductionRule
  implements DynamicDeductionRuleInterface {

  protected List<ChartItemInterface> antecedences;
  protected List<ChartItemInterface> consequences;
  protected String name;

  protected int antNeeded;

  @Override public List<ChartItemInterface> getAntecedences() {
    return this.antecedences;
  }

  @Override public void setAntecedences(List<ChartItemInterface> antecedences) {
    this.antecedences = antecedences;
  }

  @Override public String getName() {
    return this.name;
  }

  @Override public int getAntecedencesNeeded() {
    return this.antNeeded;
  }

  @Override public void clearItems() {
    antecedences = new ArrayList<ChartItemInterface>();
    consequences = new ArrayList<ChartItemInterface>();
  }

}

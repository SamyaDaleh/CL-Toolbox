package com.github.samyadaleh.cltoolbox.chartparsing;

import java.util.ArrayList;
import java.util.List;

/** Static deduction rule that stores a set of antecedences and consequences.
 * Used as axiom. */
public class StaticDeductionRule implements DeductionRuleInterface{
  private List<ChartItemInterface> antecedences = new ArrayList<>();
  final List<ChartItemInterface> consequences = new ArrayList<>();
  private String name = null;

  public void addConsequence(ChartItemInterface item) {
    consequences.add(item);
  }

  public List<ChartItemInterface> getAntecedences() {
    return antecedences;
  }

  public void setAntecedences(List<ChartItemInterface> antecedences) {
    this.antecedences = antecedences;
  }

  public List<ChartItemInterface> getConsequences() {
    return consequences;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getName() {
    return this.name;
  }

  @Override public String toString() {
    StringBuilder representation = new StringBuilder();
    for (ChartItemInterface rule : antecedences) {
      representation.append(rule.toString());
    }
    representation.append("\n______\n");
    for (ChartItemInterface rule : consequences) {
      representation.append(rule.toString());
    }
    return representation.toString();
  }
}

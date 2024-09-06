package com.github.samyadaleh.cltoolbox.chartparsing;

import com.github.samyadaleh.cltoolbox.chartparsing.dynamicdeductionrule.DynamicDeductionRuleInterface;
import com.github.samyadaleh.cltoolbox.chartparsing.item.ChartItemInterface;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** Representation of a parsing schema, based on slides from Laura Kallmeyer.
 * Consists of a set of deduction rules and goal items that have to be derived
 * with help of the rules. */
public class ParsingSchema {
  private final List<StaticDeductionRule> axioms = new ArrayList<>();
  private final List<DynamicDeductionRuleInterface> rules = new ArrayList<>();
  private final List<ChartItemInterface> goal = new ArrayList<>();
  
  public void addAxiom(StaticDeductionRule rule) {
    axioms.add(rule);
  }

  public void addRule(DynamicDeductionRuleInterface rule) {
    rules.add(rule);
  }

  public void addGoal(ChartItemInterface item) {
    this.goal.add(item);
  }

  public List<DynamicDeductionRuleInterface> getRules() {
    return this.rules;
  }

  public List<StaticDeductionRule> getAxioms() {
    return this.axioms;
  }

  public List<ChartItemInterface> getGoals() {
    return this.goal;
  }
}

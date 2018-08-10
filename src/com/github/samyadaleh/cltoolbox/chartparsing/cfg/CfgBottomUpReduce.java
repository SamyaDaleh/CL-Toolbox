package com.github.samyadaleh.cltoolbox.chartparsing.cfg;

import java.util.List;

import com.github.samyadaleh.cltoolbox.chartparsing.AbstractDynamicDeductionRule;
import com.github.samyadaleh.cltoolbox.chartparsing.DeductionItem;
import com.github.samyadaleh.cltoolbox.chartparsing.Item;
import com.github.samyadaleh.cltoolbox.common.ArrayUtils;
import com.github.samyadaleh.cltoolbox.common.cfg.CfgProductionRule;

/** If the top o the stack matches the rhs of a rule, replace it with the
 * lhs. */
public class CfgBottomUpReduce extends AbstractDynamicDeductionRule {

  private final CfgProductionRule rule;

  public CfgBottomUpReduce(CfgProductionRule rule) {
    this.rule = rule;
    this.antNeeded = 1;
    this.name = "reduce " + rule.toString();
  }

  @Override public List<Item> getConsequences() {
    if (antecedences.size() == this.antNeeded) {
      String[] itemForm = antecedences.get(0).getItemform();
      String stack = itemForm[0];
      String[] stackSplit = stack.split(" ");
      String i = itemForm[1];
      String gamma =
        ArrayUtils.getStringHeadIfEndsWith(stackSplit, rule.getRhs());
      if (gamma != null) {
        if (gamma.length() == 0) {
          consequences.add(new DeductionItem(rule.getLhs(), i));
        } else {
          consequences.add(new DeductionItem(gamma + " " + rule.getLhs(), i));
        }
      }
    }
    return consequences;
  }

  @Override public String toString() {
    return "[Γ " + ArrayUtils.toString(rule.getRhs()) + ",i]" + "\n______"
      + rule.toString() + "\n" + "[Γ " + rule.getLhs() + ",i]";
  }

}

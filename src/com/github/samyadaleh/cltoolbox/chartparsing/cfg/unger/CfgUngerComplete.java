package com.github.samyadaleh.cltoolbox.chartparsing.cfg.unger;

import java.util.List;

import com.github.samyadaleh.cltoolbox.chartparsing.dynamicdeductionrule.AbstractDynamicDeductionRule;
import com.github.samyadaleh.cltoolbox.chartparsing.item.DeductionChartItem;
import com.github.samyadaleh.cltoolbox.chartparsing.item.ChartItemInterface;
import com.github.samyadaleh.cltoolbox.common.cfg.CfgProductionRule;
import com.github.samyadaleh.cltoolbox.common.tag.Tree;

/**
 * If the top o the stack matches the rhs of a rule, replace it with the lhs.
 */
public class CfgUngerComplete extends AbstractDynamicDeductionRule {

  private final CfgProductionRule rule;

  public CfgUngerComplete(CfgProductionRule rule) {
    this.rule = rule;
    this.antNeeded = rule.getRhs().length + 1;
    this.name = "complete " + rule.toString();
  }

  @Override public List<ChartItemInterface> getConsequences() {
    if (antecedences.size() == this.antNeeded) {
      for (ChartItemInterface mayLhsItem : antecedences) {
        if (!mayLhsItem.getItemForm()[0].startsWith("•")) {
          continue;
        }
        String prevIjPlusOne = mayLhsItem.getItemForm()[1];
        for (int i = 0; i < antNeeded - 1; i++) {
          boolean found = false;
          for (ChartItemInterface mayRhsItem : antecedences) {
            if (mayRhsItem.getItemForm()[0].endsWith("•")
              && mayRhsItem.getItemForm()[0]
                .substring(0, mayRhsItem.getItemForm()[0].length() - 1)
                .equals(rule.getRhs()[i])
              && mayRhsItem.getItemForm()[1].equals(prevIjPlusOne)) {
              found = true;
              prevIjPlusOne = mayRhsItem.getItemForm()[2];
              break;
            }
          }
          if (!found) {
            return this.consequences;
          }
        }
        if (prevIjPlusOne.equals(mayLhsItem.getItemForm()[2])) {
          List<Tree> derivedTrees;
          if (antecedences.get(0).equals(mayLhsItem)) {
            derivedTrees = antecedences.get(1).getTrees();
          } else {
            derivedTrees = antecedences.get(0).getTrees();
          }
          ChartItemInterface consequence = new DeductionChartItem(rule.getLhs() + "•",
            mayLhsItem.getItemForm()[1], mayLhsItem.getItemForm()[2]);
          consequence.setTrees(derivedTrees);
          logItemGeneration(consequence);
          consequences.add(consequence);
        }
      }
    }
    return consequences;
  }

  @Override public String toString() {
    return "[•A, i_0, i_k], [A_1•, i_0, i_1], ... , [A_k•,i_(k-1), i_k]"
      + "\n______" + rule.toString() + "\n" + "[A•, i_0, i_k]";
  }

}

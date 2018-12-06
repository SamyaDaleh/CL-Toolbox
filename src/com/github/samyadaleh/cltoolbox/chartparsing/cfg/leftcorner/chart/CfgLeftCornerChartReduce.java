package com.github.samyadaleh.cltoolbox.chartparsing.cfg.leftcorner.chart;

import com.github.samyadaleh.cltoolbox.chartparsing.cfg.leftcorner.CfgLeftCornerUtils;
import com.github.samyadaleh.cltoolbox.chartparsing.dynamicdeductionrule.AbstractDynamicDeductionRule;
import com.github.samyadaleh.cltoolbox.chartparsing.item.ChartItemInterface;
import com.github.samyadaleh.cltoolbox.chartparsing.item.DeductionChartItem;
import com.github.samyadaleh.cltoolbox.common.ArrayUtils;
import com.github.samyadaleh.cltoolbox.common.cfg.CfgProductionRule;

import java.util.List;

import static com.github.samyadaleh.cltoolbox.common.Constants.DEDUCTION_RULE_CFG_LEFTCORNER_REDUCE;

public class CfgLeftCornerChartReduce extends AbstractDynamicDeductionRule {

  private final CfgProductionRule rule;

  public CfgLeftCornerChartReduce(CfgProductionRule rule) {
    this.name = DEDUCTION_RULE_CFG_LEFTCORNER_REDUCE + " " + rule.toString();
    this.rule = rule;
    this.antNeeded = 1;
  }

  @Override public List<ChartItemInterface> getConsequences() {
    if (antecedences.size() == antNeeded) {
      String[] itemForm = antecedences.get(0).getItemForm();
      String i = itemForm[1];
      String l = itemForm[2];
      if (itemForm[0].equals(rule.getRhs()[0])) {
        ChartItemInterface consequence = new DeductionChartItem(
            rule.getLhs() + " -> " + rule.getRhs()[0] + " •" + ArrayUtils
                .getSubSequenceAsString(rule.getRhs(), 1, rule.getRhs().length),
            i, l);
        CfgLeftCornerUtils
            .generateDerivedTrees(rule, antecedences, consequence);
        logItemGeneration(consequence);
        consequences.add(consequence);
      }
    }
    return this.consequences;
  }

  @Override public String toString() {
    return "[" + rule.getRhs()[0] + ",i,l]" + "\n______ \n" + "[" + rule
        .getLhs() + " - > " + rule.getRhs()[0] + " •" + ArrayUtils
        .getSubSequenceAsString(rule.getRhs(), 1, rule.getRhs().length)
        + ",i,l]";
  }
}

package com.github.samyadaleh.cltoolbox.chartparsing.cfg.leftcorner.chart;

import java.util.List;

import com.github.samyadaleh.cltoolbox.chartparsing.AbstractDynamicDeductionRule;
import com.github.samyadaleh.cltoolbox.chartparsing.DeductionChartItem;
import com.github.samyadaleh.cltoolbox.chartparsing.ChartItemInterface;
import com.github.samyadaleh.cltoolbox.common.ArrayUtils;
import com.github.samyadaleh.cltoolbox.common.cfg.CfgProductionRule;

public class CfgLeftCornerChartReduce extends AbstractDynamicDeductionRule {

  private final CfgProductionRule rule;

  public CfgLeftCornerChartReduce(CfgProductionRule rule) {
    this.name = "reduce " + rule.toString();
    this.rule = rule;
    this.antNeeded = 1;
  }

  @Override public List<ChartItemInterface> getConsequences() {
    if (antecedences.size() == antNeeded) {
      String[] itemForm = antecedences.get(0).getItemform();
      String i = itemForm[1];
      String l = itemForm[2];
      if (itemForm[0].equals(rule.getRhs()[0])) {
        consequences.add(new DeductionChartItem(
          rule.getLhs() + " -> " + rule.getRhs()[0] + " •" + ArrayUtils
            .getSubSequenceAsString(rule.getRhs(), 1, rule.getRhs().length),
          i, l));
      }
    }
    return this.consequences;
  }

  @Override public String toString() {
    return "[" + rule.getRhs()[0] + ",i,l]" + "\n______ \n" + "["
      + rule.getLhs() + " - > " + rule.getRhs()[0] + " •" + ArrayUtils
        .getSubSequenceAsString(rule.getRhs(), 1, rule.getRhs().length)
      + ",i,l]";
  }
}

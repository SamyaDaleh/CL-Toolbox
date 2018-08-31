package com.github.samyadaleh.cltoolbox.chartparsing.converter.cfg;

import com.github.samyadaleh.cltoolbox.chartparsing.item.DeductionChartItem;
import com.github.samyadaleh.cltoolbox.chartparsing.dynamicdeductionrule.DynamicDeductionRuleInterface;
import com.github.samyadaleh.cltoolbox.chartparsing.ParsingSchema;
import com.github.samyadaleh.cltoolbox.chartparsing.StaticDeductionRule;
import com.github.samyadaleh.cltoolbox.chartparsing.cfg.leftcorner.chart.CfgLeftCornerChartMove;
import com.github.samyadaleh.cltoolbox.chartparsing.cfg.leftcorner.chart.CfgLeftCornerChartReduce;
import com.github.samyadaleh.cltoolbox.chartparsing.cfg.leftcorner.chart.CfgLeftCornerChartRemove;
import com.github.samyadaleh.cltoolbox.common.cfg.Cfg;
import com.github.samyadaleh.cltoolbox.common.cfg.CfgProductionRule;

public class CfgToLeftCornerChartRulesConverter {
  /**
   * Converts a cfg to a parsing scheme for LeftCorner parsing, chart version.
   * Based on https://user.phil.hhu.de/~kallmeyer/Parsing/left-corner.pdf at the
   * moment to be used.
   */
  public static ParsingSchema cfgToLeftCornerChartRules(Cfg cfg, String w) {
    ParsingSchema schema = new ParsingSchema();
    String[] wSplit = w.split(" ");

    for (int i = 0; i < wSplit.length; i++) {
      StaticDeductionRule axiom = new StaticDeductionRule();
      axiom.addConsequence(
          new DeductionChartItem(wSplit[i], String.valueOf(i), "1"));
      axiom.setName("scan " + wSplit[i]);
      schema.addAxiom(axiom);
      axiom = new StaticDeductionRule();
      axiom.addConsequence(new DeductionChartItem("", String.valueOf(i), "0"));
      axiom.setName("scan-ε ");
      schema.addAxiom(axiom);
    }
    StaticDeductionRule axiom = new StaticDeductionRule();
    axiom.addConsequence(
        new DeductionChartItem("", String.valueOf(wSplit.length), "0"));
    axiom.setName("scan-ε ");
    schema.addAxiom(axiom);

    for (CfgProductionRule rule : cfg.getProductionRules()) {
      DynamicDeductionRuleInterface reduce = new CfgLeftCornerChartReduce(rule);
      schema.addRule(reduce);
    }

    DynamicDeductionRuleInterface remove = new CfgLeftCornerChartRemove();
    schema.addRule(remove);

    DynamicDeductionRuleInterface move = new CfgLeftCornerChartMove();
    schema.addRule(move);

    schema.addGoal(new DeductionChartItem(cfg.getStartSymbol(), "0",
        String.valueOf(wSplit.length)));
    return schema;
  }
}

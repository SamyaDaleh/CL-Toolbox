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

import static com.github.samyadaleh.cltoolbox.common.Constants.DEDUCTION_RULE_CFG_LEFTCORNER_CHART_AXIOM;
import static com.github.samyadaleh.cltoolbox.common.Constants.DEDUCTION_RULE_CFG_LEFTCORNER_CHART_AXIOM_EPSILON;

public class CfgToLeftCornerChartRulesConverter {

  /**
   * Converts a cfg to a parsing scheme for LeftCorner parsing, chart version.
   * Based on https://user.phil.hhu.de/~kallmeyer/Parsing/left-corner.pdf at the
   * moment to be used.
   */
  public static ParsingSchema cfgToLeftCornerChartRules(Cfg cfg, String w) {
    ParsingSchema schema = new ParsingSchema();
    String[] wSplit = w.split(" ");

    if (!"".equals(wSplit[0])) {
      for (int i = 0; i < wSplit.length; i++) {
        StaticDeductionRule axiom = new StaticDeductionRule();
        axiom.addConsequence(
            new DeductionChartItem(wSplit[i], String.valueOf(i), "1"));
        axiom.setName(
            DEDUCTION_RULE_CFG_LEFTCORNER_CHART_AXIOM + " " + wSplit[i]);
        schema.addAxiom(axiom);
        axiom = new StaticDeductionRule();
        axiom
            .addConsequence(new DeductionChartItem("", String.valueOf(i), "0"));
        axiom.setName(DEDUCTION_RULE_CFG_LEFTCORNER_CHART_AXIOM_EPSILON);
        schema.addAxiom(axiom);
      }
    }
    if (!"".equals(wSplit[0])) {
      StaticDeductionRule axiom = new StaticDeductionRule();
      axiom.addConsequence(
          new DeductionChartItem("", String.valueOf(wSplit.length), "0"));
      axiom.setName(DEDUCTION_RULE_CFG_LEFTCORNER_CHART_AXIOM_EPSILON);
      schema.addAxiom(axiom);
    } else {
      StaticDeductionRule axiom = new StaticDeductionRule();
      axiom.addConsequence(new DeductionChartItem("", "0", "0"));
      axiom.setName(DEDUCTION_RULE_CFG_LEFTCORNER_CHART_AXIOM_EPSILON);
      schema.addAxiom(axiom);
    }

    for (CfgProductionRule rule : cfg.getProductionRules()) {
      DynamicDeductionRuleInterface reduce = new CfgLeftCornerChartReduce(rule);
      schema.addRule(reduce);
    }

    DynamicDeductionRuleInterface remove = new CfgLeftCornerChartRemove();
    schema.addRule(remove);

    DynamicDeductionRuleInterface move = new CfgLeftCornerChartMove();
    schema.addRule(move);

    if (!"".equals(wSplit[0])) {
      schema.addGoal(new DeductionChartItem(cfg.getStartSymbol(), "0",
          String.valueOf(wSplit.length)));
    } else {
      schema.addGoal(new DeductionChartItem(cfg.getStartSymbol(), "0", "0"));
    }
    return schema;
  }
}

package com.github.samyadaleh.cltoolbox.chartparsing.converter.cfg;

import com.github.samyadaleh.cltoolbox.chartparsing.DeductionChartItem;
import com.github.samyadaleh.cltoolbox.chartparsing.DynamicDeductionRuleInterface;
import com.github.samyadaleh.cltoolbox.chartparsing.ParsingSchema;
import com.github.samyadaleh.cltoolbox.chartparsing.StaticDeductionRule;
import com.github.samyadaleh.cltoolbox.chartparsing.cfg.earley.CfgEarleyPredict;
import com.github.samyadaleh.cltoolbox.chartparsing.cfg.earley.CfgEarleyScan;
import com.github.samyadaleh.cltoolbox.chartparsing.cfg.earley.passive.CfgEarleyPassiveComplete;
import com.github.samyadaleh.cltoolbox.chartparsing.cfg.earley.passive.CfgEarleyPassiveConvert;
import com.github.samyadaleh.cltoolbox.common.cfg.Cfg;
import com.github.samyadaleh.cltoolbox.common.cfg.CfgProductionRule;

public class CfgToEarleyPassiveRulesConverter {
  /**
   * Converts a cfg to a parsing scheme for Earley parsing with passive items.
   * Based n https://user.phil.hhu.de/~kallmeyer/Parsing/earley.pdf
   */
  public static ParsingSchema cfgToEarleyPassiveRules(Cfg cfg, String w) {
    String[] wSplit = w.split(" ");
    ParsingSchema schema = new ParsingSchema();

    DynamicDeductionRuleInterface scan = new CfgEarleyScan(wSplit);
    schema.addRule(scan);

    DynamicDeductionRuleInterface complete = new CfgEarleyPassiveComplete();
    schema.addRule(complete);

    DynamicDeductionRuleInterface convert = new CfgEarleyPassiveConvert();
    schema.addRule(convert);

    for (CfgProductionRule rule : cfg.getProductionRules()) {
      if (rule.getLhs().equals(cfg.getStartSymbol())) {
        StaticDeductionRule axiom = new StaticDeductionRule();
        if (rule.getRhs()[0].equals("")) {
          axiom.addConsequence(
              new DeductionChartItem(cfg.getStartSymbol() + " -> •", "0", "0"));
        } else {
          axiom.addConsequence(new DeductionChartItem(
              cfg.getStartSymbol() + " -> •" + String.join(" ", rule.getRhs()),
              "0", "0"));
        }
        axiom.setName("axiom");
        schema.addAxiom(axiom);
        if (rule.getRhs()[0].equals("")) {
          schema.addGoal(
              new DeductionChartItem(cfg.getStartSymbol() + " -> •", "0",
                  String.valueOf(wSplit.length)));
        } else {
          schema.addGoal(new DeductionChartItem(cfg.getStartSymbol(), "0",
              String.valueOf(wSplit.length)));
        }
      }

      DynamicDeductionRuleInterface predict = new CfgEarleyPredict(rule);
      schema.addRule(predict);
    }
    return schema;
  }
}

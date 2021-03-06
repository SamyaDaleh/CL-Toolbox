package com.github.samyadaleh.cltoolbox.chartparsing.converter.cfg;

import com.github.samyadaleh.cltoolbox.chartparsing.item.DeductionChartItem;
import com.github.samyadaleh.cltoolbox.chartparsing.dynamicdeductionrule.DynamicDeductionRuleInterface;
import com.github.samyadaleh.cltoolbox.chartparsing.ParsingSchema;
import com.github.samyadaleh.cltoolbox.chartparsing.StaticDeductionRule;
import com.github.samyadaleh.cltoolbox.chartparsing.cfg.unger.CfgUngerComplete;
import com.github.samyadaleh.cltoolbox.chartparsing.cfg.unger.CfgUngerPredict;
import com.github.samyadaleh.cltoolbox.chartparsing.cfg.unger.CfgUngerScan;
import com.github.samyadaleh.cltoolbox.common.cfg.Cfg;
import com.github.samyadaleh.cltoolbox.common.cfg.CfgProductionRule;

import java.text.ParseException;

import static com.github.samyadaleh.cltoolbox.common.Constants.DEDUCTION_RULE_CFG_UNGER_AXIOM;

public class CfgToUngerRulesConverter {

  /**
   * Unger parsing tries out all possible separations, factorial runtime.
   */
  public static ParsingSchema cfgToUngerRules(Cfg cfg, String w)
      throws ParseException {
    if (cfg.hasEpsilonProductions()) {
      throw new ParseException(
          "CFG must not contain empty productions for Unger parsing.", 1);
    }
    if (cfg.hasDirectLeftRecursion()) {
      throw new ParseException(
          "CFG must not contain left recursion for Unger parsing.", 1);
    }
    String[] wSplit = w.split(" ");
    ParsingSchema schema = new ParsingSchema();

    StaticDeductionRule axiom = new StaticDeductionRule();
    axiom.setName(DEDUCTION_RULE_CFG_UNGER_AXIOM);
    axiom.addConsequence(new DeductionChartItem("•" + cfg.getStartSymbol(), "0",
        String.valueOf(wSplit.length)));
    schema.addAxiom(axiom);

    schema.addGoal(new DeductionChartItem(cfg.getStartSymbol() + "•", "0",
        String.valueOf(wSplit.length)));

    DynamicDeductionRuleInterface scan = new CfgUngerScan(wSplit);
    schema.addRule(scan);

    for (CfgProductionRule rule : cfg.getProductionRules()) {
      DynamicDeductionRuleInterface predict = new CfgUngerPredict(rule, cfg);
      schema.addRule(predict);
      DynamicDeductionRuleInterface complete = new CfgUngerComplete(rule);
      schema.addRule(complete);
    }
    return schema;
  }
}

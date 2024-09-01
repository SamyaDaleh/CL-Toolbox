package com.github.samyadaleh.cltoolbox.chartparsing.converter.cfg;

import com.github.samyadaleh.cltoolbox.chartparsing.ParsingSchema;
import com.github.samyadaleh.cltoolbox.chartparsing.StaticDeductionRule;
import com.github.samyadaleh.cltoolbox.chartparsing.cfg.leftcorner.CfgLeftCornerMove;
import com.github.samyadaleh.cltoolbox.chartparsing.cfg.leftcorner.CfgLeftCornerReduce;
import com.github.samyadaleh.cltoolbox.chartparsing.cfg.leftcorner.CfgLeftCornerRemove;
import com.github.samyadaleh.cltoolbox.chartparsing.dynamicdeductionrule.DynamicDeductionRuleInterface;
import com.github.samyadaleh.cltoolbox.chartparsing.item.DeductionChartItem;
import com.github.samyadaleh.cltoolbox.common.cfg.Cfg;
import com.github.samyadaleh.cltoolbox.common.cfg.CfgProductionRule;

import java.text.ParseException;

import static com.github.samyadaleh.cltoolbox.common.Constants.DEDUCTION_RULE_CFG_LEFTCORNER_AXIOM;

public class CfgToLeftCornerRulesConverter {

  /**
   * Converts a cfg to a parsing scheme for LeftCorner parsing. Based on
   * <a href="https://user.phil.hhu.de/~kallmeyer/Parsing/left-corner.pdf">Left Corner Deduction</a> at the moment
   * to be used.
   */
  public static ParsingSchema cfgToLeftCornerRules(Cfg cfg, String w)
      throws ParseException {
    if (cfg.hasEpsilonProductions()) {
      throw new ParseException(
          "CFG must not contain empty productions for Leftcorner parsing.", 1);
    }
    if (cfg.hasDirectLeftRecursion()) {
      throw new ParseException(
          "CFG must not contain left recursion for Leftcorner parsing.", 1);
    }
    ParsingSchema schema = new ParsingSchema();
    StaticDeductionRule axiom = new StaticDeductionRule();
    axiom.addConsequence(new DeductionChartItem(w, cfg.getStartSymbol(), ""));
    axiom.setName(DEDUCTION_RULE_CFG_LEFTCORNER_AXIOM);
    schema.addAxiom(axiom);

    for (CfgProductionRule rule : cfg.getProductionRules()) {
      DynamicDeductionRuleInterface reduce = new CfgLeftCornerReduce(rule);
      schema.addRule(reduce);
    }

    DynamicDeductionRuleInterface remove = new CfgLeftCornerRemove();
    schema.addRule(remove);

    DynamicDeductionRuleInterface move =
        new CfgLeftCornerMove(cfg.getNonterminals());
    schema.addRule(move);

    schema.addGoal(new DeductionChartItem("", "", ""));
    return schema;
  }
}

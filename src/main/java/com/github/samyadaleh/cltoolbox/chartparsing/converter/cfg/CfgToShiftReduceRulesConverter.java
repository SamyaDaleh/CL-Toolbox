package com.github.samyadaleh.cltoolbox.chartparsing.converter.cfg;

import com.github.samyadaleh.cltoolbox.chartparsing.item.DeductionChartItem;
import com.github.samyadaleh.cltoolbox.chartparsing.dynamicdeductionrule.DynamicDeductionRuleInterface;
import com.github.samyadaleh.cltoolbox.chartparsing.ParsingSchema;
import com.github.samyadaleh.cltoolbox.chartparsing.StaticDeductionRule;
import com.github.samyadaleh.cltoolbox.chartparsing.cfg.shiftreduce.CfgBottomUpReduce;
import com.github.samyadaleh.cltoolbox.chartparsing.cfg.shiftreduce.CfgBottomUpShift;
import com.github.samyadaleh.cltoolbox.common.cfg.Cfg;
import com.github.samyadaleh.cltoolbox.common.cfg.CfgProductionRule;

import java.text.ParseException;

import static com.github.samyadaleh.cltoolbox.common.Constants.DEDUCTION_RULE_CFG_SHIFTREDUCE_AXIOM;

public class CfgToShiftReduceRulesConverter {

  /**
   * Converts a cfg to a parsing scheme for ShiftReduce parsing. Based on
   * https://user.phil.hhu.de/~kallmeyer/Parsing/shift-reduce.pdf
   */
  public static ParsingSchema cfgToShiftReduceRules(Cfg cfg, String w)
      throws ParseException {
    if (cfg.hasEpsilonProductions()) {
      throw new ParseException(
          "CFG must not contain empty productions for ShiftReduce parsing.", 1);
    }
    String[] wSplit = w.split(" ");
    ParsingSchema schema = new ParsingSchema();
    DynamicDeductionRuleInterface shift = new CfgBottomUpShift(wSplit);
    schema.addRule(shift);

    for (CfgProductionRule rule : cfg.getProductionRules()) {
      DynamicDeductionRuleInterface reduce = new CfgBottomUpReduce(rule);
      schema.addRule(reduce);
    }

    StaticDeductionRule axiom = new StaticDeductionRule();
    axiom.addConsequence(new DeductionChartItem("", "0"));
    axiom.setName(DEDUCTION_RULE_CFG_SHIFTREDUCE_AXIOM);
    schema.addAxiom(axiom);
    schema.addGoal(new DeductionChartItem(cfg.getStartSymbol(),
        String.valueOf(wSplit.length)));
    return schema;
  }
}

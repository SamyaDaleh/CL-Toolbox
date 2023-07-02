package com.github.samyadaleh.cltoolbox.chartparsing.converter.pcfg;

import com.github.samyadaleh.cltoolbox.chartparsing.ParsingSchema;
import com.github.samyadaleh.cltoolbox.chartparsing.StaticDeductionRule;
import com.github.samyadaleh.cltoolbox.chartparsing.cfg.cyk.PcfgCykItem;
import com.github.samyadaleh.cltoolbox.chartparsing.cfg.earley.passive.PcfgEarleyPassiveConvert;
import com.github.samyadaleh.cltoolbox.chartparsing.cfg.earley.passive.PcfgEarleyPassiveComplete;
import com.github.samyadaleh.cltoolbox.chartparsing.cfg.leftcorner.PcfgLeftCornerPredict;
import com.github.samyadaleh.cltoolbox.chartparsing.dynamicdeductionrule.DynamicDeductionRuleInterface;
import com.github.samyadaleh.cltoolbox.chartparsing.item.ProbabilisticChartItemInterface;
import com.github.samyadaleh.cltoolbox.common.cfg.Cfg;
import com.github.samyadaleh.cltoolbox.common.cfg.Pcfg;
import com.github.samyadaleh.cltoolbox.common.cfg.PcfgProductionRule;

import java.text.ParseException;

import static com.github.samyadaleh.cltoolbox.common.Constants.DEDUCTION_RULE_PCFG_LEFTCORNER_SCAN;

public class PcfgToLeftCornerRulesConverter {
  public static ParsingSchema pcfgToLeftCornerRules(Pcfg pcfg, String w)
      throws ParseException {
    if ((new Cfg(pcfg)).hasEpsilonProductions()) {
      throw new ParseException(
          "PCFG must not contain empty productions for Leftcorner parsing.", 1);
    }
    String[] wSplit = w.split(" ");
    ParsingSchema schema = new ParsingSchema();
    if (!wSplit[0].equals("")) {
      for (int i = 0; i < wSplit.length; i++) {
        StaticDeductionRule scan = new StaticDeductionRule();
        ProbabilisticChartItemInterface consequence =
            new PcfgCykItem(0, wSplit[i], i, i + 1);
        scan.addConsequence(consequence);
        scan.setName(DEDUCTION_RULE_PCFG_LEFTCORNER_SCAN + " " + wSplit[i]);
        schema.addAxiom(scan);
      }
      schema.addGoal(new PcfgCykItem(0, pcfg.getStartSymbol(), 0, wSplit.length));
    } else {
      schema.addGoal(new PcfgCykItem(0, pcfg.getStartSymbol(), 0, 0));
    }
    for (PcfgProductionRule pRule : pcfg.getProductionRules()) {
      DynamicDeductionRuleInterface predict = new PcfgLeftCornerPredict(pRule);
      schema.addRule(predict);
    }
    DynamicDeductionRuleInterface complete = new PcfgEarleyPassiveComplete();
    schema.addRule(complete);
    DynamicDeductionRuleInterface convert = new PcfgEarleyPassiveConvert(
        pcfg.getProductionRules());
    schema.addRule(convert);
    return schema;
  }
}

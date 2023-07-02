package com.github.samyadaleh.cltoolbox.chartparsing.converter.cfg;

import com.github.samyadaleh.cltoolbox.chartparsing.item.ChartItemInterface;
import com.github.samyadaleh.cltoolbox.chartparsing.item.DeductionChartItem;
import com.github.samyadaleh.cltoolbox.chartparsing.dynamicdeductionrule.DynamicDeductionRuleInterface;
import com.github.samyadaleh.cltoolbox.chartparsing.ParsingSchema;
import com.github.samyadaleh.cltoolbox.chartparsing.StaticDeductionRule;
import com.github.samyadaleh.cltoolbox.chartparsing.cfg.earley.CfgEarleyPredict;
import com.github.samyadaleh.cltoolbox.chartparsing.cfg.earley.CfgEarleyScan;
import com.github.samyadaleh.cltoolbox.chartparsing.cfg.earley.passive.PcfgEarleyPassiveComplete;
import com.github.samyadaleh.cltoolbox.chartparsing.cfg.earley.passive.PcfgEarleyPassiveConvert;
import com.github.samyadaleh.cltoolbox.common.cfg.Cfg;
import com.github.samyadaleh.cltoolbox.common.cfg.CfgProductionRule;
import com.github.samyadaleh.cltoolbox.common.tag.Tree;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import static com.github.samyadaleh.cltoolbox.common.Constants.DEDUCTION_RULE_CFG_EARLEY_AXIOM;

public class CfgToEarleyPassiveRulesConverter {
  /**
   * Converts a cfg to a parsing scheme for Earley parsing with passive items.
   * Based n https://user.phil.hhu.de/~kallmeyer/Parsing/earley.pdf
   */
  public static ParsingSchema cfgToEarleyPassiveRules(Cfg cfg, String w)
      throws ParseException {
    String[] wSplit = w.split(" ");
    ParsingSchema schema = new ParsingSchema();

    DynamicDeductionRuleInterface scan = new CfgEarleyScan(wSplit);
    schema.addRule(scan);

    DynamicDeductionRuleInterface complete = new PcfgEarleyPassiveComplete();
    schema.addRule(complete);

    DynamicDeductionRuleInterface convert = new PcfgEarleyPassiveConvert();
    schema.addRule(convert);

    for (CfgProductionRule rule : cfg.getProductionRules()) {
      if (rule.getLhs().equals(cfg.getStartSymbol())) {
        StaticDeductionRule axiom = new StaticDeductionRule();
        ChartItemInterface consequence;
        if (rule.getRhs()[0].equals("")) {
          consequence =
              new DeductionChartItem(cfg.getStartSymbol() + " -> •", "0", "0");
        } else {
          consequence = new DeductionChartItem(
              cfg.getStartSymbol() + " -> •" + String.join(" ", rule.getRhs()),
              "0", "0");
        }
        List<Tree> derivedTrees = new ArrayList<>();
        derivedTrees.add(new Tree(rule));
        consequence.setTrees(derivedTrees);
        axiom.addConsequence(consequence);
        axiom.setName(DEDUCTION_RULE_CFG_EARLEY_AXIOM);
        schema.addAxiom(axiom);
      }

      if (w.length() == 0) {
        schema.addGoal(new DeductionChartItem(cfg.getStartSymbol(), "0", "0"));
      } else {
        schema.addGoal(new DeductionChartItem(cfg.getStartSymbol(), "0",
            String.valueOf(wSplit.length)));
      }
      DynamicDeductionRuleInterface predict = new CfgEarleyPredict(rule);
      schema.addRule(predict);
    }
    return schema;
  }
}

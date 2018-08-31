package com.github.samyadaleh.cltoolbox.chartparsing.converter.cfg;

import com.github.samyadaleh.cltoolbox.chartparsing.*;
import com.github.samyadaleh.cltoolbox.chartparsing.cfg.earley.CfgEarleyComplete;
import com.github.samyadaleh.cltoolbox.chartparsing.cfg.earley.CfgEarleyPredict;
import com.github.samyadaleh.cltoolbox.chartparsing.cfg.earley.CfgEarleyScan;
import com.github.samyadaleh.cltoolbox.chartparsing.dynamicdeductionrule.DynamicDeductionRuleInterface;
import com.github.samyadaleh.cltoolbox.chartparsing.item.ChartItemInterface;
import com.github.samyadaleh.cltoolbox.chartparsing.item.DeductionChartItem;
import com.github.samyadaleh.cltoolbox.common.cfg.Cfg;
import com.github.samyadaleh.cltoolbox.common.cfg.CfgProductionRule;
import com.github.samyadaleh.cltoolbox.common.tag.Tree;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class CfgToEarleyRulesConverter {
  /**
   * Converts a cfg to a parsing scheme for Earley parsing. Based n
   * https://user.phil.hhu.de/~kallmeyer/Parsing/earley.pdf
   */
  public static ParsingSchema cfgToEarleyRules(Cfg cfg, String w)
      throws ParseException {
    String[] wSplit = w.split(" ");
    ParsingSchema schema = new ParsingSchema();

    DynamicDeductionRuleInterface scan = new CfgEarleyScan(wSplit);
    schema.addRule(scan);

    DynamicDeductionRuleInterface complete = new CfgEarleyComplete();
    schema.addRule(complete);

    for (CfgProductionRule rule : cfg.getProductionRules()) {
      if (rule.getLhs().equals(cfg.getStartSymbol())) {
        StaticDeductionRule axiom = new StaticDeductionRule();
        if (rule.getRhs()[0].equals("")) {
          ChartItemInterface consequence =
              new DeductionChartItem(cfg.getStartSymbol() + " -> •", "0", "0");
          List<Tree> derivedTrees = new ArrayList<>();
          derivedTrees.add(new Tree(rule));
          consequence.setTrees(derivedTrees);
          axiom.addConsequence(consequence);
        } else {
          ChartItemInterface consequence = new DeductionChartItem(
              cfg.getStartSymbol() + " -> •" + String.join(" ", rule.getRhs()),
              "0", "0");
          List<Tree> derivedTrees = new ArrayList<>();
          derivedTrees.add(new Tree(rule));
          consequence.setTrees(derivedTrees);
          axiom.addConsequence(consequence);
        }
        axiom.setName("axiom");
        schema.addAxiom(axiom);
        if (rule.getRhs()[0].equals("")) {
          schema.addGoal(
              new DeductionChartItem(cfg.getStartSymbol() + " -> •", "0",
                  String.valueOf(wSplit.length)));
        } else {
          schema.addGoal(new DeductionChartItem(
              cfg.getStartSymbol() + " -> " + String.join(" ", rule.getRhs())
                  + " •", "0", String.valueOf(wSplit.length)));
        }
      }

      DynamicDeductionRuleInterface predict = new CfgEarleyPredict(rule);
      schema.addRule(predict);
    }
    return schema;
  }
}

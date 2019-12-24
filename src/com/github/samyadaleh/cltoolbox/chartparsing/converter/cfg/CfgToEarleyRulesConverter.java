package com.github.samyadaleh.cltoolbox.chartparsing.converter.cfg;

import com.github.samyadaleh.cltoolbox.chartparsing.ParsingSchema;
import com.github.samyadaleh.cltoolbox.chartparsing.StaticDeductionRule;
import com.github.samyadaleh.cltoolbox.chartparsing.cfg.earley.CfgEarleyBottomupComplete;
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

import static com.github.samyadaleh.cltoolbox.common.Constants.DEDUCTION_RULE_CFG_EARLEY_AXIOM;
import static com.github.samyadaleh.cltoolbox.common.Constants.DEDUCTION_RULE_CFG_EARLEY_BOTTOMUP_AXIOM;

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
        axiom.setName(DEDUCTION_RULE_CFG_EARLEY_AXIOM);
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

  /**
   * Converts a cfg to a parsing scheme for Earley parsing bottom-up.
   */
  public static ParsingSchema cfgToEarleyBottomupRules(Cfg cfg, String w)
      throws ParseException {
    String[] wSplit = w.split(" ");
    ParsingSchema schema = new ParsingSchema();

    DynamicDeductionRuleInterface scan = new CfgEarleyScan(wSplit);
    schema.addRule(scan);

    DynamicDeductionRuleInterface complete = new CfgEarleyBottomupComplete();
    schema.addRule(complete);

    for (CfgProductionRule rule : cfg.getProductionRules()) {
      if (rule.getLhs().equals(cfg.getStartSymbol())) {
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
      StringBuilder dottedRule =
          new StringBuilder(rule.getLhs()).append(" -> ").append("•");
      boolean notFirst = false;
      for (String rhsSym : rule.getRhs()) {
        if (!notFirst) {
          notFirst = true;
        } else {
          dottedRule.append(" ");
        }
        dottedRule.append(rhsSym);
      }
      for (int i = 0; i < wSplit.length; i++) {
        StaticDeductionRule initialize = new StaticDeductionRule();
        initialize.setName(DEDUCTION_RULE_CFG_EARLEY_BOTTOMUP_AXIOM);
        DeductionChartItem consequence =
            new DeductionChartItem(dottedRule.toString(), String.valueOf(i),
                String.valueOf(i));
        List<Tree> derivedTrees = new ArrayList<>();
        derivedTrees.add(new Tree(rule));
        consequence.setTrees(derivedTrees);
        initialize.addConsequence(consequence);
        schema.addAxiom(initialize);
      }
      if("".equals(rule.getRhs()[0])) {
        StaticDeductionRule initialize = new StaticDeductionRule();
        initialize.setName(DEDUCTION_RULE_CFG_EARLEY_BOTTOMUP_AXIOM);
        DeductionChartItem consequence =
            new DeductionChartItem(dottedRule.toString(),
                String.valueOf(wSplit.length), String.valueOf(wSplit.length));
        List<Tree> derivedTrees = new ArrayList<>();
        derivedTrees.add(new Tree(rule));
        consequence.setTrees(derivedTrees);
        initialize.addConsequence(consequence);
        schema.addAxiom(initialize);

      }
    }
    return schema;
  }
}

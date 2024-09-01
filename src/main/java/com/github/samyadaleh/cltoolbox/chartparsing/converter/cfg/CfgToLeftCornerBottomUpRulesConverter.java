package com.github.samyadaleh.cltoolbox.chartparsing.converter.cfg;

import com.github.samyadaleh.cltoolbox.chartparsing.ParsingSchema;
import com.github.samyadaleh.cltoolbox.chartparsing.StaticDeductionRule;
import com.github.samyadaleh.cltoolbox.chartparsing.cfg.earley.CfgEarleyComplete;
import com.github.samyadaleh.cltoolbox.chartparsing.cfg.earley.CfgEarleyScan;
import com.github.samyadaleh.cltoolbox.chartparsing.cfg.leftcorner.bottomup.CfgLeftCornerBottomUpLeftCorner;
import com.github.samyadaleh.cltoolbox.chartparsing.item.ChartItemInterface;
import com.github.samyadaleh.cltoolbox.chartparsing.item.DeductionChartItem;
import com.github.samyadaleh.cltoolbox.common.cfg.Cfg;
import com.github.samyadaleh.cltoolbox.common.cfg.CfgProductionRule;
import com.github.samyadaleh.cltoolbox.common.tag.Tree;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class CfgToLeftCornerBottomUpRulesConverter {

  /**
   * Converts a grammar to a parsing schema for bottom-up Left-Corner as
   * presented by Sikkel, see doc.md for reference.
   */
  public static ParsingSchema cfgToLeftCornerBottomUpRules(Cfg cfg, String w)
      throws ParseException {
    String[] wSplit = w.split(" ");
    int wLength = w.isEmpty() ? 0 : wSplit.length;
    ParsingSchema schema = new ParsingSchema();
    for (CfgProductionRule rule : cfg.getProductionRules()) {
      if (rule.getRhs().length == 0) {
        for (int i = 0; i <= wLength; i++) {
          StaticDeductionRule axiom = new StaticDeductionRule();
          axiom.setName("Epsilon");
          ChartItemInterface
              consequence = new DeductionChartItem(rule.getLhs() + " -> •",
              String.valueOf(i), String.valueOf(i));
          List<Tree> trees = new ArrayList<>();
          trees.add(new Tree(rule));
          consequence.setTrees(trees);
          axiom.addConsequence(consequence);
          schema.addAxiom(axiom);
        }
      } else {
        String lc = rule.getRhs()[0];
        int restStart = lc.length() + 1;
        if (cfg.terminalsContain(lc)) {
          for (int i = 0; i < wLength; i++) {
            if (wSplit[i].equals(lc)) {
              StaticDeductionRule axiom = new StaticDeductionRule();
              axiom.setName("LC(" + lc + ")");
              ChartItemInterface consequence;
              if (restStart > rule.getRhs().length) {
                consequence = new DeductionChartItem(rule.getLhs() + " -> "
                    + lc + " •",
                    String.valueOf(i), String.valueOf(i + 1));
              } else {
                consequence = new DeductionChartItem(rule.getLhs() + " -> "
                    + lc + " •" + String.join(" ",
                    rule.getRhs()).substring(restStart),
                    String.valueOf(i), String.valueOf(i + 1));
              }
              List<Tree> trees = new ArrayList<>();
              trees.add(new Tree(rule));
              consequence.setTrees(trees);
              axiom.addConsequence(consequence);
              schema.addAxiom(axiom);
            }
          }
        } else {
          schema.addRule(new CfgLeftCornerBottomUpLeftCorner(rule));
        }
      }
      schema.addRule(new CfgEarleyScan(wSplit));
      schema.addRule(new CfgEarleyComplete(cfg.getNonterminals()));
      if (rule.getLhs().equals(cfg.getStartSymbol())) {
        if (rule.getRhs()[0].isEmpty()) {
          schema.addGoal(
              new DeductionChartItem(cfg.getStartSymbol() + " -> •", "0",
                  String.valueOf(wLength)));
        } else {
          schema.addGoal(new DeductionChartItem(
              cfg.getStartSymbol() + " -> " + String.join(" ", rule.getRhs())
                  + " •", "0", String.valueOf(wLength)));
        }
      }
    }
    return schema;
  }
}

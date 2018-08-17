package com.github.samyadaleh.cltoolbox.chartparsing.converter;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import com.github.samyadaleh.cltoolbox.chartparsing.DeductionChartItem;
import com.github.samyadaleh.cltoolbox.chartparsing.DynamicDeductionRuleInterface;
import com.github.samyadaleh.cltoolbox.chartparsing.ChartItemInterface;
import com.github.samyadaleh.cltoolbox.chartparsing.ParsingSchema;
import com.github.samyadaleh.cltoolbox.chartparsing.StaticDeductionRule;
import com.github.samyadaleh.cltoolbox.chartparsing.cfg.cyk.CfgCykComplete;
import com.github.samyadaleh.cltoolbox.chartparsing.cfg.cyk.CfgCykCompleteGeneral;
import com.github.samyadaleh.cltoolbox.chartparsing.cfg.cyk.CfgCykCompleteUnary;
import com.github.samyadaleh.cltoolbox.chartparsing.cfg.earley.CfgEarleyComplete;
import com.github.samyadaleh.cltoolbox.chartparsing.cfg.earley.CfgEarleyPredict;
import com.github.samyadaleh.cltoolbox.chartparsing.cfg.earley.CfgEarleyScan;
import com.github.samyadaleh.cltoolbox.chartparsing.cfg.earley.passive.CfgEarleyPassiveComplete;
import com.github.samyadaleh.cltoolbox.chartparsing.cfg.earley.passive.CfgEarleyPassiveConvert;
import com.github.samyadaleh.cltoolbox.chartparsing.cfg.leftcorner.CfgLeftCornerMove;
import com.github.samyadaleh.cltoolbox.chartparsing.cfg.leftcorner.CfgLeftCornerReduce;
import com.github.samyadaleh.cltoolbox.chartparsing.cfg.leftcorner.CfgLeftCornerRemove;
import com.github.samyadaleh.cltoolbox.chartparsing.cfg.leftcorner.chart.CfgLeftCornerChartMove;
import com.github.samyadaleh.cltoolbox.chartparsing.cfg.leftcorner.chart.CfgLeftCornerChartReduce;
import com.github.samyadaleh.cltoolbox.chartparsing.cfg.leftcorner.chart.CfgLeftCornerChartRemove;
import com.github.samyadaleh.cltoolbox.chartparsing.cfg.shiftreduce.CfgBottomUpReduce;
import com.github.samyadaleh.cltoolbox.chartparsing.cfg.shiftreduce.CfgBottomUpShift;
import com.github.samyadaleh.cltoolbox.chartparsing.cfg.topdown.CfgTopDownPredict;
import com.github.samyadaleh.cltoolbox.chartparsing.cfg.topdown.CfgTopDownScan;
import com.github.samyadaleh.cltoolbox.chartparsing.cfg.unger.CfgUngerComplete;
import com.github.samyadaleh.cltoolbox.chartparsing.cfg.unger.CfgUngerPredict;
import com.github.samyadaleh.cltoolbox.chartparsing.cfg.unger.CfgUngerScan;
import com.github.samyadaleh.cltoolbox.common.cfg.Cfg;
import com.github.samyadaleh.cltoolbox.common.cfg.CfgProductionRule;
import com.github.samyadaleh.cltoolbox.common.tag.Tree;

/**
 * Generates different parsing schemes. Based on the slides from Laura Kallmeyer
 * about Parsing as Deduction.
 */
public class CfgToDeductionRulesConverter {

  /**
   * Converts a cfg to a parsing scheme for Topdown parsing. Based on
   * https://user.phil.hhu.de/~kallmeyer/Parsing/deduction.pdf
   */
  public static ParsingSchema cfgToTopDownRules(Cfg cfg, String w) {
    if (cfg.hasEpsilonProductions()) {
      System.out
        .println("CFG must not contain empty productions for TopDown parsing.");
      return null;
    }
    if (cfg.hasLeftRecursion()) {
      System.out
        .println("CFG must not contain left recursion for TopDown parsing.");
      return null;
    }
    String[] wSplit = w.split(" ");
    ParsingSchema schema = new ParsingSchema();
    DynamicDeductionRuleInterface scan = new CfgTopDownScan(wSplit);
    schema.addRule(scan);

    for (CfgProductionRule rule : cfg.getProductionRules()) {
      DynamicDeductionRuleInterface predict = new CfgTopDownPredict(rule);
      schema.addRule(predict);
    }

    StaticDeductionRule axiom = new StaticDeductionRule();
    axiom.addConsequence(new DeductionChartItem(cfg.getStartSymbol(), "0"));
    axiom.setName("axiom");
    schema.addAxiom(axiom);
    schema.addGoal(new DeductionChartItem("", String.valueOf(wSplit.length)));
    return schema;
  }

  /**
   * Converts a cfg to a parsing scheme for ShiftReduce parsing. Based on
   * https://user.phil.hhu.de/~kallmeyer/Parsing/shift-reduce.pdf
   */
  public static ParsingSchema cfgToShiftReduceRules(Cfg cfg, String w) {
    if (cfg.hasEpsilonProductions()) {
      System.out.println(
        "CFG must not contain empty productions for ShiftReduce parsing.");
      return null;
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
    axiom.setName("axiom");
    schema.addAxiom(axiom);
    schema.addGoal(
      new DeductionChartItem(cfg.getStartSymbol(), String.valueOf(wSplit.length)));
    return schema;
  }

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
          schema.addGoal(new DeductionChartItem(cfg.getStartSymbol() + " -> •", "0",
            String.valueOf(wSplit.length)));
        } else {
          schema.addGoal(new DeductionChartItem(cfg.getStartSymbol() + " -> "
            + String.join(" ", rule.getRhs()) + " •", "0",
            String.valueOf(wSplit.length)));
        }
      }

      DynamicDeductionRuleInterface predict = new CfgEarleyPredict(rule);
      schema.addRule(predict);
    }
    return schema;
  }

  /**
   * Converts a cfg to a parsing scheme for Earley parsing with passive items.
   * Based n https://user.phil.hhu.de/~kallmeyer/Parsing/earley.pdf
   */
  static ParsingSchema cfgToEarleyPassiveRules(Cfg cfg, String w) {
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
          schema.addGoal(new DeductionChartItem(cfg.getStartSymbol() + " -> •", "0",
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

  /**
   * Converts a cfg to a parsing scheme for LeftCorner parsing. Based on
   * https://user.phil.hhu.de/~kallmeyer/Parsing/left-corner.pdf at the moment
   * to be used.
   */
  public static ParsingSchema cfgToLeftCornerRules(Cfg cfg, String w) {
    if (cfg.hasEpsilonProductions()) {
      System.out.println(
        "CFG must not contain empty productions for Leftcorner parsing.");
      return null;
    }
    if (cfg.hasDirectLeftRecursion()) {
      System.out
        .println("CFG must not contain left recursion for Leftcorner parsing.");
      return null;
    }
    ParsingSchema schema = new ParsingSchema();
    StaticDeductionRule axiom = new StaticDeductionRule();
    axiom.addConsequence(new DeductionChartItem(w, cfg.getStartSymbol(), ""));
    axiom.setName("axiom");
    schema.addAxiom(axiom);

    for (CfgProductionRule rule : cfg.getProductionRules()) {
      DynamicDeductionRuleInterface reduce = new CfgLeftCornerReduce(rule);
      schema.addRule(reduce);
    }

    DynamicDeductionRuleInterface remove = new CfgLeftCornerRemove();
    schema.addRule(remove);

    DynamicDeductionRuleInterface move = new CfgLeftCornerMove(cfg.getNonterminals());
    schema.addRule(move);

    schema.addGoal(new DeductionChartItem("", "", ""));
    return schema;
  }

  /**
   * Converts a cfg to a parsing scheme for LeftCorner parsing, chart version.
   * Based on https://user.phil.hhu.de/~kallmeyer/Parsing/left-corner.pdf at the
   * moment to be used.
   */
  public static ParsingSchema cfgToLeftCornerChartRules(Cfg cfg, String w) {
    ParsingSchema schema = new ParsingSchema();
    String[] wSplit = w.split(" ");

    for (int i = 0; i < wSplit.length; i++) {
      StaticDeductionRule axiom = new StaticDeductionRule();
      axiom
        .addConsequence(new DeductionChartItem(wSplit[i], String.valueOf(i), "1"));
      axiom.setName("scan " + wSplit[i]);
      schema.addAxiom(axiom);
      axiom = new StaticDeductionRule();
      axiom.addConsequence(new DeductionChartItem("", String.valueOf(i), "0"));
      axiom.setName("scan-ε ");
      schema.addAxiom(axiom);
    }
    StaticDeductionRule axiom = new StaticDeductionRule();
    axiom.addConsequence(
      new DeductionChartItem("", String.valueOf(wSplit.length), "0"));
    axiom.setName("scan-ε ");
    schema.addAxiom(axiom);

    for (CfgProductionRule rule : cfg.getProductionRules()) {
      DynamicDeductionRuleInterface reduce = new CfgLeftCornerChartReduce(rule);
      schema.addRule(reduce);
    }

    DynamicDeductionRuleInterface remove = new CfgLeftCornerChartRemove();
    schema.addRule(remove);

    DynamicDeductionRuleInterface move = new CfgLeftCornerChartMove();
    schema.addRule(move);

    schema.addGoal(new DeductionChartItem(cfg.getStartSymbol(), "0",
      String.valueOf(wSplit.length)));
    return schema;
  }

  /**
   * Converts grammar into rules for CYK parsing for CNF.
   */
  public static ParsingSchema cfgToCykRules(Cfg cfg, String w)
    throws ParseException {
    if (!cfg.isInChomskyNormalForm()) {
      System.out.println("Grammar has to be in Chomsky Normal Form.");
      return null;
    }
    String[] wSplit = w.split(" ");
    ParsingSchema schema = new ParsingSchema();

    for (CfgProductionRule rule : cfg.getProductionRules()) {
      if (rule.getRhs().length == 1) {
        addCykScanRules(wSplit, schema, rule);
      } else {
        DynamicDeductionRuleInterface complete = new CfgCykComplete(rule);
        schema.addRule(complete);
      }
    }
    schema.addGoal(new DeductionChartItem(cfg.getStartSymbol(), "0",
      String.valueOf(wSplit.length)));
    return schema;
  }

  /**
   * Like CYK parsing, but with an additional deduction rule for chain rules,
   * hence grammar needs only to be in Canonical Two Form. Source: Giogio Satta,
   * ESSLLI 2013
   */
  public static ParsingSchema cfgToCykExtendedRules(Cfg cfg, String w)
    throws ParseException {
    if (!cfg.isInCanonicalTwoForm()) {
      System.out.println("Grammar has to be in Canonical Two Form.");
      return null;
    }
    String[] wSplit = w.split(" ");
    ParsingSchema schema = new ParsingSchema();

    for (CfgProductionRule rule : cfg.getProductionRules()) {
      if (rule.getRhs().length == 1) {
        if (cfg.terminalsContain(rule.getRhs()[0])) {
          addCykScanRules(wSplit, schema, rule);
        } else {
          DynamicDeductionRuleInterface complete = new CfgCykCompleteUnary(rule);
          schema.addRule(complete);
        }
      } else {
        DynamicDeductionRuleInterface complete = new CfgCykComplete(rule);
        schema.addRule(complete);
      }
    }
    schema.addGoal(new DeductionChartItem(cfg.getStartSymbol(), "0",
      String.valueOf(wSplit.length)));
    return schema;
  }

  private static void addCykScanRules(String[] wSplit, ParsingSchema schema,
    CfgProductionRule rule) throws ParseException {
    for (int i = 0; i < wSplit.length; i++) {
      if (wSplit[i].equals(rule.getRhs()[0])) {
        StaticDeductionRule scan = new StaticDeductionRule();
        ChartItemInterface consequence =
          new DeductionChartItem(rule.getLhs(), String.valueOf(i), "1");
        List<Tree> derivedTrees = new ArrayList<>();
        derivedTrees.add(new Tree(rule));
        consequence.setTrees(derivedTrees);
        scan.addConsequence(consequence);
        scan.setName("scan " + rule.toString());
        schema.addAxiom(scan);
      }
    }
  }

  public static ParsingSchema cfgToCykGeneralRules(Cfg cfg, String w) {
    String[] wSplit = w.split(" ");
    ParsingSchema schema = new ParsingSchema();

    for (int i = 0; i < wSplit.length; i++) {
      StaticDeductionRule scan = new StaticDeductionRule();
      scan.addConsequence(new DeductionChartItem(wSplit[i], String.valueOf(i), "1"));
      scan.setName("scan " + wSplit[i]);
      schema.addAxiom(scan);
      StaticDeductionRule scanEps = new StaticDeductionRule();
      scanEps.addConsequence(new DeductionChartItem("", String.valueOf(i), "0"));
      scanEps.setName("scan ε");
      schema.addAxiom(scanEps);
    }
    StaticDeductionRule scanEps = new StaticDeductionRule();
    scanEps.addConsequence(
      new DeductionChartItem("", String.valueOf(wSplit.length), "0"));
    scanEps.setName("scan ε");
    schema.addAxiom(scanEps);

    for (CfgProductionRule rule : cfg.getProductionRules()) {
      DynamicDeductionRuleInterface complete = new CfgCykCompleteGeneral(rule);
      schema.addRule(complete);
    }
    schema.addGoal(new DeductionChartItem(cfg.getStartSymbol(), "0",
      String.valueOf(wSplit.length)));
    return schema;
  }

  /** Unger parsing tries out all possible separations, factorial runtime. */
  public static ParsingSchema cfgToUngerRules(Cfg cfg, String w) {
    if (cfg.hasEpsilonProductions()) {
      System.out
        .println("CFG must not contain empty productions for Unger parsing.");
      return null;
    }
    if (cfg.hasDirectLeftRecursion()) {
      System.out
        .println("CFG must not contain left recursion for Unger parsing.");
      return null;
    }
    String[] wSplit = w.split(" ");
    ParsingSchema schema = new ParsingSchema();

    StaticDeductionRule axiom = new StaticDeductionRule();
    axiom.setName("axiom");
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

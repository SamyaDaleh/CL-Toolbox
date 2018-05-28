package chartparsing.converter;

import chartparsing.DeductionItem;
import chartparsing.DynamicDeductionRule;
import chartparsing.ParsingSchema;
import chartparsing.StaticDeductionRule;
import chartparsing.cfg.CfgBottomUpReduce;
import chartparsing.cfg.CfgBottomUpShift;
import chartparsing.cfg.CfgTopDownPredict;
import chartparsing.cfg.CfgTopDownScan;
import chartparsing.cfg.cyk.CfgCykComplete;
import chartparsing.cfg.cyk.CfgCykCompleteGeneral;
import chartparsing.cfg.cyk.CfgCykCompleteUnary;
import chartparsing.cfg.earley.CfgEarleyComplete;
import chartparsing.cfg.earley.CfgEarleyPredict;
import chartparsing.cfg.earley.CfgEarleyScan;
import chartparsing.cfg.leftcorner.CfgLeftCornerMove;
import chartparsing.cfg.leftcorner.CfgLeftCornerReduce;
import chartparsing.cfg.leftcorner.CfgLeftCornerRemove;
import chartparsing.cfg.leftcornerchart.CfgLeftCornerChartMove;
import chartparsing.cfg.leftcornerchart.CfgLeftCornerChartReduce;
import chartparsing.cfg.leftcornerchart.CfgLeftCornerChartRemove;
import chartparsing.cfg.unger.CfgUngerComplete;
import chartparsing.cfg.unger.CfgUngerPredict;
import chartparsing.cfg.unger.CfgUngerScan;
import common.cfg.Cfg;
import common.cfg.CfgProductionRule;

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
      System.out.println(
        "CFG must not contain left recursion for TopDown parsing.");
      return null;
    }
    String[] wSplit = w.split(" ");
    ParsingSchema schema = new ParsingSchema();
    DynamicDeductionRule scan = new CfgTopDownScan(wSplit);
    schema.addRule(scan);

    for (CfgProductionRule rule : cfg.getProductionRules()) {
      DynamicDeductionRule predict = new CfgTopDownPredict(rule);
      schema.addRule(predict);
    }

    StaticDeductionRule axiom = new StaticDeductionRule();
    axiom.addConsequence(new DeductionItem(cfg.getStartSymbol(), "0"));
    axiom.setName("axiom");
    schema.addAxiom(axiom);
    schema.addGoal(new DeductionItem("", String.valueOf(wSplit.length)));
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
    DynamicDeductionRule shift = new CfgBottomUpShift(wSplit);
    schema.addRule(shift);

    for (CfgProductionRule rule : cfg.getProductionRules()) {
      DynamicDeductionRule reduce = new CfgBottomUpReduce(rule);
      schema.addRule(reduce);
    }

    StaticDeductionRule axiom = new StaticDeductionRule();
    axiom.addConsequence(new DeductionItem("", "0"));
    axiom.setName("axiom");
    schema.addAxiom(axiom);
    schema.addGoal(
      new DeductionItem(cfg.getStartSymbol(), String.valueOf(wSplit.length)));
    return schema;
  }

  /**
   * Converts a cfg to a parsing scheme for Earley parsing. Based n
   * https://user.phil.hhu.de/~kallmeyer/Parsing/earley.pdf
   */
  public static ParsingSchema cfgToEarleyRules(Cfg cfg, String w) {
    String[] wSplit = w.split(" ");
    ParsingSchema schema = new ParsingSchema();

    DynamicDeductionRule scan = new CfgEarleyScan(wSplit);
    schema.addRule(scan);

    DynamicDeductionRule complete = new CfgEarleyComplete();
    schema.addRule(complete);

    for (CfgProductionRule rule : cfg.getProductionRules()) {
      if (rule.getLhs().equals(cfg.getStartSymbol())) {
        StaticDeductionRule axiom = new StaticDeductionRule();
        if (rule.getRhs()[0].equals("")) {
          axiom.addConsequence(new DeductionItem("S -> •", "0", "0"));
        } else {
          axiom.addConsequence(new DeductionItem(
            "S -> •" + String.join(" ", rule.getRhs()), "0", "0"));
        }
        axiom.setName("axiom");
        schema.addAxiom(axiom);
        if (rule.getRhs()[0].equals("")) {
          schema.addGoal(
            new DeductionItem("S -> •", "0", String.valueOf(wSplit.length)));
        } else {
          schema.addGoal(
            new DeductionItem("S -> " + String.join(" ", rule.getRhs()) + " •",
              "0", String.valueOf(wSplit.length)));
        }
      }

      DynamicDeductionRule predict = new CfgEarleyPredict(rule);
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
    axiom.addConsequence(new DeductionItem(w, cfg.getStartSymbol(), ""));
    axiom.setName("axiom");
    schema.addAxiom(axiom);

    for (CfgProductionRule rule : cfg.getProductionRules()) {
      DynamicDeductionRule reduce = new CfgLeftCornerReduce(rule);
      schema.addRule(reduce);
    }

    DynamicDeductionRule remove = new CfgLeftCornerRemove();
    schema.addRule(remove);

    DynamicDeductionRule move = new CfgLeftCornerMove(cfg.getNonterminals());
    schema.addRule(move);

    schema.addGoal(new DeductionItem("", "", ""));
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
        .addConsequence(new DeductionItem(wSplit[i], String.valueOf(i), "1"));
      axiom.setName("scan " + wSplit[i]);
      schema.addAxiom(axiom);
      axiom = new StaticDeductionRule();
      axiom.addConsequence(new DeductionItem("", String.valueOf(i), "0"));
      axiom.setName("scan-ε ");
      schema.addAxiom(axiom);
    }
    StaticDeductionRule axiom = new StaticDeductionRule();
    axiom.addConsequence(
      new DeductionItem("", String.valueOf(wSplit.length), "0"));
    axiom.setName("scan-ε ");
    schema.addAxiom(axiom);

    for (CfgProductionRule rule : cfg.getProductionRules()) {
      DynamicDeductionRule reduce = new CfgLeftCornerChartReduce(rule);
      schema.addRule(reduce);
    }

    DynamicDeductionRule remove = new CfgLeftCornerChartRemove();
    schema.addRule(remove);

    DynamicDeductionRule move = new CfgLeftCornerChartMove();
    schema.addRule(move);

    schema.addGoal(new DeductionItem(cfg.getStartSymbol(), "0",
      String.valueOf(wSplit.length)));
    return schema;
  }

  /** Converts grammar into rules for CYK parsing for CNF. */
  public static ParsingSchema cfgToCykRules(Cfg cfg, String w) {
    if (!cfg.isInChomskyNormalForm()) {
      System.out.println("Grammar has to be in Chomsky Normal Form.");
      return null;
    }
    String[] wSplit = w.split(" ");
    ParsingSchema schema = new ParsingSchema();

    for (CfgProductionRule rule : cfg.getProductionRules()) {
      if (rule.getRhs().length == 1) {
        for (int i = 0; i < wSplit.length; i++) {
          if (wSplit[i].equals(rule.getRhs()[0])) {
            StaticDeductionRule scan = new StaticDeductionRule();
            scan.addConsequence(
              new DeductionItem(rule.getLhs(), String.valueOf(i), "1"));
            scan.setName("scan " + rule.toString());
            schema.addAxiom(scan);
          }
        }
      } else {
        DynamicDeductionRule complete = new CfgCykComplete(rule);
        schema.addRule(complete);
      }
    }
    schema.addGoal(new DeductionItem(cfg.getStartSymbol(), "0",
      String.valueOf(wSplit.length)));
    return schema;
  }

  /**
   * Like CYK parsing, but with an additional deduction rule for chain rules,
   * hence grammar needs only to be in Canonical Two Form. Source: Giogio Satta,
   * ESSLLI 2013
   */
  public static ParsingSchema cfgToCykExtendedRules(Cfg cfg, String w) {
    if (!cfg.isInCanonicalTwoForm()) {
      System.out.println("Grammar has to be in Canonical Two Form.");
      return null;
    }
    String[] wSplit = w.split(" ");
    ParsingSchema schema = new ParsingSchema();

    for (CfgProductionRule rule : cfg.getProductionRules()) {
      if (rule.getRhs().length == 1) {
        if (cfg.terminalsContain(rule.getRhs()[0])) {
          for (int i = 0; i < wSplit.length; i++) {
            if (wSplit[i].equals(rule.getRhs()[0])) {
              StaticDeductionRule scan = new StaticDeductionRule();
              scan.addConsequence(
                new DeductionItem(rule.getLhs(), String.valueOf(i), "1"));
              scan.setName("scan " + rule.toString());
              schema.addAxiom(scan);
            }
          }
        } else {
          DynamicDeductionRule complete = new CfgCykCompleteUnary(rule);
          schema.addRule(complete);
        }
      } else {
        DynamicDeductionRule complete = new CfgCykComplete(rule);
        schema.addRule(complete);
      }
    }
    schema.addGoal(new DeductionItem(cfg.getStartSymbol(), "0",
      String.valueOf(wSplit.length)));
    return schema;
  }

  public static ParsingSchema cfgToCykGeneralRules(Cfg cfg, String w) {
    String[] wSplit = w.split(" ");
    ParsingSchema schema = new ParsingSchema();

    for (int i = 0; i < wSplit.length; i++) {
      StaticDeductionRule scan = new StaticDeductionRule();
      scan.addConsequence(new DeductionItem(wSplit[i], String.valueOf(i), "1"));
      scan.setName("scan " + wSplit[i]);
      schema.addAxiom(scan);
      StaticDeductionRule scanEps = new StaticDeductionRule();
      scanEps.addConsequence(new DeductionItem("", String.valueOf(i), "0"));
      scanEps.setName("scan ε");
      schema.addAxiom(scanEps);
    }
    StaticDeductionRule scanEps = new StaticDeductionRule();
    scanEps.addConsequence(
      new DeductionItem("", String.valueOf(wSplit.length), "0"));
    scanEps.setName("scan ε");
    schema.addAxiom(scanEps);

    for (CfgProductionRule rule : cfg.getProductionRules()) {
      DynamicDeductionRule complete = new CfgCykCompleteGeneral(rule);
      schema.addRule(complete);
    }
    schema.addGoal(new DeductionItem(cfg.getStartSymbol(), "0",
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
    axiom.addConsequence(new DeductionItem("•" + cfg.getStartSymbol(), "0",
      String.valueOf(wSplit.length)));
    schema.addAxiom(axiom);

    schema.addGoal(new DeductionItem(cfg.getStartSymbol() + "•", "0",
      String.valueOf(wSplit.length)));

    DynamicDeductionRule scan = new CfgUngerScan(wSplit);
    schema.addRule(scan);

    for (CfgProductionRule rule : cfg.getProductionRules()) {
      DynamicDeductionRule predict = new CfgUngerPredict(rule, cfg);
      schema.addRule(predict);
      DynamicDeductionRule complete = new CfgUngerComplete(rule);
      schema.addRule(complete);
    }
    return schema;
  }
}

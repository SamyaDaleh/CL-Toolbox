package chartparsing;

import chartparsing.cfgrules.CfgBottomupReduce;
import chartparsing.cfgrules.CfgBottomupShift;
import chartparsing.cfgrules.CfgCykComplete;
import chartparsing.cfgrules.CfgCykCompleteUnary;
import chartparsing.cfgrules.CfgEarleyComplete;
import chartparsing.cfgrules.CfgEarleyPredict;
import chartparsing.cfgrules.CfgEarleyScan;
import chartparsing.cfgrules.CfgLeftcornerMove;
import chartparsing.cfgrules.CfgLeftcornerReduce;
import chartparsing.cfgrules.CfgLeftcornerRemove;
import chartparsing.cfgrules.CfgTopdownPredict;
import chartparsing.cfgrules.CfgTopdownScan;
import common.cfg.Cfg;
import common.cfg.CfgDollarItem;
import common.cfg.CfgDottedItem;
import common.cfg.CfgItem;
import common.cfg.CfgProductionRule;

/** Generates different parsing schemes. Based on the slides from Laura
 * Kallmeyer about Parsing as Deduction. */
class CfgToDeductionRulesConverter {

  /** Instead of calling the respective function this method works as entry
   * point for all of them. Takes a cfg, an input string w and a string
   * specifying which parsing algorithm shall be applied. Returns the respective
   * parsing scheme. */
  public static ParsingSchema CfgToParsingSchema(Cfg cfg, String w,
    String schema) {
    switch (schema) {
    case "topdown":
      return CfgToTopDownRules(cfg, w);
    case "shiftreduce":
      return CfgToShiftReduceRules(cfg, w);
    case "earley":
      return CfgToEarleyRules(cfg, w);
    case "leftcorner":
      return CfgToLeftCornerRules(cfg, w);
    case "cyk":
      return CfgToCykRules(cfg, w);
    case "cyk-extended":
      return CfgToCykExtendedRules(cfg, w);
    default:
      return null;
    }
  }

  /** Converts a cfg to a parsing scheme for Topdown parsing. Based on
   * https://user.phil.hhu.de/~kallmeyer/Parsing/deduction.pdf */
  public static ParsingSchema CfgToTopDownRules(Cfg cfg, String w) {
    if (cfg.hasEpsilonProductions()) {
      System.out
        .println("CFG must not contain empty productions for TopDown parsing.");
      return null;
    }
    String[] wsplit = w.split(" ");
    ParsingSchema schema = new ParsingSchema();
    DynamicDeductionRule scan = new CfgTopdownScan(wsplit);
    schema.addRule(scan);

    for (CfgProductionRule rule : cfg.getProductionrules()) {
      DynamicDeductionRule predict = new CfgTopdownPredict(rule);
      schema.addRule(predict);
    }

    StaticDeductionRule axiom = new StaticDeductionRule();
    axiom.addConsequence(new CfgItem(cfg.getStartsymbol(), 0));
    axiom.setName("axiom");
    schema.addAxiom(axiom);
    schema.addGoal(new CfgItem("", wsplit.length));
    return schema;
  }

  /** Converts a cfg to a parsing scheme for ShiftReduce parsing. Based on
   * https://user.phil.hhu.de/~kallmeyer/Parsing/shift-reduce.pdf */
  public static ParsingSchema CfgToShiftReduceRules(Cfg cfg, String w) {
    if (cfg.hasEpsilonProductions()) {
      System.out.println(
        "CFG must not contain empty productions for ShiftReduce parsing.");
      return null;
    }
    String[] wsplit = w.split(" ");
    ParsingSchema schema = new ParsingSchema();
    DynamicDeductionRule shift = new CfgBottomupShift(wsplit);
    schema.addRule(shift);

    for (CfgProductionRule rule : cfg.getProductionrules()) {
      DynamicDeductionRule reduce = new CfgBottomupReduce(rule);
      schema.addRule(reduce);
    }

    StaticDeductionRule axiom = new StaticDeductionRule();
    axiom.addConsequence(new CfgItem("", 0));
    axiom.setName("axiom");
    schema.addAxiom(axiom);
    schema.addGoal(new CfgItem(cfg.getStartsymbol(), wsplit.length));
    return schema;
  }

  /** Converts a cfg to a parsing scheme for Earley parsing. Based n
   * https://user.phil.hhu.de/~kallmeyer/Parsing/earley.pdf */
  public static ParsingSchema CfgToEarleyRules(Cfg cfg, String w) {
    String[] wsplit = w.split(" ");
    ParsingSchema schema = new ParsingSchema();

    DynamicDeductionRule scan = new CfgEarleyScan(wsplit);
    schema.addRule(scan);

    DynamicDeductionRule complete = new CfgEarleyComplete();
    schema.addRule(complete);

    for (CfgProductionRule rule : cfg.getProductionrules()) {
      if (rule.getLhs().equals(cfg.getStartsymbol())) {
        StaticDeductionRule axiom = new StaticDeductionRule();
        if (rule.getRhs()[0].equals("")) {
          axiom.addConsequence(new CfgDottedItem("S -> •", 0, 0));
        } else {
          axiom.addConsequence(new CfgDottedItem(
            "S -> •" + String.join(" ", rule.getRhs()), 0, 0));
        }
        axiom.setName("axiom");
        schema.addAxiom(axiom);
        if (rule.getRhs()[0].equals("")) {
          schema.addGoal(new CfgDottedItem("S -> •", 0, wsplit.length));
        } else {
          schema.addGoal(
            new CfgDottedItem("S -> " + String.join(" ", rule.getRhs()) + " •",
              0, wsplit.length));
        }
      }

      DynamicDeductionRule predict = new CfgEarleyPredict(rule);
      schema.addRule(predict);
    }
    return schema;
  }

  /** Converts a cfg to a parsing scheme for LeftCorner parsing. Based on
   * https://user.phil.hhu.de/~kallmeyer/Parsing/left-corner.pdf at the moment
   * to be used. */
  public static ParsingSchema CfgToLeftCornerRules(Cfg cfg, String w) {
    if (cfg.hasEpsilonProductions()) {
      System.out
        .println("CFG must not contain empty productions for Leftcorner parsing.");
      return null;
    }
    if (cfg.hasDirectLeftRecursion()) {
      System.out
        .println("CFG must not contain left recursion for Leftcorner parsing.");
      return null;
    }
    ParsingSchema schema = new ParsingSchema();
    StaticDeductionRule axiom = new StaticDeductionRule();
    axiom.addConsequence(new CfgDollarItem(w, cfg.getStartsymbol(), ""));
    axiom.setName("axiom");
    schema.addAxiom(axiom);

    for (CfgProductionRule rule : cfg.getProductionrules()) {
      DynamicDeductionRule reduce = new CfgLeftcornerReduce(rule);
      schema.addRule(reduce);
    }

    DynamicDeductionRule remove = new CfgLeftcornerRemove();
    schema.addRule(remove);

    DynamicDeductionRule move = new CfgLeftcornerMove(cfg.getNonterminals());
    schema.addRule(move);

    schema.addGoal(new CfgDollarItem("", "", ""));
    return schema;
  }

  /** Converts grammar into rules for CYK parsing for CNF. */
  public static ParsingSchema CfgToCykRules(Cfg cfg, String w) {
    if (!cfg.isInChomskyNormalForm()) {
      System.out.println("Grammar has to be in Chomsky Normal Form.");
      return null;
    }
    String[] wsplit = w.split(" ");
    ParsingSchema schema = new ParsingSchema();

    for (CfgProductionRule rule : cfg.getProductionrules()) {
      if (rule.getRhs().length == 1) {
        for (int i = 0; i < wsplit.length; i++) {
          if (wsplit[i].equals(rule.getRhs()[0])) {
            StaticDeductionRule scan = new StaticDeductionRule();
            scan.addConsequence(new CfgItem(rule.getLhs(), i, 1));
            scan.setName("scan");
            schema.addAxiom(scan);
          }
        }
      } else {
        DynamicDeductionRule complete = new CfgCykComplete(rule);
        schema.addRule(complete);
      }
    }
    schema.addGoal(new CfgItem(cfg.getStartsymbol(), 0, wsplit.length));
    return schema;
  }

  /** Like CYK parsing, but with an additional deduction rule for chain rules,
   * hence grammar needs only to be in Canonical Two Form. 
   * Source: Giogio Satta, ESSLLI 2013*/
  public static ParsingSchema CfgToCykExtendedRules(Cfg cfg, String w) {
    if (!cfg.isInCanonicalTwoForm()) {
      System.out.println("Grammar has to be in Canonical Two Form.");
      return null;
    }
    String[] wsplit = w.split(" ");
    ParsingSchema schema = new ParsingSchema();

    for (CfgProductionRule rule : cfg.getProductionrules()) {
      if (rule.getRhs().length == 1) {
        if (cfg.terminalsContain(rule.getRhs()[0])) {
          for (int i = 0; i < wsplit.length; i++) {
            if (wsplit[i].equals(rule.getRhs()[0])) {
              StaticDeductionRule scan = new StaticDeductionRule();
              scan.addConsequence(new CfgItem(rule.getLhs(), i, 1));
              scan.setName("scan");
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
    schema.addGoal(new CfgItem(cfg.getStartsymbol(), 0, wsplit.length));
    return schema;
  }
}

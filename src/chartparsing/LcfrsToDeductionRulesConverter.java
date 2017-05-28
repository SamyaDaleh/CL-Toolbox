package chartparsing;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import chartparsing.lcfrsrules.SrcgCykBinary;
import chartparsing.lcfrsrules.SrcgCykUnary;
import chartparsing.lcfrsrules.SrcgDeductionUtils;
import chartparsing.lcfrsrules.SrcgEarleyComplete;
import chartparsing.lcfrsrules.SrcgEarleyConvert;
import chartparsing.lcfrsrules.SrcgEarleyPredict;
import chartparsing.lcfrsrules.SrcgEarleyResume;
import chartparsing.lcfrsrules.SrcgEarleyScan;
import chartparsing.lcfrsrules.SrcgEarleySuspend;
import common.lcfrs.Clause;
import common.lcfrs.Predicate;
import common.lcfrs.RangeVector;
import common.lcfrs.Srcg;
import common.lcfrs.SrcgCykItem;
import common.lcfrs.SrcgEarleyActiveItem;

/** Generates different parsing schemes. Based on the slides from Laura
 * Kallmeyer about Parsing as Deduction. */
class LcfrsToDeductionRulesConverter {

  /** Instead of calling the respective function this method works as entry
   * point for all of them. Takes a srcg, an input string w and a string
   * specifying which parsing algorithm shall be applied. Returns the respective
   * parsing scheme. */
  public static ParsingSchema SrcgToParsingSchema(Srcg srcg, String w,
    String schema) {
    switch (schema) {
    case "earley":
      return LcfrsToEarleyRules(srcg, w);
    case "cyk-extended":
      return LcfrsToCykExtendedRules(srcg, w);
    default:
      return null;
    }
  }

  private static ParsingSchema LcfrsToCykExtendedRules(Srcg srcg, String w) {
    if (srcg.hasEpsilonProductions()) {
      System.out.println(
        "sRCG is not allowed to have epsilon productions for this CYK algorithm.");
      return null;
    }
    if (!srcg.isBinarized()) {
      System.out.println("sRCG must be binarized for this CYK algorithm.");
      return null;
    }
    String[] wsplit = w.split(" ");
    ParsingSchema schema = new ParsingSchema();

    for (Clause clause : srcg.getClauses()) {
      if (clause.getRhs().size() == 2) {
        DynamicDeductionRule binary = new SrcgCykBinary(clause);
        schema.addRule(binary);
      } else if (clause.getRhs().size() == 0) {
        for (Integer[] ranges : getAllRanges(clause.getLhs(), wsplit)) {
          StaticDeductionRule scan = new StaticDeductionRule();
          scan.addConsequence(
            new SrcgCykItem(clause.getLhs().getNonterminal(), ranges));
          scan.setName("Scan");
          schema.addAxiom(scan);
        }
      } else {
        DynamicDeductionRule unary = new SrcgCykUnary(clause, wsplit);
        schema.addRule(unary);
      }
    }
    schema.addGoal(new SrcgCykItem(srcg.getStartSymbol(), 0, wsplit.length));
    return schema;
  }

  /** If lhs is a lhs Predicate of a clause and and wsplit is the splitted input
   * string this returns a list of all possible (non overlapping) ranges the
   * arguments could have over parts of the input. All symbols in the Predictae
   * have to be terminals. */
  private static List<Integer[]> getAllRanges(Predicate lhs, String[] wsplit) {
    List<Integer[]> ranges = new LinkedList<Integer[]>();
    ArrayList<Integer> tryoutrange = new ArrayList<Integer>();
    tryoutrange.add(0);
    tryoutrange.add(1);

    String[] lhsSymbolsAsPlainArray = lhs.getSymbolsAsPlainArray();

    while (tryoutrange.size() > 0) {
      while (tryoutrange.size() < lhsSymbolsAsPlainArray.length * 2) {
        int end = tryoutrange.get(tryoutrange.size() - 1);
        tryoutrange.add(end);
        tryoutrange.add(end + 1);
      }
      if (tryoutrange.get(tryoutrange.size() - 2) >= wsplit.length) {
        break;
      }

      boolean match = true;
      for (int i = 0; i * 2 < tryoutrange.size(); i++) {
        if (!lhsSymbolsAsPlainArray[i].equals(wsplit[tryoutrange.get(i * 2)])) {
          match = false;
        }
      }
      if (match) {
        ranges.add(SrcgDeductionUtils.getRangesForArguments(
          tryoutrange.toArray(new Integer[tryoutrange.size()]), lhs));
      }
      tryoutrange.set(tryoutrange.size() - 2,
        tryoutrange.get(tryoutrange.size() - 2) + 1);
      tryoutrange.set(tryoutrange.size() - 1,
        tryoutrange.get(tryoutrange.size() - 1) + 1);
      while (tryoutrange.get(tryoutrange.size() - 1) > wsplit.length) {
        tryoutrange.remove(tryoutrange.size() - 1);
        tryoutrange.remove(tryoutrange.size() - 1);
        tryoutrange.set(tryoutrange.size() - 2,
          tryoutrange.get(tryoutrange.size() - 2) + 1);
        tryoutrange.set(tryoutrange.size() - 1,
          tryoutrange.get(tryoutrange.size() - 1) + 1);
      }
    }
    return ranges;
  }

  static ParsingSchema LcfrsToEarleyRules(Srcg srcg, String w) {
    if (srcg.hasEpsilonProductions()) {
      System.out.println(
        "sRCG is not allowed to have epsilon productions for this Earley algorithm.");
      return null;
    }
    if (!srcg.isOrdered()) {
      System.out.println("sRCG must be ordered for this Earley algorithm.");
      return null;
    }
    String[] wsplit = w.split(" ");
    ParsingSchema schema = new ParsingSchema();

    for (Clause clause : srcg.getClauses()) {
      DynamicDeductionRule predict = new SrcgEarleyPredict(clause);
      schema.addRule(predict);
      if (clause.getLhs().getNonterminal().equals(srcg.getStartSymbol())) {
        StaticDeductionRule initialize = new StaticDeductionRule();
        initialize
          .addConsequence(new SrcgEarleyActiveItem(clause.toString(), 0, 1, 0,
            new RangeVector(clause.getLhs().getSymbolsAsPlainArray().length)));
        initialize.setName("Initialize");
        schema.addAxiom(initialize);
        schema.addGoal(new SrcgEarleyActiveItem(clause.toString(),
          wsplit.length, 1, clause.getLhs().getSymbolsAsPlainArray().length,
          new RangeVector(clause.getLhs().getSymbolsAsPlainArray().length)));
      }
    }
    DynamicDeductionRule scan = new SrcgEarleyScan(wsplit);
    schema.addRule(scan);
    DynamicDeductionRule suspend = new SrcgEarleySuspend(srcg.getVariables());
    schema.addRule(suspend);
    DynamicDeductionRule convert = new SrcgEarleyConvert();
    schema.addRule(convert);
    DynamicDeductionRule complete = new SrcgEarleyComplete();
    schema.addRule(complete);
    DynamicDeductionRule resume = new SrcgEarleyResume(srcg.getVariables());
    schema.addRule(resume);

    return schema;
  }
}

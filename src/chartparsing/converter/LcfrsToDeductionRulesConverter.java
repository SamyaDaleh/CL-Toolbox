package chartparsing.converter;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import chartparsing.DynamicDeductionRule;
import chartparsing.ParsingSchema;
import chartparsing.StaticDeductionRule;
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
public class LcfrsToDeductionRulesConverter {

  /** Instead of calling the respective function this method works as entry
   * point for all of them. Takes a srcg, an input string w and a string
   * specifying which parsing algorithm shall be applied. Returns the respective
   * parsing scheme. */
  public static ParsingSchema srcgToParsingSchema(Srcg srcg, String w,
    String schema) {
    switch (schema) {
    case "earley":
      return srcgToEarleyRules(srcg, w);
    case "cyk":
      return srcgToCykRules(srcg, w);
    case "cyk-extended":
      return srcgToCykExtendedRules(srcg, w);
    default:
      return null;
    }
  }

  static ParsingSchema srcgToCykRules(Srcg srcg, String w) {
    if (srcg.hasEpsilonProductions()) {
      System.out.println(
        "sRCG is not allowed to have epsilon productions for this CYK algorithm.");
      return null;
    }
    if (!srcg.isBinarized()) {
      System.out.println("sRCG must be binarized for this CYK algorithm.");
      return null;
    }
    if (srcg.hasChainRules()) {
      System.out
        .println("sRCG must not contain chain rules for this CYK algorithm.");
      return null;
    }
    String[] wsplit = w.split(" ");
    ParsingSchema schema = new ParsingSchema();

    for (Clause clause : srcg.getClauses()) {
      if (clause.getRhs().size() == 2) {
        DynamicDeductionRule binary = new SrcgCykBinary(clause, wsplit);
        schema.addRule(binary);
      } else if (clause.getRhs().size() == 0) {
        for (Integer[] ranges : getAllRanges(clause.getLhs(), wsplit)) {
          StaticDeductionRule scan = new StaticDeductionRule();
          scan.addConsequence(
            new SrcgCykItem(clause.getLhs().getNonterminal(), ranges));
          scan.setName("scan " + clause.toString());
          schema.addAxiom(scan);
        }
      }
    }
    schema.addGoal(new SrcgCykItem(srcg.getStartSymbol(), 0, wsplit.length));
    return schema;
  }

  static ParsingSchema srcgToCykExtendedRules(Srcg srcg, String w) {
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
        DynamicDeductionRule binary = new SrcgCykBinary(clause, wsplit);
        schema.addRule(binary);
      } else if (clause.getRhs().size() == 0) {
        for (Integer[] ranges : getAllRanges(clause.getLhs(), wsplit)) {
          StaticDeductionRule scan = new StaticDeductionRule();
          scan.addConsequence(
            new SrcgCykItem(clause.getLhs().getNonterminal(), ranges));
          scan.setName("scan " + clause.toString());
          schema.addAxiom(scan);
        }
      } else if (clause.getRhs().size() == 1) {
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
  private static List<Integer[]> getAllRanges(Predicate lhs, String[] wSplit) {
    List<Integer[]> ranges = new LinkedList<Integer[]>();
    ArrayList<Integer> tryOutRange = new ArrayList<Integer>();
    tryOutRange.add(0);
    tryOutRange.add(1);

    String[] lhsSymbolsAsPlainArray = lhs.getSymbolsAsPlainArray();

    while (tryOutRange.size() > 0) {
      while (tryOutRange.size() < lhsSymbolsAsPlainArray.length * 2) {
        int end = tryOutRange.get(tryOutRange.size() - 1);
        tryOutRange.add(end);
        tryOutRange.add(end + 1);
      }
      if (tryOutRange.get(tryOutRange.size() - 2) < wSplit.length) {

        boolean match = true;
        for (int i = 0; i * 2 < tryOutRange.size(); i++) {
          if (!lhsSymbolsAsPlainArray[i]
            .equals(wSplit[tryOutRange.get(i * 2)])) {
            match = false;
          }
        }
        if (match) {
          ranges.add(SrcgDeductionUtils.getRangesForArguments(
            tryOutRange.toArray(new Integer[tryOutRange.size()]), lhs));
        }
      }
      tryOutRange.set(tryOutRange.size() - 2,
        tryOutRange.get(tryOutRange.size() - 2) + 1);
      tryOutRange.set(tryOutRange.size() - 1,
        tryOutRange.get(tryOutRange.size() - 1) + 1);
      while (tryOutRange.get(tryOutRange.size() - 1) > wSplit.length) {
        tryOutRange.remove(tryOutRange.size() - 1);
        tryOutRange.remove(tryOutRange.size() - 1);
        if (tryOutRange.size() == 0) {
          break;
        }
        tryOutRange.set(tryOutRange.size() - 2,
          tryOutRange.get(tryOutRange.size() - 2) + 1);
        tryOutRange.set(tryOutRange.size() - 1,
          tryOutRange.get(tryOutRange.size() - 1) + 1);
      }
    }
    return ranges;
  }

  static ParsingSchema srcgToEarleyRules(Srcg srcg, String w) {
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
        initialize.setName("initialize");
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

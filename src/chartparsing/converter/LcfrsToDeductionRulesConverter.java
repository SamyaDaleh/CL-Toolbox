package chartparsing.converter;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import chartparsing.DynamicDeductionRule;
import chartparsing.Item;
import chartparsing.ParsingSchema;
import chartparsing.StaticDeductionRule;
import chartparsing.lcfrs.SrcgDeductionUtils;
import chartparsing.lcfrs.cyk.SrcgCykBinary;
import chartparsing.lcfrs.cyk.SrcgCykGeneral;
import chartparsing.lcfrs.cyk.SrcgCykItem;
import chartparsing.lcfrs.cyk.SrcgCykUnary;
import chartparsing.lcfrs.earley.SrcgEarleyActiveItem;
import chartparsing.lcfrs.earley.SrcgEarleyComplete;
import chartparsing.lcfrs.earley.SrcgEarleyConvert;
import chartparsing.lcfrs.earley.SrcgEarleyPredict;
import chartparsing.lcfrs.earley.SrcgEarleyResume;
import chartparsing.lcfrs.earley.SrcgEarleyScan;
import chartparsing.lcfrs.earley.SrcgEarleySuspend;
import common.lcfrs.Clause;
import common.lcfrs.Predicate;
import common.lcfrs.RangeVector;
import common.lcfrs.Srcg;
import common.tag.Tree;

/**
 * Generates different parsing schemes. Based on the slides from Laura Kallmeyer
 * about Parsing as Deduction.
 */
public class LcfrsToDeductionRulesConverter {

  private static void addSrcgCykScanRules(String[] wsplit, ParsingSchema schema,
    Clause clause) throws ParseException {
    for (List<Integer> ranges : getAllRanges(clause.getLhs(), wsplit)) {
      StaticDeductionRule scan = new StaticDeductionRule();
      Item consequence =
        new SrcgCykItem(clause.getLhs().getNonterminal(), ranges);
      StringBuilder treeString =
        new StringBuilder("( " + clause.getLhs().getNonterminal() + " ");
      for (int i = 0; i * 2 < ranges.size(); i++) {
        treeString.append(clause.getLhs().getSymAt(i + 1, 0)).append('<')
          .append(ranges.get(2 * i)).append(',').append(ranges.get(2 * i + 1))
          .append("> ");
      }
      treeString.append(")");
      consequence.setTree(new Tree(treeString.toString()));
      scan.addConsequence(consequence);
      scan.setName("scan " + clause.toString());
      schema.addAxiom(scan);
    }
  }

  public static ParsingSchema srcgToCykExtendedRules(Srcg srcg, String w)
    throws ParseException {
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
        addSrcgCykScanRules(wsplit, schema, clause);
      } else if (clause.getRhs().size() == 1) {
        DynamicDeductionRule unary = new SrcgCykUnary(clause, wsplit);
        schema.addRule(unary);
      }
    }
    schema.addGoal(new SrcgCykItem(srcg.getStartSymbol(), 0, wsplit.length));
    return schema;
  }

  public static ParsingSchema srcgToCykGeneralRules(Srcg srcg, String w)
    throws ParseException {
    if (srcg.hasEpsilonProductions()) {
      System.out.println(
        "sRCG is not allowed to have epsilon productions for this CYK algorithm.");
      return null;
    }
    String[] wsplit = w.split(" ");
    ParsingSchema schema = new ParsingSchema();

    for (Clause clause : srcg.getClauses()) {
      if (clause.getRhs().size() == 0) {
        addSrcgCykScanRules(wsplit, schema, clause);
      } else {
        DynamicDeductionRule general = new SrcgCykGeneral(clause, wsplit);
        schema.addRule(general);
      }
    }
    schema.addGoal(new SrcgCykItem(srcg.getStartSymbol(), 0, wsplit.length));
    return schema;
  }

  /**
   * If lhs is a lhs Predicate of a clause and and wsplit is the splitted input
   * string this returns a list of all possible (non overlapping) ranges the
   * arguments could have over parts of the input. All symbols in the Predicate
   * have to be terminals.
   */
  @SuppressWarnings("unchecked") private static List<List<Integer>>
    getAllRanges(Predicate lhs, String[] wSplit) {
    ArrayList<List<Integer>> ranges = new ArrayList<List<Integer>>();
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
            break;
          }
        }
        if (match) {
          ranges.add((List<Integer>) SrcgDeductionUtils
            .getRangesForArguments(tryOutRange, lhs));
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

  public static ParsingSchema srcgToEarleyRules(Srcg srcg, String w) {
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
      if (!clause.getLhs().getNonterminal().equals(srcg.getStartSymbol())) {
        continue;
      }
      StaticDeductionRule initialize = new StaticDeductionRule();
      initialize
        .addConsequence(new SrcgEarleyActiveItem(clause.toString(), 0, 1, 0,
          new RangeVector(clause.getLhs().getSymbolsAsPlainArray().length)));
      initialize.setName("initialize");
      schema.addAxiom(initialize);
      schema.addGoal(new SrcgEarleyActiveItem(clause.toString(), wsplit.length,
        1, clause.getLhs().getSymbolsAsPlainArray().length,
        new RangeVector(clause.getLhs().getSymbolsAsPlainArray().length)));
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

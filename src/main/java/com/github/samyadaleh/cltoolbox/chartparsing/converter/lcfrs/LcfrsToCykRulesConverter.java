package com.github.samyadaleh.cltoolbox.chartparsing.converter.lcfrs;

import com.github.samyadaleh.cltoolbox.chartparsing.item.ChartItemInterface;
import com.github.samyadaleh.cltoolbox.chartparsing.dynamicdeductionrule.DynamicDeductionRuleInterface;
import com.github.samyadaleh.cltoolbox.chartparsing.ParsingSchema;
import com.github.samyadaleh.cltoolbox.chartparsing.StaticDeductionRule;
import com.github.samyadaleh.cltoolbox.chartparsing.lcfrs.SrcgDeductionUtils;
import com.github.samyadaleh.cltoolbox.chartparsing.lcfrs.cyk.SrcgCykBinary;
import com.github.samyadaleh.cltoolbox.chartparsing.lcfrs.cyk.SrcgCykGeneral;
import com.github.samyadaleh.cltoolbox.chartparsing.lcfrs.cyk.SrcgCykItem;
import com.github.samyadaleh.cltoolbox.chartparsing.lcfrs.cyk.SrcgCykUnary;
import com.github.samyadaleh.cltoolbox.common.lcfrs.Clause;
import com.github.samyadaleh.cltoolbox.common.lcfrs.Predicate;
import com.github.samyadaleh.cltoolbox.common.lcfrs.Srcg;
import com.github.samyadaleh.cltoolbox.common.tag.Tree;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import static com.github.samyadaleh.cltoolbox.common.Constants.DEDUCTION_RULE_LCFRS_CYK_AXIOM;
import static com.github.samyadaleh.cltoolbox.common.Constants.EPSILON;

public class LcfrsToCykRulesConverter {

  private static void addSrcgCykScanRules(String[] wsplit, ParsingSchema schema,
      Clause clause) throws ParseException {
    for (List<Integer> ranges : getAllRanges(clause.getLhs(), wsplit)) {
      StaticDeductionRule scan = new StaticDeductionRule();
      ChartItemInterface consequence =
          new SrcgCykItem(clause.getLhs().getNonterminal(), ranges);
      StringBuilder treeString =
          new StringBuilder("( " + clause.getLhs().getNonterminal() + " ");
      for (int i = 0; i * 2 < ranges.size(); i++) {
        String[] arg = clause.getLhs().getArgumentByIndex(i + 1);
        for (int j = 0; j < arg.length; j++) {
          if (arg[j].length() == 0) {
            treeString.append(EPSILON);
          } else {
            treeString.append(clause.getLhs().getSymAt(i + 1, j));
          }
          treeString.append('<').append(ranges.get(2 * i) + j).append("> ");
        }
      }
      treeString.append(")");
      List<Tree> derivedTrees = new ArrayList<>();
      derivedTrees.add(new Tree(treeString.toString()));
      consequence.setTrees(derivedTrees);
      scan.addConsequence(consequence);
      scan.setName(DEDUCTION_RULE_LCFRS_CYK_AXIOM + " " + clause.toString());
      schema.addAxiom(scan);
    }
  }

  public static ParsingSchema srcgToCykExtendedRules(Srcg srcg, String w)
      throws ParseException {
    if (srcg.hasEpsilonProductions()) {
      throw new ParseException(
          "sRCG is not allowed to have epsilon productions for this CYK algorithm.",
          1);
    }
    if (!srcg.isBinarized()) {
      throw new ParseException("sRCG must be binarized for this CYK algorithm.",
          1);
    }
    String[] wsplit = w.split(" ");
    ParsingSchema schema = new ParsingSchema();

    for (Clause clause : srcg.getClauses()) {
      if (clause.getRhs().size() == 2) {
        DynamicDeductionRuleInterface binary =
            new SrcgCykBinary(clause, wsplit);
        schema.addRule(binary);
      } else if (clause.getRhs().size() == 0) {
        addSrcgCykScanRules(wsplit, schema, clause);
      } else if (clause.getRhs().size() == 1) {
        DynamicDeductionRuleInterface unary = new SrcgCykUnary(clause, wsplit);
        schema.addRule(unary);
      }
    }
    if (w.length() == 0) {
      schema.addGoal(new SrcgCykItem(srcg.getStartSymbol(), 0, 0));
    } else {
      schema.addGoal(new SrcgCykItem(srcg.getStartSymbol(), 0, wsplit.length));
    }
    return schema;
  }

  public static ParsingSchema srcgToCykGeneralRules(Srcg srcg, String w)
      throws ParseException {
    if (srcg.hasEpsilonProductions()) {
      throw new ParseException(
          "sRCG is not allowed to have epsilon productions for this CYK algorithm.",
          1);
    }
    String[] wsplit = w.split(" ");
    ParsingSchema schema = new ParsingSchema();

    for (Clause clause : srcg.getClauses()) {
      if (clause.getRhs().size() == 0) {
        addSrcgCykScanRules(wsplit, schema, clause);
      } else {
        DynamicDeductionRuleInterface general =
            new SrcgCykGeneral(clause, wsplit);
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
  @SuppressWarnings("unchecked")
  private static List<List<Integer>> getAllRanges(Predicate lhs,
      String[] wSplit) {
    ArrayList<List<Integer>> ranges = new ArrayList<>();
    ArrayList<Integer> tryOutRange = new ArrayList<>();
    if (wSplit.length == 1 && "".equals(wSplit[0])) {
      tryOutRange.add(0);
      tryOutRange.add(0);
      ranges.add(tryOutRange);
      return ranges;
    }
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
        if (match && rangesFitArguments(tryOutRange, lhs)) {
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

  /**
   * Returns true if ranges of consecutive terminals meet.
   */
  private static boolean rangesFitArguments(ArrayList<Integer> tryOutRange,
      Predicate lhs) {
    for (int i = 0; i < lhs.getDim(); i++) {
      String[] arg = lhs.getArgumentByIndex(i + 1);
      for (int j = 1; j < arg.length; j++) {
        int pos = lhs.getAbsolutePos(i + 1, j);
        if (!tryOutRange.get(pos * 2).equals(tryOutRange.get(pos * 2 - 1))) {
          return false;
        }
      }
    }
    return true;
  }
}

package com.github.samyadaleh.cltoolbox.common;

import java.text.ParseException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.github.samyadaleh.cltoolbox.chartparsing.ChartItemInterface;
import com.github.samyadaleh.cltoolbox.common.cfg.CfgProductionRule;
import com.github.samyadaleh.cltoolbox.common.lcfrs.Clause;
import com.github.samyadaleh.cltoolbox.common.lcfrs.Predicate;
import com.github.samyadaleh.cltoolbox.common.tag.Tree;

public class TreeUtils {

  public static CfgProductionRule
    getCfgRuleRepresentationOfSrcgEpsilonClauseString(ChartItemInterface item)
      throws ParseException {
    Clause clause = new Clause(item.getItemform()[0]);
    StringBuilder extractedRule = new StringBuilder();
    extractedRule.append(clause.getLhs().getNonterminal()).append(" ->");
    String[] lhsSyms = clause.getLhs().getSymbolsAsPlainArray();
    for (int i = 0; i < lhsSyms.length; i++) {
      extractedRule.append(' ').append(lhsSyms[i]).append('<');
      extractedRule.append(item.getItemform()[i * 2 + 4]);
      extractedRule.append('>');
    }
    return new CfgProductionRule(extractedRule.toString());
  }

  public static CfgProductionRule getCfgRuleRepresentationOfSrcgClauseString(
    ChartItemInterface item) throws ParseException {
    String srcgClauseString = item.getItemform()[0];
    Clause clause = new Clause(srcgClauseString);
    StringBuilder extractedRule = new StringBuilder();
    extractedRule.append(clause.getLhs().getNonterminal()).append(" ->");
    int terminalsInLhs = 0;
    for (String symbol : clause.getLhs().getSymbolsAsPlainArray()) {
      if (!TreeUtils.symbolIsVariable(clause, symbol)) {
        terminalsInLhs++;
      }
    }
    String[] lhsSymbols = clause.getLhs().getSymbolsAsPlainArray();
    int i = 0;
    for (int terminalsProcessed = 0; terminalsProcessed < terminalsInLhs
      / 2; i++) {
      String symbol = lhsSymbols[i];
      boolean found = TreeUtils.symbolIsVariable(clause, symbol);
      if (!found) {
        terminalsProcessed++;
        extractedRule.append(" ").append(symbol).append('<')
          .append(item.getItemform()[i * 2 + 4]).append('>');
      }
    }
    for (Predicate rhs : clause.getRhs()) {
      extractedRule.append(" ").append(rhs.getNonterminal());
    }
    for (; i < lhsSymbols.length; i++) {
      String symbol = lhsSymbols[i];
      boolean found = TreeUtils.symbolIsVariable(clause, symbol);
      if (!found) {
        extractedRule.append(" ").append(symbol).append('<')
          .append(item.getItemform()[i * 2 + 4]).append('>');
      }
    }
    return new CfgProductionRule(extractedRule.toString());
  }

  public static CfgProductionRule getCfgRuleRepresentationOfSrcgClause(
    Clause clause, List<Integer> vector) throws ParseException {
    StringBuilder extractedRule = new StringBuilder();
    extractedRule.append(clause.getLhs().getNonterminal()).append(" ->");
    int terminalsInLhs = 0;
    for (String symbol : clause.getLhs().getSymbolsAsPlainArray()) {
      if (!TreeUtils.symbolIsVariable(clause, symbol)) {
        terminalsInLhs++;
      }
    }
    String[] lhsSymbols = clause.getLhs().getSymbolsAsPlainArray();
    int i = 0;
    for (int terminalsProcessed = 0; terminalsProcessed < terminalsInLhs
      / 2; i++) {
      String symbol = lhsSymbols[i];
      boolean found = TreeUtils.symbolIsVariable(clause, symbol);
      if (!found) {
        terminalsProcessed++;
        extractedRule.append(" ").append(symbol).append('<')
          .append(vector.get(i * 2)).append('>');
      }
    }
    for (Predicate rhs : clause.getRhs()) {
      extractedRule.append(" ").append(rhs.getNonterminal());
    }
    for (; i < lhsSymbols.length; i++) {
      String symbol = lhsSymbols[i];
      boolean found = TreeUtils.symbolIsVariable(clause, symbol);
      if (!found) {
        extractedRule.append(" ").append(symbol).append('<')
          .append(vector.get(i * 2)).append('>');
      }
    }
    return new CfgProductionRule(extractedRule.toString());
  }

  public static CfgProductionRule
    getCfgRuleRepresentationOfSrcgClause(Clause clause) throws ParseException {
    if (clause.getRhs().size() == 0) {
      return new CfgProductionRule(clause.getLhs().getNonterminal() + " -> "
        + ArrayUtils.getSubSequenceAsString(
          clause.getLhs().getSymbolsAsPlainArray(), 0,
          clause.getLhs().getSymbolsAsPlainArray().length));
    }
    StringBuilder cfgRuleString =
      new StringBuilder(clause.getLhs().getNonterminal());
    cfgRuleString.append(" ->");
    for (Predicate rhsPred : clause.getRhs()) {
      cfgRuleString.append(" ").append(rhsPred.getNonterminal());
    }
    for (String sym : clause.getLhs().getSymbolsAsPlainArray()) {
      boolean found = false;
      for (Predicate rhsPred : clause.getRhs()) {
        int[] indices = rhsPred.find(sym);
        if (indices[0] != -1) {
          found = true;
          break;
        }
      }
      if (!found) {
        cfgRuleString.append(" ").append(sym);
      }
    }
    return new CfgProductionRule(cfgRuleString.toString());
  }

  private static boolean symbolIsVariable(Clause clause, String symbol) {
    boolean found = false;
    for (Predicate rhsPred : clause.getRhs()) {
      int[] indices = rhsPred.find(symbol);
      if (indices[0] >= 0) {
        found = true;
        break;
      }
    }
    return found;
  }

  /**
   * Performs leftmost substitution of tree into derivatedTree.
   */
  public static Tree performLeftmostSubstitution(Tree derivedTree, Tree tree)
    throws ParseException {
    String derivedTreeString = derivedTree.toString();
    Tree newDerivatedTree = new Tree(derivedTreeString.substring(0,
      derivedTreeString.indexOf("(" + tree.getRoot().getLabel() + " )"))
      + tree.toString()
      + derivedTreeString.substring(
        derivedTreeString.indexOf("(" + tree.getRoot().getLabel() + " )")
          + tree.getRoot().getLabel().length() + 3));
    return newDerivatedTree;
  }

  /**
   * Returns a new tree where the leftmost terminal without position index has
   * got index pos.
   * @throws ParseException
   */
  public static Tree performPositionSubstitution(Tree tree, String terminal,
    String pos) throws ParseException {
    String derivedTreeString = tree.toString();
    String searchFor = terminal + " ";
    int index = derivedTreeString.indexOf(searchFor);
    Tree newTree = new Tree(derivedTreeString.substring(0, index) + terminal
      + "<" + pos + ">" + derivedTreeString
        .substring(index + searchFor.length(), derivedTreeString.length()));
    return newTree;
  }

  public static Tree performSecondLeftmostSubstitution(Tree derivedTree,
    Tree tree) throws ParseException {
    String derivedTreeString = derivedTree.toString();
    Pattern pattern = Pattern.compile("\\(\\w+ \\)");
    Matcher matcher = pattern.matcher(derivedTreeString);
    matcher.find();
    int firstChild = matcher.start();
    int pos = derivedTreeString.indexOf("(" + tree.getRoot().getLabel() + " )",
      firstChild + 1);
    Tree newDerivatedTree = new Tree(
      derivedTreeString.substring(0, pos) + tree.toString() + derivedTreeString
        .substring(pos + tree.getRoot().getLabel().length() + 3));
    return newDerivatedTree;
  }
}

package com.github.samyadaleh.cltoolbox.common;

import java.text.ParseException;
import java.util.List;

import com.github.samyadaleh.cltoolbox.common.cfg.CfgProductionRule;
import com.github.samyadaleh.cltoolbox.common.lcfrs.Clause;
import com.github.samyadaleh.cltoolbox.common.lcfrs.Predicate;
import com.github.samyadaleh.cltoolbox.common.tag.Tree;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TreeUtils {
  private static final Logger log = LogManager.getLogger();

  public static Tree getTreeOfSrcgClause(Clause clause, List<Integer> vector) {
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
    for (int terminalsProcessed = 0;
         terminalsProcessed < terminalsInLhs / 2; i++) {
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
    try {
      return new Tree(new CfgProductionRule(extractedRule.toString()));
    } catch (ParseException e) {
      log.error(e.getMessage(), e);
      return null;
    }
  }

  public static Tree getTreeOfSrcgClause(Clause clause) {
    if (clause.getRhs().size() == 0) {
      try {
        return new Tree(new CfgProductionRule(
            clause.getLhs().getNonterminal() + " -> " + ArrayUtils
                .getSubSequenceAsString(
                    clause.getLhs().getSymbolsAsPlainArray(), 0,
                    clause.getLhs().getSymbolsAsPlainArray().length)));
      } catch (ParseException e) {
        log.error(e.getMessage(), e);
        return null;
      }
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
    try {
      return new Tree(new CfgProductionRule(cfgRuleString.toString()));
    } catch (ParseException e) {
      log.error(e.getMessage(), e);
      return null;
    }
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
  public static Tree performLeftmostSubstitution(Tree derivedTree, Tree tree) {
    String derivedTreeString = derivedTree.toString();
    try {
      return new Tree(derivedTreeString.substring(0,
          derivedTreeString.indexOf("(" + tree.getRoot().getLabel() + " )"))
          + tree.toString() + derivedTreeString.substring(
          derivedTreeString.indexOf("(" + tree.getRoot().getLabel() + " )")
              + tree.getRoot().getLabel().length() + 3));
    } catch (ParseException e) {
      log.error(e.getMessage(), e);
    }
    return null;
  }

  /**
   * Returns a new tree where the leftmost terminal without position index has
   * got index pos.
   */
  public static Tree performPositionSubstitution(Tree tree, String terminal,
      String pos) {
    String derivedTreeString = tree.toString();
    String searchFor = terminal + " ";
    int index = derivedTreeString.indexOf(searchFor);
    try {
      return new Tree(
          derivedTreeString.substring(0, index) + terminal + "<" + pos + ">"
              + derivedTreeString.substring(index + searchFor.length()));
    } catch (ParseException e) {
      log.error(e.getMessage(), e);
    }
    return null;
  }

}

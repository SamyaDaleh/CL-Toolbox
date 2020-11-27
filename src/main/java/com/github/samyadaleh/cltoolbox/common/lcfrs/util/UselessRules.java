package com.github.samyadaleh.cltoolbox.common.lcfrs.util;

import java.util.ArrayList;
import java.util.List;

import com.github.samyadaleh.cltoolbox.common.ArrayUtils;
import com.github.samyadaleh.cltoolbox.common.lcfrs.Clause;
import com.github.samyadaleh.cltoolbox.common.lcfrs.Predicate;
import com.github.samyadaleh.cltoolbox.common.lcfrs.Srcg;

public class UselessRules {

  /**
   * Returns a list of all nonterminals that do not lead to a sequence of
   * terminals. Terminating nonterminals are all that appear as lhs on rules
   * whose rhs is empty or whose rhs only constists of terminating nonterminals.
   */
  private static List<String> getNonterminatingSymbols(Srcg srcg) {
    List<String> terminatingSymbols = new ArrayList<>();
    for (Clause clause : srcg.getClauses()) {
      String nt = clause.getLhs().getNonterminal();
      if (clause.getRhs().size() == 0 && !terminatingSymbols.contains(nt)) {
        terminatingSymbols.add(nt);
      }
    }
    boolean changed = true;
    while (changed) {
      changed = false;
      for (Clause clause : srcg.getClauses()) {
        String lhsNt = clause.getLhs().getNonterminal();
        if (!terminatingSymbols.contains(lhsNt)) {
          boolean allterminating = true;
          for (Predicate rhsPred : clause.getRhs()) {
            if (!terminatingSymbols.contains(rhsPred.getNonterminal())) {
              allterminating = false;
              break;
            }
          }
          if (allterminating) {
            terminatingSymbols.add(lhsNt);
            changed = true;
          }
        }
      }
    }
    List<String> nonterminatingSymbols = new ArrayList<>();
    for (String nt : srcg.getNonterminals()) {
      if (!terminatingSymbols.contains(nt)) {
        nonterminatingSymbols.add(nt);
      }
    }
    return nonterminatingSymbols;
  }

  private static List<String> getNonreachableSymbols(Srcg srcg) {
    List<String> reachableSymbols = new ArrayList<>();
    reachableSymbols.add(srcg.getStartSymbol());
    boolean changed = true;
    while (changed) {
      changed = false;
      for (Clause clause : srcg.getClauses()) {
        String lhsNt = clause.getLhs().getNonterminal();
        if (reachableSymbols.contains(lhsNt)) {
          for (Predicate rhsPred : clause.getRhs()) {
            String rhsNt = rhsPred.getNonterminal();
            if (!reachableSymbols.contains(rhsNt)) {
              reachableSymbols.add(rhsNt);
              changed = true;
            }
          }
        }
      }
    }
    List<String> nonreachableSymbols = new ArrayList<>();
    for (String nt : srcg.getNonterminals()) {
      if (!reachableSymbols.contains(nt)) {
        nonreachableSymbols.add(nt);
      }
    }
    return nonreachableSymbols;
  }

  /**
   * Returns an equivalent sRCG without nonreachable and nongenerating rules.
   */

  public static Srcg getSrcgWithoutUselessRules(Srcg srcg) {
    List<String> nonTerminatingNt = getNonterminatingSymbols(srcg);
    Srcg woNonterminating = getSrcgWithoutNonterminals(srcg, nonTerminatingNt);
    if (woNonterminating == null) {
      return null;
    }
    List<String> nonReachableNt = getNonreachableSymbols(woNonterminating);
    return getSrcgWithoutNonterminals(woNonterminating, nonReachableNt);
  }

  /**
   * Returns an equivalent sRCG without the given nonterminals and without any
   * rule that uses one of them. (Equivalent only when called with useless
   * symbols.) Also removes variables and terminals that are not used anymore.
   */
  private static Srcg getSrcgWithoutNonterminals(Srcg srcg,
    List<String> uselessNonterminals) {
    if (uselessNonterminals.contains(srcg.getStartSymbol())) {
      return null;
    }
    Srcg newSrcg = new Srcg();
    newSrcg.setStartSymbol(srcg.getStartSymbol());
    List<String> usedVariables = new ArrayList<>();
    List<String> usedTerminals = new ArrayList<>();
    // nonterminals
    for (Clause clause : srcg.getClauses()) {
      Predicate lhsPred = clause.getLhs();
      if (uselessNonterminals.contains(lhsPred.getNonterminal())) {
        continue;
      }
      boolean continueFlag = false;
      for (Predicate rhsPred : clause.getRhs()) {
        if (uselessNonterminals.contains(rhsPred.getNonterminal())) {
          continueFlag = true;
          break;
        }
      }
      if (continueFlag) {
        continue;
      }
      newSrcg.addClause(clause);
      // variables and terminals. What's not declared a variable must be a
      // terminal.
      for (String symbol : lhsPred.getSymbolsAsPlainArray()) {
        if ("".equals(symbol)) {
          continue;
        }
        if (!usedVariables.contains(symbol)) {
          if (ArrayUtils.contains(srcg.getVariables(), symbol)) {
            usedVariables.add(symbol);
          } else {
            if (!usedTerminals.contains(symbol)) {
              usedTerminals.add(symbol);
            }
          }
        }
      }
    }
    List<String> nonterminals = new ArrayList<>();
    for (String nt : srcg.getNonterminals()) {
      if(!uselessNonterminals.contains(nt)) {
        nonterminals.add(nt);
      }
    }
    String[] ntArray = nonterminals.toArray(new String[0]);
    newSrcg.setNonterminals(ntArray);
    String[] varArray = usedVariables.toArray(new String[0]);
    newSrcg.setVariables(varArray);
    String[] terArray = usedTerminals.toArray(new String[0]);
    newSrcg.setTerminals(terArray);
    return newSrcg;
  }

}

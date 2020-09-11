package com.github.samyadaleh.cltoolbox.common.cfg.util;

import com.github.samyadaleh.cltoolbox.common.ArrayUtils;
import com.github.samyadaleh.cltoolbox.common.cfg.Cfg;
import com.github.samyadaleh.cltoolbox.common.cfg.CfgProductionRule;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Takes care that different nonterminals are used as left-corner and in other
 * places, so no symbol appears in both positions.
 */
public class Doubling {

  /**
   * Returns a Cfg where no symbol appears both as left corner and in other
   * positions.
   * Separates terminals from nonterminals beforehand.
   */
  public static Cfg doubleSymbols(Cfg cfgOld) throws ParseException {
    Cfg cfgNew = new Cfg();
    cfgNew.setTerminals(cfgOld.getTerminals());
    cfgNew.setStartSymbol(cfgOld.getStartSymbol());
    Cfg cfg = cfgOld.getCfgWithEitherOneTerminalOrNonterminalsOnRhs();
    List<CfgProductionRule> newRules =
        new ArrayList<>(cfg.getProductionRules());
    List<String> newNonterminals =
        new ArrayList<>(Arrays.asList(cfg.getNonterminals()));

    for (int i = 0; i < newRules.size(); i++) {
      CfgProductionRule rule = newRules.get(i);
      if (rule.getRhs().length > 0 && !"ε".equals(rule.getRhs()[0])) {
        handleLcOccuringInOtherRules(newRules, newNonterminals, rule);
      }
    }

    for (CfgProductionRule rule : newRules) {
      cfgNew.addProductionRule(rule.toString());
    }
    cfgNew.setNonterminals(newNonterminals.toArray(new String[0]));
    return cfgNew;
  }

  private static void handleLcOccuringInOtherRules(
      List<CfgProductionRule> newRules, List<String> newNonterminals,
      CfgProductionRule rule) throws ParseException {
    String lc = rule.getRhs()[0];
    String newNt = null;
    //  if N occurs in any other production rule not as lc:
    for (int j = 0; j < newRules.size(); j++) {
      CfgProductionRule rule2 = newRules.get(j);
      if (rule2.getRhs().length > 0 && !"ε".equals(rule2.getRhs()[0])) {
        for (int k = 1; k < rule2.getRhs().length; k++) {
          if (lc.equals(rule2.getRhs()[k])) {
            if (newNt == null) {
              newNt = introduceNewNonterminal(newNonterminals, lc);
              duplicateRules(newRules, lc, newNt);
              newNonterminals.add(newNt);
            }
            replaceInnerNonterminal(newRules, newNt, j, rule2, k);
          }
        }
      }
    }
  }

  private static void duplicateRules(List<CfgProductionRule> newRules,
      String lc, String newNt) throws ParseException {
    for (int l = 0; l < newRules.size(); l++) {
      CfgProductionRule rule3 = newRules.get(l);
      if (lc.equals(rule3.getLhs())) {
        newRules.add(new CfgProductionRule(
            newNt + " -> " + rule3.toString().split("->")[1]));
      }
    }
  }

  private static String introduceNewNonterminal(List<String> newNonterminals,
      String lc) {
    String newNt;
    newNt = lc + "'";
    while (newNonterminals.contains(newNt)) {
      newNt += "'";
    }
    return newNt;
  }

  private static void replaceInnerNonterminal(List<CfgProductionRule> newRules,
      String newNt, int j, CfgProductionRule rule2, int k)
      throws ParseException {
    String rhsBeforeReplacement =
        ArrayUtils.getSubSequenceAsString(rule2.getRhs(), 0, k);
    String rhsAfterReplacement = ArrayUtils
        .getSubSequenceAsString(rule2.getRhs(), k + 1,
            rule2.getRhs().length);
    CfgProductionRule replaceRule = new CfgProductionRule(
        rule2.getLhs() + " -> " + rhsBeforeReplacement + " " + newNt
            + " " + rhsAfterReplacement);
    newRules.set(j, replaceRule);
  }
}

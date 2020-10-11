package com.github.samyadaleh.cltoolbox.common.cfg.util;

import com.github.samyadaleh.cltoolbox.common.cfg.Cfg;
import com.github.samyadaleh.cltoolbox.common.cfg.CfgProductionRule;

import java.text.ParseException;

/**
 * Util class that reverses the production rules.
 */
public class ReverseProductionRules {

  /**
   * Returns a cfg with all rhs of all production rules flipped.
   */
  public static Cfg getCfgWithReversedProductionRules(Cfg cfg) {
    Cfg cfgNew = new Cfg();
    cfgNew.setNonterminals(cfg.getNonterminals());
    cfgNew.setTerminals(cfg.getTerminals());
    cfgNew.setStartSymbol(cfg.getStartSymbol());
    for (CfgProductionRule rule : cfg.getProductionRules()) {
      try {
        cfgNew.addProductionRule(rule.getLhs() + " -> " + reverseRhs(rule));
      } catch (ParseException e) {
        throw new RuntimeException(e);
      }
    }
    return cfgNew;
  }

  private static String reverseRhs(CfgProductionRule rule) {
    String ruleString = rule.toString();
    String[] ruleSplit = ruleString.split("->");
    String rhsString = ruleSplit[1];
    StringBuilder reversedRhs = new StringBuilder();
    for (int i = rhsString.length() - 1; i >= 0; i--) {
      reversedRhs.append(rhsString.charAt(i));
    }
    return reversedRhs.toString();
  }
}

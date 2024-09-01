package com.github.samyadaleh.cltoolbox.common.cfg.util;

import com.github.samyadaleh.cltoolbox.common.ArrayUtils;
import com.github.samyadaleh.cltoolbox.common.cfg.Cfg;
import com.github.samyadaleh.cltoolbox.common.cfg.CfgProductionRule;

import java.text.ParseException;
import java.util.*;

import static com.github.samyadaleh.cltoolbox.common.Constants.ARROW_RIGHT;

/**
 * Util methods to handle factoring.
 */
public class Factoring {

  /**
   * Return a left-factored grammar, hence a grammar where no production rules
   * with the same lhs start with the same symbols
   */
  public static Cfg getLeftFactoredCfg(Cfg cfgOld) {
    Cfg cfgNew = new Cfg();
    cfgNew.setStartSymbol(cfgOld.getStartSymbol());
    try {
      Set<CfgProductionRule> newProductionRulesSet = new LinkedHashSet<>();
      for (CfgProductionRule rule : cfgOld.getProductionRules()) {
        newProductionRulesSet.add(new CfgProductionRule(rule.toString()));
      }
      List<CfgProductionRule> newProductionRules =
          new ArrayList<>(newProductionRulesSet);
      cfgNew.setTerminals(cfgOld.getTerminals());
      List<String> newNonterminals =
          new ArrayList<>(cfgOld.getNonterminals());
      for (String lhs : cfgOld.getNonterminals()) {
        boolean changed = true;
        while (changed) {
          changed = false;
          String longestPrefix =
              findLongestCommonPrefix(lhs, newProductionRules);
          if (!longestPrefix.isEmpty()) {
            changed = true;
            replacePrefix(lhs, newProductionRules, newNonterminals,
                longestPrefix);
          }
        }
      }
      cfgNew.setNonterminals(newNonterminals);
      for (CfgProductionRule rule : newProductionRules) {
        cfgNew.addProductionRule(rule.toString());
      }
    } catch (ParseException e) {
      // should never happen after debugging.
      throw new RuntimeException(e);
    }
    return cfgNew;
  }

  private static void replacePrefix(String lhs,
      List<CfgProductionRule> productionRules, List<String> nonterminals,
      String longestPrefix) throws ParseException {
    int prefLength = longestPrefix.split(" ").length;
    int i = 1;
    String newNt = "N" + i;
    while (nonterminals.contains(newNt)) {
      i++;
      newNt = "N" + i;
    }
    nonterminals.add(newNt);
    for (int j = productionRules.size() - 1; j >= 0; j--) {
      CfgProductionRule rule = productionRules.get(j);
      if (rule.getRhs().length >= prefLength && rule.getLhs().equals(lhs)
          && ArrayUtils.getSubSequenceAsString(rule.getRhs(), 0, prefLength)
          .equals(longestPrefix)) {
        productionRules.remove(j);
        j++;
        String rhsRest = ArrayUtils
            .getSubSequenceAsString(rule.getRhs(), prefLength,
                rule.getRhs().length);
        productionRules.add(new CfgProductionRule(newNt + " " + ARROW_RIGHT + " " + rhsRest));
      }
    }
    productionRules
        .add(new CfgProductionRule(lhs + " " + ARROW_RIGHT + " " + longestPrefix + " " + newNt));
  }

  private static String findLongestCommonPrefix(String lhs,
      List<CfgProductionRule> productionRules) {
    Map<String, Integer> prefixes = new HashMap<>();
    for (CfgProductionRule rule : productionRules) {
      if (!rule.getLhs().equals(lhs)) {
        continue;
      }
      String[] rhs = rule.getRhs();
      for (int i = 1; i <= rhs.length; i++) {
        String prefix = ArrayUtils.getSubSequenceAsString(rhs, 0, i);
        if (prefixes.containsKey(prefix)) {
          prefixes.put(prefix, prefixes.get(prefix) + 1);
        } else {
          prefixes.put(prefix, 1);
        }
      }
    }
    String longestPrefix = "";
    for (Map.Entry<String, Integer> entry : prefixes.entrySet()) {
      if (entry.getKey().length() > longestPrefix.length()
          && entry.getValue() > 1) {
        longestPrefix = entry.getKey();
      }
    }
    return longestPrefix;
  }
}

package com.github.samyadaleh.cltoolbox.common.cfg.util;

import com.github.samyadaleh.cltoolbox.common.ArrayUtils;
import com.github.samyadaleh.cltoolbox.common.cfg.Cfg;
import com.github.samyadaleh.cltoolbox.common.cfg.CfgProductionRule;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExpandGreibach {
  public static Cfg getCfgWithProductionsInGnf(Cfg cfgOld)
      throws ParseException {
    if (cfgOld.isInGreibachNormalForm()) {
      return cfgOld;
    }
    Cfg cfgNew = new Cfg();
    cfgNew.setStartSymbol(cfgOld.getStartSymbol());
    cfgNew.setTerminals(cfgOld.getTerminals());
    cfgNew.setNonterminals(cfgOld.getNonterminals());
    Map<String, List<String[]>> expansions
        = calculateNonterminalExpansions(cfgOld);
    for (CfgProductionRule rule : cfgOld.getProductionRules()) {
      if (cfgOld.isInGreibachNormalForm(rule)) {
        cfgNew.addProductionRule(rule.toString());
        continue;
      }
      List<CfgProductionRule> expandedRules
          = expandProductionRule(rule, expansions);
      cfgNew.getProductionRules().addAll(expandedRules);
    }
    return cfgNew;
  }

  private static List<CfgProductionRule> expandProductionRule(
      CfgProductionRule rule, Map<String, List<String[]>> expansions) {
    List<CfgProductionRule> expandedRules = new ArrayList<>();
    String lhs = rule.getLhs();
    for(String[] expansion : expansions.get(lhs)) {
      String[] newRhs = ArrayUtils.concat(expansion,
          ArrayUtils.getSequenceWithoutIAsArray(rule.getRhs(), 0));
      expandedRules.add(new CfgProductionRule(lhs, newRhs));
    }
    return expandedRules;
  }

  private static Map<String, List<String[]>> calculateNonterminalExpansions(
      Cfg cfg) {
    Map<String, List<String[]>> expansions = new HashMap<>();
    for (CfgProductionRule rule : cfg.getProductionRules()) {
      String lhs = rule.getLhs();
      String[] rhs = rule.getRhs();
      if (rhs.length == 1 && cfg.terminalsContain(rhs[0])) {
        if (!expansions.containsKey(lhs)) {
          expansions.put(lhs, new ArrayList<>());
        }
        expansions.get(lhs).add(rhs);
      }
    }
    boolean change = true;
    while (change) {
      change = false;
      for (CfgProductionRule rule : cfg.getProductionRules()) {
        String lhs = rule.getLhs();
        String[] rhs = rule.getRhs();
        String lc = rhs[0];
        if (expansions.containsKey(lc)) {
          if (!expansions.containsKey(lhs)) {
            expansions.put(lhs, new ArrayList<>());
          }
          for (String[] expansion : expansions.get(lc)) {
            String[] newExpansion = ArrayUtils.concat(expansion,
                ArrayUtils.getSequenceWithoutIAsArray(rhs, 0));
            if (!ArrayUtils.contains(expansions.get(lhs), newExpansion)) {
              expansions.get(lhs).add(newExpansion);
              change = true;
            }
          }
        }
      }
    }
    return expansions;
  }
}

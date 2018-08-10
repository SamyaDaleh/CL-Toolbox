package com.github.samyadaleh.cltoolbox.common.cfg.util;

import java.util.ArrayList;

import com.github.samyadaleh.cltoolbox.common.cfg.Cfg;
import com.github.samyadaleh.cltoolbox.common.cfg.CfgProductionRule;

public class ChainRules {

  /** Returns an equivalent grammar without chain rules, that are rules of the
   * form A -> B. Remove epsilon productions beforehand. */
  public static Cfg removeChainRules(Cfg cfgold) {
    Cfg cfg = new Cfg();
    cfg.setTerminals(cfgold.getTerminals());
    cfg.setStartSymbol(cfgold.getStartSymbol());
    cfg.setNonterminals(cfgold.getNonterminals());
    for (CfgProductionRule rule : cfgold.getProductionRules()) {
      if (!(rule.getRhs().length == 1
        && cfgold.nonterminalsContain(rule.getRhs()[0]))) {
        cfg.getProductionRules().add(rule);
      }
    }
    ArrayList<String[]> unitPairs = getUnitPairs(cfgold);
    doRemoveChainRules(cfg, unitPairs, cfgold);
    return cfg;
  }

  /** Removes chain rules and for example S -> A all rhs of rules with A on the
   * left side are added as rules to S. */
  private static void doRemoveChainRules(Cfg cfg, ArrayList<String[]> unitPairs,
    Cfg cfgOld) {
    for (String[] unitPair : unitPairs) {
      for (CfgProductionRule rule : cfgOld.getProductionRules()) {
        if (!isChainRuleAndConcernOfUnitPair(unitPair, rule, cfg)) {
          continue;
        }
        boolean alreadyThere = false;
        for (CfgProductionRule rule2 : cfg.getProductionRules()) {
          if (rule.getLhs().equals(unitPair[0])
            && rule2.getRhs().length == rule.getRhs().length) {
            boolean alright = false;
            for (int i = 0; i < rule.getRhs().length; i++) {
              if (!rule.getRhs()[i].equals(rule2.getRhs()[i])) {
                alright = true;
              }
            }
            if (!alright) {
              alreadyThere = true;
            }
          }
        }
        if (!alreadyThere) {
          cfg.getProductionRules()
            .add(new CfgProductionRule(unitPair[0], rule.getRhs()));
        }
      }
    }
  }

  /** Returns true if rules is chain rule with a nonterminal as rhs and lhs is
   * second component of unit pair. */
  private static boolean isChainRuleAndConcernOfUnitPair(String[] unitPair,
    CfgProductionRule rule, Cfg cfg) {
    return !(rule.getRhs().length == 1
      && cfg.nonterminalsContain(rule.getRhs()[0]))
      && rule.getLhs().equals(unitPair[1]);
  }

  /** Get all unit pairs, that are pairs of nonterminals where the derivation A
   * =>* B is possible. */
  private static ArrayList<String[]> getUnitPairs(Cfg cfg) {
    ArrayList<String[]> unitPairs = new ArrayList<String[]>();
    for (String nt : cfg.getNonterminals()) {
      unitPairs.add(new String[] {nt, nt});
    }
    boolean changed = true;
    while (changed) {
      changed = false;
      for (CfgProductionRule rule : cfg.getProductionRules()) {
        if (rule.getRhs().length == 1
          && cfg.nonterminalsContain(rule.getRhs()[0])) {
          boolean found = false;
          for (String[] unitPair : unitPairs) {
            if (unitPair[0].equals(rule.getLhs())
              && unitPair[1].equals(rule.getRhs()[0])) {
              found = true;
              break;
            }
          }
          if (!found) {
            unitPairs.add(new String[] {rule.getLhs(), rule.getRhs()[0]});
            changed = true;
          }
        }
      }
    }
    return unitPairs;
  }

  /** Returns true if grammar has rules of the form A -> B. */
  public static boolean hasChainRules(Cfg cfg) {
    for (CfgProductionRule rule : cfg.getProductionRules()) {
      if (rule.getRhs().length == 1
        && cfg.nonterminalsContain(rule.getRhs()[0])) {
        return true;
      }
    }
    return false;
  }
}

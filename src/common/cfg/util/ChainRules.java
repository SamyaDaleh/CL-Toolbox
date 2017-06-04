package common.cfg.util;

import java.util.ArrayList;

import common.cfg.Cfg;
import common.cfg.CfgProductionRule;

public class ChainRules {

  /** Returns an equivalent grammar without chain rules, that are rules of the
   * form A -> B. Remove epsilon productions beforehand. */
  public static Cfg removeChainRules(Cfg cfgold) {
    Cfg cfg = new Cfg();
    cfg.setTerminals(cfgold.getTerminals());
    cfg.setStartsymbol(cfgold.getStartsymbol());
    cfg.setNonterminals(cfgold.getNonterminals());
    for (CfgProductionRule rule : cfgold.getProductionrules()) {
      if (!(rule.getRhs().length == 1
        && cfgold.nonterminalsContain(rule.getRhs()[0]))) {
        cfg.getProductionrules().add(rule);
      }
    }
    ArrayList<String[]> unitpairs = getUnitPairs(cfgold);
    doRemoveChainRules(cfg, unitpairs, cfgold);
    return cfg;
  }

  /** Removes chain rules and for example S -> A all rhs of rules with A on the
   * left side are added as rules to S. */
  private static void doRemoveChainRules(Cfg cfg, ArrayList<String[]> unitpairs,
    Cfg cfgold) {
    for (String[] unitpair : unitpairs) {
      for (CfgProductionRule rule : cfgold.getProductionrules()) {
        if (isChainRuleAndConcernOfUnitPair(unitpair, rule, cfg)) {
          boolean alreadythere = false;
          for (CfgProductionRule rule2 : cfg.getProductionrules()) {
            if (rule.getLhs().equals(unitpair[0])
              && rule2.getRhs().length == rule.getRhs().length) {
              boolean alright = false;
              for (int i = 0; i < rule.getRhs().length; i++) {
                if (!rule.getRhs()[i].equals(rule2.getRhs()[i])) {
                  alright = true;
                }
              }
              if (!alright) {
                alreadythere = true;
              }
            }
          }
          if (!alreadythere) {
            cfg.getProductionrules()
              .add(new CfgProductionRule(unitpair[0], rule.getRhs()));
          }
        }
      }
    }
  }

  /** Returns true if rules is chain rule with a nonterminal as rhs and lhs is
   * second component of unit pair. */
  private static boolean isChainRuleAndConcernOfUnitPair(String[] unitpair,
    CfgProductionRule rule, Cfg cfg) {
    return !(rule.getRhs().length == 1
      && cfg.nonterminalsContain(rule.getRhs()[0]))
      && rule.getLhs().equals(unitpair[1]);
  }

  /** Get all unit pairs, that are pairs of nonterminals where the derivation A
   * =>* B is possible. */
  private static ArrayList<String[]> getUnitPairs(Cfg cfg) {
    ArrayList<String[]> unitpairs = new ArrayList<String[]>();
    for (String nt : cfg.getNonterminals()) {
      unitpairs.add(new String[] {nt, nt});
    }
    boolean changed = true;
    while (changed) {
      changed = false;
      for (CfgProductionRule rule : cfg.getProductionrules()) {
        if (rule.getRhs().length == 1
          && cfg.nonterminalsContain(rule.getRhs()[0])) {
          boolean found = false;
          for (String[] unitpair : unitpairs) {
            if (unitpair[0].equals(rule.getLhs())
              && unitpair[1].equals(rule.getRhs()[0])) {
              found = true;
              break;
            }
          }
          if (!found) {
            unitpairs.add(new String[] {rule.getLhs(), rule.getRhs()[0]});
            changed = true;
          }
        }
      }
    }
    return unitpairs;
  }

  /** Returns true if grammar has rules of the form A -> B. */
  public static boolean hasChainRules(Cfg cfg) {
    for (CfgProductionRule rule : cfg.getProductionrules()) {
      if (rule.getRhs().length == 1
        && cfg.nonterminalsContain(rule.getRhs()[0])) {
        return true;
      }
    }
    return false;
  }
}

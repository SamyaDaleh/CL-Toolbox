package common.cfg.util;

import java.util.ArrayList;
import java.util.Collections;

import common.cfg.Cfg;
import common.cfg.CfgProductionRule;

public class LeftRecursion {

  /** Returns true if CFG has one rule with direct left recursion, of the form A
   * -> A.... Remove epsilon productions to make sure no indirect left recursion
   * is left. */
  public static boolean hasDirectLeftRecursion(Cfg cfg) {
    for (CfgProductionRule rule : cfg.getProductionRules()) {
      if (rule.getLhs().equals(rule.getRhs()[0])) {
        return true;
      }
    }
    return false;
  }

  /** Removes direct left recursion. S -> S is ignored. S -> S a | b are
   * replaced by S -> b S1, S1 -> a S1 | ε Adds empty productions to the grammar
   * and maybe chain rules. Remove empty productions first to make sure grammar
   * does not contain indirect left recursion. */
  public static Cfg removeLeftRecursion(Cfg cfgOld) {
    Cfg cfg = new Cfg();
    cfg.setTerminals(cfgOld.getTerminals());
    cfg.setStartSymbol(cfgOld.getStartSymbol());
    ArrayList<String> newNts = new ArrayList<String>();
    Collections.addAll(newNts, cfgOld.getNonterminals());
    for (String nt : cfgOld.getNonterminals()) {
      int i = 1;
      String newNt = nt + String.valueOf(i);
      i++;
      while (newNts.contains(newNt) || cfg.terminalsContain(newNt)) {
        newNt = nt + String.valueOf(i);
        i++;
      }
      newNts.add(newNt);
      cfg.getProductionRules()
        .add(new CfgProductionRule(newNt, new String[] {""}));
      doRemoveLeftRecursion(cfg, nt, newNt, cfgOld);
    }
    cfg.setNonterminals(newNts.toArray(new String[newNts.size()]));
    return cfg;
  }

  /** Actually replaces S -> S a | b by S -> b S1, S1 -> a S1 | ε where nt is the
   * old nonterminal and newnt is S1 in this example. */
  private static void doRemoveLeftRecursion(Cfg cfg, String nt, String newNt,
    Cfg cfgOld) {
    for (CfgProductionRule rule : cfgOld.getProductionRules()) {
      if (rule.getLhs().equals(nt)) {
        if (rule.getRhs()[0].equals(nt) && rule.getRhs().length > 1) {
          String[] newRhs = new String[rule.getRhs().length];
          System.arraycopy(rule.getRhs(), 1, newRhs, 0,
            rule.getRhs().length - 1);
          newRhs[newRhs.length - 1] = newNt;
          cfg.getProductionRules().add(new CfgProductionRule(nt, newRhs));
        } else if (!rule.getRhs()[0].equals(nt)) {
          if (rule.getRhs()[0].equals("")) {
            cfg.getProductionRules()
              .add(new CfgProductionRule(nt, new String[] {newNt}));
          } else {
            String[] newRhs = new String[rule.getRhs().length + 1];
            System.arraycopy(rule.getRhs(), 0, newRhs, 0, rule.getRhs().length);
            newRhs[newRhs.length - 1] = newNt;
            cfg.getProductionRules().add(new CfgProductionRule(nt, newRhs));
          }
        }
      }
    }
  }
}

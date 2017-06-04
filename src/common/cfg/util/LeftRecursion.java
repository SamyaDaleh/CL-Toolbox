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
    for (CfgProductionRule rule : cfg.getProductionrules()) {
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
  public static Cfg removeLeftRecursion(Cfg cfgold) {
    Cfg cfg = new Cfg();
    cfg.setTerminals(cfgold.getTerminals());
    cfg.setStartsymbol(cfgold.getStartsymbol());
    ArrayList<String> newnts = new ArrayList<String>();
    Collections.addAll(newnts, cfgold.getNonterminals());
    for (String nt : cfgold.getNonterminals()) {
      int i = 1;
      String newnt = nt + String.valueOf(i);
      i++;
      while (newnts.contains(newnt)) {
        newnt = nt + String.valueOf(i);
        i++;
      }
      newnts.add(newnt);
      cfg.getProductionrules()
        .add(new CfgProductionRule(newnt, new String[] {""}));
      doRemoveLeftRecursion(cfg, nt, newnt, cfgold);
    }
    cfg.setNonterminals(newnts.toArray(new String[newnts.size()]));
    return cfg;
  }

  /** Actually replaces S -> S a | b by S -> b S1, S1 -> a S1 where nt is the
   * old nonterminal and newnt is S1 in this example. */
  private static void doRemoveLeftRecursion(Cfg cfg, String nt, String newnt,
    Cfg cfgold) {
    for (CfgProductionRule rule : cfgold.getProductionrules()) {
      if (rule.getLhs().equals(nt)) {
        if (rule.getRhs()[0].equals(nt) && rule.getRhs().length > 1) {
          String[] newrhs = new String[rule.getRhs().length];
          System.arraycopy(rule.getRhs(), 1, newrhs, 0,
            rule.getRhs().length - 1);
          newrhs[newrhs.length - 1] = newnt;
          cfg.getProductionrules().add(new CfgProductionRule(nt, newrhs));
        } else if (!rule.getRhs()[0].equals(nt)) {
          if (rule.getRhs()[0].equals("")) {
            cfg.getProductionrules()
              .add(new CfgProductionRule(nt, new String[] {newnt}));
          } else {
            String[] newrhs = new String[rule.getRhs().length + 1];
            System.arraycopy(rule.getRhs(), 0, newrhs, 0, rule.getRhs().length);
            newrhs[newrhs.length - 1] = newnt;
            cfg.getProductionrules().add(new CfgProductionRule(nt, newrhs));
          }
        }
      }
    }
  }
}

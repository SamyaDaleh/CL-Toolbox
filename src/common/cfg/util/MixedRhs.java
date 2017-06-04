package common.cfg.util;

import java.util.ArrayList;
import java.util.Collections;

import common.cfg.Cfg;
import common.cfg.CfgProductionRule;

public class MixedRhs {

  /** Returns true if there is at least one production rule that contains
   * terminals and nonterminals as rhs symbols. */
  public static boolean hasMixedRhs(Cfg cfg) {
    for (CfgProductionRule rule : cfg.getProductionrules()) {
      for (int i = 1; i < rule.getRhs().length; i++) {
        if (ifSymbolIsOneKindAndPreviousSymbolIsAnother(rule, i, cfg)) {
          return true;
        }
      }
    }
    return false;
  }

  /** returns true if current symbol is nonterminal and previous one is terminal
   * or vice versa. */
  private static boolean ifSymbolIsOneKindAndPreviousSymbolIsAnother(
    CfgProductionRule rule, int i, Cfg cfgold) {
    return (cfgold.terminalsContain(rule.getRhs()[i - 1])
      && cfgold.nonterminalsContain(rule.getRhs()[i]))
      || (cfgold.terminalsContain(rule.getRhs()[i])
        && cfgold.nonterminalsContain(rule.getRhs()[i - 1]));
  }

  /** Returns a new grammar where in all rhs > 1 terminals are replaced by
   * nonterminals and new rules A -> a are added. */
  public static Cfg replaceTerminals(Cfg cfgold) {
    Cfg cfg = new Cfg();
    cfg.setStartsymbol(cfgold.getStartsymbol());
    cfg.setTerminals(cfgold.getTerminals());
    ArrayList<String[]> newtrules = new ArrayList<String[]>();
    ArrayList<String> newnt = new ArrayList<String>();
    Collections.addAll(newnt, cfgold.getNonterminals());
    doReplaceTerminals(cfg, newtrules, newnt, cfgold);
    cfg.setNonterminals(newnt.toArray(new String[newnt.size()]));
    return cfg;
  }

  /** In rhs > 1 all terminals are replaced by a new nonterminal, new rules X1
   * -> a are added. Adds only one new nonterminal for each terminal. */
  private static void doReplaceTerminals(Cfg cfg, ArrayList<String[]> newtrules,
    ArrayList<String> newnt, Cfg cfgold) {
    int i = 1;
    for (CfgProductionRule rule : cfgold.getProductionrules()) {
      if (rule.getRhs().length == 1) {
        cfg.getProductionrules().add(rule);
      } else {
        ArrayList<String> newrhs = new ArrayList<String>();
        for (String sym : rule.getRhs()) {
          if (cfgold.nonterminalsContain(sym)) {
            newrhs.add(sym);
          } else {
            String newlhs = null;
            for (String[] tryrule : newtrules) {
              if (tryrule[1].equals(sym)) {
                newlhs = tryrule[0];
              }
            }
            boolean isnew = false;
            if (newlhs == null) {
              newlhs = "Y" + String.valueOf(i);
              i++;
              isnew = true;
              cfg.getProductionrules()
                .add(new CfgProductionRule(newlhs, new String[] {sym}));
            }
            while (cfgold.nonterminalsContain(newlhs)) {
              newlhs = "Y" + String.valueOf(i);
              i++;
            }
            if (isnew) {
              newnt.add(newlhs);
              newtrules.add(new String[] {newlhs, sym});
            }
            newrhs.add(newlhs);
          }
        }
        cfg.getProductionrules().add(new CfgProductionRule(rule.getLhs(),
          newrhs.toArray(new String[newrhs.size()])));
      }
    }
  }
}

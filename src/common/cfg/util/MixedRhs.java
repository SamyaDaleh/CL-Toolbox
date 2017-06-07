package common.cfg.util;

import java.util.ArrayList;
import java.util.Collections;

import common.cfg.Cfg;
import common.cfg.CfgProductionRule;

public class MixedRhs {

  /** Returns true if there is at least one production rule that contains
   * terminals and nonterminals as rhs symbols. */
  public static boolean hasMixedRhs(Cfg cfg) {
    for (CfgProductionRule rule : cfg.getProductionRules()) {
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
  public static Cfg replaceTerminals(Cfg cfgOld) {
    Cfg cfg = new Cfg();
    cfg.setStartSymbol(cfgOld.getStartSymbol());
    cfg.setTerminals(cfgOld.getTerminals());
    ArrayList<String[]> newTRules = new ArrayList<String[]>();
    ArrayList<String> newNt = new ArrayList<String>();
    Collections.addAll(newNt, cfgOld.getNonterminals());
    doReplaceTerminals(cfg, newTRules, newNt, cfgOld);
    cfg.setNonterminals(newNt.toArray(new String[newNt.size()]));
    return cfg;
  }

  /** In rhs > 1 all terminals are replaced by a new nonterminal, new rules X1
   * -> a are added. Adds only one new nonterminal for each terminal. */
  private static void doReplaceTerminals(Cfg cfg, ArrayList<String[]> newTRules,
    ArrayList<String> newNt, Cfg cfgOld) {
    int i = 1;
    for (CfgProductionRule rule : cfgOld.getProductionRules()) {
      if (rule.getRhs().length == 1) {
        cfg.getProductionRules().add(rule);
      } else {
        ArrayList<String> newRhs = new ArrayList<String>();
        for (String sym : rule.getRhs()) {
          if (cfgOld.nonterminalsContain(sym)) {
            newRhs.add(sym);
          } else {
            String newLhs = null;
            for (String[] tryRule : newTRules) {
              if (tryRule[1].equals(sym)) {
                newLhs = tryRule[0];
              }
            }
            boolean isNew = false;
            if (newLhs == null) {
              newLhs = "Y" + String.valueOf(i);
              i++;
              isNew = true;
              cfg.getProductionRules()
                .add(new CfgProductionRule(newLhs, new String[] {sym}));
            }
            while (cfgOld.nonterminalsContain(newLhs)) {
              newLhs = "Y" + String.valueOf(i);
              i++;
            }
            if (isNew) {
              newNt.add(newLhs);
              newTRules.add(new String[] {newLhs, sym});
            }
            newRhs.add(newLhs);
          }
        }
        cfg.getProductionRules().add(new CfgProductionRule(rule.getLhs(),
          newRhs.toArray(new String[newRhs.size()])));
      }
    }
  }
}

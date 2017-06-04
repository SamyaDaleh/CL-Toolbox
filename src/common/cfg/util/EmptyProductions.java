package common.cfg.util;

import java.util.ArrayList;
import java.util.Collections;

import common.ArrayUtils;
import common.cfg.Cfg;
import common.cfg.CfgProductionRule;

public class EmptyProductions {

  /** Returns true if there is at least one rule with an empty right side,
   * except it's a start symbol rule and the start symbol never occurs on any
   * rhs. */
  public static boolean hasEpsilonProductions(Cfg cfg) {
    for (CfgProductionRule rule : cfg.getProductionrules()) {
      if (rule.getRhs().length == 1 && rule.getRhs()[0].equals("")) {
        if (rule.getLhs().equals(cfg.getStartsymbol())) {

          for (CfgProductionRule rule2 : cfg.getProductionrules()) {
            String[] rhs = rule2.getRhs();
            for (String symbol : rhs) {
              if (symbol.equals(cfg.getStartsymbol())) {
                return true;
              }
            }
          }
        } else {
          return true;
        }
      }
    }
    return false;
  }

  /** Returns an equivalent CFG without empty productions, only S -> ε is
   * allowed in which case it is removed from all rhs'. May leaves non
   * generating symbols behind. */
  public static Cfg removeEmptyProductions(Cfg cfgold) {
    Cfg cfg = new Cfg();
    cfg.setTerminals(cfgold.getTerminals());
    cfg.setStartsymbol(cfgold.getStartsymbol());

    ArrayList<String> newnt = new ArrayList<String>();
    Collections.addAll(newnt, cfgold.getNonterminals());
    cfg.getProductionrules().addAll(cfgold.getProductionrules());
    ArrayList<String> eliminateable = getEliminateable(cfgold);
    doEliminateEmptyProductions(cfg, newnt, eliminateable, cfgold);
    for (int i = cfg.getProductionrules().size() - 1; i >= 0; i--) {
      if (ifEmptyProductionShouldBeRemoved(cfg, i)) {
        cfg.getProductionrules().remove(i);
      }
    }

    cfg.setNonterminals(newnt.toArray(new String[newnt.size()]));
    return cfg;
  }

  /** Returns true if production derives to the empty string and doesn't have
   * the start symbol as lhs. */
  private static boolean ifEmptyProductionShouldBeRemoved(Cfg cfg, int i) {
    return cfg.getProductionrules().get(i).getRhs().length == 1
      && cfg.getProductionrules().get(i).getRhs()[0].equals("")
      && !cfg.getProductionrules().get(i).getLhs().equals(cfg.getStartsymbol());
  }

  /** Removes all empty productions except if epsilon can be derived from the
   * start symbol, in which case if the start symbol appears in a rhs, a new
   * start symbol is added. */
  private static void doEliminateEmptyProductions(Cfg cfg, ArrayList<String> newnt,
    ArrayList<String> eliminateable, Cfg cfgold) {
    for (String nt : eliminateable) {
      for (int j = 0; j < cfg.getProductionrules().size(); j++) {
        CfgProductionRule rule = cfg.getProductionrules().get(j);
        for (int i = 0; i < rule.getRhs().length; i++) {
          if (rule.getRhs()[i].equals(nt)) {
            cfg.getProductionrules().add(new CfgProductionRule(rule.getLhs(),
              ArrayUtils.getSequenceWithoutIAsArray(rule.getRhs(), i)));
          }
        }
      }
      if (nt.equals(cfgold.getStartsymbol())) {
        int i = 1;
        String newstart = "S" + String.valueOf(i);
        while (newnt.contains(newstart)) {
          i++;
          newstart = "S" + String.valueOf(i);
        }
        cfg.getProductionrules().add(new CfgProductionRule(
          new String[] {newstart, cfgold.getStartsymbol()}));
        cfg.getProductionrules()
          .add(new CfgProductionRule(new String[] {newstart, ""}));
        newnt.add(newstart);
        cfg.setStartsymbol(newstart);
      }
    }
  }

  /** Gets all nonterminals where a derivation =>* ε is possible. */
  private static ArrayList<String> getEliminateable(Cfg cfg) {
    ArrayList<String> eliminateable = new ArrayList<String>();
    boolean changed = true;
    while (changed) {
      changed = false;
      for (CfgProductionRule rule : cfg.getProductionrules()) {
        if (rule.getRhs().length == 1 && rule.getRhs()[0].equals("")
          && !eliminateable.contains(rule.getLhs())) {
          eliminateable.add(rule.getLhs());
          changed = true;
        }
      }
    }
    return eliminateable;
  }
}

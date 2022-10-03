package com.github.samyadaleh.cltoolbox.common.cfg.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.github.samyadaleh.cltoolbox.common.ArrayUtils;
import com.github.samyadaleh.cltoolbox.common.cfg.Cfg;
import com.github.samyadaleh.cltoolbox.common.cfg.CfgProductionRule;

public class EmptyProductions {

  /**
   * Returns true if there is at least one rule with an empty right side, except
   * it's a start symbol rule and the start symbol never occurs on any rhs.
   */
  public static boolean hasEpsilonProductions(Cfg cfg) {
    for (CfgProductionRule rule : cfg.getProductionRules()) {
      if (isEmptyProductionRule(cfg, rule)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Returns true if this production rule has an empty rhs except of the lhs
   * is the start symbol that occurs on no rhs.
   */
  public static boolean isEmptyProductionRule(
      Cfg cfg, CfgProductionRule rule) {
    if (!rule.getRhs()[0].equals("")) {
      return false;
    }
    String nt = rule.getLhs();
    if (nt.equals(cfg.getStartSymbol())) {
      return nonterminalOccursInAnyRhs(cfg, nt);
    } else {
      return true;
    }
  }

  public static boolean nonterminalOccursInAnyRhs(Cfg cfg, String nt) {
    for (CfgProductionRule rule2 : cfg.getProductionRules()) {
      String[] rhs = rule2.getRhs();
      for (String symbol : rhs) {
        if (symbol.equals(nt)) {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * Returns an equivalent CFG without empty productions, only S -> ε is allowed
   * in which case it is removed from all rhs'. May leaves non generating
   * symbols behind.
   */
  public static Cfg removeEmptyProductions(Cfg cfgOld) {
    Cfg cfg = new Cfg();
    cfg.setTerminals(cfgOld.getTerminals());
    cfg.setStartSymbol(cfgOld.getStartSymbol());

    ArrayList<String> newNt = new ArrayList<>();
    Collections.addAll(newNt, cfgOld.getNonterminals());
    cfg.getProductionRules().addAll(cfgOld.getProductionRules());
    List<String> eliminateable = getEliminateable(cfgOld);
    doEliminateEmptyProductions(cfg, newNt, eliminateable, cfgOld);
    for (int i = cfg.getProductionRules().size() - 1; i >= 0; i--) {
      if (ifEmptyProductionShouldBeRemoved(cfg, i)) {
        cfg.getProductionRules().remove(i);
      }
    }

    cfg.setNonterminals(newNt.toArray(new String[0]));
    return cfg;
  }

  /**
   * Returns true if production derives to the empty string and doesn't have the
   * start symbol as lhs.
   */
  private static boolean ifEmptyProductionShouldBeRemoved(Cfg cfg, int i) {
    return (cfg.getProductionRules().get(i).getRhs().length == 1 && cfg
        .getProductionRules().get(i).getRhs()[0].equals("")
        || cfg.getProductionRules().get(i).getRhs().length == 0) && !cfg
        .getProductionRules().get(i).getLhs().equals(cfg.getStartSymbol());
  }

  /**
   * Removes all empty productions except if epsilon can be derived from the
   * start symbol, in which case if the start symbol appears in a rhs, a new
   * start symbol is added.
   */
  private static void doEliminateEmptyProductions(Cfg cfg, List<String> newNt,
    List<String> eliminateable, Cfg cfgOld) {
    for (String nt : eliminateable) {
      for (int j = 0; j < cfg.getProductionRules().size(); j++) {
        CfgProductionRule rule = cfg.getProductionRules().get(j);
        for (int i = 0; i < rule.getRhs().length; i++) {
          if (rule.getRhs()[i].equals(nt)) {
            cfg.getProductionRules().add(new CfgProductionRule(rule.getLhs(),
              ArrayUtils.getSequenceWithoutIAsArray(rule.getRhs(), i)));
          }
        }
      }
      if (nt.equals(cfgOld.getStartSymbol())) {
        int i = 1;
        String newStart = "S" + i;
        while (newNt.contains(newStart) || cfgOld.terminalsContain(newStart)) {
          i++;
          newStart = "S" + i;
        }
        cfg.getProductionRules().add(new CfgProductionRule(
          new String[] {newStart, cfgOld.getStartSymbol()}));
        cfg.getProductionRules()
          .add(new CfgProductionRule(new String[] {newStart, ""}));
        newNt.add(newStart);
        cfg.setStartSymbol(newStart);
      }
    }
  }

  /** Gets all nonterminals where a derivation =>* ε is possible. */
  static List<String> getEliminateable(Cfg cfg) {
    ArrayList<String> eliminateable = new ArrayList<>();
    boolean changed = true;
    while (changed) {
      changed = false;
      for (CfgProductionRule rule : cfg.getProductionRules()) {
        if (allRhsSymbolsAreEliminateable(rule.getRhs(), eliminateable)
            && !eliminateable.contains(rule.getLhs())) {
          eliminateable.add(rule.getLhs());
          changed = true;
        }
      }
    }
    return eliminateable;
  }

  private static boolean allRhsSymbolsAreEliminateable(String[] rhs,
      ArrayList<String> eliminateable) {
    if (rhs.length == 0 || "".equals(rhs[0])) {
      return true;
    }
    for (String rh : rhs) {
      if (!eliminateable.contains(rh)) {
        return false;
      }
    }
    return true;
  }
}

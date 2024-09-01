package com.github.samyadaleh.cltoolbox.common.cfg.util;

import java.util.ArrayList;

import com.github.samyadaleh.cltoolbox.common.cfg.Cfg;
import com.github.samyadaleh.cltoolbox.common.cfg.CfgProductionRule;

public class UselessSymbols {

  /** Returns an equivalent grammar without non-generating symbols. Call this
   * before removing non-reachable symbols. */
  public static Cfg removeNonGeneratingSymbols(Cfg cfgOld) {
    Cfg cfg = new Cfg();
    ArrayList<String> generating = new ArrayList<>(cfgOld.getTerminals());
    getGeneratingSymbols(generating, cfgOld);
    cfg.setTerminals(cfgOld.getTerminals());
    cfg.setStartSymbol(cfgOld.getStartSymbol());
    ArrayList<String> restNts = new ArrayList<>();
    for (String symbol : generating) {
      if (cfgOld.nonterminalsContain(symbol)) {
        restNts.add(symbol);
      }
    }
    if (restNts.isEmpty()) {
      System.err.println("Input grammar has no generating symbols.");
      return null;
    }
    cfg.setNonterminals(restNts);
    for (CfgProductionRule rule : cfgOld.getProductionRules()) {
      boolean notGeneratingSeen = false;
      for (String symbol : rule.getRhs()) {
        if (!generating.contains(symbol) && !"".equals(symbol)) {
          notGeneratingSeen = true;
          break;
        }
      }
      if (!notGeneratingSeen && generating.contains(rule.getLhs())) {
        cfg.getProductionRules().add(rule);
      }
    }
    return cfg;
  }

  /** Returns all symbols where strings only containing terminals can be derived
   * from. */
  private static void getGeneratingSymbols(ArrayList<String> generating,
    Cfg cfgOld) {
    boolean changed = true;
    addEpsilonProductions(generating, cfgOld);
    while (changed) {
      changed = false;
      for (CfgProductionRule rule : cfgOld.getProductionRules()) {
        boolean notGeneratingSeen = false;
        for (String symbol : rule.getRhs()) {
          if (!generating.contains(symbol)) {
            notGeneratingSeen = true;
            break;
          }
        }
        if (!notGeneratingSeen && !generating.contains(rule.getLhs())) {
          changed = true;
          generating.add(rule.getLhs());
        }
      }
    }
  }

  private static void addEpsilonProductions(ArrayList<String> generating,
      Cfg cfgOld) {
    for (CfgProductionRule rule : cfgOld.getProductionRules()) {
      if (rule.getRhs().length == 0 || "".equals(rule.getRhs()[0])) {
        generating.add(rule.getLhs());
      }
    }
  }

  /** Returns an equivalent grammar without non-reachable symbols. Before
   * calling this, remove all non-generating symbols. */
  public static Cfg removeNonReachableSymbols(Cfg cfgOld) {
    Cfg cfg = new Cfg();
    ArrayList<String> reachable = new ArrayList<>();
    reachable.add(cfgOld.getStartSymbol());
    boolean changed = true;
    while (changed) {
      changed = false;
      for (CfgProductionRule rule : cfgOld.getProductionRules()) {
        if (!reachable.contains(rule.getLhs())) {
          continue;
        }
        for (String symbol : rule.getRhs()) {
          if (!reachable.contains(symbol)) {
            reachable.add(symbol);
            changed = true;
          }
        }
      }
    }
    cfg.setStartSymbol(cfgOld.getStartSymbol());
    ArrayList<String> newNts = new ArrayList<>();
    for (String nt : cfgOld.getNonterminals()) {
      if (reachable.contains(nt)) {
        newNts.add(nt);
      }
    }
    ArrayList<String> newTerms = new ArrayList<>();
    for (String t : cfgOld.getTerminals()) {
      if (reachable.contains(t)) {
        newTerms.add(t);
      }
    }
    cfg.setNonterminals(newNts);
    cfg.setTerminals(newTerms);
    for (CfgProductionRule rule : cfgOld.getProductionRules()) {
      if (reachable.contains(rule.getLhs())) {
        cfg.getProductionRules().add(rule);
      }
    }
    return cfg;
  }

  /**
   * Returns true if grammar has generating nonterminals.
   */
  public static boolean hasGeneratingSymbols(Cfg cfg) {
    ArrayList<String> generating = new ArrayList<>(cfg.getTerminals());
    getGeneratingSymbols(generating, cfg);
    return !cfg.getTerminals().isEmpty()
      && generating.size() - cfg.getTerminals().size() > 0;
  }
}

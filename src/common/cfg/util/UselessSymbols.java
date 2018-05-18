package common.cfg.util;

import java.util.ArrayList;
import java.util.Collections;

import common.cfg.Cfg;
import common.cfg.CfgProductionRule;

public class UselessSymbols {

  /** Returns an equivalent grammar without non-generating symbols. Call this
   * before removing non-reachable symbols. */
  public static Cfg removeNonGeneratingSymbols(Cfg cfgOld) {
    Cfg cfg = new Cfg();
    ArrayList<String> generating = new ArrayList<String>();
    Collections.addAll(generating, cfgOld.getTerminals());
    getGeneratingSymbols(generating, cfgOld);
    cfg.setTerminals(cfgOld.getTerminals());
    cfg.setStartSymbol(cfgOld.getStartSymbol());
    ArrayList<String> restNts = new ArrayList<String>();
    for (String symbol : generating) {
      if (cfgOld.nonterminalsContain(symbol)) {
        restNts.add(symbol);
      }
    }
    if (restNts.isEmpty()) {
      System.err.println("Input grammar has no generating symbols.");
      return null;
    }
    cfg.setNonterminals(restNts.toArray(new String[restNts.size()]));
    for (CfgProductionRule rule : cfgOld.getProductionRules()) {
      boolean notGeneratingSeen = false;
      for (String symbol : rule.getRhs()) {
        if (!generating.contains(symbol)) {
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

  /** Returns an equivalent grammar without non-reachable symbols. Before
   * calling this, remove all non-generating symbols. */
  public static Cfg removeNonReachableSymbols(Cfg cfgOld) {
    Cfg cfg = new Cfg();
    ArrayList<String> reachable = new ArrayList<String>();
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
    ArrayList<String> newNts = new ArrayList<String>();
    for (String nt : cfgOld.getNonterminals()) {
      if (reachable.contains(nt)) {
        newNts.add(nt);
      }
    }
    ArrayList<String> newTerms = new ArrayList<String>();
    for (String t : cfgOld.getTerminals()) {
      if (reachable.contains(t)) {
        newTerms.add(t);
      }
    }
    cfg.setNonterminals(newNts.toArray(new String[newNts.size()]));
    cfg.setTerminals(newTerms.toArray(new String[newTerms.size()]));
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
    ArrayList<String> generating = new ArrayList<String>();
    Collections.addAll(generating, cfg.getTerminals());
    getGeneratingSymbols(generating, cfg);
    return cfg.getTerminals().length > 0
      && generating.size() - cfg.getTerminals().length > 0;
  }
}

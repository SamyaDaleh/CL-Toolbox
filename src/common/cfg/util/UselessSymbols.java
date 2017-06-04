package common.cfg.util;

import java.util.ArrayList;
import java.util.Collections;

import common.cfg.Cfg;
import common.cfg.CfgProductionRule;

public class UselessSymbols {

  /** Returns an equivalent grammar without non-generating symbols. Call this
   * before removing non-reachable symbols. */
  public static Cfg removeNonGeneratingSymbols(Cfg cfgold) {
    Cfg cfg = new Cfg();
    ArrayList<String> generating = new ArrayList<String>();
    Collections.addAll(generating, cfgold.getTerminals());
    getGeneratingSymbols(generating, cfgold);
    cfg.setTerminals(cfgold.getTerminals());
    cfg.setStartsymbol(cfgold.getStartsymbol());
    ArrayList<String> restnts = new ArrayList<String>();
    for (String symbol : generating) {
      if (cfgold.nonterminalsContain(symbol)) {
        restnts.add(symbol);
      }
    }
    cfg.setNonterminals(restnts.toArray(new String[restnts.size()]));
    for (CfgProductionRule rule : cfgold.getProductionrules()) {
      boolean notgeneratingseen = false;
      for (String symbol : rule.getRhs()) {
        if (!generating.contains(symbol)) {
          notgeneratingseen = true;
          break;
        }
      }
      if (!notgeneratingseen && generating.contains(rule.getLhs())) {
        cfg.getProductionrules().add(rule);
      }
    }
    return cfg;
  }

  /** Returns all symbols where strings only containing terinals can be derived
   * from. */
  private static void getGeneratingSymbols(ArrayList<String> generating,
    Cfg cfgold) {
    boolean changed = true;
    while (changed) {
      changed = false;
      for (CfgProductionRule rule : cfgold.getProductionrules()) {
        boolean notgeneratingseen = false;
        for (String symbol : rule.getRhs()) {
          if (!generating.contains(symbol)) {
            notgeneratingseen = true;
            break;
          }
        }
        if (!notgeneratingseen && !generating.contains(rule.getLhs())) {
          changed = true;
          generating.add(rule.getLhs());
        }
      }
    }
  }

  /** Returns an equivalent grammar without non-reachable symbols. Before
   * calling this, remove all non-generating symbols. */
  public static Cfg removeNonReachableSymbols(Cfg cfgold) {
    Cfg cfg = new Cfg();
    ArrayList<String> reachable = new ArrayList<String>();
    reachable.add(cfgold.getStartsymbol());
    boolean changed = true;
    while (changed) {
      changed = false;
      for (CfgProductionRule rule : cfgold.getProductionrules()) {
        if (reachable.contains(rule.getLhs())) {
          for (String symbol : rule.getRhs()) {
            if (!reachable.contains(symbol)) {
              reachable.add(symbol);
              changed = true;
            }
          }
        }
      }
    }
    cfg.setStartsymbol(cfgold.getStartsymbol());
    ArrayList<String> newvars = new ArrayList<String>();
    for (String nt : cfgold.getNonterminals()) {
      if (reachable.contains(nt)) {
        newvars.add(nt);
      }
    }
    ArrayList<String> newterms = new ArrayList<String>();
    for (String t : cfgold.getTerminals()) {
      if (reachable.contains(t)) {
        newterms.add(t);
      }
    }
    cfg.setNonterminals(newvars.toArray(new String[newvars.size()]));
    cfg.setTerminals(newterms.toArray(new String[newterms.size()]));
    for (CfgProductionRule rule : cfgold.getProductionrules()) {
      if (reachable.contains(rule.getLhs())) {
        cfg.getProductionrules().add(rule);
      }
    }
    return cfg;
  }
}

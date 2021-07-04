package com.github.samyadaleh.cltoolbox.common.cfg.util;

import com.github.samyadaleh.cltoolbox.common.cfg.Cfg;
import com.github.samyadaleh.cltoolbox.common.cfg.CfgProductionRule;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Loops {

  public static Cfg getCfgWithoutLoops(Cfg cfgOld) {
    Cfg cfg = new Cfg();
    List<String> currentNonterminals =
        new ArrayList<>(Arrays.asList(cfgOld.getNonterminals()));
    cfg.setTerminals(cfgOld.getTerminals());
    List<CfgProductionRule> workingProductionRules = cfgOld.getProductionRules();
    cfg.setStartSymbol(cfgOld.getStartSymbol());
    List<String> removeNts = new ArrayList<>();
    for (String nt : cfgOld.getNonterminals()) {
      if (removeNts.contains(nt)) {
        continue;
      }
      List<String> loopNts = getLoopMembers(cfgOld, nt);
      removeNts.addAll(loopNts);
      if (loopNts.size() > 1) {
        String replaceNt = getNewNt(currentNonterminals);
        currentNonterminals.add(replaceNt);
        for (int j = workingProductionRules.size() - 1; j >= 0; j--) {
          CfgProductionRule currentRule = workingProductionRules.get(j);
          StringBuilder replaceRule = new StringBuilder();
          if (loopNts.contains(currentRule.getLhs())){
            replaceRule.append(replaceNt);
          } else {
            replaceRule.append(currentRule.getLhs());
          }
          replaceRule.append(" ->");
          for (String rhsSym : currentRule.getRhs()) {
            replaceRule.append(" ");
            if (loopNts.contains(rhsSym)) {
              replaceRule.append(replaceNt);
            } else {
              replaceRule.append(rhsSym);
            }
          }
          try {
            if (!replaceRule.toString().equals(replaceNt + " -> " + replaceNt)) {
              workingProductionRules.set(j, new CfgProductionRule(replaceRule.toString()));
            } else {
              workingProductionRules.remove(j);
            }
          } catch (ParseException e) {
            // should never happen
            throw new RuntimeException(e);
          }
          for (String loopNt : loopNts) {
            currentNonterminals.remove(loopNt);
            if (loopNt.equals(cfg.getStartSymbol())) {
              cfg.setStartSymbol(replaceNt);
            }
          }
        }
      }
    }
    for (CfgProductionRule rule : workingProductionRules) {
      cfg.getProductionRules().add(rule);
    }
    cfg.setNonterminals(currentNonterminals.toArray(new String[0]));
    return cfg;
  }

  private static String getNewNt(List<String> currentNonterminals) {
    String newNt;
    int i = 0;
    do {
      i++;
      newNt = "N" + i;
    } while (currentNonterminals.contains(newNt));
    return newNt;

  }

  private static List<String> getLoopMembers(Cfg cfg, String nt) {
    List<String> loopMembers = new ArrayList<>();
    loopMembers.add(nt);
    boolean changed = true;
    while (changed) {
      changed = false;
      for (CfgProductionRule rule : cfg.getProductionRules()) {
        if (loopMembers.contains(rule.getLhs()) && rule.getRhs().length == 1
            && !loopMembers.contains(rule.getRhs()[0])
            && cfg.nonterminalsContain(rule.getRhs()[0])) {
          loopMembers.add(rule.getRhs()[0]);
          changed = true;
        }
      }
    }
    return loopMembers;
  }
}

package com.github.samyadaleh.cltoolbox.common.cfg.util;

import com.github.samyadaleh.cltoolbox.common.cfg.Cfg;
import com.github.samyadaleh.cltoolbox.common.cfg.CfgProductionRule;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.github.samyadaleh.cltoolbox.common.Constants.ARROW_RIGHT;

public class Loops {

  public static Cfg getCfgWithoutLoops(Cfg cfgOld) {
    Cfg cfg = new Cfg();
    List<String> currentNonterminals =
        new ArrayList<>(cfgOld.getNonterminals());
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
          replaceRule.append(" ").append(ARROW_RIGHT);
          for (String rhsSym : currentRule.getRhs()) {
            replaceRule.append(" ");
            if (loopNts.contains(rhsSym)) {
              replaceRule.append(replaceNt);
            } else {
              replaceRule.append(rhsSym);
            }
          }
          try {
            if (!replaceRule.toString().equals(replaceNt + " " + ARROW_RIGHT + " " + replaceNt)) {
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
    cfg.setNonterminals(currentNonterminals);
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
    return getLoopMembers(cfg, nt, nt, Collections.singletonList(nt));
  }

  private static List<String> getLoopMembers(
      Cfg cfg, String ntStart, String ntLhs, List<String> loopCandidates) {
    List<String> loopMembers = new ArrayList<>();
    for (CfgProductionRule rule : cfg.getProductionRules()) {
      String lc = rule.getRhs()[0];
      if (rule.getRhs().length == 1 && rule.getLhs().equals(ntLhs) && cfg
          .nonterminalsContain(lc) && !lc
          .equals(ntLhs)) {
        if (lc.equals(ntStart)) {
          loopMembers.addAll(loopCandidates);
        } else {
          if (!loopCandidates.contains(lc)) {
            List<String> newLoopCandidates = new ArrayList<>(loopCandidates);
            newLoopCandidates.add(lc);
            loopMembers.addAll(
                getLoopMembers(cfg, ntStart, lc, newLoopCandidates));
          }
        }
      }
    }
    return loopMembers;
  }
}

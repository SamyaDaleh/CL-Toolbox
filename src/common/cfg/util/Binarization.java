package common.cfg.util;

import java.util.ArrayList;
import java.util.Collections;

import common.ArrayUtils;
import common.cfg.Cfg;
import common.cfg.CfgProductionRule;

public class Binarization {

  /** Returns an equivalent CFG where all rhs' have at most length 2. */
  public static Cfg binarize(Cfg cfg) {
    Cfg newCfg = new Cfg();
    newCfg.setTerminals(cfg.getTerminals());
    newCfg.setStartSymbol(cfg.getStartSymbol());
    ArrayList<String> newNt = new ArrayList<String>();
    Collections.addAll(newNt, cfg.getNonterminals());
    ArrayList<String[]> newP = new ArrayList<String[]>();
    doBinarize(newNt, newP, cfg);
    newCfg.setNonterminals(newNt.toArray(new String[newNt.size()]));
    for (String[] newRule : newP) {
      newCfg.getProductionRules()
        .add(new CfgProductionRule(newRule[0], newRule[1].split(" ")));
    }
    return newCfg;
  }

  /** Splits production rules with rhs length > 2 and replaces overlong part by
   * new nonterminals. */
  private static void doBinarize(ArrayList<String> newNt,
    ArrayList<String[]> newP, Cfg cfg) {
    int i = 1;
    for (CfgProductionRule rule : cfg.getProductionRules()) {
      if (rule.getRhs().length > 2) {
        CfgProductionRule ruleRest = rule;
        while (ruleRest.getRhs().length > 2) {
          String newN = "X" + String.valueOf(i);
          while (cfg.nonterminalsContain(newN) || cfg.terminalsContain(newN)) {
            i++;
            newN = "X" + String.valueOf(i);
          }
          newNt.add(newN);
          String newRhs = ruleRest.getRhs()[0] + " " + newN;
          String[] newRule = new String[] {ruleRest.getLhs(), newRhs};
          newP.add(newRule);
          i++;
          ruleRest =
            new CfgProductionRule(newN, ArrayUtils.getSubSequenceAsArray(
              ruleRest.getRhs(), 1, ruleRest.getRhs().length));
        }
        newP.add(new String[] {ruleRest.getLhs(),
          ruleRest.getRhs()[0] + " " + ruleRest.getRhs()[1]});
      } else if (rule.getRhs().length == 2) {
        newP.add(new String[] {rule.getLhs(),
          rule.getRhs()[0] + " " + rule.getRhs()[1]});
      }
      if (rule.getRhs().length == 1) {
        newP.add(new String[] {rule.getLhs(), rule.getRhs()[0]});
      }
    }
  }

  /** Returns true if all rhs' have at most length 2. */
  public static boolean isBinarized(Cfg cfg) {
    for (CfgProductionRule rule : cfg.getProductionRules()) {
      if (rule.getRhs().length > 2) {
        return false;
      }
    }
    return true;
  }
}

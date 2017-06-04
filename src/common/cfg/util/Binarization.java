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
    newCfg.setStartsymbol(cfg.getStartsymbol());
    ArrayList<String> newnt = new ArrayList<String>();
    Collections.addAll(newnt, cfg.getNonterminals());
    ArrayList<String[]> newp = new ArrayList<String[]>();
    doBinarize(newnt, newp, cfg);
    newCfg.setNonterminals(newnt.toArray(new String[newnt.size()]));
    newCfg.setProductionrules(newp.toArray(new String[newp.size()][]));
    return newCfg;
  }

  /** Splits production rules with rhs length > 2 and replaces overlong part by
   * new nonterminals. */
  private static void doBinarize(ArrayList<String> newnt,
    ArrayList<String[]> newp, Cfg cfg) {
    int i = 1;
    for (CfgProductionRule rule : cfg.getProductionrules()) {
      if (rule.getRhs().length > 2) {
        CfgProductionRule rulerest = rule;
        while (rulerest.getRhs().length > 2) {
          String newn = "X" + String.valueOf(i);
          while (cfg.nonterminalsContain(newn)) {
            i++;
            newn = "X" + String.valueOf(i);
          }
          newnt.add(newn);
          String newrhs = rulerest.getRhs()[0] + " " + newn;
          String[] newrule = new String[] {rulerest.getLhs(), newrhs};
          newp.add(newrule);
          i++;
          rulerest =
            new CfgProductionRule(newn, ArrayUtils.getSubSequenceAsArray(
              rulerest.getRhs(), 1, rulerest.getRhs().length));
        }
        newp.add(new String[] {rulerest.getLhs(),
          rulerest.getRhs()[0] + " " + rulerest.getRhs()[1]});
      } else if (rule.getRhs().length == 2) {
        newp.add(new String[] {rule.getLhs(),
          rule.getRhs()[0] + " " + rule.getRhs()[1]});
      }
      if (rule.getRhs().length == 1) {
        newp.add(new String[] {rule.getLhs(), rule.getRhs()[0]});
      }
    }
  }

  /** Returns true if all rhs' have at most length 2. */
  public static boolean isBinarized(Cfg cfg) {
    for (CfgProductionRule rule : cfg.getProductionrules()) {
      if (rule.getRhs().length > 2) {
        return false;
      }
    }
    return true;
  }
}

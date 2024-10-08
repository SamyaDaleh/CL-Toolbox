package com.github.samyadaleh.cltoolbox.common.cfg.util;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import com.github.samyadaleh.cltoolbox.common.ArrayUtils;
import com.github.samyadaleh.cltoolbox.common.cfg.Cfg;
import com.github.samyadaleh.cltoolbox.common.cfg.CfgProductionRule;

import static com.github.samyadaleh.cltoolbox.common.Constants.ARROW_RIGHT;

public class LeftRecursion {

  public static final String DELIMITER = ":";

  /**
   * Returns true if CFG has one rule with direct left recursion, of the form A
   * -> A.... Remove epsilon productions to make sure no indirect left recursion
   * is left.
   */
  public static boolean hasDirectLeftRecursion(Cfg cfg) {
    for (CfgProductionRule rule : cfg.getProductionRules()) {
      if (hasDirectLeftRecursion(rule)) {
        return true;
      }
    }
    return false;
  }

  /** Returns true if rule has direct left recursion. */
  private static boolean hasDirectLeftRecursion(CfgProductionRule rule) {
    return rule.getLhs().equals(rule.getRhs()[0]) && rule.getRhs().length > 1;
  }

  /**
   * Removes direct left recursion. S -> S is ignored. S -> S a | b are replaced
   * by S -> b S1, S1 -> a S1 | ε Adds empty productions to the grammar and
   * maybe chain rules. Remove empty productions first to make sure grammar does
   * not contain indirect left recursion.
   */
  public static Cfg removeDirectLeftRecursion(Cfg cfgOld) {
    Cfg cfg = new Cfg();
    cfg.setTerminals(cfgOld.getTerminals());
    cfg.setStartSymbol(cfgOld.getStartSymbol());
    ArrayList<String> newNts = new ArrayList<>(cfgOld.getNonterminals());
    for (String nt : cfgOld.getNonterminals()) {
      if (!nonterminalIsLhsOfDirectLeftRecursion(cfgOld, nt)) {
        continue;
      } else if (!nonterminalIsLhsOfTermination(cfgOld, nt)) {
        System.err
          .println(nt + " has left recursive rule but no termination rule.");
        return null;
      }
      int i = 1;
      String newNt = nt + i;
      i++;
      while (newNts.contains(newNt) || cfg.terminalsContain(newNt)) {
        newNt = nt + i;
        i++;
      }
      newNts.add(newNt);
      doRemoveDirectLeftRecursion(cfg, nt, newNt, cfgOld);
    }
    cfg.setNonterminals(newNts);
    return cfg;
  }

  private static void removeDirectLeftRecursion(Cfg cfg,
    ArrayList<String> newNts, String nt) throws ParseException {
    int l = 1;
    String newNt = nt + l;
    while (newNts.contains(newNt) || cfg.terminalsContain(newNt)) {
      newNt = nt + l;
      l++;
    }
    newNts.add(newNt);
    List<CfgProductionRule> rulesCopy = new ArrayList<>();
    for(CfgProductionRule rule : cfg.getProductionRules()) {
      if(rule.getLhs().equals(nt)) {
      rulesCopy.add(new CfgProductionRule(rule.toString()));
      }
    }
    doRemoveDirectLeftRecursion(cfg, nt, newNt, cfg);
    for (int k = cfg.getProductionRules().size() - 1; k >= 0; k--) {
      String ruleString = cfg.getProductionRules().get(k).toString();
      for (int m = rulesCopy.size()-1; m >=0 ; m--) {
        if(rulesCopy.get(m).toString().equals(ruleString)) {
          cfg.getProductionRules().remove(k);
          rulesCopy.remove(m);
        }
      }
    }
  }

  /**
   * Removes any kind of left recursion including direct and indirect one,
   * but epsilon productions and loops have to be removed first.
   * S -> S is ignored. S -> S a | b are
   * replaced by S -> b S1, S1 -> a S1 | ε Adds empty productions to the grammar
   * and maybe chain rules. Remove empty productions first to make sure grammar
   * does not contain indirect left recursion.
   * Paull's algorithm, see Moore.
   */
  public static Cfg removeLeftRecursionPaull(Cfg cfgOld) throws ParseException {
    Cfg cfg = new Cfg();
    cfg.setTerminals(cfgOld.getTerminals());
    cfg.setStartSymbol(cfgOld.getStartSymbol());
    ArrayList<String> newNts = new ArrayList<>(cfgOld.getNonterminals());
    for (CfgProductionRule rule : cfgOld.getProductionRules()) {
      if (!rule.getLhs().equals(rule.getRhs()[0])
          || rule.getRhs().length != 1) {
            cfg.addProductionRule(rule.toString());
          }
    }
    for (int i = 0; i < cfgOld.getNonterminals().size(); i++) {
      String nt = cfgOld.getNonterminals().get(i);
      if (!nonterminalIsLhsOfLeftRecursion(cfgOld, nt)) {
        continue;
      } else if (!nonterminalIsLhsOfTermination(cfgOld, nt)) {
        System.err
          .println(nt + " has left recursive rule but no termination rule.");
        return null;
      }
      boolean change = true;
      while (change) {
        change = false;
        for (int k = cfg.getProductionRules().size() - 1; k >= 0; k--) {
          CfgProductionRule rule = cfg.getProductionRules().get(k);
          if (rule.getLhs().equals(nt)) {
            String nt2 = rule.getRhs()[0];
            for (int j = 0; j < i && j < newNts.size(); j++) {
              if (nt2.equals(newNts.get(j))) {
                change = true;
                removeIndirectLeftRecursion(cfg, nt, k, rule, nt2);
              }
            }
          }
        }
      }
      if (nonterminalIsLhsOfDirectLeftRecursion(cfg, nt)) {
        removeDirectLeftRecursion(cfg, newNts, nt);
      }
    }
    cfg.setNonterminals(newNts);
    return cfg;
  }

  /**
   * Removes any kind of left recursion including direct and indirect one,
   * but epsilon productions and loops have to be removed first.
   * Example: S -> A a, S -> B b, A -> S a, B -> b
   * 1. If the LC is terminal or a non-recursive terminal add A -> X A:X
   * For S -> B b add S -> B S:B. For B -> b add B -> b B:b.
   * 2. If LC is recursive and has more productions, add a new rule for each:
   * For A add S:A -> a S:A. For S add A:S -> a A:S.
   * 3. For any rule I guess.
   * For S -> A a add S:A -> a. For S -> B b add S:B -> b.
   * For A -> S a add A:S -> a. For B -> b add B:b -> ε
   * 4. For non-recursive nonterminals. Copy B -> b to new grammar.
   */
  public static Cfg removeLeftRecursionMoore(Cfg cfgOld) throws ParseException {
    Cfg cfg = new Cfg();
    cfg.setTerminals(cfgOld.getTerminals());
    cfg.setStartSymbol(cfgOld.getStartSymbol());
    List<String> newNts = new ArrayList<>(cfgOld.getNonterminals());
    for (CfgProductionRule rule : cfgOld.getProductionRules()) {
      String A = rule.getLhs();
      if (nonterminalIsLhsOfLeftRecursion(cfgOld, A)) {
        String lc = rule.getRhs()[0];
        if (nonterminalIsLhsOfLeftRecursion(cfgOld, lc)) {
          // Rule 2
          for (CfgProductionRule rule2 : cfgOld.getProductionRules()) {
            if (rule2.getLhs().equals(lc)) {
              String X = rule2.getRhs()[0];
              String beta = ArrayUtils.getSubSequenceAsString(
                  rule2.getRhs(), 1, rule2.getRhs().length);
              String AX = createNewNt(newNts, A, X);
              String AB = createNewNt(newNts, A, lc);
              if (!beta.isEmpty() || !AX.equals(AB)) {
                cfg.addProductionRule(AX + " " + ARROW_RIGHT + " " + beta + " " + AB);
              }
            }
          }
        } else {
          // Rule 1
          String AX = createNewNt(newNts, A, lc);
          cfg.addProductionRule(A + " " + ARROW_RIGHT + " " + lc + " " + AX);
        }
        // Rule 3
        String AX = createNewNt(newNts, A, lc);
        String rhsRest = ArrayUtils.getSubSequenceAsString(
            rule.getRhs(), 1, rule.getRhs().length);
        cfg.addProductionRule(AX + " " + ARROW_RIGHT + " " + rhsRest);
      } else {
        // Rule 4
        cfg.addProductionRule(rule.toString());
      }
    }
    cfg.setNonterminals(newNts);
    return cfg;
  }

  private static String createNewNt(List<String> newNts, String A, String lc) {
    String AX = A + DELIMITER + lc;
    if (!newNts.contains(AX)) {
      newNts.add(AX);
    }
    return AX;
  }

  private static void removeIndirectLeftRecursion(Cfg cfg, String nt, int k,
    CfgProductionRule rule, String nt2) throws ParseException {
    String[] bi = ArrayUtils.getSubSequenceAsArray(rule.getRhs(), 1,
      rule.getRhs().length);
    cfg.getProductionRules().remove(k);
    List<String> newRules = new ArrayList<>();
    for (CfgProductionRule rule2 : cfg.getProductionRules()) {
      if (rule2.getLhs().equals(nt2)) {
        newRules.add(nt + " "+ ARROW_RIGHT + " " + String.join(" ", rule2.getRhs())
          + " " + String.join(" ", bi));
      }
    }
    for (String newRule : newRules) {
      cfg.getProductionRules().add(new CfgProductionRule(newRule));
    }
  }

  /**
   * Returns true if nonterminal can be derived to itself by any kind of left
   * recursion including indirect one.
   */
  private static boolean nonterminalIsLhsOfLeftRecursion(Cfg cfg, String nt) {
    boolean change = true;
    List<String> transitiveClosure = new ArrayList<>();
    transitiveClosure.add(nt);
    List<String> epsilonNts = EmptyProductions.getEliminateable(cfg);
    while (change) {
      change = false;
      for (CfgProductionRule rule : cfg.getProductionRules()) {
        if (transitiveClosure.contains(rule.getLhs())) {
          List<String> consider = new ArrayList<>();
          int i = 0;
          String considerNt;
          do {
            considerNt = rule.getRhs()[i];
            consider.add(considerNt);
            i++;
          } while (epsilonNts.contains(considerNt) && i < rule.getRhs().length);
          if (consider.contains(nt)) {
            return true;
          }
          for (String considerThis : consider) {
            if (cfg.nonterminalsContain(considerThis)
              && !transitiveClosure.contains(considerThis)) {
              change = true;
              transitiveClosure.add(considerThis);
            }
          }
        }
      }
    }
    return false;
  }

  /**
   * Returns true if in this grammar there is any not left recursive rule with
   * this nonterminals as left hand side. Does not really check for termination,
   * remove useless symbols first.
   */
  private static boolean nonterminalIsLhsOfTermination(Cfg cfg, String nt) {
    for (CfgProductionRule rule : cfg.getProductionRules()) {
      if (rule.getLhs().equals(nt) && !hasDirectLeftRecursion(rule)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Returns true if in this grammar there is any left recursive rule with this
   * nonterminals as left hand side.
   */
  private static boolean nonterminalIsLhsOfDirectLeftRecursion(Cfg cfg,
    String nt) {
    for (CfgProductionRule rule : cfg.getProductionRules()) {
      if (rule.getLhs().equals(nt) && hasDirectLeftRecursion(rule)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Actually replaces S -> S a | b by S -> b S1, S1 -> a S1 | ε where nt is the
   * old nonterminal and newnt is S1 in this example. Actually it's not removing
   * but not copying to new cfg.
   */
  private static void doRemoveDirectLeftRecursion(Cfg cfg, String nt,
    String newNt, Cfg cfgOld) {
    cfg.getProductionRules()
      .add(new CfgProductionRule(newNt, new String[] {""}));
    for (int k = cfgOld.getProductionRules().size() - 1; k >= 0; k--) {
      CfgProductionRule rule = cfgOld.getProductionRules().get(k);
      if (rule.getLhs().equals(nt)) {
        if (rule.getRhs()[0].equals(nt) && rule.getRhs().length > 1) {
          String[] newRhs = new String[rule.getRhs().length];
          System.arraycopy(rule.getRhs(), 1, newRhs, 0,
            rule.getRhs().length - 1);
          newRhs[newRhs.length - 1] = newNt;
          cfg.getProductionRules().add(new CfgProductionRule(newNt, newRhs));
        } else if (!rule.getRhs()[0].equals(nt)) {
          if (rule.getRhs()[0].equals("")) {
            cfg.getProductionRules()
              .add(new CfgProductionRule(nt, new String[] {newNt}));
          } else {
            String[] newRhs = new String[rule.getRhs().length + 1];
            System.arraycopy(rule.getRhs(), 0, newRhs, 0, rule.getRhs().length);
            newRhs[newRhs.length - 1] = newNt;
            cfg.getProductionRules().add(rule);
            cfg.getProductionRules().add(new CfgProductionRule(nt, newRhs));
          }
        }
      }
    }
  }

  /**
   * Returns true if grammar contains any direct or indirect left recursion.
   */
  public static boolean hasLeftRecursion(Cfg cfg) {
    for (String nt : cfg.getNonterminals()) {
      if (nonterminalIsLhsOfLeftRecursion(cfg, nt)) {
        return true;
      }
    }
    return false;
  }
}

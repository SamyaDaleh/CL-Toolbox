package common.cfg;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import common.ArrayUtils;

/** Representation of a context-free grammar consisting of nonterminals,
 * terminals, production rules and a start symbol. */
public class Cfg extends AbstractCfg{
  private final List<CfgProductionRule> productionrules =
    new LinkedList<CfgProductionRule>();

  public Cfg() {
    super();
  }

  /** Creates a CFG from a PCFG by throwing away all probabilities. */
  public Cfg(Pcfg pcfg) {
    this.nonterminals = pcfg.getNonterminals();
    this.terminals = pcfg.getTerminals();
    this.startsymbol = pcfg.getStartsymbol();
    for (PcfgProductionRule rule : pcfg.getProductionrules()) {
      CfgProductionRule newrule =
        new CfgProductionRule(rule.getLhs(), rule.getRhs());
      this.productionrules.add(newrule);
    }
  }

  public List<CfgProductionRule> getProductionrules() {
    return productionrules;
  }

  public void setProductionrules(String[][] rules) {
    for (String[] rule : rules) {
      this.productionrules.add(new CfgProductionRule(rule));
    }
  }

  /** Returns true if there is at least one rule with an empty right side,
   * except it's a start symbol rule and the start symbol never occurs on any
   * rhs. */
  public boolean hasEpsilonProductions() {
    for (CfgProductionRule rule : this.getProductionrules()) {
      if (rule.getRhs().length == 1 && rule.getRhs()[0].equals("")) {
        if (rule.getLhs().equals(this.startsymbol)) {

          for (CfgProductionRule rule2 : this.getProductionrules()) {
            String[] rhs = rule2.getRhs();
            for (String symbol : rhs) {
              if (symbol.equals(this.startsymbol)) {
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

  /** Returns true if grammar is in Canonical Two Form. C2F is like Chomsky
   * Normal form, but chain rules are also allowed. */
  public boolean isInCanonicalTwoForm() {
    for (CfgProductionRule rule : this.productionrules) {
      if (rule.getRhs().length == 1) {
        if (rule.getLhs().equals(startsymbol) && rule.getRhs()[0].equals("")) {
          for (CfgProductionRule rule2 : this.productionrules) {
            if (rule2.getRhs()[0].equals(startsymbol)
              || rule2.getRhs()[1].equals(startsymbol)) {
              return false;
            }
          }
        }
      } else if (!nonterminalsContain(rule.getRhs()[0])
        || !nonterminalsContain(rule.getRhs()[1])) {
        return false;
      }
    }
    return true;
  }

  /** Returns true if the grammar is in Chomsky Normal Form. A grammar is in CNF
   * if all rules are either of the form A -> t or A -> BC. S -> ε is allowed,
   * in which case S must not appear on any right hand side. */
  public boolean isInChomskyNormalForm() {
    if (!isInCanonicalTwoForm()) {
      return false;
    }
    for (CfgProductionRule rule : this.productionrules) {
      if (rule.getRhs().length == 1) {
        if (!terminalsContain(rule.getRhs()[0])) {
          return false;
        }
      }
    }
    return true;
  }

  /** Returns true if grammar is in Greibach Normal Form. All right hand sides
   * must start with a terminal, followed by arbitrary many nonterminals
   * including none. */
  public boolean isInGreibachNormalForm() {
    for (CfgProductionRule rule : this.productionrules) {
      if (rule.getRhs()[0].equals("")) {
        return false;
      }
      if (!terminalsContain(rule.getRhs()[0])) {
        return false;
      }
      for (int i = 1; i < rule.getRhs().length; i++) {
        if (!nonterminalsContain(rule.getRhs()[i])) {
          return false;
        }
      }
    }
    return true;
  }

  /** Returns an equivalent CFG where all rhs' have at most length 2. */
  public Cfg binarize() {
    Cfg newCfg = new Cfg();
    newCfg.setTerminals(this.terminals);
    newCfg.setStartsymbol(this.startsymbol);
    ArrayList<String> newnt = new ArrayList<String>();
    Collections.addAll(newnt, this.nonterminals);
    ArrayList<String[]> newp = new ArrayList<String[]>();
    int i = 1;
    for (CfgProductionRule rule : this.productionrules) {
      if (rule.getRhs().length > 2) {
        CfgProductionRule rulerest = rule;
        while (rulerest.getRhs().length > 2) {
          String newn = "X" + String.valueOf(i);
          while (nonterminalsContain(newn)) {
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
    newCfg.setNonterminals(newnt.toArray(new String[newnt.size()]));
    newCfg.setProductionrules(newp.toArray(new String[newp.size()][]));
    return newCfg;
  }

  /** Returns true if all rhs' have at most length 2. */
  public boolean isBinarized() {
    for (CfgProductionRule rule : this.productionrules) {
      if (rule.getRhs().length > 2) {
        return false;
      }
    }
    return true;
  }

  /** Returns an equivalent grammar without non-generating symbols. Call this
   * before removing non-reachable symbols. */
  public Cfg removeNonGeneratingSymbols() {
    Cfg cfg = new Cfg();
    ArrayList<String> generating = new ArrayList<String>();
    Collections.addAll(generating, this.terminals);
    boolean changed = true;
    while (changed) {
      changed = false;
      for (CfgProductionRule rule : this.getProductionrules()) {
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
    cfg.terminals = this.getTerminals();
    cfg.startsymbol = this.startsymbol;
    ArrayList<String> restnts = new ArrayList<String>();
    for (String symbol : generating) {
      if (this.nonterminalsContain(symbol)) {
        restnts.add(symbol);
      }
    }
    cfg.nonterminals = restnts.toArray(new String[restnts.size()]);
    for (CfgProductionRule rule : this.getProductionrules()) {
      boolean notgeneratingseen = false;
      for (String symbol : rule.getRhs()) {
        if (!generating.contains(symbol)) {
          notgeneratingseen = true;
          break;
        }
      }
      if (!notgeneratingseen && generating.contains(rule.getLhs())) {
        cfg.productionrules.add(rule);
      }
    }
    return cfg;
  }

  /** Returns an equivalent grammar without non-reachable symbols. Before
   * calling this, remove all non-generating symbols. */
  public Cfg removeNonReachableSymbols() {
    Cfg cfg = new Cfg();
    ArrayList<String> reachable = new ArrayList<String>();
    reachable.add(this.startsymbol);
    boolean changed = true;
    while (changed) {
      changed = false;
      for (CfgProductionRule rule : this.productionrules) {
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
    cfg.startsymbol = this.startsymbol;
    ArrayList<String> newvars = new ArrayList<String>();
    for (String nt : this.nonterminals) {
      if (reachable.contains(nt)) {
        newvars.add(nt);
      }
    }
    ArrayList<String> newterms = new ArrayList<String>();
    for (String t : this.terminals) {
      if (reachable.contains(t)) {
        newterms.add(t);
      }
    }
    cfg.nonterminals = newvars.toArray(new String[newvars.size()]);
    cfg.terminals = newterms.toArray(new String[newterms.size()]);
    for (CfgProductionRule rule : this.getProductionrules()) {
      if (reachable.contains(rule.getLhs())) {
        cfg.productionrules.add(rule);
      }
    }
    return cfg;
  }

  /** Returns an equivalent CFG without empty productions, only S -> ε is
   * allowed in which case it is removed from all rhs'. May leaves non
   * generating symbols behind. */
  public Cfg removeEmptyProductions() {
    Cfg cfg = new Cfg();
    cfg.terminals = this.terminals;
    cfg.startsymbol = this.startsymbol;

    ArrayList<String> newnt = new ArrayList<String>();
    Collections.addAll(newnt, this.nonterminals);
    cfg.productionrules.addAll(this.productionrules);
    ArrayList<String> eliminateable = new ArrayList<String>();
    boolean changed = true;
    while (changed) {
      changed = false;
      for (CfgProductionRule rule : this.productionrules) {
        if (rule.getRhs().length == 1 && rule.getRhs()[0].equals("")
          && !eliminateable.contains(rule.getLhs())) {
          eliminateable.add(rule.getLhs());
          changed = true;
        }
      }
    }
    for (String nt : eliminateable) {
      for (int j = 0; j < cfg.productionrules.size(); j++) {
        CfgProductionRule rule = cfg.productionrules.get(j);
        for (int i = 0; i < rule.getRhs().length; i++) {
          if (rule.getRhs()[i].equals(nt)) {
            cfg.productionrules.add(new CfgProductionRule(rule.getLhs(),
              ArrayUtils.getSequenceWithoutIAsArray(rule.getRhs(), i)));
          }
        }
      }
      if (nt.equals(this.startsymbol)) {
        int i = 1;
        String newstart = "S" + String.valueOf(i);
        while (newnt.contains(newstart)) {
          i++;
          newstart = "S" + String.valueOf(i);
        }
        cfg.productionrules.add(
          new CfgProductionRule(new String[] {newstart, this.startsymbol}));
        cfg.productionrules
          .add(new CfgProductionRule(new String[] {newstart, ""}));
        newnt.add(newstart);
        cfg.startsymbol = newstart;
      }
    }
    for (int i = cfg.productionrules.size() - 1; i >= 0; i--) {
      if (cfg.productionrules.get(i).getRhs().length == 1
        && cfg.productionrules.get(i).getRhs()[0].equals("")
        && !cfg.productionrules.get(i).getLhs().equals(cfg.startsymbol)) {
        cfg.productionrules.remove(i);
      }
    }

    cfg.nonterminals = newnt.toArray(new String[newnt.size()]);
    return cfg;
  }

  /** Returns an equivalent grammar without chain rules, that are rules of the
   * form A -> B. Remove epsilon productions beforehand. */
  public Cfg removeChainRules() {
    Cfg cfg = new Cfg();
    cfg.terminals = this.terminals;
    cfg.startsymbol = this.startsymbol;
    cfg.nonterminals = this.nonterminals;
    for (CfgProductionRule rule : this.productionrules) {
      if (!(rule.getRhs().length == 1
        && nonterminalsContain(rule.getRhs()[0]))) {
        cfg.productionrules.add(rule);
      }
    }
    ArrayList<String[]> unitpairs = new ArrayList<String[]>();
    for (String nt : this.nonterminals) {
      unitpairs.add(new String[] {nt, nt});
    }
    boolean changed = true;
    while (changed) {
      changed = false;
      for (CfgProductionRule rule : this.productionrules) {
        if (rule.getRhs().length == 1
          && nonterminalsContain(rule.getRhs()[0])) {
          boolean found = false;
          for (String[] unitpair : unitpairs) {
            if (unitpair[0].equals(rule.getLhs())
              && unitpair[1].equals(rule.getRhs()[0])) {
              found = true;
              break;
            }
          }
          if (!found) {
            unitpairs.add(new String[] {rule.getLhs(), rule.getRhs()[0]});
            changed = true;
          }
        }
      }
    }

    for (String[] unitpair : unitpairs) {
      for (CfgProductionRule rule : this.productionrules) {
        if (!(rule.getRhs().length == 1
          && nonterminalsContain(rule.getRhs()[0]))
          && rule.getLhs().equals(unitpair[1])) {
          boolean alreadythere = false;
          for (CfgProductionRule rule2 : cfg.getProductionrules()) {
            if (rule.getLhs().equals(unitpair[0])
              && rule2.getRhs().length == rule.getRhs().length) {
              boolean alright = false;
              for (int i = 0; i < rule.getRhs().length; i++) {
                if (!rule.getRhs()[i].equals(rule2.getRhs()[i])) {
                  alright = true;
                }
              }
              if (!alright) {
                alreadythere = true;
              }
            }
          }
          if (!alreadythere) {
            cfg.productionrules
              .add(new CfgProductionRule(unitpair[0], rule.getRhs()));
          }
        }
      }
    }

    return cfg;
  }

  /** Returns true if grammar has rules of the form A -> B. */
  public boolean hasChainRules() {
    for (CfgProductionRule rule : this.productionrules) {
      if (rule.getRhs().length == 1 && nonterminalsContain(rule.getRhs()[0])) {
        return true;
      }
    }
    return false;
  }

  /** Returns a new grammar where in all rhs > 1 terminals are replaced by
   * nonterminals and new rules A -> a are added. */
  public Cfg replaceTerminals() {
    Cfg cfg = new Cfg();
    cfg.startsymbol = this.startsymbol;
    cfg.terminals = this.terminals;

    ArrayList<String[]> newtrules = new ArrayList<String[]>();
    ArrayList<String> newnt = new ArrayList<String>();

    Collections.addAll(newnt, this.nonterminals);
    int i = 1;
    for (CfgProductionRule rule : this.productionrules) {
      if (rule.getRhs().length == 1) {
        cfg.productionrules.add(rule);
      } else {
        ArrayList<String> newrhs = new ArrayList<String>();
        for (String sym : rule.getRhs()) {
          if (nonterminalsContain(sym)) {
            newrhs.add(sym);
          } else {
            String newlhs = null;
            for (String[] tryrule : newtrules) {
              if (tryrule[1].equals(sym)) {
                newlhs = tryrule[0];
              }
            }
            boolean isnew = false;
            if (newlhs == null) {
              newlhs = "Y" + String.valueOf(i);
              i++;
              isnew = true;
              cfg.productionrules
                .add(new CfgProductionRule(newlhs, new String[] {sym}));
            }
            while (this.nonterminalsContain(newlhs)) {
              newlhs = "Y" + String.valueOf(i);
              i++;
            }
            if (isnew) {
              newnt.add(newlhs);
              newtrules.add(new String[] {newlhs, sym});
            }
            newrhs.add(newlhs);
          }
        }
        cfg.productionrules.add(new CfgProductionRule(rule.getLhs(),
          newrhs.toArray(new String[newrhs.size()])));
      }
    }

    cfg.nonterminals = newnt.toArray(new String[newnt.size()]);
    return cfg;
  }

  @Override public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("G = <N, T, S, P>\n");
    builder.append("N = {").append(String.join(", ", nonterminals))
      .append("}\n");
    builder.append("T = {").append(String.join(", ", terminals)).append("}\n");
    builder.append("S = ").append(startsymbol).append("\n");
    builder.append("P = {");
    for (int i = 0; i < productionrules.size(); i++) {
      if (i > 0) {
        builder.append(", ");
      }
      builder.append(productionrules.get(i).toString());
    }
    builder.append("}\n");
    return builder.toString();
  }

  /** Returns true if CFG has one rule with direct left recursion, of the form A
   * -> A.... Remove epsilon productions to make sure no indirect left recursion
   * is left. */
  public boolean hasDirectLeftRecursion() {
    for (CfgProductionRule rule : this.getProductionrules()) {
      if (rule.getLhs().equals(rule.getRhs()[0])) {
        return true;
      }
    }
    return false;
  }

  /** Removes direct left recursion. S -> S is ignored. S -> S a | b are
   * replaced by S -> b S1, S1 -> a S1 | ε Adds empty productions to the grammar
   * and maybe chain rules. Remove empty productions first to make sure grammar
   * does not contain indirect left recursion. */
  public Cfg removeLeftRecursion() {
    Cfg cfg = new Cfg();
    cfg.setTerminals(this.terminals);
    cfg.setStartsymbol(this.startsymbol);
    ArrayList<String> newnts = new ArrayList<String>();
    Collections.addAll(newnts, this.nonterminals);
    for (String nt : this.nonterminals) {
      int i = 1;
      String newnt = nt + String.valueOf(i);
      i++;
      while (newnts.contains(newnt)) {
        newnt = nt + String.valueOf(i);
        i++;
      }
      newnts.add(newnt);
      cfg.productionrules.add(new CfgProductionRule(newnt, new String[] {""}));

      for (CfgProductionRule rule : this.productionrules) {
        if (rule.getLhs().equals(nt)) {
          if (rule.getRhs()[0].equals(nt) && rule.getRhs().length > 1) {
            String[] newrhs = new String[rule.getRhs().length];
            System.arraycopy(rule.getRhs(), 1, newrhs, 0,
              rule.getRhs().length - 1);
            newrhs[newrhs.length - 1] = newnt;
            cfg.productionrules.add(new CfgProductionRule(nt, newrhs));
          } else if (!rule.getRhs()[0].equals(nt)) {
            if (rule.getRhs()[0].equals("")) {
              cfg.productionrules
                .add(new CfgProductionRule(nt, new String[] {newnt}));
            } else {
              String[] newrhs = new String[rule.getRhs().length + 1];
              System.arraycopy(rule.getRhs(), 0, newrhs, 0,
                rule.getRhs().length);
              newrhs[newrhs.length - 1] = newnt;
              cfg.productionrules.add(new CfgProductionRule(nt, newrhs));
            }
          }
        }
      }
    }
    cfg.setNonterminals(newnts.toArray(new String[newnts.size()]));
    return cfg;
  }

  /** Returns true if there is at least one production rule that contains
   * terminals and nonterminals as rhs symbols. */
  public boolean hasMixedRhs() {
    for (CfgProductionRule rule : this.productionrules) {
      for (int i = 1; i < rule.getRhs().length; i++) {
        if ((this.terminalsContain(rule.getRhs()[i - 1])
          && this.nonterminalsContain(rule.getRhs()[i]))
          || (this.terminalsContain(rule.getRhs()[i])
            && this.nonterminalsContain(rule.getRhs()[i - 1]))) {
          return true;
        }
      }
    }
    return false;
  }
}

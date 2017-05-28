package common.cfg;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import common.ArrayUtils;

/** Representation of a context-free grammar consisting of nonterminals,
 * terminals, production rules and a start symbol. */
public class Cfg {
  private String vars[];
  private final List<CfgProductionRule> R = new LinkedList<CfgProductionRule>();
  private String start_var;
  private String terminals[];

  public Cfg() {
    super();
  }

  /** Creates a CFG from a PCFG by throwing away all probabilities. */
  public Cfg(Pcfg pcfg) {
    this.vars = pcfg.vars;
    this.terminals = pcfg.terminals;
    this.start_var = pcfg.getStart_var();
    for (PcfgProductionRule rule : pcfg.getR()) {
      CfgProductionRule newrule =
        new CfgProductionRule(rule.getLhs(), rule.getRhs());
      this.R.add(newrule);
    }
  }

  public String[] getVars() {
    return vars;
  }

  public void setVars(String[] vars) {
    this.vars = vars;
  }

  public List<CfgProductionRule> getR() {
    return R;
  }

  public void setR(String[][] rules) {
    for (String[] rule : rules) {
      this.R.add(new CfgProductionRule(rule));
    }
  }

  public String getStart_var() {
    return start_var;
  }

  public void setStart_var(String start_var) {
    this.start_var = start_var;
  }

  public String[] getTerminals() {
    return terminals;
  }

  public void setTerminals(String[] terminals) {
    this.terminals = terminals;
  }

  /** Returns true if mayt is on of the terminal symbols. */
  public boolean terminalsContain(String mayt) {
    for (String term : terminals) {
      if (term.equals(mayt))
        return true;
    }
    return false;
  }

  /** Returns true if maynt is on of the nonterminal symbols. */
  public boolean nonterminalsContain(String maynt) {
    for (String nt : vars) {
      if (nt.equals(maynt))
        return true;
    }
    return false;
  }

  /** Returns true if mayvar is one of the nonterminal symbols. */
  private boolean varsContain(String mayvar) {
    for (String var : vars) {
      if (var.equals(mayvar))
        return true;
    }
    return false;
  }

  /** Returns true if there is at least one rule with an empty right side,
   * except it's a start symbol rule and the start symbol never occurs on any
   * rhs. */
  public boolean hasEpsilonProductions() {
    for (CfgProductionRule rule : this.getR()) {
      if (rule.getRhs().length == 1 && rule.getRhs()[0].equals("")) {
        if (rule.getLhs().equals(this.start_var)) {

          for (CfgProductionRule rule2 : this.getR()) {
            String[] rhs = rule2.getRhs();
            for (String symbol : rhs) {
              if (symbol.equals(this.start_var)) {
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
    for (CfgProductionRule rule : this.R) {
      if (rule.rhs.length == 1) {
        if (rule.lhs.equals(start_var) && rule.rhs[0].equals("")) {
          for (CfgProductionRule rule2 : this.R) {
            if (rule2.rhs[0].equals(start_var)
              || rule2.rhs[1].equals(start_var)) {
              return false;
            }
          }
        }
      } else if (!varsContain(rule.rhs[0]) || !varsContain(rule.rhs[1])) {
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
    for (CfgProductionRule rule : this.R) {
      if (rule.rhs.length == 1) {
        if (!terminalsContain(rule.rhs[0])) {
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
    for (CfgProductionRule rule : this.R) {
      if (rule.rhs[0].equals("")) {
        return false;
      }
      if (!terminalsContain(rule.rhs[0])) {
        return false;
      }
      for (int i = 1; i < rule.rhs.length; i++) {
        if (!varsContain(rule.rhs[i])) {
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
    newCfg.setStart_var(this.start_var);
    ArrayList<String> newnt = new ArrayList<String>();
    Collections.addAll(newnt, this.vars);
    ArrayList<String[]> newp = new ArrayList<String[]>();
    int i = 1;
    for (CfgProductionRule rule : this.R) {
      if (rule.rhs.length > 2) {
        CfgProductionRule rulerest = rule;
        while (rulerest.rhs.length > 2) {
          String newn = "X" + String.valueOf(i);
          while (varsContain(newn)) {
            i++;
            newn = "X" + String.valueOf(i);
          }
          newnt.add(newn);
          String newrhs = rulerest.rhs[0] + " " + newn;
          String[] newrule = new String[] {rulerest.lhs, newrhs};
          newp.add(newrule);
          i++;
          rulerest = new CfgProductionRule(newn, ArrayUtils
            .getSubSequenceAsArray(rulerest.rhs, 1, rulerest.rhs.length));
        }
        newp.add(
          new String[] {rulerest.lhs, rulerest.rhs[0] + " " + rulerest.rhs[1]});
      } else if (rule.rhs.length == 2) {
        newp.add(new String[] {rule.lhs, rule.rhs[0] + " " + rule.rhs[1]});
      }
      if (rule.rhs.length == 1) {
        newp.add(new String[] {rule.lhs, rule.rhs[0]});
      }
    }
    newCfg.setVars(newnt.toArray(new String[newnt.size()]));
    newCfg.setR(newp.toArray(new String[newp.size()][]));
    return newCfg;
  }

  /** Returns true if all rhs' have at most length 2. */
  public boolean isBinarized() {
    for (CfgProductionRule rule : this.R) {
      if (rule.rhs.length > 2) {
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
    for (String t : this.terminals) {
      generating.add(t);
    }
    boolean changed = true;
    while (changed) {
      changed = false;
      for (CfgProductionRule rule : this.getR()) {
        boolean notgeneratingseen = false;
        for (String symbol : rule.getRhs()) {
          if (!generating.contains(symbol)) {
            notgeneratingseen = true;
            break;
          }
        }
        if (!notgeneratingseen && !generating.contains(rule.lhs)) {
          changed = true;
          generating.add(rule.lhs);
        }
      }
    }
    cfg.terminals = this.getTerminals();
    cfg.start_var = this.start_var;
    ArrayList<String> restnts = new ArrayList<String>();
    for (String symbol : generating) {
      if (this.nonterminalsContain(symbol)) {
        restnts.add(symbol);
      }
    }
    cfg.vars = restnts.toArray(new String[restnts.size()]);
    for (CfgProductionRule rule : this.getR()) {
      boolean notgeneratingseen = false;
      for (String symbol : rule.getRhs()) {
        if (!generating.contains(symbol)) {
          notgeneratingseen = true;
          break;
        }
      }
      if (!notgeneratingseen && generating.contains(rule.lhs)) {
        cfg.R.add(rule);
      }
    }
    return cfg;
  }

  /** Returns an equivalent grammar without non-reachable symbols. Before
   * calling this, remove all non-generating symbols. */
  public Cfg removeNonReachableSymbols() {
    Cfg cfg = new Cfg();
    ArrayList<String> reachable = new ArrayList<String>();
    reachable.add(this.start_var);
    boolean changed = true;
    while (changed) {
      changed = false;
      for (CfgProductionRule rule : this.R) {
        if (reachable.contains(rule.lhs)) {
          for (String symbol : rule.rhs) {
            if (!reachable.contains(symbol)) {
              reachable.add(symbol);
              changed = true;
            }
          }
        }
      }
    }
    cfg.start_var = this.start_var;
    ArrayList<String> newvars = new ArrayList<String>();
    for (String nt : this.vars) {
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
    cfg.vars = newvars.toArray(new String[newvars.size()]);
    cfg.terminals = newterms.toArray(new String[newterms.size()]);
    for (CfgProductionRule rule : this.getR()) {
      if (reachable.contains(rule.lhs)) {
        cfg.R.add(rule);
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
    cfg.start_var = this.start_var;

    ArrayList<String> newnt = new ArrayList<String>();
    Collections.addAll(newnt, this.vars);
    cfg.R.addAll(this.R);
    ArrayList<String> eliminateable = new ArrayList<String>();
    boolean changed = true;
    while (changed) {
      changed = false;
      for (CfgProductionRule rule : this.R) {
        if (rule.rhs.length == 1 && rule.rhs[0].equals("")
          && !eliminateable.contains(rule.lhs)) {
          eliminateable.add(rule.lhs);
          changed = true;
        }
      }
    }
    for (String nt : eliminateable) {
      for (int j = 0; j < cfg.R.size(); j++) {
        CfgProductionRule rule = cfg.R.get(j);
        for (int i = 0; i < rule.rhs.length; i++) {
          if (rule.rhs[i].equals(nt)) {
            cfg.R.add(new CfgProductionRule(rule.getLhs(),
              ArrayUtils.getSequenceWithoutIAsArray(rule.rhs, i)));
          }
        }
      }
      if (nt.equals(this.start_var)) {
        int i = 1;
        String newstart = "S" + String.valueOf(i);
        while (newnt.contains(newstart)) {
          i++;
          newstart = "S" + String.valueOf(i);
        }
        cfg.R
          .add(new CfgProductionRule(new String[] {newstart, this.start_var}));
        cfg.R.add(new CfgProductionRule(new String[] {newstart, ""}));
        newnt.add(newstart);
        cfg.start_var = newstart;
      }
    }
    for (int i = cfg.R.size() - 1; i >= 0; i--) {
      if (cfg.R.get(i).rhs.length == 1 && cfg.R.get(i).rhs[0].equals("")
        && !cfg.R.get(i).lhs.equals(cfg.start_var)) {
        cfg.R.remove(i);
      }
    }

    cfg.vars = newnt.toArray(new String[newnt.size()]);
    return cfg;
  }

  /** Returns an equivalent grammar without chain rules, that are rules of the
   * form A -> B. Remove epsilon productions beforehand. */
  public Cfg removeChainRules() {
    Cfg cfg = new Cfg();
    cfg.terminals = this.terminals;
    cfg.start_var = this.start_var;
    cfg.vars = this.vars;
    for (CfgProductionRule rule : this.R) {
      if (!(rule.rhs.length == 1 && nonterminalsContain(rule.rhs[0]))) {
        cfg.R.add(rule);
      }
    }
    ArrayList<String[]> unitpairs = new ArrayList<String[]>();
    for (String nt : this.vars) {
      unitpairs.add(new String[] {nt, nt});
    }
    boolean changed = true;
    while (changed) {
      changed = false;
      for (CfgProductionRule rule : this.R) {
        if (rule.rhs.length == 1 && nonterminalsContain(rule.rhs[0])) {
          boolean found = false;
          for (String[] unitpair : unitpairs) {
            if (unitpair[0].equals(rule.lhs)
              && unitpair[1].equals(rule.rhs[0])) {
              found = true;
              break;
            }
          }
          if (!found) {
            unitpairs.add(new String[] {rule.lhs, rule.rhs[0]});
            changed = true;
          }
        }
      }
    }

    for (String[] unitpair : unitpairs) {
      for (CfgProductionRule rule : this.R) {
        if (!(rule.rhs.length == 1 && nonterminalsContain(rule.rhs[0]))
          && rule.lhs.equals(unitpair[1])) {
          boolean alreadythere = false;
          for (CfgProductionRule rule2 : cfg.getR()) {
            if (rule.lhs.equals(unitpair[0])
              && rule2.rhs.length == rule.rhs.length) {
              boolean alright = false;
              for (int i = 0; i < rule.rhs.length; i++) {
                if (!rule.rhs[i].equals(rule2.rhs[i])) {
                  alright = true;
                }
              }
              if (!alright) {
                alreadythere = true;
              }
            }
          }
          if (!alreadythere) {
            cfg.R.add(new CfgProductionRule(unitpair[0], rule.rhs));
          }
        }
      }
    }

    return cfg;
  }

  /** Returns true if grammar has rules of the form A -> B. */
  public boolean hasChainRules() {
    for (CfgProductionRule rule : this.R) {
      if (rule.rhs.length == 1 && nonterminalsContain(rule.rhs[0])) {
        return true;
      }
    }
    return false;
  }

  /** Returns a new grammar where in all rhs > 1 terminals are replaced by
   * nonterminals and new rules A -> a are added. */
  public Cfg replaceTerminals() {
    Cfg cfg = new Cfg();
    cfg.start_var = this.start_var;
    cfg.terminals = this.terminals;

    ArrayList<String[]> newtrules = new ArrayList<String[]>();
    ArrayList<String> newnt = new ArrayList<String>();

    Collections.addAll(newnt, this.vars);
    int i = 1;
    for (CfgProductionRule rule : this.R) {
      if (rule.rhs.length == 1) {
        cfg.R.add(rule);
      } else {
        ArrayList<String> newrhs = new ArrayList<String>();
        for (String sym : rule.rhs) {
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
              cfg.R.add(new CfgProductionRule(newlhs, new String[] {sym}));
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
        cfg.R.add(new CfgProductionRule(rule.lhs,
          newrhs.toArray(new String[newrhs.size()])));
      }
    }

    cfg.vars = newnt.toArray(new String[newnt.size()]);
    return cfg;
  }

  @Override public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("G = <N, T, S, P>\n");
    builder.append("N = {").append(String.join(", ", vars)).append("}\n");
    builder.append("T = {").append(String.join(", ", terminals)).append("}\n");
    builder.append("S = ").append(start_var).append("\n");
    builder.append("P = {");
    for (int i = 0; i < R.size(); i++) {
      if (i > 0) {
        builder.append(", ");
      }
      builder.append(R.get(i).toString());
    }
    builder.append("}\n");
    return builder.toString();
  }

  /** Removes left recursion. S -> S is ignored. S -> S a | b are replaced by S
   * -> b S1, S1 -> a S1 | ε Adds empty productions to the grammar and maybe
   * chain rules. */
  public Cfg removeLeftRecursion() {
    Cfg cfg = new Cfg();
    cfg.setTerminals(this.terminals);
    cfg.setStart_var(this.start_var);
    ArrayList<String> newnts = new ArrayList<String>();
    Collections.addAll(newnts, this.vars);
    for (String nt : this.vars) {
      int i = 1;
      String newnt = nt + String.valueOf(i);
      i++;
      while (newnts.contains(newnt)) {
        newnt = nt + String.valueOf(i);
        i++;
      }
      newnts.add(newnt);
      cfg.R.add(new CfgProductionRule(newnt, new String[] {""}));

      for (CfgProductionRule rule : this.R) {
        if (rule.lhs.equals(nt)) {
          if (rule.rhs[0].equals(nt) && rule.rhs.length > 1) {
            String[] newrhs = new String[rule.rhs.length];
            System.arraycopy(rule.rhs, 1, newrhs, 0, rule.rhs.length - 1);
            newrhs[newrhs.length - 1] = newnt;
            cfg.R.add(new CfgProductionRule(nt, newrhs));
          } else if (!rule.rhs[0].equals(nt)) {
            if (rule.rhs[0].equals("")) {
              cfg.R.add(new CfgProductionRule(nt, new String[] {newnt}));
            } else {
              String[] newrhs = new String[rule.rhs.length + 1];
              System.arraycopy(rule.rhs, 0, newrhs, 0, rule.rhs.length);
              newrhs[newrhs.length - 1] = newnt;
              cfg.R.add(new CfgProductionRule(nt, newrhs));
            }
          }
        }
      }
    }
    cfg.setVars(newnts.toArray(new String[newnts.size()]));
    return cfg;
  }

  public boolean hasMixedRhs() {
    for (CfgProductionRule rule : this.R) {
      for (int i = 1; i < rule.getRhs().length; i++) {
        if ((this.terminalsContain(rule.getRhs()[i - 1])
          && this.varsContain(rule.getRhs()[i]))
          || (this.terminalsContain(rule.getRhs()[i])
            && this.varsContain(rule.getRhs()[i - 1]))) {
          return true;
        }
      }
    }
    return false;
  }
}

package common.cfg;

import java.util.LinkedList;
import java.util.List;

/** Representation of a context-free grammar consisting of nonterminals,
 * terminals, production rules and a start symbol. */
public class Cfg {
  String vars[];
  List<CfgProductionRule> R = new LinkedList<CfgProductionRule>();
  String start_var;
  String terminals[];

  public Cfg() {
    super();
  }

  /** Creates a CFG from a PCFG by throwing away all probabilities. */
  Cfg(Pcfg pcfg) {
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

  /** Returns true if mayvar is one of the nonterminal symbols. */
  public boolean varsContain(String mayvar) {
    for (String var : vars) {
      if (var.equals(mayvar))
        return true;
    }
    return false;
  }

  /** Returns true if there is at least one rule with an empty right side. */
  public boolean hasEpsilonProductions() {
    for (CfgProductionRule rule : this.getR()) {
      if (rule.getLhs().length() == 0) {
        return true;
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
      } else {
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
  
  /**
   * Returns true if grammar is in Greibach Normal Form. All right hand sides
   * must start with a terminal, followed by arbitrary many nonterminals
   * including none.
   */
  public boolean isInGreibachNormalForm() {
    for (CfgProductionRule rule : this.R) {
      if (rule.rhs[0].equals("")) {
        return false;
      }
      if (!terminalsContain(rule.rhs[0])) {
        return false;
      }
      for(int i = 1; i < rule.rhs.length; i++) {
        if (!varsContain(rule.rhs[i])) {
          return false;
        }
      }
    }
    return true;
  }
}

package common;

import java.util.LinkedList;
import java.util.List;

/** Representation of an arbitrary grammar where lengths of left and right rule
 * sides are unrestricted. */
class Grammar {
  private String[] vars;
  private final List<GrammarProductionRule> R = new LinkedList<GrammarProductionRule>();
  private String start_var;
  private String[] terminals;

  public String[] getVars() {
    return vars;
  }

  public void setVars(String[] vars) {
    this.vars = vars;
  }

  public List<GrammarProductionRule> getR() {
    return R;
  }

  public void setR(String[][] rules) {
    for (String[] rule : rules) {
      this.R.add(new GrammarProductionRule(rule));
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

  public boolean terminalsContain(String mayt) {
    for (String term : terminals) {
      if (term.equals(mayt))
        return true;
    }
    return false;
  }

  public boolean varsContain(String mayvar) {
    for (String var : vars) {
      if (var.equals(mayvar))
        return true;
    }
    return false;
  }
}

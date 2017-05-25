package common.cfg;

import java.util.LinkedList;
import java.util.List;

/** Representation of a context free grammar where the rules have
 * probabilities. */
public class Pcfg {
  String vars[];
  private List<PcfgProductionRule> R = new LinkedList<PcfgProductionRule>();
  private String start_var;
  String terminals[];

  public String[] getVars() {
    return vars;
  }

  public void setVars(String[] vars) {
    this.vars = vars;
  }

  public List<PcfgProductionRule> getR() {
    return R;
  }

  public void setR(String[][] rules) {
    for (String[] rule : rules) {
      this.R.add(new PcfgProductionRule(rule));
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

  /** Returns true if mayvar is one of the nonterminal symbols. */
  public boolean terminalsContain(String mayt) {
    for (String term : terminals) {
      if (term.equals(mayt))
        return true;
    }
    return false;
  }

  /** Returns true if there is at least one rule with an empty right side. */
  public boolean varsContain(String mayvar) {
    for (String var : vars) {
      if (var.equals(mayvar))
        return true;
    }
    return false;
  }

  @Override public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("G = <N, T, S, P>\n");
    builder.append("N = {" + String.join(", ", vars) + "}\n");
    builder.append("T = {" + String.join(", ", terminals) + "}\n");
    builder.append("S = " + start_var + "\n");
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
}

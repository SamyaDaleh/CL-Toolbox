package com.github.samyadaleh.cltoolbox.common.cfg;

abstract class AbstractCfg {
  String[] nonterminals;
  String startSymbol;
  String[] terminals;

  public String[] getNonterminals() {
    return nonterminals;
  }

  public void setNonterminals(String[] nonterminals) {
    this.nonterminals = nonterminals;
  }

  public String getStartSymbol() {
    return startSymbol;
  }

  public void setStartSymbol(String startSymbol) {
    this.startSymbol = startSymbol;
  }

  public String[] getTerminals() {
    return terminals;
  }

  public void setTerminals(String[] terminals) {
    this.terminals = terminals;
  }

  /** Returns true if mayvar is one of the nonterminal symbols. */
  public boolean terminalsContain(String mayT) {
    for (String term : terminals) {
      if (term.equals(mayT))
        return true;
    }
    return false;
  }

  /** Returns true if there is at least one rule with an empty right side. */
  public boolean nonterminalsContain(String mayNt) {
    for (String nt : nonterminals) {
      if (nt.equals(mayNt))
        return true;
    }
    return false;
  }
  
  @Override public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("G = <N, T, S, P>\n");
    builder.append("N = {").append(String.join(", ", nonterminals))
      .append("}\n");
    builder.append("T = {").append(String.join(", ", terminals)).append("}\n");
    builder.append("S = ").append(startSymbol).append("\n");
    builder.append("P = {");
    appendRuleRepresentation(builder);
    builder.append("}\n");
    return builder.toString();
  }
  
  protected abstract void appendRuleRepresentation(StringBuilder builder);
}

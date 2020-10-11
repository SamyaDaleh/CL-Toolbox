package com.github.samyadaleh.cltoolbox.common;

public class AbstractNTSGrammar {
  private String[] nonterminals;
  private String startSymbol;
  private String[] terminals;

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
    if (nonterminals == null) {
      return false;
    }
    for (String nt : nonterminals) {
      if (nt.equals(mayNt))
        return true;
    }
    return false;
  }
}

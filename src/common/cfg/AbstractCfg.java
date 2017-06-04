package common.cfg;

public abstract class AbstractCfg {
  protected String nonterminals[];
  protected String startsymbol;
  protected String terminals[];

  public String[] getNonterminals() {
    return nonterminals;
  }

  public void setNonterminals(String[] nonterminals) {
    this.nonterminals = nonterminals;
  }


  public String getStartsymbol() {
    return startsymbol;
  }

  public void setStartsymbol(String startsymbol) {
    this.startsymbol = startsymbol;
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
  public boolean nonterminalsContain(String maynt) {
    for (String nt : nonterminals) {
      if (nt.equals(maynt))
        return true;
    }
    return false;
  }
}
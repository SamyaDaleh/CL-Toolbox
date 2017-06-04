package common.cfg;

import java.util.LinkedList;
import java.util.List;

/** Representation of a context free grammar where the rules have
 * probabilities. */
public class Pcfg {
  private String nonterminals[];
  private final List<PcfgProductionRule> productionrules =
    new LinkedList<PcfgProductionRule>();
  private String startsymbol;
  private String terminals[];

  public Pcfg() {
    super();
  }

  /** Create a PCFG from a CFG where all rules have the same probability. */
  public Pcfg(Cfg cfg) {
    this.nonterminals = cfg.getNonterminals();
    this.terminals = cfg.getTerminals();
    this.startsymbol = cfg.getStartsymbol();
    for (String nt : nonterminals) {
      int rulecount = 0;
      for (CfgProductionRule rule : cfg.getProductionrules()) {
        if (rule.getLhs().equals(nt)) {
          rulecount++;
        }
      }
      for (CfgProductionRule rule : cfg.getProductionrules()) {
        if (rule.getLhs().equals(nt)) {
          this.productionrules.add(new PcfgProductionRule(rule.getLhs(),
            rule.getRhs(), 1.0 / rulecount));
        }
      }
    }
  }

  public String[] getNonterminals() {
    return nonterminals;
  }

  public void setNonterminals(String[] nonterminals) {
    this.nonterminals = nonterminals;
  }

  public List<PcfgProductionRule> getProductionrules() {
    return productionrules;
  }

  public void setProductionrules(String[][] rules) {
    for (String[] rule : rules) {
      this.productionrules.add(new PcfgProductionRule(rule));
    }
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
}

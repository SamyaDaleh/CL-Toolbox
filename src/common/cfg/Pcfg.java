package common.cfg;

import java.util.LinkedList;
import java.util.List;

/** Representation of a context free grammar where the rules have
 * probabilities. */
public class Pcfg extends AbstractCfg{
  private final List<PcfgProductionRule> productionrules =
    new LinkedList<PcfgProductionRule>();

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

  public List<PcfgProductionRule> getProductionrules() {
    return productionrules;
  }

  public void setProductionrules(String[][] rules) {
    for (String[] rule : rules) {
      this.productionrules.add(new PcfgProductionRule(rule));
    }
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

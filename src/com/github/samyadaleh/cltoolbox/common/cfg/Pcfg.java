package com.github.samyadaleh.cltoolbox.common.cfg;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/** Representation of a context free grammar where the rules have
 * probabilities. */
public class Pcfg extends AbstractCfg{
  private final List<PcfgProductionRule> productionRules =
    new ArrayList<PcfgProductionRule>();

  public Pcfg() {
    super();
  }

  /** Create a PCFG from a CFG where all rules have the same probability. */
  public Pcfg(Cfg cfg) {
    this.nonterminals = cfg.getNonterminals();
    this.terminals = cfg.getTerminals();
    this.startSymbol = cfg.getStartSymbol();
    for (String nt : nonterminals) {
      int ruleCount = 0;
      for (CfgProductionRule rule : cfg.getProductionRules()) {
        if (rule.getLhs().equals(nt)) {
          ruleCount++;
        }
      }
      for (CfgProductionRule rule : cfg.getProductionRules()) {
        if (rule.getLhs().equals(nt)) {
          this.productionRules.add(new PcfgProductionRule(rule.getLhs(),
            rule.getRhs(), 1.0 / ruleCount));
        }
      }
    }
  }

  public List<PcfgProductionRule> getProductionRules() {
    return productionRules;
  }

  public void setProductionRules(String[][] rules) {
    for (String[] rule : rules) {
      this.productionRules.add(new PcfgProductionRule(rule));
    }
  }
  
  protected void appendRuleRepresentation(StringBuilder builder) {
    for (int i = 0; i < productionRules.size(); i++) {
      if (i > 0) {
        builder.append(", ");
      }
      builder.append(productionRules.get(i).toString());
    }
  }

  /** Creates a PcfgProductionRule from the string representation and adds it to
   * its set of rules. 
   * @throws ParseException */
  public void addProductionRule(String rule) throws ParseException {
    this.productionRules.add(new PcfgProductionRule(rule));
  }
}

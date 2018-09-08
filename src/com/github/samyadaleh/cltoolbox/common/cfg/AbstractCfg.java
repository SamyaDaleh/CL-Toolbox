package com.github.samyadaleh.cltoolbox.common.cfg;

import com.github.samyadaleh.cltoolbox.common.AbstractNTSGrammar;

public abstract class AbstractCfg
    extends AbstractNTSGrammar {

  @Override public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("G = <N, T, S, P>\n");
    builder.append("N = {").append(String.join(", ", this.getNonterminals()))
        .append("}\n");
    builder.append("T = {").append(String.join(", ", this.getTerminals()))
        .append("}\n");
    builder.append("S = ").append(this.getStartSymbol()).append("\n");
    builder.append("P = {");
    appendRuleRepresentation(builder);
    builder.append("}\n");
    return builder.toString();
  }

  protected abstract void appendRuleRepresentation(StringBuilder builder);
}

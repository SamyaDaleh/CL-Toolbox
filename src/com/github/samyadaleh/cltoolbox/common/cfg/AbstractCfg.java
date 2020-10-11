package com.github.samyadaleh.cltoolbox.common.cfg;

import com.github.samyadaleh.cltoolbox.common.AbstractNTSGrammar;

public abstract class AbstractCfg extends AbstractNTSGrammar {

  @Override public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("G = <N, T, S, P>\n");

    builder.append("N = {");
    if (this.getNonterminals() != null) {
      builder.append(String.join(", ", this.getNonterminals()));
    }
    builder.append("}\n").append("T = {");
    if (this.getTerminals() != null) {
      builder.append(String.join(", ", this.getTerminals()));
    }
    builder.append("}\n").append("S = ").append(this.getStartSymbol())
        .append("\n");
    builder.append("P = {");
    appendRuleRepresentation(builder);
    builder.append("}\n");
    return builder.toString();
  }

  protected abstract void appendRuleRepresentation(StringBuilder builder);

}

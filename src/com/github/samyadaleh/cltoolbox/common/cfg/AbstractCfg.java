package com.github.samyadaleh.cltoolbox.common.cfg;

import com.github.samyadaleh.cltoolbox.common.AbstractNTSGrammar;

import java.util.HashSet;
import java.util.Set;

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

  protected Set<String> getValidCategories() {
    Set<String> validCategories = new HashSet<>();
    validCategories.add("N");
    validCategories.add("T");
    validCategories.add("S");
    validCategories.add("P");
    validCategories.add("G");
    return validCategories;
  }

  protected abstract void appendRuleRepresentation(StringBuilder builder);
}

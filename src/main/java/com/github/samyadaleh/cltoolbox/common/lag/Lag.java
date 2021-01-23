package com.github.samyadaleh.cltoolbox.common.lag;

import com.github.samyadaleh.cltoolbox.common.parser.inner.InnerLagGrammarParser;

import java.io.BufferedReader;
import java.text.ParseException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Left Associative Grammar representation
 */
public class Lag {

  private LagWord[] lexicon;
  private Map<String, LagRule> lagRules = new LinkedHashMap<>();
  private LagState[] initialStates;
  private LagState[] finalStates;

  public Lag(BufferedReader in) throws ParseException {
    new InnerLagGrammarParser(this, in).invoke();
  }

  public Map<String, LagRule> getLagRules() {
    return lagRules;
  }

  public void setLagRules(Map<String, LagRule> lagRules) {
    this.lagRules = lagRules;
  }

  public LagState[] getInitialStates() {
    return initialStates;
  }

  public void setInitialStates(LagState[] initialStates) {
    this.initialStates = initialStates;
  }

  public LagState[] getFinalStates() {
    return finalStates;
  }

  public void setFinalStates(LagState[] finalStates) {
    this.finalStates = finalStates;
  }

  public LagWord[] getLexicon() {
    return lexicon;
  }

  public void setLexicon(LagWord[] lexicon) {
    this.lexicon = lexicon;
  }

  public void addRule(String key, LagRule lagRule) {
    this.lagRules.put(key, lagRule);
  }

  @Override public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("G = <W, C, LX, CO, RP, ST_S, ST_F>\n");

    builder.append("LX = {");
    if (getLexicon() != null) {
      boolean notfirst = false;
      for (LagWord word : getLexicon()) {
        if (notfirst) {
          builder.append(", ");
        }
        builder.append(word);
        notfirst = true;
      }
    }
    builder.append("}\n").append("ST_S = {");
    if (getInitialStates() != null) {
      boolean notfirst = false;
      for (LagState state : getInitialStates()) {
        if (notfirst) {
          builder.append(", ");
        }
        builder.append(state);
        notfirst = true;
      }
    }
    builder.append("}\n").append("RP = {");
      boolean notfirst = false;
      for (Map.Entry<String,LagRule> entry : getLagRules().entrySet()) {
        if (notfirst) {
          builder.append(",\n");
        }
        builder.append(entry.getKey()).append(" : ").append(entry.getValue());
        notfirst = true;
    }
    builder.append("}\n").append("ST_F = {");
    if (getFinalStates() != null) {
      notfirst = false;
      for (LagState state : getFinalStates()) {
        if (notfirst) {
          builder.append(", ");
        }
        builder.append(state);
        notfirst = true;
      }
    }
    builder.append("}\n");
    return builder.toString();
  }
}

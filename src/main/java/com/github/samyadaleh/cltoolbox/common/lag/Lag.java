package com.github.samyadaleh.cltoolbox.common.lag;

import com.github.samyadaleh.cltoolbox.common.parser.inner.InnerLagGrammarParser;

import java.io.BufferedReader;
import java.text.ParseException;
import java.util.Map;

public class Lag {

  private LagWord[] lexicon;
  private Map<String, LagRule> lagRules;
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
}

package com.github.samyadaleh.cltoolbox.common.finiteautomata;

import com.github.samyadaleh.cltoolbox.common.cfg.Cfg;
import com.github.samyadaleh.cltoolbox.common.cfg.CfgProductionRule;
import com.github.samyadaleh.cltoolbox.common.finiteautomata.util.ReverseLanguage;
import com.github.samyadaleh.cltoolbox.common.finiteautomata.util.UnifyFinalStates;

import java.text.ParseException;
import java.util.*;

/**
 * Representation of a nondeterministic finite automaton as defined by Hopcroft
 * 2011 p. 85.
 */
public class NondeterministicFiniteAutomaton {

  private String[] states;
  private String[] terminals;
  private String initialState;
  private String[] finalStates;
  private Map<String[], String[]> transitionFunction;

  /**
   * CTOR for programmatic construction.
   */
  public NondeterministicFiniteAutomaton() {
  }

  /**
   * Creates a finite automata from a rightlinear cfg.
   */
  public NondeterministicFiniteAutomaton(Cfg cfg) throws ParseException {
    if (cfg.isRightLinear()) {
      Map<String[], List<String>> newTransitionFunction = new HashMap<>();
      terminals = cfg.getTerminals();
      initialState = cfg.getStartSymbol();
      List<String> newStates =
          new ArrayList<>(Arrays.asList(cfg.getNonterminals()));
      int i = 0;
      String newFinalState = "q" + i;
      while (cfg.terminalsContain(newFinalState) || cfg
          .nonterminalsContain(newFinalState)) {
        i++;
        newFinalState = "q" + i;
      }
      newStates.add(newFinalState);
      finalStates = new String[] {newFinalState};
      for (CfgProductionRule rule : cfg.getProductionRules()) {
        String[] rhs = rule.getRhs();
        String lastState = rule.getLhs();
        if (rhs.length == 0 || rhs.length == 1 && "ε".equals(rhs[0])) {
          String[] key = new String[] {lastState, "ε"};
          if (!newTransitionFunction.containsKey(key)) {
            newTransitionFunction.put(key, new ArrayList<>());
          }
          newTransitionFunction.get(key).add(newFinalState);
          continue;
        }
        for (int j = 0; j < rhs.length; j++) {
          String rhsSym = rhs[j];
          if (cfg.nonterminalsContain(rhsSym)) {
            String[] key = new String[] {lastState, "ε"};
            if (!newTransitionFunction.containsKey(key)) {
              newTransitionFunction.put(key, new ArrayList<>());
            }
            newTransitionFunction.get(key).add(rhsSym);
            break;
          } else {
            String[] key = new String[] {lastState, rhsSym};
            if (!newTransitionFunction.containsKey(key)) {
              newTransitionFunction.put(key, new ArrayList<>());
            }
            if (j == rhs.length - 1) {
              newTransitionFunction.get(key).add(newFinalState);
            } else {
              String nextRhsSym = rhs[j + 1];
              if (cfg.nonterminalsContain(nextRhsSym)) {
                newTransitionFunction.get(key).add(nextRhsSym);
                break;
              } else {
                String newState = getNewState(newStates);
                newStates.add(newState);
                lastState = newState;
                newTransitionFunction.get(key).add(newState);
              }
            }
          }
        }
      }
      states = newStates.toArray(new String[0]);
      transitionFunction = new HashMap<>();
      for (Map.Entry<String[], List<String>> entry : newTransitionFunction
          .entrySet()) {
        transitionFunction
            .put(entry.getKey(), entry.getValue().toArray(new String[0]));

      }
    } else {
      throw new ParseException("Can create NFA from right-linear CFG only.", 0);
    }
  }

  private String getNewState(List<String> newStates) {
    int i = 0;
    String stateCandidate = "q" + i;
    while (newStates.contains(stateCandidate)) {
      i++;
      stateCandidate = "q" + i;
    }
    return stateCandidate;
  }

  public String[] transitionFunction(String state, String terminal) {
    return transitionFunction.get(new String[] {state, terminal});
  }

  public String[] getStates() {
    return states;
  }

  public String[] getTerminals() {
    return terminals;
  }

  public String getInitialState() {
    return initialState;
  }

  public String[] getFinalStates() {
    return finalStates;
  }

  public Map<String[], String[]> getTransitionFunction() {
    return transitionFunction;
  }

  public void setStates(String[] states) {
    this.states = states;
  }

  public void setTerminals(String[] terminals) {
    this.terminals = terminals;
  }

  public void setInitialState(String initialState) {
    this.initialState = initialState;
  }

  public void setFinalStates(String[] finalStates) {
    this.finalStates = finalStates;
  }

  public void setTransitionFunction(
      Map<String[], String[]> transitionFunction) {
    this.transitionFunction = transitionFunction;
  }

  public NondeterministicFiniteAutomaton getReversedLanguageAutomaton() {
    return ReverseLanguage.getReversedLanguageAutomaton(this);
  }

  public NondeterministicFiniteAutomaton getNfaWithSingleFinalState() {
    return UnifyFinalStates.getNfaWithSingleFinalState(this);
  }

  @Override public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("A = <Q, Σ, δ, q0, F>\n");

    builder.append("Q = {");
    if (this.getStates() != null) {
      builder.append(String.join(", ", this.getStates()));
    }
    builder.append("}\n").append("Σ = {");
    if (this.getTerminals() != null) {
      builder.append(String.join(", ", this.getTerminals()));
    }
    builder.append("}\n").append("δ = {");
    appendTransitionFunctionRepresentation(builder);
    builder.append("}\n").append("q0 = ").append(this.getInitialState())
        .append("\n");
    builder.append("F = {");
    if (this.getTerminals() != null) {
      builder.append(String.join(", ", this.getFinalStates()));
    }
    builder.append("}\n");
    return builder.toString();
  }

  private void appendTransitionFunctionRepresentation(StringBuilder builder) {
    boolean notFirst = false;
    for (Map.Entry<String[], String[]> entry : getTransitionFunction()
        .entrySet()) {
      if (notFirst) {
        builder.append(", ");
      }
      notFirst = true;
      builder.append("δ(").append(entry.getKey()[0]).append(", ")
          .append(entry.getKey()[1]).append(") = {")
          .append(String.join(", ", entry.getValue())).append("}");
    }
  }
}

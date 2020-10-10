package com.github.samyadaleh.cltoolbox.common.finiteautomata;

import com.github.samyadaleh.cltoolbox.common.cfg.Cfg;
import com.github.samyadaleh.cltoolbox.common.cfg.CfgProductionRule;

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
   * Creates a finite automata from a rightlinear cfg.
   */
  public NondeterministicFiniteAutomaton(Cfg cfg) throws ParseException {
    if (cfg.isRightLinear()) {
      Map<String[], List<String>> newTransitionFunction = new HashMap<>();
      terminals = cfg.getTerminals();
      initialState = cfg.getStartSymbol();
      List<String> newStates = Arrays.asList(cfg.getNonterminals());
      newStates.add("ε");
      finalStates = new String[]{"ε"};
      for (CfgProductionRule rule : cfg.getProductionRules()) {
        String[] rhs = rule.getRhs();
        String lastState = rule.getLhs();
        if (rhs.length == 0 || rhs.length == 1 && "ε".equals(rhs[0])) {
          String[] key = new String[] {lastState, "ε"};
          if (!newTransitionFunction.containsKey(key)) {
            newTransitionFunction.put(key, new ArrayList<>());
          }
          newTransitionFunction.get(key).add("ε");
          continue;
        }
        for (int i = 0; i < rhs.length; i++) {
          String rhsSym = rhs[i];
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
            if (i == rhs.length - 1) {
              newTransitionFunction.get(key).add("ε");
            } else {
              String nextRhsSym = rhs[i + 1];
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
    String stateCandidate = "q_" + i;
    while (newStates.contains(stateCandidate)) {
      i++;
      stateCandidate = "q_" + i;
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
}

package com.github.samyadaleh.cltoolbox.common.finiteautomata.util;

import com.github.samyadaleh.cltoolbox.common.finiteautomata.NondeterministicFiniteAutomaton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utility class carrying the logic to reverse a finite automaton.
 */
public class ReverseLanguage {

  /**
   * Returns an automaton for the reversed language: all transitions are
   * reversed and initial and final state are swapped. If there are several
   * final states, they are unified first.
   */
  public static NondeterministicFiniteAutomaton getReversedLanguageAutomaton(
      NondeterministicFiniteAutomaton nfa) {
    if (nfa.getFinalStates().length == 0) {
      return nfa;
    }
    NondeterministicFiniteAutomaton nfaNew =
        new NondeterministicFiniteAutomaton();
    NondeterministicFiniteAutomaton nfaOld = nfa.getNfaWithSingleFinalState();
    nfaNew.setFinalStates(new String[] {nfaOld.getInitialState()});
    nfaNew.setInitialState(nfaOld.getFinalStates()[0]);
    nfaNew.setTerminals(nfaOld.getTerminals());
    nfaNew.setStates(nfa.getStates());

    Map<String[], List<String>> newTransitionFunction = new HashMap<>();
    for (Map.Entry<String[], String[]> entry : nfaOld.getTransitionFunction()
        .entrySet()) {
      for (String transitionState : entry.getValue()) {
        String[] newKey = new String[] {transitionState, entry.getKey()[1]};
        if (!newTransitionFunction.containsKey(newKey)) {
          newTransitionFunction.put(newKey, new ArrayList<>());
        }
        newTransitionFunction.get(newKey).add(entry.getKey()[0]);
      }
    }
    Map<String[], String[]> transitionFunction = new HashMap<>();
    for (Map.Entry<String[], List<String>> entry : newTransitionFunction
        .entrySet()) {
      transitionFunction
          .put(entry.getKey(), entry.getValue().toArray(new String[0]));
    }
    nfaNew.setTransitionFunction(transitionFunction);
    return nfaNew;
  }
}

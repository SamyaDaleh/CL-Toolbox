package com.github.samyadaleh.cltoolbox.common.finiteautomata.util;

import com.github.samyadaleh.cltoolbox.common.ArrayUtils;
import com.github.samyadaleh.cltoolbox.common.finiteautomata.NondeterministicFiniteAutomaton;

import java.util.*;

/**
 * Replaces the final states of an automaton with a single one.
 */
public class UnifyFinalStates {

  /**
   * Returns a new automaton with only a single final state.
   */
  public static NondeterministicFiniteAutomaton getNfaWithSingleFinalState(
      NondeterministicFiniteAutomaton nfa) {
    if (nfa.getFinalStates().length < 2) {
      return nfa;
    }
    NondeterministicFiniteAutomaton nfaNew =
        new NondeterministicFiniteAutomaton();
    nfaNew.setTerminals(nfa.getTerminals());
    nfaNew.setInitialState(nfa.getInitialState());
    int i = 0;
    String newFinalState = "q" + i;
    while (ArrayUtils.contains(nfa.getStates(), newFinalState)) {
      i++;
      newFinalState = "q" + i;
    }
    nfaNew.setFinalStates(new String[] {newFinalState});
    List<String> newStates = Arrays.asList(nfa.getStates());
    newStates.add(newFinalState);
    nfaNew.setStates(newStates.toArray(new String[0]));
    Map<String[], List<String>> newTransitionFunction = new HashMap<>();
    for (Map.Entry<String[], String[]> entry : nfa.getTransitionFunction()
        .entrySet()) {
      newTransitionFunction
          .put(entry.getKey(), Arrays.asList(entry.getValue()));
    }
    for (String formerFinalState : nfa.getFinalStates()) {
      String[] key = new String[] {formerFinalState, "ε"};
      if (!newTransitionFunction.containsKey(key)) {
        newTransitionFunction.put(key, new ArrayList<>());
      }
      newTransitionFunction.get(key).add(newFinalState);
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

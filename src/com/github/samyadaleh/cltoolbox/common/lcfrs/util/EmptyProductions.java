package com.github.samyadaleh.cltoolbox.common.lcfrs.util;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Objects;

import com.github.samyadaleh.cltoolbox.common.lcfrs.Clause;
import com.github.samyadaleh.cltoolbox.common.lcfrs.Predicate;
import com.github.samyadaleh.cltoolbox.common.lcfrs.Srcg;

public class EmptyProductions {

  /** Returns true if there is at least one clause that contains the empty
   * string in one of its lhs arguments, except if it is the start symbol in
   * which case it must not occur on any rhs. */
  public static boolean hasEpsilonProductions(Srcg srcg) {
    for (Clause clause : srcg.getClauses()) {
      for (String[] argument : clause.getLhs().getSymbols()) {
        if (!argument[0].equals("")) {
          continue;
        }
        if (clause.getLhs().getNonterminal().equals(srcg.getStartSymbol())) {
          for (Clause clause2 : srcg.getClauses()) {
            for (Predicate rhsPred : clause2.getRhs()) {
              if (rhsPred.getNonterminal().equals(srcg.getStartSymbol())) {
                return true;
              }
            }
          }
        } else {
          return true;
        }
      }
    }
    return false;
  }

  /** Return an equivalent sRCG without epsilon as any lhs argument. */
  public static Srcg getSrcgWithoutEmptyProductions(Srcg oldSrcg)
    throws ParseException {
    ArrayList<String[]> epsilonCandidates = getEpsilonCandidates(oldSrcg);
    Srcg newSrcg = new Srcg();
    newSrcg.setVariables(oldSrcg.getVariables());
    newSrcg.setTerminals(oldSrcg.getTerminals());
    newSrcg.setStartSymbol(oldSrcg.getStartSymbol());
    ArrayList<String> newNts = new ArrayList<String>();
    for (String[] candidate : epsilonCandidates) {
      if (candidate[1].contains("1")) {
        newNts.add(candidate[0] + "^" + candidate[1]);
      }
    }
    for (String[] candidate : epsilonCandidates) {
      if (candidate[0].equals(oldSrcg.getStartSymbol())
        && candidate[1].length() == 1) {
        StringBuilder newS = new StringBuilder("S'");
        while (oldSrcg.nonTerminalsContain(newS.toString())) {
          newS.append('\'');
        }
        newSrcg.setStartSymbol(newS.toString());
        if (!newNts.contains(newS.toString())) {
          newNts.add(newS.toString());
        }
        if (candidate[1].equals("0")) {
          newSrcg.addClause(newS + "(ε) -> ε");
        } else {
          newSrcg.addClause(newS + "(" + oldSrcg.getVariables()[0] + ") -> "
            + oldSrcg.getStartSymbol() + "^1" + "(" + oldSrcg.getVariables()[0]
            + ")");
        }
      }
    }
    for (Clause clause : oldSrcg.getClauses()) {
      for (ArrayList<String[]> combination : getCombinationsForRhs(
        epsilonCandidates, clause)) {
        Clause newClause = getClauseForEpsilonCombination(clause, combination);
        Predicate oldLhs = newClause.getLhs();
        String jotaLhs = getJotaForPredicate(oldLhs);
        if (jotaLhs.contains("1")) {
          Predicate newLhs =
            getPredicateWithoutEmptyArguments(jotaLhs, newClause.getLhs());
          newClause.setLhs(newLhs);
          newSrcg.addClause(newClause);
        }
      }
      if (!clause.getRhs().isEmpty()) {
        continue;
      }
      Clause newClause = new Clause(clause.toString());
      String jotaLhs = getJotaForPredicate(newClause.getLhs());
      if (jotaLhs.contains("1")) {
        Predicate newLhs =
          getPredicateWithoutEmptyArguments(jotaLhs, newClause.getLhs());
        newClause.setLhs(newLhs);
        newSrcg.addClause(newClause);
      }
    }
    newSrcg.setNonterminals(newNts.toArray(new String[newNts.size()]));
    return newSrcg;
  }

  private static Clause getClauseForEpsilonCombination(Clause clause,
    ArrayList<String[]> combination) throws ParseException {
    Clause newClause = new Clause(clause.toString());
    int predDeleted = 0;
    for (int i = 0; i < newClause.getRhs().size(); i++) {
      Predicate oldRhs = newClause.getRhs().get(i);
      StringBuilder newRhsPred = new StringBuilder();
      newRhsPred.append(combination.get(i)[0]).append('^')
        .append(combination.get(i)[1]).append('(');
      String jota = combination.get(i)[1];
      boolean oneEncountered = false;
      for (int j = 0; j < jota.length(); j++) {
        if (jota.charAt(j) == '0') {
          String epsilonVariable = oldRhs.getSymAt(j + 1, 0);
          Predicate oldLhs = newClause.getLhs();
          Predicate newLhs =
            removeVariableFromPredicate(epsilonVariable, oldLhs);
          newClause.setLhs(newLhs);
        } else {
          if (oneEncountered) {
            newRhsPred.append(',');
          }
          newRhsPred.append(oldRhs.getArgumentByIndex(j + 1)[0]);
          oneEncountered = true;
        }
      }
      newRhsPred.append(')');
      Predicate newRhs = new Predicate(newRhsPred.toString());
      if (oneEncountered) {
        newClause.getRhs().set(i, newRhs);
      } else {
        newClause.getRhs().remove(i - predDeleted);
        predDeleted++;
      }
    }
    return newClause;
  }

  private static Predicate getPredicateWithoutEmptyArguments(String jotaLhs,
    Predicate oldLhs) throws ParseException {
    StringBuilder newLhsPred = new StringBuilder();
    newLhsPred.append(oldLhs.getNonterminal()).append('^').append(jotaLhs)
      .append('(');
    boolean argAdded = false;
    for (int i = 0; i < oldLhs.getDim(); i++) {
      if (Objects.equals(oldLhs.getArgumentByIndex(i + 1)[0], "")) {
        continue;
      }
      if (argAdded) {
        newLhsPred.append(',');
      }
      argAdded = true;
      for (int j = 0; j < oldLhs.getArgumentByIndex(i + 1).length; j++) {
        if (j > 0) {
          newLhsPred.append(' ');
        }
        newLhsPred.append(oldLhs.getSymAt(i + 1, j));
      }
    }
    newLhsPred.append(')');
    return new Predicate(newLhsPred.toString());
  }

  private static Predicate removeVariableFromPredicate(String epsilonVariable,
    Predicate oldLhs) throws ParseException {
    StringBuilder newLhsPred = new StringBuilder();
    int[] indices = oldLhs.find(epsilonVariable);
    newLhsPred.append(oldLhs.getNonterminal()).append('(');
    for (int k = 0; k < oldLhs.getDim(); k++) {
      if (k > 0) {
        newLhsPred.append(',');
      }
      for (int l = 0; l < oldLhs.getArgumentByIndex(k + 1).length; l++) {
        if (k + 1 != indices[0] || l != indices[1]) {
          if (l > 0) {
            newLhsPred.append(' ');
          }
          newLhsPred.append(oldLhs.getSymAt(k + 1, l));
        }
      }
    }
    newLhsPred.append(')');
    return new Predicate(newLhsPred.toString());
  }

  private static String getJotaForPredicate(Predicate oldLhs) {
    StringBuilder jotaLhs = new StringBuilder();
    for (int i = 0; i < oldLhs.getDim(); i++) {
      if (oldLhs.getArgumentByIndex(i + 1)[0].equals("")) {
        jotaLhs.append('0');
      } else {
        jotaLhs.append('1');
      }
    }
    return jotaLhs.toString();
  }

  /** Take a list of epsilonCandidates and a clause, returns a list of
   * combinations of candidates for the rhs of the clause. Let's say there is a
   * clause like S -> A B C. A has 2 possibilities of containing epsilon, B has
   * 3, C has 0. Returns 6 lists considering all combinations which contain an
   * empty placeholder for C. */
  @SuppressWarnings({"serial", "unchecked"}) private static
    ArrayList<ArrayList<String[]>> getCombinationsForRhs(
      ArrayList<String[]> epsilonCandidates, Clause clause) {
    ArrayList<ArrayList<String[]>> combinations =
      new ArrayList<ArrayList<String[]>>();
    for (int i = 0; i < clause.getRhs().size(); i++) {
      if (i == 0) {
        boolean somethingAppended = false;
        for (String[] candidate : epsilonCandidates) {
          if (candidate[0].equals(clause.getRhs().get(i).getNonterminal())
            && candidate[1].length() == clause.getRhs().get(i).getDim()) {
            somethingAppended = true;
            combinations.add(new ArrayList<String[]>() {
              {
                add(candidate);
              }
            });
          }
        }
        if (!somethingAppended) {
          combinations.add(new ArrayList<String[]>() {
            {
              add(new String[] {});
            }
          });
        }
      } else {
        boolean somethingAppended = false;
        ArrayList<ArrayList<String[]>> newCombinations =
          new ArrayList<ArrayList<String[]>>();
        for (String[] candidate : epsilonCandidates) {
          if (candidate[0].equals(clause.getRhs().get(i).getNonterminal())) {
            somethingAppended = true;
            for (ArrayList<String[]> combination : combinations) {
              newCombinations.add((ArrayList<String[]>) combination.clone());
              newCombinations.get(newCombinations.size() - 1).add(candidate);
            }
          }
        }
        if (!somethingAppended) {
          for (ArrayList<String[]> combination : combinations) {
            combination.add(new String[] {});
          }
        } else {
          combinations = newCombinations;
        }
      }
    }
    return combinations;
  }

  /** Returns a list of nonterminals whose predicates can have arguments. The
   * list consists of arrays of length 2. The first entry contains the
   * respective nonterminal. The second entry contains a vector (String) of 0
   * and 1 that specifies which arguments can become empty. */
  private static ArrayList<String[]> getEpsilonCandidates(Srcg srcg) {
    ArrayList<String[]> epsilonCandidates = getDirectEpsilonCandidates(srcg);
    boolean changed = true;
    while (changed) {
      changed = false;
      for (Clause clause : srcg.getClauses()) {
        for (int i = 0; i < epsilonCandidates.size(); i++) {
          String[] candidate = epsilonCandidates.get(i);
          Predicate clauseLhs = clause.getLhs();
          String[] newCandidate = getEpsilonCandidateForLhsWithoutCandidate(
            clause, candidate, clauseLhs, srcg);
          boolean found = false;
          for (String[] oldCandidate : epsilonCandidates) {
            if (oldCandidate[0].equals(newCandidate[0])
              && oldCandidate[1].equals(newCandidate[1])) {
              found = true;
              break;
            }
          }
          if (!found) {
            epsilonCandidates.add(newCandidate);
            changed = true;
          }
        }
      }
    }
    return epsilonCandidates;
  }

  private static String[] getEpsilonCandidateForLhsWithoutCandidate(
    Clause clause, String[] candidate, Predicate clauseLhs, Srcg srcg) {
    ArrayList<String> lhsVector = new ArrayList<String>();
    for (int j = 0; j < clauseLhs.getDim(); j++) {
      String[] argument = clauseLhs.getArgumentByIndex(j + 1);
      StringBuilder newArgument = new StringBuilder();
      for (int k = 0; k < argument.length; k++) {
        if (k > 0) {
          newArgument.append(' ');
        }
        String element = argument[k];
        if (srcg.terminalsContain(element)) {
          newArgument.append(element);
        } else {
          for (Predicate rhsPred : clause.getRhs()) {
            int[] indices = rhsPred.find(element);
            if (indices[0] == -1) {
              continue;
            }
            if (rhsPred.getNonterminal().equals(candidate[0])
              && candidate[1].length() == rhsPred.getDim()
              && candidate[1].charAt(indices[0] - 1) == '0') {
              newArgument.append("");
            } else {
              newArgument.append(element);
            }
          }
        }
      }
      lhsVector.add(newArgument.toString());
    }
    StringBuilder jota = new StringBuilder();
    for (String aLhsVector : lhsVector) {
      if (aLhsVector.equals("")) {
        jota.append('0');
      } else {
        jota.append('1');
      }
    }
    return new String[] {clause.getLhs().getNonterminal(), jota.toString()};
  }

  private static ArrayList<String[]> getDirectEpsilonCandidates(Srcg srcg) {
    ArrayList<String[]> epsilonCandidates = new ArrayList<String[]>();
    for (Clause clause : srcg.getClauses()) {
      if (clause.getRhs().isEmpty()) {
        String jota = getJotaForPredicate(clause.getLhs());

        String[] newPair =
          new String[] {clause.getLhs().getNonterminal(), jota};
        boolean found = false;
        for (String[] oldPair : epsilonCandidates) {
          if (oldPair[0].equals(newPair[0])) {
            if (oldPair[1].equals(newPair[1])) {
              found = true;
              break;
            }
          }
        }
        if (!found) {
          epsilonCandidates.add(newPair);
        }
      }
    }
    return epsilonCandidates;
  }
}

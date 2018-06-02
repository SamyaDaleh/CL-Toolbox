package common.lcfrs.util;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import common.ArrayUtils;
import common.lcfrs.Clause;
import common.lcfrs.Predicate;
import common.lcfrs.Srcg;

public class Binarization {

  /** Returns true if each rhs contains at most two predicates. */
  public static boolean isBinarized(Srcg srcg) {
    boolean binarized = true;
    for (Clause clause : srcg.getClauses()) {
      if (clause.getRhs().size() > 2) {
        binarized = false;
      }
    }
    return binarized;
  }

  /**
   * Returns an equivalent Srcg were all clauses have at most two rhs
   * predicates.
   */
  public static Srcg getBinarizedSrcg(Srcg oldSrcg) throws ParseException {
    Srcg newSrcg = new Srcg();
    newSrcg.setStartSymbol(oldSrcg.getStartSymbol());
    newSrcg.setTerminals(oldSrcg.getTerminals());
    newSrcg.setVariables(oldSrcg.getVariables());
    ArrayList<String> newNonterminals = new ArrayList<String>();
    Collections.addAll(newNonterminals, oldSrcg.getNonterminals());
    for (Clause clause : oldSrcg.getClauses()) {
      if (clause.getRhs().size() <= 2) {
        newSrcg.addClause(clause);
      } else {
        ArrayList<Clause> r = new ArrayList<Clause>();
        List<Clause> clausesToBeFurtherReduced = new LinkedList<Clause>();
        clausesToBeFurtherReduced.add(clause);
        while (clausesToBeFurtherReduced.size() > 0) {
          if (clausesToBeFurtherReduced.get(0).getRhs().size() <= 2) {
            r.add(clausesToBeFurtherReduced.get(0));
            clausesToBeFurtherReduced.remove(0);
            continue;
          }
          Integer[] k = getBestCharacteristicStringPos(oldSrcg.getVariables(),
            clausesToBeFurtherReduced.get(0));
          StringBuilder nonterminaltoDeriveFrom = new StringBuilder();
          for (int entry : k) {
            nonterminaltoDeriveFrom.append(clausesToBeFurtherReduced.get(0)
              .getRhs().get(entry).getNonterminal());
          }
          String newNt =
            getNewNonterminal(oldSrcg, nonterminaltoDeriveFrom.toString());
          newNonterminals.add(newNt);
          Clause clauseCandidate =
            getReducedClause(clausesToBeFurtherReduced.get(0), k, newNt,
              clause.getLhs(), oldSrcg);
          if (clauseCandidate.getRhs().size() <= 2) {
            r.add(clauseCandidate);
          } else {
            clausesToBeFurtherReduced.add(clauseCandidate);
          }
          List<Predicate> clauseRhs = clausesToBeFurtherReduced.get(0).getRhs();
          List<Predicate> lastCreatedRhs = clauseCandidate.getRhs();
          Predicate lastNewPredicate =
            lastCreatedRhs.get(lastCreatedRhs.size() - 1);
          StringBuilder newClause =
            new StringBuilder(lastNewPredicate + " -> ");
          clausesToBeFurtherReduced.remove(0);
          for (int l = 0; l < clauseRhs.size(); l++) {
            if (!ArrayUtils.contains(k, l)) {
              newClause.append(clauseRhs.get(l));
            }
          }
          clausesToBeFurtherReduced.add(new Clause(newClause.toString()));
        }
        for (Clause rbar : r) {
          newSrcg.addClause(getClauseReplaceLongRhsArguments(rbar));
        }
      }
    }
    newSrcg.setNonterminals(
      newNonterminals.toArray(new String[newNonterminals.size()]));
    return newSrcg;
  }

  private static String getNewNonterminal(Srcg oldSrcg, String nonterminal) {
    String newNt;
    int i = 1;
    newNt = nonterminal + String.valueOf(i);
    i++;
    while (oldSrcg.nonTerminalsContain(newNt)) {
      newNt = nonterminal + String.valueOf(i);
      i++;
    }
    return newNt;
  }

  private static Integer[] getBestCharacteristicStringPos(String[] variables,
    Clause clause) {
    Integer[] posBest = new Integer[] {0};
    int bestArityAndVars = Integer.MAX_VALUE;
    for (int i = 0; i < clause.getRhs().size(); i++) {
      String characteristicString =
        getCharacteristicString(clause, new int[] {i});
      int arity =
        getArityOfCharacteristicString(variables, characteristicString);
      int dim = clause.getRhs().get(i).getDim();
      if (arity + dim < bestArityAndVars) {
        posBest = new Integer[] {i};
        bestArityAndVars = arity + dim;
      }
    }
    if (clause.getRhs().size() > 3) {
      for (int i = 0; i < clause.getRhs().size(); i++) {
        for (int j = i + 1; j < clause.getRhs().size(); j++) {
          String characteristicString =
            getCharacteristicString(clause, new int[] {i, j});
          int arity =
            getArityOfCharacteristicString(variables, characteristicString);
          int dim = clause.getRhs().get(i).getDim();
          if (arity + dim < bestArityAndVars) {
            posBest = new Integer[] {i, j};
            bestArityAndVars = arity + dim;
          }
        }
      }
    }
    return posBest;
  }

  private static int getArityOfCharacteristicString(String[] variables,
    String characteristicString) {
    int arity = 0;
    boolean prevVar = false;
    Set<String> variablesSet = new HashSet<String>(Arrays.asList(variables));
    for (String token : characteristicString.split(" ")) {
      if (variablesSet.contains(token)) {
        if (!prevVar) {
          prevVar = true;
          arity++;
        }
      } else {
        prevVar = false;
      }
    }
    return arity;
  }

  private static String getCharacteristicString(Clause clause, int[] l) {
    StringBuilder characteristicString = new StringBuilder();
    Predicate lhs = clause.getLhs();
    for (int i = 0; i < lhs.getDim(); i++) {
      if (i > 0) {
        characteristicString.append(' ').append('$');
      }
      for (String symbol : lhs.getArgumentByIndex(i + 1)) {
        int[] pos = new int[] {-1, -1};
        for (int j = 0; j < l.length && pos[0] == -1; j++) {
          pos = clause.getRhs().get(l[j]).find(symbol);
        }
        if (pos[0] == -1) {
          characteristicString.append(' ').append(symbol);
        } else {
          characteristicString.append(' ').append('$');
        }
      }
    }
    return characteristicString.toString();
  }

  private static Clause getClauseReplaceLongRhsArguments(Clause rbar)
    throws ParseException {
    StringBuilder newClause = new StringBuilder();
    newClause.append(rbar.getLhs().getNonterminal()).append('(');
    for (int i = 0; i < rbar.getLhs().getDim(); i++) {
      if (i > 0) {
        newClause.append(',');
      }
      for (int j = 0; j < rbar.getLhs().getSymbols()[i].length; j++) {
        if (newClause.charAt(newClause.length() - 1) != ' ') {
          newClause.append(' ');
        }
        boolean found = false;
        String interestingSymbol = rbar.getLhs().getSymAt(i + 1, j);
        for (Predicate rhsPred : rbar.getRhs()) {
          int[] indices = rhsPred.find(interestingSymbol);
          if (indices[0] >= 0) {
            if (indices[1] == 0) {
              newClause.append(interestingSymbol);
            }
            found = true;
            break;
          }
        }
        if (!found) {
          newClause.append(interestingSymbol);
        }
      }
    }
    newClause.append(") -> ");
    for (int i = 0; i < rbar.getRhs().size(); i++) {
      Predicate rhsPred = rbar.getRhs().get(i);
      newClause.append(rhsPred.getNonterminal()).append('(');
      for (int j = 0; j < rhsPred.getDim(); j++) {
        if (j > 0) {
          newClause.append(',');
        }
        newClause.append(rhsPred.getSymbols()[j][0]);
      }
      newClause.append(") ");
    }
    return new Clause(newClause.toString());
  }

  private static Clause getReducedClause(Clause clause, Integer[] j,
    String newNt, Predicate predicate, Srcg srcg) throws ParseException {
    StringBuilder newClause =
      new StringBuilder(String.valueOf(predicate) + " ->");
    List<Predicate> rhsPredToReduceBy = new LinkedList<Predicate>();
    for (int entry : j) {
      newClause.append(' ').append(clause.getRhs().get(entry).toString());
      rhsPredToReduceBy.add(clause.getRhs().get(entry));
    }
    newClause.append(' ').append(newNt).append('(').append(
      getVectorLhsReducedByRhsPredAsString(predicate, rhsPredToReduceBy, srcg))
      .append(')');
    return new Clause(newClause.toString());
  }

  /**
   * reduction of a1 e T U V*k1 is a2 e V*k2 where all variables in a2 occur in
   * a1 as follows: take all V from a1 in their order that are not in a2 while
   * starting a new element in the resulting vector whenever a variable is, in
   * a1, in a different element than the preceding variable in the result or in
   * the same element but not adjacent to it. example: <a X1, X2, b X3> reduced
   * by <X2> = <X1, X3> example: <a X1 X2 b X3> reduced by <X2> = <X1, X3>
   */
  private static String getVectorLhsReducedByRhsPredAsString(Predicate lhs,
    List<Predicate> rhss, Srcg srcg) {
    StringBuilder vector = new StringBuilder();
    boolean cut = false;
    for (int i = 0; i < lhs.getDim(); i++) {
      for (int j = 0; j < lhs.getArgumentByIndex(i + 1).length; j++) {
        String interestingSymbol = lhs.getSymAt(i + 1, j);
        int[] indices = new int[] {-1, -1};
        for (int k = 0; k < rhss.size() && indices[0] == -1; k++) {
          indices = rhss.get(k).find(interestingSymbol);
        }
        if (cut) {
          cut = false;
          if (vector.length() > 0
            && vector.charAt(vector.length() - 1) != ',') {
            vector.append(',');
          }
        }
        if (indices[0] == -1) {
          if (!srcg.terminalsContain(interestingSymbol)) {
            if (vector.length() > 0) {
              vector.append(' ');
            }
            vector.append(lhs.getSymAt(i + 1, j));
          } else {
            cut = true;
          }
        } else {
          cut = true;
        }
      }
    }
    return vector.toString();
  }
}

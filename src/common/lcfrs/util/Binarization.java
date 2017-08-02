package common.lcfrs.util;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

  /** Returns an equivalent Srcg were all clauses have at most two rhs
   * predicates. */
  public static Srcg getBinarizedSrcg(Srcg oldSrcg) throws ParseException {
    Srcg newSrcg = new Srcg();
    newSrcg.setStartSymbol(oldSrcg.getStartSymbol());
    newSrcg.setTerminals(oldSrcg.getTerminals());
    newSrcg.setVariables(oldSrcg.getVariables());
    ArrayList<String> newNonterminals = new ArrayList<String>();
    Collections.addAll(newNonterminals, oldSrcg.getNonterminals());
    for (Clause clause : oldSrcg.getClauses()) {
      if (clause.getRhs().size() > 2) {
        ArrayList<Clause> r = new ArrayList<Clause>();
        int i = 1;
        ArrayList<String> newNtsLocal = new ArrayList<String>();
        for (int j = 1; j <= clause.getRhs().size() - 2; j++) {
          String newNt = "C" + String.valueOf(i);
          i++;
          while (oldSrcg.nonTerminalsContain(newNt)) {
            newNt = "C" + String.valueOf(i);
            i++;
          }
          newNtsLocal.add(newNt);
        }
        newNonterminals.addAll(newNtsLocal);
        r.add(getReducedClause(clause, 0, newNtsLocal.get(0), clause.getLhs(),
          oldSrcg));
        for (int j = 1; j < clause.getRhs().size() - 2; j++) {
          List<Predicate> lastRhs = r.get(r.size() - 1).getRhs();
          r.add(getReducedClause(clause, j, newNtsLocal.get(j),
            lastRhs.get(lastRhs.size() - 1), oldSrcg));
        }
        List<Predicate> clauseRhs = clause.getRhs();
        List<Predicate> lastCreatedRhs = r.get(r.size() - 1).getRhs();
        Predicate lastNewPredicate = lastCreatedRhs.get(lastCreatedRhs.size()-1);
        r.add(new Clause(lastNewPredicate + " -> "
          + clauseRhs.get(clauseRhs.size() - 2)
          + clauseRhs.get(clauseRhs.size() - 1)));
        for (Clause rbar : r) {
          newSrcg.addClause(getClauseReplaceLongRhsArguments(rbar));
        }
      } else {
        newSrcg.addClause(clause);
      }
    }
    newSrcg.setNonterminals(
      newNonterminals.toArray(new String[newNonterminals.size()]));
    return newSrcg;
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
        for (Predicate rhsPred : rbar.getRhs()) {
          String interestingSymbol = rbar.getLhs().getSymAt(i + 1, j);
          int[] indices = rhsPred.find(interestingSymbol);
          if (indices[0] >= 0) {
            if (indices[1] == 0) {
              newClause.append(interestingSymbol);
            }
            break;
          }
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

  private static Clause getReducedClause(Clause clause, int j, String newNt,
    Predicate predicate, Srcg srcg) throws ParseException {
    String newClause =
        String.valueOf(predicate) + " -> " + clause.getRhs().get(0).toString()
            + ' ' + newNt + '(' + getVectorLhsReducedByRhsPredAsString(
            predicate, clause.getRhs().get(j), srcg) + ')';
    return new Clause(newClause);
  }

  /** reduction of a1 e T U V*k1 is a2 e V*k2 where all variables in a2 occur in
   * a1 as follows: take all V from a1 in their order that are not in a2 while
   * starting a new element in the resulting vector whenever a variable is, in
   * a1, in a different element than the preceding variable in the result or in
   * the same element but not adjacent to it. example: <a X1, X2, b X3> reduced
   * by <X2> = <X1, X3> example: <a X1 X2 b X3> reduced by <X2> = <X1, X3> */
  private static String getVectorLhsReducedByRhsPredAsString(Predicate lhs,
    Predicate rhs, Srcg srcg) {
    StringBuilder vector = new StringBuilder();
    boolean cut = false;
    for (int i = 0; i < lhs.getDim(); i++) {
      for (int j = 0; j < lhs.getArgumentByIndex(i + 1).length; j++) {
        String interestingSymbol = lhs.getSymAt(i + 1, j);
        int[] indices = rhs.find(interestingSymbol);
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

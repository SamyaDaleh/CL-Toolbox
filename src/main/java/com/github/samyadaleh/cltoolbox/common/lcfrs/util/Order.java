package com.github.samyadaleh.cltoolbox.common.lcfrs.util;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;

import com.github.samyadaleh.cltoolbox.common.lcfrs.Clause;
import com.github.samyadaleh.cltoolbox.common.lcfrs.Predicate;
import com.github.samyadaleh.cltoolbox.common.lcfrs.Srcg;

import static com.github.samyadaleh.cltoolbox.common.Constants.EPSILON;

public class Order {

  public static final char ORDER_MARKING_LEFT = '[';
  public static final char ORDER_MARKING_RIGHT = ']';

  /**
   * Returns true if all variables in rhs predicates appear in the same order as
   * in the lhs predicate.
   */
  public static boolean isOrdered(Srcg srcg) {
    for (Clause clause : srcg.getClauses()) {
      boolean clauseIsOrdered = isOrdered(clause);
      if (!clauseIsOrdered) {
        return false;
      }
    }
    return true;
  }

  /**
   * Returns true if all predicates in rhs of clause are ordered regarding the
   * lhs predicate.
   */
  private static boolean isOrdered(Clause clause) {
    for (Predicate rhsPred : clause.getRhs()) {
      boolean predicateIsOrdered = isOrdered(clause, rhsPred);
      if (!predicateIsOrdered) {
        return false;
      }
    }
    return true;
  }

  /**
   * Return true if variables in rhs predicate occur in same order as in lhs
   * predicate of clause.
   */
  private static boolean isOrdered(Clause clause, Predicate rhsPred) {
    ArrayList<Integer> posInLhs = getOrderInLhs(clause, rhsPred);
    for (int i = 1; i < posInLhs.size(); i++) {
      if (!(posInLhs.get(i - 1) < posInLhs.get(i))) {
        return false;
      }
    }
    return true;
  }

  private static ArrayList<Integer> getOrderInLhs(Clause clause,
      Predicate rhsPred) {
    ArrayList<Integer> posInLhs = new ArrayList<>();
    for (String symbol : rhsPred.getSymbolsAsPlainArray()) {
      int[] indices = clause.getLhs().find(symbol);
      int abspos = clause.getLhs().getAbsolutePos(indices[0], indices[1]);
      posInLhs.add(abspos);
    }
    return posInLhs;
  }

  /**
   * Returns an equivalent sRCG where the variables are ordered in each rule for
   * each predicate. Might leave useless nonterminals behind.
   */
  public static Srcg getOrderedSrcg(Srcg oldSrcg) throws ParseException {
    Srcg newSrcg = new Srcg();
    newSrcg.setTerminals(oldSrcg.getTerminals());
    newSrcg.setVariables(oldSrcg.getVariables());
    newSrcg.setStartSymbol(
        oldSrcg.getStartSymbol() + "^" + ORDER_MARKING_LEFT + "1"
            + ORDER_MARKING_RIGHT);
    ArrayList<String> newNonterminals = new ArrayList<>();
    for (Clause clause : oldSrcg.getClauses()) {
      newSrcg.addClause(getClauseWithPositionVectors(clause, newNonterminals));
    }
    newSrcg.setNonterminals(newNonterminals.toArray(new String[0]));
    boolean change = true;
    while (change) {
      change = false;
      for (int j = 0; j < newSrcg.getClauses().size(); j++) {
        Clause clause = newSrcg.getClauses().get(j);
        for (int i = 0; i < newSrcg.getClauses().get(j).getRhs().size(); i++) {
          Predicate rhsPred = clause.getRhs().get(i);
          String oldNt = rhsPred.getNonterminal();
          if (isOrdered(clause, rhsPred)) {
            continue;
          }
          change = true;
          ArrayList<Integer> orderVector =
              getNormalizedPos(getOrderInLhs(clause, rhsPred));
          StringBuilder newNt = new StringBuilder();
          newNt.append(oldNt).append("^" + ORDER_MARKING_LEFT);
          for (int k = 0; k < orderVector.size(); k++) {
            if (k > 0) {
              newNt.append(',');
            }
            newNt.append(String.valueOf(orderVector.get(k)));
          }
          newNt.append(ORDER_MARKING_RIGHT);

          int pos1 = newNt.toString().indexOf('^');
          int pos2 = newNt.toString().indexOf('^', pos1 + 1);
          if (pos2 != -1) {
            newNt = handleSecondReordering(newNt, pos1, pos2);
          }
          String newNtString = newNt.toString();
          clause.getRhs()
              .set(i, orderedPredicate(rhsPred, newNtString, orderVector));
          if (newSrcg.nonterminalsContain(newNt.toString())) {
            continue;
          }
          ArrayList<String> newNts = new ArrayList<>();
          Collections.addAll(newNts, newSrcg.getNonterminals());
          newNts.add(newNtString);
          newSrcg.setNonterminals(newNts.toArray(new String[0]));
          replaceUnorderedPredicateInAllLhs(newSrcg, oldNt, orderVector,
              newNtString);
        }
      }
    }
    return newSrcg;
  }

  private static StringBuilder handleSecondReordering(StringBuilder newNt,
      int pos1, int pos2) {
    StringBuilder newestNt = new StringBuilder();
    newestNt.append(newNt.substring(0, pos1)).append("^" + ORDER_MARKING_LEFT);
    String[] vec1 = newNt.substring(pos1 + 2, pos2 - 1).split(",");
    String[] vec2 = newNt.substring(pos2 + 2, newNt.length() - 1).split(",");
    for (int k = 1; k <= vec1.length; k++) {
      for (int l = 0; l < vec1.length; l++) {
        if (Integer.parseInt(vec2[l]) == k) {
          if (k > 1) {
            newestNt.append(',');
          }
          newestNt.append(vec1[l]);
          break;
        }
      }
    }
    newestNt.append(ORDER_MARKING_RIGHT);
    newNt = newestNt;
    return newNt;
  }

  private static void replaceUnorderedPredicateInAllLhs(Srcg newSrcg,
      String oldNt, ArrayList<Integer> orderVector, String newNtString)
      throws ParseException {
    for (int l = 0; l < newSrcg.getClauses().size(); l++) {
      Clause clause2 = newSrcg.getClauses().get(l);
      if (clause2.getLhs().getNonterminal().equals(oldNt)) {
        String clause2String = clause2.toString();
        int ibrack = clause2String.indexOf(')');
        String newClause =
            orderedPredicate(clause2.getLhs(), newNtString, orderVector)
                + clause2String.substring(ibrack + 1);
        newSrcg.addClause(newClause);
      }
    }
  }

  /**
   * Returns a new clause where all nonterminals where replaced by nonterminal +
   * order vector. Adds all generated nonterminals to list if they are not there
   * yet.
   */
  private static String getClauseWithPositionVectors(Clause clause,
      ArrayList<String> newNonterminals) {
    StringBuilder newClause = new StringBuilder();
    StringBuilder newNt = new StringBuilder();
    newNt.append(clause.getLhs().getNonterminal())
        .append("^" + ORDER_MARKING_LEFT);
    for (int i = 1; i <= clause.getLhs().getDim(); i++) {
      if (i > 1) {
        newNt.append(',');
      }
      newNt.append(String.valueOf(i));
    }
    newNt.append(ORDER_MARKING_RIGHT);
    if (!newNonterminals.contains(newNt.toString())) {
      newNonterminals.add(newNt.toString());
    }
    newClause.append(newNt);
    String predRep = clause.getLhs().toString();
    int ibrack = predRep.indexOf('(');
    newClause.append(predRep.substring(ibrack));
    newClause.append(" ->");
    if (clause.getRhs().isEmpty()) {
      newClause.append(EPSILON);
    }
    for (Predicate rhsPred : clause.getRhs()) {
      newNt = new StringBuilder();
      newNt.append(rhsPred.getNonterminal()).append("^" + ORDER_MARKING_LEFT);
      for (int i = 1; i <= rhsPred.getDim(); i++) {
        if (i > 1) {
          newNt.append(',');
        }
        newNt.append(String.valueOf(i));
      }
      newNt.append(ORDER_MARKING_RIGHT);
      if (!newNonterminals.contains(newNt.toString())) {
        newNonterminals.add(newNt.toString());
      }
      newClause.append(" ").append(newNt);
      predRep = rhsPred.toString();
      ibrack = predRep.indexOf('(');
      newClause.append(predRep.substring(ibrack));
    }

    return newClause.toString();
  }

  /**
   * Returns a Predicate where the nonterminal is replaced by newNt and the
   * arguments swapped places according to orderVector.
   */
  private static Predicate orderedPredicate(Predicate rhsPred, String newNt,
      ArrayList<Integer> orderVector) throws ParseException {
    StringBuilder newPred = new StringBuilder();
    newPred.append(newNt);
    newPred.append('(');
    for (int i = 1; i <= rhsPred.getDim(); i++) {
      for (int j = 0; j < orderVector.size(); j++) {
        if (orderVector.get(j) == i) {
          if (i > 1) {
            newPred.append(',');
          }
          newPred.append(String.join(" ", rhsPred.getArgumentByIndex(j + 1)));
        }
      }
    }
    newPred.append(')');
    return new Predicate(newPred.toString());
  }

  /**
   * Normalizes the position vector to consecutive numbers starting with 1.
   * Example: 5,0,6 becomes 2,1,3
   */
  private static ArrayList<Integer> getNormalizedPos(
      ArrayList<Integer> posInLhs) {
    @SuppressWarnings("unchecked") ArrayList<Integer> posNormalized =
        (ArrayList<Integer>) posInLhs.clone();
    int searchInt = 0;
    int argInt = 1;
    while (argInt <= posInLhs.size()) {
      for (int i = 0; i < posInLhs.size(); i++) {
        if (posInLhs.get(i) == searchInt) {
          posNormalized.set(i, argInt);
          argInt++;
          break;
        }
      }
      searchInt++;
    }
    return posNormalized;
  }
}

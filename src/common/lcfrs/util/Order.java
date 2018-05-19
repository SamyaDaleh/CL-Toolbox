package common.lcfrs.util;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;

import common.lcfrs.Clause;
import common.lcfrs.Predicate;
import common.lcfrs.Srcg;

public class Order {

  /** Returns true if all variables in rhs predicates appear in the same order
   * as in the lhs predicate. */
  public static boolean isOrdered(Srcg srcg) {
    for (Clause clause : srcg.getClauses()) {
      boolean clauseIsOrdered = isOrdered(clause);
      if (!clauseIsOrdered) {
        return false;
      }
    }
    return true;
  }

  /** Returns true if all predicates in rhs of clause are ordered regarding the
   * lhs predicate. */
  private static boolean isOrdered(Clause clause) {
    for (Predicate rhsPred : clause.getRhs()) {
      boolean predicateIsOrdered = isOrdered(clause, rhsPred);
      if (!predicateIsOrdered) {
        return false;
      }
    }
    return true;
  }

  /** Return true if variables in rhs predicate occur in same order as in lhs
   * predicate of clause. */
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
    ArrayList<Integer> posInLhs = new ArrayList<Integer>();
    for (String symbol : rhsPred.getSymbolsAsPlainArray()) {
      int[] indices = clause.getLhs().find(symbol);
      int abspos = clause.getLhs().getAbsolutePos(indices[0], indices[1]);
      posInLhs.add(abspos);
    }
    return posInLhs;
  }

  /** Returns an equivalent sRCG where the variables are ordered in each rule
   * for each predicate. Might leave useless nonterminals behind. */
  public static Srcg getOrderedSrcg(Srcg oldSrcg) throws ParseException {
    Srcg newSrcg = new Srcg();
    newSrcg.setTerminals(oldSrcg.getTerminals());
    newSrcg.setVariables(oldSrcg.getVariables());
    newSrcg.setStartSymbol(oldSrcg.getStartSymbol() + "^<1>");
    ArrayList<String> newNonterminals = new ArrayList<String>();
    for (Clause clause : oldSrcg.getClauses()) {
      newSrcg.addClause(getClauseWithPositionVectors(clause, newNonterminals));
    }
    newSrcg.setNonterminals(
      newNonterminals.toArray(new String[newNonterminals.size()]));
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
          newNt.append(oldNt).append("^<");
          for (int k = 0; k < orderVector.size(); k++) {
            if (k > 0) {
              newNt.append(',');
            }
            newNt.append(String.valueOf(orderVector.get(k)));
          }
          newNt.append(">");

          int pos1 = newNt.toString().indexOf('^');
          int pos2 = newNt.toString().indexOf('^', pos1 + 1);
          if (pos2 != -1) {
            StringBuilder newestNt = new StringBuilder();
            newestNt.append(newNt.substring(0, pos1)).append("^<");
            String[] vec1 = newNt.substring(pos1 + 2, pos2 - 1).split(",");
            String[] vec2 =
              newNt.substring(pos2 + 2, newNt.length() - 1).split(",");
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
            newestNt.append('>');
            newNt = newestNt;
          }

          clause.getRhs().set(i,
            orderedPredicate(rhsPred, newNt.toString(), orderVector));
          if (newSrcg.nonTerminalsContain(newNt.toString())) {
            continue;
          }
          ArrayList<String> newNts = new ArrayList<String>();
          Collections.addAll(newNts, newSrcg.getNonterminals());
          newNts.add(newNt.toString());
          newSrcg.setNonterminals(newNts.toArray(new String[newNts.size()]));
          for (int l = 0; l < newSrcg.getClauses().size(); l++) {
            Clause clause2 = newSrcg.getClauses().get(l);
            if (clause2.getLhs().getNonterminal().equals(oldNt)) {
              String clause2String = clause2.toString();
              int ibrack = clause2String.indexOf(')');
              String newClause = orderedPredicate(clause2.getLhs(),
                newNt.toString(), orderVector)
                + clause2String.substring(ibrack + 1, clause2String.length());
              newSrcg.addClause(newClause);
            }
          }
        }
      }
    }
    return newSrcg;
  }

  /** Returns a new clause where all nonterminals where replaced by nonterminal
   * + order vector. Adds all generated nonterminals to list if they are not
   * there yet. */
  private static String getClauseWithPositionVectors(Clause clause,
    ArrayList<String> newNonterminals) {
    StringBuilder newClause = new StringBuilder();
    StringBuilder newNt = new StringBuilder();
    newNt.append(clause.getLhs().getNonterminal()).append("^<");
    for (int i = 1; i <= clause.getLhs().getDim(); i++) {
      if (i > 1) {
        newNt.append(',');
      }
      newNt.append(String.valueOf(i));
    }
    newNt.append('>');
    if (!newNonterminals.contains(newNt.toString())) {
      newNonterminals.add(newNt.toString());
    }
    newClause.append(newNt);
    String predRep = clause.getLhs().toString();
    int ibrack = predRep.indexOf('(');
    newClause.append(predRep.substring(ibrack));
    newClause.append(" ->");
    if (clause.getRhs().isEmpty()) {
      newClause.append('ε');
    }
    for (Predicate rhsPred : clause.getRhs()) {
      newNt = new StringBuilder();
      newNt.append(rhsPred.getNonterminal()).append("^<");
      for (int i = 1; i <= rhsPred.getDim(); i++) {
        if (i > 1) {
          newNt.append(',');
        }
        newNt.append(String.valueOf(i));
      }
      newNt.append('>');
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

  /** Returns a Predicate where the nonterminal is replaced by newNt and the
   * arguments swapped places according to orderVector. */
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

  /** Normalizes the position vector to consecutive numbers starting with 1.
   * Example: 5,0,6 becomes 2,1,3 */
  private static ArrayList<Integer>
    getNormalizedPos(ArrayList<Integer> posInLhs) {
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

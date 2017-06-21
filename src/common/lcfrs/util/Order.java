package common.lcfrs.util;

import java.text.ParseException;
import java.util.ArrayList;

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

  private static ArrayList<Integer> getOrderInLhs(Clause clause, Predicate rhsPred) {
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
    newSrcg.setStartSymbol(oldSrcg.getStartSymbol());
    newSrcg.setNonterminals(oldSrcg.getNonterminals());
    for (Clause clause : oldSrcg.getClauses()) {
      newSrcg.addClause(clause.toString());
    }
    boolean change = true;
    while (change) {
      change = false;
      for (int j = 0; j < newSrcg.getClauses().size(); j++) {
        Clause clause = newSrcg.getClauses().get(j);
        for (int i = 0; i < newSrcg.getClauses().get(j).getRhs().size(); i++) {
          Predicate rhsPred = clause.getRhs().get(i);
          String oldNt = rhsPred.getNonterminal();
          if (!isOrdered(clause, rhsPred)) {
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

            clause.getRhs().set(i,
              orderedPredicate(rhsPred, newNt.toString(), orderVector));
            if (!newSrcg.nonTerminalsContain(newNt.toString())) {
              ArrayList<String> newNts = new ArrayList<String>();
              for (String copyNt : newSrcg.getNonterminals()) {
                newNts.add(copyNt);
              }
              newNts.add(newNt.toString());
              newSrcg
                .setNonterminals(newNts.toArray(new String[newNts.size()]));
            }
            for (int l = 0; l < newSrcg.getClauses().size(); l++) {
              Clause clause2 = newSrcg.getClauses().get(l);
              if (clause2.getLhs().getNonterminal().equals(oldNt)) {
                String clause2String = clause2.toString();
                int ibrack = clause2String.indexOf('(');
                String newClause = newNt
                  + clause2String.substring(ibrack, clause2String.length());
                newSrcg.addClause(newClause);
              }
            }
          }
        }
      }
    }
    return newSrcg;
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
          newPred.append(rhsPred.getArgumentByIndex(j + 1)[0]);
        }
      }
    }
    newPred.append(')');
    return new Predicate(newPred.toString());
  }

  /** Normalizes the position vector to consecutive numbers starting with 1.
   * Example: 5,0,6 becomes 2,1,3 */
  private static ArrayList<Integer> getNormalizedPos(ArrayList<Integer> posInLhs) {
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

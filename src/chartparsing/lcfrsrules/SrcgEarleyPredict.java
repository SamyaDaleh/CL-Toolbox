package chartparsing.lcfrsrules;

import java.text.ParseException;
import java.util.List;

import chartparsing.AbstractDynamicDeductionRule;
import common.Item;
import common.lcfrs.Clause;
import common.lcfrs.Predicate;
import common.lcfrs.RangeVector;
import common.lcfrs.SrcgEarleyActiveItem;

/** Whenever our dot is left of a variable that is the first argument of some
 * rhs predicate B, we predict new B-rules. */
public class SrcgEarleyPredict extends AbstractDynamicDeductionRule {
  
  private final Clause outClause;

  public SrcgEarleyPredict(Clause outclause) {
    this.outClause = outclause;
    this.name = "predict";
    this.antNeeded = 1;
  }

  @Override public List<Item> getConsequences() {
    if (antecedences.size() == antNeeded) {
      String[] itemForm = antecedences.get(0).getItemform();
      String clause = itemForm[0];
      if (itemForm[0].contains("->")) {
        Clause clauseParsed;
        try {
          clauseParsed = new Clause(clause);
        } catch (ParseException e) {
          e.printStackTrace();
          return this.consequences;
        }
        String pos = itemForm[1];
        int posInt = Integer.parseInt(pos);
        String i = itemForm[2];
        int iInt = Integer.parseInt(i);
        String j = itemForm[3];
        int jInt = Integer.parseInt(j);
        if (clauseParsed.getLhs().ifSymExists(iInt, jInt)) {
          String mayV = clauseParsed.getLhsSymAt(iInt, jInt);
          for (Predicate rhsPred : clauseParsed.getRhs()) {
            if (rhsPred.getSymAt(1, 0).equals(mayV) && rhsPred.getNonterminal()
              .equals(outClause.getLhs().getNonterminal())) {
              consequences.add(new SrcgEarleyActiveItem(outClause.toString(),
                posInt, 1, 0, new RangeVector(
                  outClause.getLhs().getSymbolsAsPlainArray().length)));
              this.name = "predict " + outClause.toString();
            }
          }
        }
      }
    }
    return this.consequences;
  }

  @Override public String toString() {
    return "[A(φ) -> ... B(X,...)...,pos,<i,j>,ρ_A]" + "\n______ φ(i,j) = X\n"
        + "[" + outClause.toString() + ",pos,<1,0>,ρ_init']";
  }

}

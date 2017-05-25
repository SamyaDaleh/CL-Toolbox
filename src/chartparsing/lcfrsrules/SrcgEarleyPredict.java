package chartparsing.lcfrsrules;

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
  
  private Clause outclause;

  public SrcgEarleyPredict(Clause outclause) {
    this.outclause = outclause;
    this.name = "Predict";
    this.antneeded = 1;
  }

  @Override public List<Item> getConsequences() {
    if (antecedences.size() == antneeded) {
      String[] itemform = antecedences.get(0).getItemform();
      String clause = itemform[0];
      if (itemform[0].contains("->")) {
        Clause clauseparsed = new Clause(clause);
        String pos = itemform[1];
        int posint = Integer.parseInt(pos);
        String i = itemform[2];
        int iint = Integer.parseInt(i);
        String j = itemform[3];
        int jint = Integer.parseInt(j);
        if (clauseparsed.getLhs().ifSymExists(iint, jint)) {
          String mayv = clauseparsed.getLhsSymAt(iint, jint);
          for (Predicate rhspred : clauseparsed.getRhs()) {
            if (rhspred.getSymAt(1, 0).equals(mayv) && rhspred.getNonterminal()
              .equals(outclause.getLhsNonterminal())) {
              consequences.add(new SrcgEarleyActiveItem(outclause.toString(),
                posint, 1, 0, new RangeVector(
                  outclause.getLhs().getSymbolsAsPlainArray().length)));
            }
          }
        }
      }
    }
    return this.consequences;
  }

  @Override public String toString() {
    StringBuilder representation = new StringBuilder();
    representation.append("[A(φ) -> ... B(X,...)...,pos,<i,j>,ρ_A]");
    representation.append("\n______ φ(i,j) = X\n");
    representation.append("[" + outclause.toString() + ",pos,<1,0>,ρ_init']");
    return representation.toString();
  }

}

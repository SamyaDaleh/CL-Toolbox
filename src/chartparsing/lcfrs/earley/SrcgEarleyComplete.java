package chartparsing.lcfrs.earley;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import chartparsing.AbstractDynamicDeductionRule;
import chartparsing.Item;
import common.lcfrs.Clause;
import common.lcfrs.Predicate;

/** Whenever we have a passive B item we can use it to move the dot over the
 * variable of the last argument of B in a parent A-rule that was used to
 * predict it. */
public class SrcgEarleyComplete extends AbstractDynamicDeductionRule {
  
  public SrcgEarleyComplete() {
    this.name = "complete";
    this.antNeeded = 2;
  }

  @Override public List<Item> getConsequences() {
    if (antecedences.size() == antNeeded) {
      String[] itemForm1 = antecedences.get(0).getItemform();
      String[] itemForm2 = antecedences.get(1).getItemform();
      calculateConsequences(itemForm1, itemForm2);
      calculateConsequences(itemForm2, itemForm1);
    }
    return this.consequences;
  }

  private void calculateConsequences(String[] itemForm1, String[] itemForm2) {
    if (!itemForm1[0].contains("->") && itemForm2[0].contains("->")) {
      String nt = itemForm1[0];

      String clause2 = itemForm2[0];
      Clause clause2Parsed;
      try {
        clause2Parsed = new Clause(clause2);
      } catch (ParseException e) {
        e.printStackTrace();
        return;
      }
      String pos2 = itemForm2[1];
      String i2 = itemForm2[2];
      int iInt2 = Integer.parseInt(i2);
      String j2 = itemForm2[3];
      int jInt2 = Integer.parseInt(j2);

      for (int n = 0; n < clause2Parsed.getRhs().size(); n++) {
        Predicate rhsPred = clause2Parsed.getRhs().get(n);
        boolean vectorsMatch = true;
        for (int m = 0; m < (itemForm1.length - 1) / 2 - 1; m++) {
          String varInRhs = rhsPred.getSymAt(m + 1, 0); // there is only 1
          int[] indices = clause2Parsed.getLhs().find(varInRhs);
          int absPosOfVarIn2 =
            clause2Parsed.getLhs().getAbsolutePos(indices[0], indices[1]);
          if (!itemForm1[m * 2 + 1].equals(itemForm2[absPosOfVarIn2 * 2 + 4])
            || !itemForm1[m * 2 + 2]
              .equals(itemForm2[absPosOfVarIn2 * 2 + 5])) {
            vectorsMatch = false;
            break;
          }
        }

        String nt2 = rhsPred.getNonterminal();
        if (vectorsMatch && itemForm1[itemForm1.length - 2].equals(pos2)
          && nt.equals(nt2) ) {
          String posB = itemForm1[itemForm1.length - 1];
          int posBInt = Integer.parseInt(posB);
          ArrayList<String> newVector = new ArrayList<String>();
          for (int k = 0; k < (itemForm2.length-4 ) / 2; k++) {
            newVector.add(itemForm2[k * 2 + 4]);
            newVector.add(itemForm2[k * 2 + 5]);
          }
          int IndexOfFirstQuestionMark = newVector.indexOf("?");
          if (IndexOfFirstQuestionMark == -1) {
            return;
          }
          newVector.set(IndexOfFirstQuestionMark, pos2);
          newVector.set(IndexOfFirstQuestionMark+1, posB);
          consequences.add(new SrcgEarleyActiveItem(clause2, posBInt, iInt2,
            jInt2 + 1, newVector));
        }
      }
    } 
  }

  @Override public String toString() {
    return "[B,ρ_B], [A(φ) -> ... B(ξ)...,pos,<k,l>,ρ_A]" + "\n______ \n"
        + "[A(φ) -> ... B(ξ)...,pos',<k,l+1>,ρ]";
  }

}

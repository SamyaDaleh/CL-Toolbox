package chartparsing.lcfrsrules;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import chartparsing.AbstractDynamicDeductionRule;
import common.Item;
import common.lcfrs.Clause;
import common.lcfrs.Predicate;
import common.lcfrs.SrcgEarleyActiveItem;

/** Whenever we have a passive B item we can use it to move the dot over the
 * variable of the last argument of B in a parent A-rule that was used to
 * predict it. */
public class SrcgEarleyComplete extends AbstractDynamicDeductionRule {
  
  public SrcgEarleyComplete() {
    this.name = "complete";
    this.antneeded = 2;
  }

  @Override public List<Item> getConsequences() {
    if (antecedences.size() == antneeded) {
      String[] itemform1 = antecedences.get(0).getItemform();
      String[] itemform2 = antecedences.get(1).getItemform();
      calculateConsequences(itemform1, itemform2);
      calculateConsequences(itemform2, itemform1);
    }
    return this.consequences;
  }

  private void calculateConsequences(String[] itemform1, String[] itemform2) {
    if (!itemform1[0].contains("->") && itemform2[0].contains("->")) {
      String nt = itemform1[0];

      String clause2 = itemform2[0];
      Clause clause2parsed;
      try {
        clause2parsed = new Clause(clause2);
      } catch (ParseException e) {
        e.printStackTrace();
        return;
      }
      String pos2 = itemform2[1];
      String i2 = itemform2[2];
      int iint2 = Integer.parseInt(i2);
      String j2 = itemform2[3];
      int jint2 = Integer.parseInt(j2);

      for (int n = 0; n < clause2parsed.getRhs().size(); n++) {
        Predicate rhspred = clause2parsed.getRhs().get(n);

        // [A, <1,2>, <3,4>]
        // [A(a X1,b •X2) -> A(X1,X2), 3, <2,1>, (<0,1>, <1,2>, <2,3>, <?,?>)]
        // Let's assume item 1 is the one on the rhs of item 2. I want all
        // variables from A left of the dot to match the vectors in item 1.
        // I want to fill the next gap after the dot with the next vector
        // from item 1.
        boolean vectorsmatch = true;
        for (int m = 0; m < (itemform1.length - 1) / 2 - 1; m++) {
          String varinrhs = rhspred.getSymAt(m + 1, 0); // there is only 1
          int[] indices = clause2parsed.getLhs().find(varinrhs);
          int absposofvarin2 =
            clause2parsed.getLhs().getAbsolutePos(indices[0], indices[1]);
          if (!itemform1[m * 2 + 1].equals(itemform2[absposofvarin2 * 2 + 4])
            || !itemform1[m * 2 + 2]
              .equals(itemform2[absposofvarin2 * 2 + 5])) {
            vectorsmatch = false;
          }
        }

        String nt2 = rhspred.getNonterminal();
        if (vectorsmatch && itemform1[itemform1.length - 2].equals(pos2)
          && nt.equals(nt2) ) {
          String posb = itemform1[itemform1.length - 1];
          int posbint = Integer.parseInt(posb);
          ArrayList<String> newvector = new ArrayList<String>();
          for (int k = 0; k < (itemform2.length-5 ) / 2; k++) {
            newvector.add(itemform2[k * 2 + 4]);
            newvector.add(itemform2[k * 2 + 5]);
          }
          newvector.add(pos2);
          newvector.add(posb);
          consequences.add(new SrcgEarleyActiveItem(clause2, posbint, iint2,
            jint2 + 1, newvector.toArray(new String[newvector.size()])));
        }
      }
    } 
  }

  @Override public String toString() {
    return "[B,ρ_B], [A(φ) -> ... B(ξ)...,pos,<k,l>,ρ_A]" + "\n______ \n"
        + "[A(φ) -> ... B(ξ)...,pos',<k,l+1>,ρ]";
  }

}

package chartparsing.lcfrsrules;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import chartparsing.DynamicDeductionRule;
import common.Item;
import common.lcfrs.Clause;
import common.lcfrs.Predicate;
import common.lcfrs.SrcgEarleyActiveItem;

/** Whenever we have a passive B item we can use it to move the dot over the
 * variable of the last argument of B in a parent A-rule that was used to
 * predict it. */
public class SrcgEarleyComplete implements DynamicDeductionRule {

  private List<Item> antecedences = new LinkedList<Item>();
  private List<Item> consequences = new LinkedList<Item>();
  private String name = "Complete";

  private int antneeded = 2;

  @Override public void addAntecedence(Item item) {
    this.antecedences.add(item);
  }

  @Override public void addConsequence(Item item) {
    // ignore
  }

  @Override public List<Item> getAntecedences() {
    return this.antecedences;
  }

  @Override public void setAntecedences(List<Item> antecedences) {
    this.antecedences = antecedences;
  }

  @Override public List<Item> getConsequences() {
    if (antecedences.size() == antneeded) {
      String[] itemform1 = antecedences.get(0).getItemform();
      String[] itemform2 = antecedences.get(1).getItemform();

      if (!itemform1[0].contains("->") && itemform2[0].contains("->")) {
        String nt = itemform1[0];

        String clause2 = itemform2[0];
        Clause clause2parsed = new Clause(clause2);
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

      } else if (!itemform2[0].contains("->") && itemform1[0].contains("->")) {
        String nt = itemform2[0];

        String clause1 = itemform1[0];
        Clause clause1parsed = new Clause(clause1);
        String pos1 = itemform1[1];
        String i1 = itemform1[2];
        int iint1 = Integer.parseInt(i1);
        String j1 = itemform1[3];
        int jint1 = Integer.parseInt(j1);

        for (int n = 0; n < clause1parsed.getRhs().size(); n++) {
          Predicate rhspred = clause1parsed.getRhs().get(n);

          boolean vectorsmatch = true;
          for (int m = 0; m < (itemform2.length - 1) / 2 - 1; m++) {
            if (itemform2[m * 2 + 1].equals(itemform1[(iint1 - 1 + m) * 2 + 4])
              && itemform2[m * 2 + 2]
                .equals(itemform1[(iint1 - 1 + m) * 2 + 5])) {
              vectorsmatch = false;
              break;
            }
          }

          String nt1 = rhspred.getNonterminal();
          if (vectorsmatch && itemform1[itemform2.length - 2].equals(pos1)
            && nt.equals(nt1)) {
            String posb = itemform2[itemform2.length - 1];
            int posbint = Integer.parseInt(posb);
            ArrayList<String> newvector = new ArrayList<String>();
            for (int k = 0; k < (itemform1.length-5 ) / 2; k++) {
              newvector.add(itemform1[k * 2 + 4]);
              newvector.add(itemform1[k * 2 + 5]);
            }
            newvector.add(pos1);
            newvector.add(posb);
            consequences.add(new SrcgEarleyActiveItem(clause1, posbint, iint1,
              jint1 + 1, newvector.toArray(new String[newvector.size()])));
          }
        }
      }
    }
    return this.consequences;
  }

  @Override public void setConsequences(List<Item> consequences) {
    // ignore
  }

  @Override public String getName() {
    return this.name;
  }

  @Override public int getAntecedencesNeeded() {
    return this.antneeded;
  }

  @Override public void clearItems() {
    this.antecedences = new LinkedList<Item>();
    this.consequences = new LinkedList<Item>();
  }

  @Override public String toString() {
    StringBuilder representation = new StringBuilder();
    representation.append("[B,ρ_B], [A(φ) -> ... B(ξ)...,pos,<k,l>,ρ_A]");
    representation.append("\n______ \n");
    representation.append("[A(φ) -> ... B(ξ)...,pos',<k,l+1>,ρ]");
    return representation.toString();
  }

}

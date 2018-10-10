package com.github.samyadaleh.cltoolbox.chartparsing.lcfrs.earley;

import com.github.samyadaleh.cltoolbox.chartparsing.dynamicdeductionrule.AbstractDynamicDecutionRuleTwoAntecedences;
import com.github.samyadaleh.cltoolbox.chartparsing.item.ChartItemInterface;
import com.github.samyadaleh.cltoolbox.common.lcfrs.Clause;
import com.github.samyadaleh.cltoolbox.common.lcfrs.Predicate;
import com.github.samyadaleh.cltoolbox.common.tag.Tree;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Whenever we have a passive B item we can use it to move the dot over the
 * variable of the last argument of B in a parent A-rule that was used to
 * predict it.
 */
public class SrcgEarleyComplete
    extends AbstractDynamicDecutionRuleTwoAntecedences {

  public SrcgEarleyComplete() {
    this.name = "complete";
    this.antNeeded = 2;
  }

  protected void calculateConsequences(String[] itemForm1, String[] itemForm2) {
    if (!itemForm1[0].contains("->") && itemForm2[0].contains("->")) {
      String nt = itemForm1[0];
      String clause2 = itemForm2[0];
      try {
        Clause clause2Parsed = new Clause(clause2);
        String pos2 = itemForm2[1];
        int iInt2 = Integer.parseInt(itemForm2[2]);
        int jInt2 = Integer.parseInt(itemForm2[3]);
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
          if (vectorsMatch && itemForm1[itemForm1.length - 2].equals(pos2) && nt
              .equals(nt2)) {
            this.name =
                "complete " + clause2Parsed.getLhs().getSymAt(iInt2, jInt2);
            String posB = itemForm1[itemForm1.length - 1];
            int posBInt = Integer.parseInt(posB);
            ArrayList<String> newVector = new ArrayList<>();
            for (int k = 0; k < (itemForm2.length - 4) / 2; k++) {
              newVector.add(itemForm2[k * 2 + 4]);
              newVector.add(itemForm2[k * 2 + 5]);
            }
            int IndexOfFirstQuestionMark = newVector.indexOf("?");
            if (IndexOfFirstQuestionMark == -1) {
              return;
            }
            newVector.set(IndexOfFirstQuestionMark, pos2);
            newVector.set(IndexOfFirstQuestionMark + 1, posB);
            ChartItemInterface consequence =
                new SrcgEarleyActiveItem(clause2, posBInt, iInt2, jInt2 + 1,
                    newVector);
            List<Tree> derivedTrees;
            if (Arrays.equals(itemForm1, antecedences.get(0).getItemForm())) {
              derivedTrees = antecedences.get(0).getTrees();
            } else {
              derivedTrees = antecedences.get(1).getTrees();
            }
            consequence.setTrees(derivedTrees);
            logItemGeneration(consequence);
            consequences.add(consequence);
          }
        }
      } catch (ParseException e) {
        throw new RuntimeException(e);
      }
    }
  }

  @Override public String toString() {
    return "[B,ρ_B], [A(φ) -> ... B(ξ)...,pos,<k,l>,ρ_A]" + "\n______ \n"
        + "[A(φ) -> ... B(ξ)...,pos',<k,l+1>,ρ]";
  }

}

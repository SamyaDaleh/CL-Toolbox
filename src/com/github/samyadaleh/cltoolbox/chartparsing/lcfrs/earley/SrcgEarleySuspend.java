package com.github.samyadaleh.cltoolbox.chartparsing.lcfrs.earley;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;

import com.github.samyadaleh.cltoolbox.chartparsing.AbstractDynamicDecutionRuleTwoAntecedences;
import com.github.samyadaleh.cltoolbox.chartparsing.lcfrs.SrcgDeductionUtils;
import com.github.samyadaleh.cltoolbox.common.ArrayUtils;
import com.github.samyadaleh.cltoolbox.common.lcfrs.Clause;
import com.github.samyadaleh.cltoolbox.common.lcfrs.Predicate;

/**
 * Whenever we arrive at the end of an argument that is not the last argument,
 * we suspend the processing of this rule and we go back to the item that we
 * used to predict it.
 */
public class SrcgEarleySuspend
  extends AbstractDynamicDecutionRuleTwoAntecedences {

  private final String[] variables;

  /** Remember variables to check if symbols are one of them. */
  public SrcgEarleySuspend(String[] variables) {
    this.variables = variables;
    this.name = "suspend";
    this.antNeeded = 2;
  }

  protected void calculateConsequences(String[] itemForm1, String[] itemForm2) {
    if (itemForm1[0].contains("->") && itemForm2[0].contains("->")) {
      String clause1 = itemForm1[0];
      Clause clause1Parsed;
      try {
        clause1Parsed = new Clause(clause1);
      } catch (ParseException e1) {
        e1.printStackTrace();
        return;
      }
      String pos1 = itemForm1[1];
      int posInt1 = Integer.parseInt(pos1);
      String i1 = itemForm1[2];
      int iInt1 = Integer.parseInt(i1);
      String j1 = itemForm1[3];
      int jInt1 = Integer.parseInt(j1);

      String clause2 = itemForm2[0];
      Clause clause2Parsed;
      try {
        clause2Parsed = new Clause(clause2);
      } catch (ParseException e1) {
        e1.printStackTrace();
        return;
      }
      String pos2 = itemForm2[1];
      String i2 = itemForm2[2];
      int iInt2 = Integer.parseInt(i2);
      String j2 = itemForm2[3];
      int jInt2 = Integer.parseInt(j2);
      boolean isVar2 = false;
      if (clause2Parsed.getLhs().ifSymExists(iInt2, jInt2)) {
        String mayV2 = clause2Parsed.getLhsSymAt(iInt2, jInt2);
        for (String var : variables) {
          if (var.equals(mayV2)) {
            isVar2 = true;
            break;
          }
        }
        for (int n = 0; n < clause2Parsed.getRhs().size(); n++) {
          Predicate rhsPred = clause2Parsed.getRhs().get(n);
          if (rhsPred.getNonterminal()
            .equals(clause1Parsed.getLhs().getNonterminal())
            && rhsPred.getSymAt(iInt1, 0).equals(mayV2) && isVar2
            && itemForm1.length > (iInt1 - 1) * 2 + 5
            && itemForm2.length > (iInt1 - 1 + n) * 2 + 5) {
            if (itemForm1[2 * (iInt1 - 1) + 4].equals(pos2)
              && iInt1 < clause1Parsed.getLhs().getDim() && clause1Parsed
                .getLhs().getArgumentByIndex(iInt1).length == jInt1) {
              boolean vectorsMatch =
                SrcgDeductionUtils.ifRhsVectorMatchesLhsVector(clause1Parsed,
                  itemForm1, rhsPred, iInt1, clause2Parsed, itemForm2);
              if (vectorsMatch) {
                addNewConsequence(itemForm2, pos1, posInt1, clause2,
                  clause2Parsed, pos2, iInt2, jInt2);
              }
            }
          }
        }
      }
    }
  }

  private void addNewConsequence(String[] itemForm2, String pos1, int posInt1,
    String clause2, Clause clause2Parsed, String pos2, int iInt2, int jInt2) {
    ArrayList<String> newVector;
    newVector = new ArrayList<String>(Arrays.asList(
      ArrayUtils.getSubSequenceAsArray(itemForm2, 4, itemForm2.length)));
    int indabspos = clause2Parsed.getLhs().getAbsolutePos(iInt2, jInt2);
    newVector.set(indabspos * 2, pos2);
    newVector.set(indabspos * 2 + 1, pos1);
    consequences.add(
      new SrcgEarleyActiveItem(clause2, posInt1, iInt2, jInt2 + 1, newVector));
  }

  @Override public String toString() {
    return "[B(ψ) -> Ψ,pos',<i,j>,ρ_B], [A(φ) -> ... B(ξ)...,pos,<k,l>,ρ_A]"
      + "\n______ \n" + "[A(φ) -> ... B(ξ)...,pos',<k,l+1>,ρ]";
  }

}

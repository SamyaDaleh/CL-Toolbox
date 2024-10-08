package com.github.samyadaleh.cltoolbox.chartparsing.lcfrs.earley;

import com.github.samyadaleh.cltoolbox.chartparsing.dynamicdeductionrule.AbstractDynamicDecutionRuleTwoAntecedences;
import com.github.samyadaleh.cltoolbox.chartparsing.item.ChartItemInterface;
import com.github.samyadaleh.cltoolbox.chartparsing.lcfrs.SrcgDeductionUtils;
import com.github.samyadaleh.cltoolbox.common.ArrayUtils;
import com.github.samyadaleh.cltoolbox.common.lcfrs.Clause;
import com.github.samyadaleh.cltoolbox.common.lcfrs.Predicate;
import com.github.samyadaleh.cltoolbox.common.tag.Tree;

import java.text.ParseException;
import java.util.Arrays;
import java.util.List;

import static com.github.samyadaleh.cltoolbox.common.Constants.DEDUCTION_RULE_LCFRS_EARLEY_RESUME;

/**
 * Whenever we are left of a variable that is not the first argument of one of
 * the rhs predicates, we resume the rule of the rhs predicate.
 */
public class SrcgEarleyResume
    extends AbstractDynamicDecutionRuleTwoAntecedences {

  private final List<String> variables;

  /**
   * Remember variables to check if symbols are one of them.
   */
  public SrcgEarleyResume(List<String> variables) {
    this.variables = variables;
    this.name = DEDUCTION_RULE_LCFRS_EARLEY_RESUME;
    this.antNeeded = 2;
  }

  protected void calculateConsequences(String[] itemForm1, String[] itemForm2) {
    String clause1 = itemForm1[0];
    String clause2 = itemForm2[0];
    if (itemForm1[0].contains("->") && itemForm2[0].contains("->")) {
      try {
        Clause clause1Parsed = new Clause(clause1);
      int posInt1 = Integer.parseInt(itemForm1[1]);
      int iInt1 = Integer.parseInt(itemForm1[2]);
      int jInt1 = Integer.parseInt(itemForm1[3]);
        Clause clause2Parsed = new Clause(clause2);
        int iInt2 = Integer.parseInt(itemForm2[2]);
        int jInt2 = Integer.parseInt(itemForm2[3]);
        boolean mayV1FirstArg = false;
        boolean isVar1 = false;
        if (clause1Parsed.getLhs().ifSymExists(iInt1, jInt1)) {
          String mayV1 = clause1Parsed.getLhsSymAt(iInt1, jInt1);
          for (String var : variables) {
            if (var.equals(mayV1)) {
              isVar1 = true;
              break;
            }
          }
          for (Predicate rhs : clause1Parsed.getRhs()) {
            if (rhs.getSymAt(1, 0).equals(mayV1)) {
              mayV1FirstArg = true;
            }
          }
          for (Predicate rhs : clause1Parsed.getRhs()) {
            handleRhsPredicate(itemForm1, itemForm2, clause1Parsed, posInt1,
                iInt1, clause2Parsed, iInt2, jInt2, mayV1FirstArg, isVar1,
                mayV1, rhs);
          }
        }
      } catch (ParseException e) {
        throw new RuntimeException(e);
      }
    }
  }

  private void handleRhsPredicate(String[] itemForm1, String[] itemForm2,
      Clause clause1Parsed, int posInt1, int iInt1, Clause clause2Parsed,
      int iInt2, int jInt2, boolean mayV1FirstArg, boolean isVar1, String mayV1,
      Predicate rhs) {
    int[] indices = rhs.find(mayV1);
    boolean dotIsAtArgEnd =
        clause2Parsed.getLhs().ifSymExists(iInt2, 0) && jInt2 == clause2Parsed
            .getLhs().getSymbols()[iInt2 - 1].length;
    if (indices[0] == iInt2 + 1 && isVar1 && !mayV1FirstArg && dotIsAtArgEnd
        && clause2Parsed.getLhs().ifSymExists(iInt2 + 1, 0)) {
      boolean vectorsmatch = SrcgDeductionUtils
          .ifRhsVectorMatchesLhsVectorResume(clause1Parsed, itemForm1, rhs,
              iInt1, clause2Parsed, itemForm2);
      if (vectorsmatch) {
        ChartItemInterface consequence =
            new SrcgEarleyActiveItem(itemForm2[0], posInt1, iInt2 + 1, 0,
                ArrayUtils
                    .getSubSequenceAsList(itemForm2, 4, itemForm2.length));
        List<Tree> derivedTrees;
        if (Arrays.equals(antecedences.get(0).getItemForm(), itemForm1)) {
          derivedTrees = antecedences.get(0).getTrees();
        } else {
          derivedTrees = antecedences.get(1).getTrees();
        }
        consequence.setTrees(derivedTrees);
        logItemGeneration(consequence);
        consequences.add(consequence);
      }
    }
  }

  @Override public String toString() {
    return "[A(φ) -> ... B(ξ)...,pos,<i,j>,ρ_A], [B(ψ) -> Ψ,pos',<k-1,l>,ρ_B]"
        + "\n______ \n" + "[B(ψ) -> Ψ,pos,<k,0>,ρ_B]";
  }

}

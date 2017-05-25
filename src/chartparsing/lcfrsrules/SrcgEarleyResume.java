package chartparsing.lcfrsrules;

import java.util.List;

import chartparsing.AbstractDynamicDeductionRule;
import common.ArrayUtils;
import common.Item;
import common.lcfrs.Clause;
import common.lcfrs.Predicate;
import common.lcfrs.SrcgEarleyActiveItem;

/** Whenever we are left of a variable that is not the first argument of one of
 * the rhs predicates, we resume the rule of the rhs predicate. */
public class SrcgEarleyResume extends AbstractDynamicDeductionRule {

  private String[] variables;

  /** Remember variables to check if symbols are one of them. */
  public SrcgEarleyResume(String[] variables) {
    this.variables = variables;
    this.name = "Resume";
    this.antneeded = 2;
  }

  @Override public List<Item> getConsequences() {
    if (antecedences.size() == antneeded) {
      String[] itemform1 = antecedences.get(0).getItemform();
      String clause1 = itemform1[0];

      String[] itemform2 = antecedences.get(1).getItemform();
      String clause2 = itemform2[0];

      if (itemform1[0].contains("->") && itemform2[0].contains("->")) {
        Clause clause1parsed = new Clause(clause1);
        String pos1 = itemform1[1];
        int posint1 = Integer.parseInt(pos1);
        String i1 = itemform1[2];
        int iint1 = Integer.parseInt(i1);
        String j1 = itemform1[3];
        int jint1 = Integer.parseInt(j1);

        Clause clause2parsed = new Clause(clause2);
        String pos2 = itemform2[1];
        int posint2 = Integer.parseInt(pos2);
        String i2 = itemform2[2];
        int iint2 = Integer.parseInt(i2);
        String j2 = itemform2[3];
        int jint2 = Integer.parseInt(j2);

        boolean mayv1firstarg = false;
        boolean mayv2firstarg = false;
        boolean isvar1 = false;
        boolean isvar2 = false;
        if (clause1parsed.getLhs().ifSymExists(iint1, jint1)) {
          String mayv1 = clause1parsed.getLhsSymAt(iint1, jint1);
          for (String var : variables) {
            if (var.equals(mayv1)) {
              isvar1 = true;
              break;
            }
          }
          for (Predicate rhs : clause1parsed.getRhs()) {
            if (rhs.getSymAt(1, 0).equals(mayv1)) {
              mayv1firstarg = true;
            }
          }
          for (Predicate rhs : clause1parsed.getRhs()) {
            int[] indices = rhs.find(mayv1);
            boolean dotisatargend = clause2parsed.getLhs().ifSymExists(iint2, 0)
              && jint2 == clause2parsed.getLhs().getSymbols()[iint2 - 1].length;

            if (indices[0] == iint2 + 1 && isvar1 && !mayv1firstarg
              && dotisatargend
              && clause2parsed.getLhs().ifSymExists(iint2 + 1, 0)) {
              boolean vectorsmatch =
                SrcgDeductionUtils.ifRhsVectorMatchesLhsVectorResume(clause1parsed,
                  itemform1, rhs, iint1, clause2parsed, itemform2);
              if (vectorsmatch) {
                consequences.add(new SrcgEarleyActiveItem(itemform2[0], posint1,
                  iint2 + 1, 0, ArrayUtils.getSubSequenceAsArray(itemform2, 4,
                    itemform2.length)));
              }
            }
          }
        }
        // the other way round
        if (clause2parsed.getLhs().ifSymExists(iint2, jint2)) {
          String mayv2 = clause2parsed.getLhsSymAt(iint2, jint2);
          for (String var : variables) {
            if (var.equals(mayv2)) {
              isvar2 = true;
              break;
            }
          }
          for (Predicate rhs : clause2parsed.getRhs()) {
            if (rhs.getSymAt(1, 0).equals(mayv2)) {
              mayv2firstarg = true;
            }
          }
          for (Predicate rhs : clause2parsed.getRhs()) {
            int[] indices = rhs.find(mayv2);
            boolean dotisatargend = clause1parsed.getLhs().ifSymExists(iint1, 0)
              && jint1 == clause1parsed.getLhs().getSymbols()[iint1 - 1].length;

            if (indices[0] == iint1 + 1 && isvar2 && !mayv2firstarg
              && dotisatargend
              && clause1parsed.getLhs().ifSymExists(iint1 + 1, 0)) {
              boolean vectorsmatch =
                SrcgDeductionUtils.ifRhsVectorMatchesLhsVectorResume(clause2parsed,
                  itemform2, rhs, iint2, clause1parsed, itemform1);
              if (vectorsmatch) {
                consequences.add(new SrcgEarleyActiveItem(itemform1[0], posint2,
                  iint1 + 1, 0, ArrayUtils.getSubSequenceAsArray(itemform1, 4,
                    itemform1.length)));
              }
            }
          }
        }
      }
    }
    return this.consequences;
  }

  @Override public String toString() {
    StringBuilder representation = new StringBuilder();
    representation.append(
      "[A(φ) -> ... B(ξ)...,pos,<i,j>,ρ_A], [B(ψ) -> Ψ,pos',<k-1,l>,ρ_B]");
    representation.append("\n______ \n");
    representation.append("[B(ψ) -> Ψ,pos,<k,0>,ρ_B]");
    return representation.toString();
  }

}

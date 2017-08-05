package chartparsing.lcfrs;

import java.text.ParseException;
import java.util.List;

import chartparsing.AbstractDynamicDeductionRule;
import chartparsing.Item;
import common.ArrayUtils;
import common.lcfrs.Clause;
import common.lcfrs.Predicate;

/** Whenever we are left of a variable that is not the first argument of one of
 * the rhs predicates, we resume the rule of the rhs predicate. */
public class SrcgEarleyResume extends AbstractDynamicDeductionRule {

  private final String[] variables;

  /** Remember variables to check if symbols are one of them. */
  public SrcgEarleyResume(String[] variables) {
    this.variables = variables;
    this.name = "resume";
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
    String clause1 = itemForm1[0];
    String clause2 = itemForm2[0];

    if (itemForm1[0].contains("->") && itemForm2[0].contains("->")) {
      Clause clause1Parsed;
      try {
        clause1Parsed = new Clause(clause1);
      } catch (ParseException e) {
        e.printStackTrace();
        return;
      }
      String pos1 = itemForm1[1];
      int posInt1 = Integer.parseInt(pos1);
      String i1 = itemForm1[2];
      int iInt1 = Integer.parseInt(i1);
      String j1 = itemForm1[3];
      int jInt1 = Integer.parseInt(j1);

      Clause clause2Parsed;
      try {
        clause2Parsed = new Clause(clause2);
      } catch (ParseException e) {
        e.printStackTrace();
        return;
      }
      String i2 = itemForm2[2];
      int iInt2 = Integer.parseInt(i2);
      String j2 = itemForm2[3];
      int jInt2 = Integer.parseInt(j2);

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
          int[] indices = rhs.find(mayV1);
          boolean dotIsAtArgEnd = clause2Parsed.getLhs().ifSymExists(iInt2, 0)
            && jInt2 == clause2Parsed.getLhs().getSymbols()[iInt2 - 1].length;

          if (indices[0] == iInt2 + 1 && isVar1 && !mayV1FirstArg
            && dotIsAtArgEnd
            && clause2Parsed.getLhs().ifSymExists(iInt2 + 1, 0)) {
            boolean vectorsmatch =
              SrcgDeductionUtils.ifRhsVectorMatchesLhsVectorResume(clause1Parsed,
                itemForm1, rhs, iInt1, clause2Parsed, itemForm2);
            if (vectorsmatch) {
              consequences.add(new SrcgEarleyActiveItem(itemForm2[0], posInt1,
                iInt2 + 1, 0, ArrayUtils.getSubSequenceAsList(itemForm2, 4,
                  itemForm2.length)));
            }
          }
        }
      }
    }
  }

  @Override public String toString() {
    return "[A(φ) -> ... B(ξ)...,pos,<i,j>,ρ_A], [B(ψ) -> Ψ,pos',<k-1,l>,ρ_B]"
        + "\n______ \n" + "[B(ψ) -> Ψ,pos,<k,0>,ρ_B]";
  }

}

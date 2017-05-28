package chartparsing.lcfrsrules;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import chartparsing.AbstractDynamicDeductionRule;
import common.ArrayUtils;
import common.Item;
import common.lcfrs.Clause;
import common.lcfrs.Predicate;
import common.lcfrs.SrcgEarleyActiveItem;

/** Whenever we arrive at the end of an argumebt that is not the last argument,
 * we suspend the processing of this rule and we go back to the item that we
 * used to predict it. */
public class SrcgEarleySuspend extends AbstractDynamicDeductionRule {

  private final String[] variables;

  /** Remember variables to check if symbols are one of them. */
  public SrcgEarleySuspend(String[] variables) {
    this.variables = variables;
    this.name = "Suspend";
    this.antneeded = 2;
  }

  @Override public List<Item> getConsequences() {
    if (antecedences.size() == antneeded) {
      String[] itemform1 = antecedences.get(0).getItemform();
      String[] itemform2 = antecedences.get(1).getItemform();

      if (itemform1[0].contains("->") && itemform2[0].contains("->")) {
        String clause1 = itemform1[0];
        Clause clause1parsed = new Clause(clause1);
        String pos1 = itemform1[1];
        int posint1 = Integer.parseInt(pos1);
        String i1 = itemform1[2];
        int iint1 = Integer.parseInt(i1);
        String j1 = itemform1[3];
        int jint1 = Integer.parseInt(j1);

        String clause2 = itemform2[0];
        Clause clause2parsed = new Clause(clause2);
        String pos2 = itemform2[1];
        int posint2 = Integer.parseInt(pos2);
        String i2 = itemform2[2];
        int iint2 = Integer.parseInt(i2);
        String j2 = itemform2[3];
        int jint2 = Integer.parseInt(j2);
        boolean isvar1 = false;
        boolean isvar2 = false;
        if (clause2parsed.getLhs().ifSymExists(iint2, jint2)) {
          String mayv2 = clause2parsed.getLhsSymAt(iint2, jint2);
          for (String var : variables) {
            if (var.equals(mayv2)) {
              isvar2 = true;
              break;
            }
          }

          for (int n = 0; n < clause2parsed.getRhs().size(); n++) {
            Predicate rhspred = clause2parsed.getRhs().get(n);
            if (rhspred.getSymAt(iint1, 0).equals(mayv2) &&  isvar2
              && rhspred.getNonterminal()
                .equals(clause1parsed.getLhs().getNonterminal())
              && itemform1.length > (iint1 - 1) * 2 + 5
              && itemform2.length > (iint1 - 1 + n) * 2 + 5) {
              if (itemform1[2 * (iint1 - 1) + 4].equals(pos2)
                && iint1 < clause1parsed.getLhs().getDim() && clause1parsed
                  .getLhs().getArgumentByIndex(iint1).length == jint1) {

                boolean vectorsmatch =
                  SrcgDeductionUtils.ifRhsVectorMatchesLhsVector(clause1parsed,
                    itemform1, rhspred, iint1, clause2parsed, itemform2);
                if (vectorsmatch) {
                  ArrayList<String> newvector;
                  newvector = new ArrayList<String>(Arrays.asList(ArrayUtils
                    .getSubSequenceAsArray(itemform2, 4, itemform2.length)));
                  int indabspos =
                    clause2parsed.getLhs().getAbsolutePos(iint2, jint2);
                  try { // DEBUG
                  newvector.set(indabspos * 2, pos2);
                  newvector.set(indabspos * 2 + 1, pos1);
                  consequences.add(
                    new SrcgEarleyActiveItem(clause2, posint1, iint2, jint2 + 1,
                      newvector.toArray(new String[newvector.size()])));
                  } catch (IndexOutOfBoundsException e) {
                    System.out.println(e.getLocalizedMessage());
                  }
                }
              }
            }
          }
        }

        if (clause1parsed.getLhs().ifSymExists(iint1, jint1)) {
          String mayv1 = clause1parsed.getLhsSymAt(iint1, jint1);
          for (String var : variables) {
            if (var.equals(mayv1)) {
              isvar1 = true;
              break;
            }
          }
          // the other way round
          for (int n = 0; n < clause1parsed.getRhs().size(); n++) {
            Predicate rhspred = clause1parsed.getRhs().get(n);
            if (rhspred.getSymAt(iint2, 0).equals(mayv1) &&  isvar1
              && rhspred.getNonterminal()
                .equals(clause2parsed.getLhs().getNonterminal())
              && itemform2.length > (iint2 - 1) * 2 + 5
              && itemform1.length > (iint2 - 1 + n) * 2 + 5) {
              if (itemform2[2 * (iint2-1) + 4].equals(pos1)
                && iint2 < clause2parsed.getLhs().getDim() && clause2parsed
                  .getLhs().getArgumentByIndex(iint2).length == jint2) {

                boolean vectorsmatch =
                  SrcgDeductionUtils.ifRhsVectorMatchesLhsVector(clause2parsed,
                    itemform2, rhspred, iint2, clause1parsed, itemform1);
                if (vectorsmatch) {
                  ArrayList<String> newvector;
                  newvector = new ArrayList<String>(Arrays.asList(ArrayUtils
                    .getSubSequenceAsArray(itemform1, 4, itemform1.length)));
                  int indabspos =
                    clause1parsed.getLhs().getAbsolutePos(iint1, jint1);
                  try {
                  newvector.set(indabspos * 2, pos1);
                  newvector.set(indabspos * 2 + 1, pos2);
                  consequences.add(
                    new SrcgEarleyActiveItem(clause1, posint2, iint1, jint1 + 1,
                      newvector.toArray(new String[newvector.size()])));
                  } catch (IndexOutOfBoundsException e) {
                    System.out.println(e.getLocalizedMessage());
                  }
                }
              }
            }
          }
        }
      }
    }
    return this.consequences;
  }

  @Override public String toString() {
    return "[B(ψ) -> Ψ,pos',<i,j>,ρ_B], [A(φ) -> ... B(ξ)...,pos,<k,l>,ρ_A]"
        + "\n______ \n" + "[A(φ) -> ... B(ξ)...,pos',<k,l+1>,ρ]";
  }

}

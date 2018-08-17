package com.github.samyadaleh.cltoolbox.chartparsing.lcfrs.earley;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import com.github.samyadaleh.cltoolbox.chartparsing.AbstractDynamicDeductionRule;
import com.github.samyadaleh.cltoolbox.chartparsing.ChartItemInterface;
import com.github.samyadaleh.cltoolbox.common.TreeUtils;
import com.github.samyadaleh.cltoolbox.common.lcfrs.Clause;
import com.github.samyadaleh.cltoolbox.common.tag.Tree;

/**
 * Whenever the next symbol after the dot is the next terminal in the input, we
 * can scan it.
 */
public class SrcgEarleyScan extends AbstractDynamicDeductionRule {

  private final String[] wSplit;

  /** Remembers the input string to compare it with the next symbol to scan. */
  public SrcgEarleyScan(String[] wSplit) {
    this.wSplit = wSplit;
    this.name = "scan";
    this.antNeeded = 1;
  }

  @Override public List<ChartItemInterface> getConsequences()
    throws ParseException {
    if (antecedences.size() == antNeeded) {
      String[] itemForm = antecedences.get(0).getItemForm();
      String clause = itemForm[0];
      if (itemForm[0].contains("->")) {
        Clause clauseParsed;
        try {
          clauseParsed = new Clause(clause);
        } catch (ParseException e) {
          e.printStackTrace();
          return this.consequences;
        }
        String pos = itemForm[1];
        int posInt = Integer.parseInt(pos);
        String i = itemForm[2];
        int iInt = Integer.parseInt(i);
        String j = itemForm[3];
        int jInt = Integer.parseInt(j);
        int place = clauseParsed.getLhs().getAbsolutePos(iInt, jInt);
        if (clauseParsed.getLhs().ifSymExists(iInt, jInt)
          && posInt < wSplit.length
          && clauseParsed.getLhsSymAt(iInt, jInt).equals(wSplit[posInt])) {
          this.name = "scan " + wSplit[posInt];
          ArrayList<String> newVector = new ArrayList<>();
          for (int k = 0; k * 2 + 5 < itemForm.length; k++) {
            newVector.add(itemForm[2 * k + 4]);
            newVector.add(itemForm[2 * k + 5]);
          }
          newVector.set(place * 2, pos);
          newVector.set(place * 2 + 1, String.valueOf(posInt + 1));
          ChartItemInterface consequence = new SrcgEarleyActiveItem(clause,
            posInt + 1, iInt, jInt + 1, newVector);
          List<Tree> derivedTrees = new ArrayList<>();
          for (Tree tree : antecedences.get(0).getTrees()) {
            derivedTrees.add(
              TreeUtils.performPositionSubstitution(tree, wSplit[posInt], pos));
          }
          consequence.setTrees(derivedTrees);
          consequences.add(consequence);
        }
      }
    }
    return this.consequences;
  }

  @Override public String toString() {
    return "[A(φ) -> Φ,pos,<i,j>,ρ]" + "\n______ φ(i,j) = w_pos\n"
      + "[A(φ) -> Φ,pos,<i,j+1>,ρ']";
  }
}

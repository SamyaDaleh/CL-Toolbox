package com.github.samyadaleh.cltoolbox.chartparsing.lcfrs.earley;

import com.github.samyadaleh.cltoolbox.chartparsing.dynamicdeductionrule.AbstractDynamicDeductionRule;
import com.github.samyadaleh.cltoolbox.chartparsing.item.ChartItemInterface;
import com.github.samyadaleh.cltoolbox.common.TreeUtils;
import com.github.samyadaleh.cltoolbox.common.lcfrs.Clause;
import com.github.samyadaleh.cltoolbox.common.tag.Tree;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import static com.github.samyadaleh.cltoolbox.common.Constants.DEDUCTION_RULE_LCFRS_EARLEY_SCAN;

/**
 * Whenever the next symbol after the dot is the next terminal in the input, we
 * can scan it.
 */
public class SrcgEarleyScan extends AbstractDynamicDeductionRule {

  private final String[] wSplit;

  /**
   * Remembers the input string to compare it with the next symbol to scan.
   */
  public SrcgEarleyScan(String[] wSplit) {
    this.wSplit = wSplit;
    this.name = DEDUCTION_RULE_LCFRS_EARLEY_SCAN;
    this.antNeeded = 1;
  }

  @Override public List<ChartItemInterface> getConsequences() {
    if (antecedences.size() == antNeeded) {
      String[] itemForm = antecedences.get(0).getItemForm();
      if (itemForm[0].contains("->")) {
        try {
          Clause clauseParsed = new Clause(itemForm[0]);
          int posInt = Integer.parseInt(itemForm[1]);
          int iInt = Integer.parseInt(itemForm[2]);
          int jInt = Integer.parseInt(itemForm[3]);
          int place = clauseParsed.getLhs().getAbsolutePos(iInt, jInt);
          if (clauseParsed.getLhs().ifSymExists(iInt, jInt)
              && posInt < wSplit.length && clauseParsed.getLhsSymAt(iInt, jInt)
              .equals(wSplit[posInt])) {
            this.name = DEDUCTION_RULE_LCFRS_EARLEY_SCAN + " " + wSplit[posInt];
            ArrayList<String> newVector = new ArrayList<>();
            for (int k = 0; k * 2 + 5 < itemForm.length; k++) {
              newVector.add(itemForm[2 * k + 4]);
              newVector.add(itemForm[2 * k + 5]);
            }
            newVector.set(place * 2, itemForm[1]);
            newVector.set(place * 2 + 1, String.valueOf(posInt + 1));
            ChartItemInterface consequence =
                new SrcgEarleyActiveItem(itemForm[0], posInt + 1, iInt,
                    jInt + 1, newVector);
            List<Tree> derivedTrees = new ArrayList<>();
            for (Tree tree : antecedences.get(0).getTrees()) {
              derivedTrees.add(TreeUtils
                  .performPositionSubstitution(tree, wSplit[posInt],
                      itemForm[1]));
            }
            consequence.setTrees(derivedTrees);
            logItemGeneration(consequence);
            consequences.add(consequence);
          }
        } catch (ParseException e) {
          throw new RuntimeException(e);
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

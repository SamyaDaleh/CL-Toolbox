package com.github.samyadaleh.cltoolbox.chartparsing.ccg;

import com.github.samyadaleh.cltoolbox.chartparsing.dynamicdeductionrule.AbstractDynamicDecutionRuleTwoAntecedences;
import com.github.samyadaleh.cltoolbox.chartparsing.item.DeductionChartItem;

import static com.github.samyadaleh.cltoolbox.common.Constants.DEDUCTION_RULE_CCG_DEDUCTION_FORWARDCOMPOSITION1;

public class CcgForwardComposition1
    extends AbstractDynamicDecutionRuleTwoAntecedences {
  public CcgForwardComposition1() {
    this.name = DEDUCTION_RULE_CCG_DEDUCTION_FORWARDCOMPOSITION1;
    this.antNeeded = 2;
  }

  @Override
  protected void calculateConsequences(String[] itemForm1, String[] itemForm2) {
    if (!itemForm1[2].equals(itemForm2[1])) {
      return;
    }
    char symbol = '/';
    int slashPos = CcgDeductionUtils.findSymbolPos(itemForm1[0], symbol);
    if (slashPos == -1) {
      return;
    }
    int slashPos2 = CcgDeductionUtils.findSymbolPos(itemForm2[0], symbol);
    if (slashPos2 == -1) {
      return;
    }
    String x = itemForm1[0].substring(0, slashPos);
    String y = itemForm1[0].substring(slashPos + 1);
    String z = itemForm2[0].substring(slashPos2 + 1);
    if (itemForm2[0].substring(0, slashPos2).equals(y)) {
      DeductionChartItem consequence =
          new DeductionChartItem(x + "/" + z, itemForm1[1], itemForm2[2]);
      CcgDeductionUtils.addNewTrees(this, itemForm1, consequence, x + "/" + z);
      consequences.add(consequence);
    }
  }

  @Override public String toString() {
    return "[X/Y,i,j] [Y/Z,j,k]\n_______\n    [X/Z,i,k]";
  }
}

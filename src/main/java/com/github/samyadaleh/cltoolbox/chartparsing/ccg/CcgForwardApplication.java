package com.github.samyadaleh.cltoolbox.chartparsing.ccg;

import com.github.samyadaleh.cltoolbox.chartparsing.dynamicdeductionrule.AbstractDynamicDecutionRuleTwoAntecedences;
import com.github.samyadaleh.cltoolbox.chartparsing.item.DeductionChartItem;

import static com.github.samyadaleh.cltoolbox.common.Constants.DEDUCTION_RULE_CCG_DEDUCTION_FORWARDAPPLICATION;

public class CcgForwardApplication
    extends AbstractDynamicDecutionRuleTwoAntecedences {

  public CcgForwardApplication() {
    this.name = DEDUCTION_RULE_CCG_DEDUCTION_FORWARDAPPLICATION;
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
    String x = itemForm1[0].substring(0, slashPos);
    String y = itemForm1[0].substring(slashPos + 1);
    if (x.startsWith("(") && x.endsWith(")")) {
      x = x.substring(1, x.length() - 1);
    }
    if (itemForm2[0].equals(y)) {
      DeductionChartItem consequence =
          new DeductionChartItem(x, itemForm1[1], itemForm2[2]);
      CcgDeductionUtils.addNewTrees(this, itemForm1, consequence, x);
      consequences.add(consequence);
    }
  }

  @Override public String toString() {
    return "[X/Y,i,j] [Y,j,k]\n_______\n    [X,i,k]";
  }
}

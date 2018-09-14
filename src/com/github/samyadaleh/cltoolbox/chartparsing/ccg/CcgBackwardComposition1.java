package com.github.samyadaleh.cltoolbox.chartparsing.ccg;

import com.github.samyadaleh.cltoolbox.chartparsing.dynamicdeductionrule.AbstractDynamicDecutionRuleTwoAntecedences;
import com.github.samyadaleh.cltoolbox.chartparsing.item.DeductionChartItem;

public class CcgBackwardComposition1
    extends AbstractDynamicDecutionRuleTwoAntecedences {
  public CcgBackwardComposition1() {
    this.name = "Backward Composition 1";
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
    char symbol2 = '\\';
    int slashPos2 = CcgDeductionUtils.findSymbolPos(itemForm2[0], symbol2);
    if (slashPos2 == -1) {
      return;
    }
    String x = itemForm2[0].substring(0, slashPos2);
    String y = itemForm1[0].substring(0, slashPos);
    String z = itemForm1[0].substring(slashPos + 1);
    if (itemForm2[0].substring(slashPos2 + 1).equals(y)) {
      DeductionChartItem consequence =
          new DeductionChartItem(x + "\\" + z, itemForm1[1], itemForm2[2]);
      CcgDeductionUtils.addNewTrees(this, itemForm1, consequence, x);
      consequences.add(consequence);
    }
  }

  @Override public String toString() {
    return "[Y/Z,i,j] [X\\Y,j,k]\n_______\n    [X\\Z,i,k]";
  }
}

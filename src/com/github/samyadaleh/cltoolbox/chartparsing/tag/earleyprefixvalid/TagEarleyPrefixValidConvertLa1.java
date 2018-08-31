package com.github.samyadaleh.cltoolbox.chartparsing.tag.earleyprefixvalid;

import java.util.List;

import com.github.samyadaleh.cltoolbox.chartparsing.dynamicdeductionrule.AbstractDynamicDeductionRule;
import com.github.samyadaleh.cltoolbox.chartparsing.item.DeductionChartItem;
import com.github.samyadaleh.cltoolbox.chartparsing.item.ChartItemInterface;

public class TagEarleyPrefixValidConvertLa1
    extends AbstractDynamicDeductionRule {

  public TagEarleyPrefixValidConvertLa1() {
    this.name = "convert la1";
    this.antNeeded = 1;
  }

  @Override public List<ChartItemInterface> getConsequences() {
    if (antecedences.size() == antNeeded) {
      String[] itemForm = antecedences.get(0).getItemForm();
      String iGamma = itemForm[3];
      if (itemForm[2].equals("la") && itemForm[8].equals("0") && !iGamma
          .equals("~") && !itemForm[4].equals("~") && !itemForm[5].equals("~")
          && !itemForm[6].equals("~")) {
        ChartItemInterface consequence =
            new DeductionChartItem(itemForm[0], itemForm[1], "la", iGamma, "~",
                "~", "~", itemForm[7], "0");
        consequence.setTrees(antecedences.get(0).getTrees());
        logItemGeneration(consequence);
        consequences.add(consequence);
      }
    }
    return consequences;
  }

  @Override public String toString() {
    return "[ɣ,p,la,i_ɣ,i,j,k,l,0]" + "\n______ \n" + "[ɣ,p,la,i_ɣ,~,~,~,l,0]";
  }

}

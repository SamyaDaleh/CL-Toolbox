package com.github.samyadaleh.cltoolbox.chartparsing.tag.earleyprefixvalid;

import java.util.List;

import com.github.samyadaleh.cltoolbox.chartparsing.dynamicdeductionrule.AbstractDynamicDeductionRule;
import com.github.samyadaleh.cltoolbox.chartparsing.item.DeductionChartItem;
import com.github.samyadaleh.cltoolbox.chartparsing.item.ChartItemInterface;

import static com.github.samyadaleh.cltoolbox.common.Constants.DEDUCTION_RULE_TAG_EARLEY_PREFIXVALID_CONVERTRB;

public class TagEarleyPrefixValidConvertRb
    extends AbstractDynamicDeductionRule {

  public TagEarleyPrefixValidConvertRb() {
    this.name = DEDUCTION_RULE_TAG_EARLEY_PREFIXVALID_CONVERTRB;
    this.antNeeded = 1;
  }

  @Override public List<ChartItemInterface> getConsequences() {
    if (antecedences.size() == antNeeded) {
      String[] itemForm = antecedences.get(0).getItemForm();
      if (itemForm[2].equals("rb") && itemForm[8].equals("0") && itemForm[3]
          .equals("~") && !itemForm[5].equals("~") && !itemForm[6]
          .equals("~")) {
        ChartItemInterface consequence =
            new DeductionChartItem(itemForm[0], itemForm[1], "rb", "~",
                itemForm[4], "~", "~", itemForm[7], "0");
        consequence.setTrees(antecedences.get(0).getTrees());
        logItemGeneration(consequence);
        consequences.add(consequence);
      }
    }
    return consequences;
  }

  @Override public String toString() {
    return "[ɣ,p,lb,~,i,j,k,l,0]" + "\n______ \n" + "[ɣ,p.1,la,~,i,~,~,l,0]";
  }

}

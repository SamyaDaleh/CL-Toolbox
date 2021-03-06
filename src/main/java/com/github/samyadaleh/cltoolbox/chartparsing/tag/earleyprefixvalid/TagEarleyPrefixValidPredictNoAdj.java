package com.github.samyadaleh.cltoolbox.chartparsing.tag.earleyprefixvalid;

import com.github.samyadaleh.cltoolbox.chartparsing.dynamicdeductionrule.AbstractDynamicDeductionRule;
import com.github.samyadaleh.cltoolbox.chartparsing.item.ChartItemInterface;
import com.github.samyadaleh.cltoolbox.chartparsing.item.DeductionChartItem;
import com.github.samyadaleh.cltoolbox.common.tag.Tag;

import java.util.List;

import static com.github.samyadaleh.cltoolbox.common.Constants.DEDUCTION_RULE_TAG_EARLEY_PREFIXVALID_PREDICTNOADJOIN;

public class TagEarleyPrefixValidPredictNoAdj
    extends AbstractDynamicDeductionRule {

  private final Tag tag;

  public TagEarleyPrefixValidPredictNoAdj(Tag tag) {
    this.tag = tag;
    this.name = DEDUCTION_RULE_TAG_EARLEY_PREFIXVALID_PREDICTNOADJOIN;
    this.antNeeded = 1;
  }

  @Override public List<ChartItemInterface> getConsequences() {
    if (antecedences.size() == antNeeded) {
      String[] itemForm = antecedences.get(0).getItemForm();
      String treeName = itemForm[0];
      String node = itemForm[1];
      String iGamma = itemForm[3];
      String l = itemForm[7];
      if (itemForm[2].equals("la") && itemForm[8].equals("0") && !iGamma
          .equals("~") && !itemForm[4].equals("~") && !itemForm[5].equals("~")
          && !itemForm[6].equals("~") && !tag.getTree(treeName).isInOA(node)) {
        ChartItemInterface consequence =
            new DeductionChartItem(treeName, node, "lb", iGamma, l, "-", "-", l,
                "0");
        consequence.setTrees(antecedences.get(0).getTrees());
        logItemGeneration(consequence);
        consequences.add(consequence);
      }
    }
    return consequences;
  }

  @Override public String toString() {
    return "[ɣ,p,la,i_ɣ,i,j,k,l,0]" + "\n______ f_OA(ɣ,p) = 0\n"
        + "[ɣ,p,lb,i_ɣ,i,j,k,l,0]";
  }

}

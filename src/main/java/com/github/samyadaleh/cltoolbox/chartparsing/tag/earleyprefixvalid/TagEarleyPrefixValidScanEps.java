package com.github.samyadaleh.cltoolbox.chartparsing.tag.earleyprefixvalid;

import java.util.List;

import com.github.samyadaleh.cltoolbox.chartparsing.dynamicdeductionrule.AbstractDynamicDeductionRule;
import com.github.samyadaleh.cltoolbox.chartparsing.item.DeductionChartItem;
import com.github.samyadaleh.cltoolbox.chartparsing.item.ChartItemInterface;
import com.github.samyadaleh.cltoolbox.common.tag.Tag;

import static com.github.samyadaleh.cltoolbox.common.Constants.DEDUCTION_RULE_TAG_EARLEY_SCANEPS;

public class TagEarleyPrefixValidScanEps extends AbstractDynamicDeductionRule {

  private final Tag tag;

  public TagEarleyPrefixValidScanEps(Tag tag) {
    this.tag = tag;
    this.name = DEDUCTION_RULE_TAG_EARLEY_SCANEPS;
    this.antNeeded = 1;
  }

  @Override public List<ChartItemInterface> getConsequences() {
    if (antecedences.size() == antNeeded) {
      String[] itemForm = antecedences.get(0).getItemForm();
      String treeName = itemForm[0];
      String node = itemForm[1];
      if (itemForm[2].equals("la") && itemForm[8].equals("0") && tag
          .getTree(treeName).getNodeByGornAddress(node).getLabel().equals("")
          && !"~".equals(itemForm[6])) {
        ChartItemInterface consequence =
            new DeductionChartItem(treeName, node, "ra", itemForm[3],
                itemForm[4], itemForm[5], itemForm[6], itemForm[7], "0");
        consequence.setTrees(antecedences.get(0).getTrees());
        logItemGeneration(consequence);
        consequences.add(consequence);
      }
    }
    return consequences;
  }

  @Override public String toString() {
    return "[ɣ,p,la,i_ɣ,i,j,k,l,0]" + "\n______ l(ɣ,p) = ε\n"
        + "[ɣ,p,ra,i_ɣ,i,j,k,l,0]";
  }

}

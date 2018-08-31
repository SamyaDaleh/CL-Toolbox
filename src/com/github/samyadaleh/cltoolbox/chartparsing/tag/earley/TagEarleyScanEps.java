package com.github.samyadaleh.cltoolbox.chartparsing.tag.earley;

import java.util.List;

import com.github.samyadaleh.cltoolbox.chartparsing.dynamicdeductionrule.AbstractDynamicDeductionRule;
import com.github.samyadaleh.cltoolbox.chartparsing.item.DeductionChartItem;
import com.github.samyadaleh.cltoolbox.chartparsing.item.ChartItemInterface;
import com.github.samyadaleh.cltoolbox.common.tag.Tag;

/**
 * If the node's label is epsilon, just move on.
 */
public class TagEarleyScanEps extends AbstractDynamicDeductionRule {

  private final Tag tag;

  /**
   * Constructor needs the grammar to retrieve information about the
   * antecedence.
   */
  public TagEarleyScanEps(Tag tag) {
    this.tag = tag;
    this.name = "scan ε";
    this.antNeeded = 1;
  }

  @Override public List<ChartItemInterface> getConsequences() {
    if (antecedences.size() == antNeeded) {
      String[] itemForm = antecedences.get(0).getItemForm();
      String treeName = itemForm[0];
      String node = itemForm[1];
      if (itemForm[2].equals("la") && itemForm[7].equals("0") && tag
          .getTree(treeName).getNodeByGornAdress(node).getLabel().equals("")) {
        ChartItemInterface consequence =
            new DeductionChartItem(treeName, node, "ra", itemForm[3],
                itemForm[4], itemForm[5], itemForm[6], "0");
        consequence.setTrees(antecedences.get(0).getTrees());
        logItemGeneration(consequence);
        consequences.add(consequence);
      }
    }
    return consequences;
  }

  @Override public String toString() {
    return "[ɣ,p,la,i,j,k,l,0]" + "\n______ l(ɣ,p) = ε\n"
        + "[ɣ,p,ra,i,j,k,l,0]";
  }

}

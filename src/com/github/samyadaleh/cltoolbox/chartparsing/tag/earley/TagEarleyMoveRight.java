package com.github.samyadaleh.cltoolbox.chartparsing.tag.earley;

import java.util.List;

import com.github.samyadaleh.cltoolbox.chartparsing.dynamicdeductionrule.AbstractDynamicDeductionRule;
import com.github.samyadaleh.cltoolbox.chartparsing.item.DeductionChartItem;
import com.github.samyadaleh.cltoolbox.chartparsing.item.ChartItemInterface;
import com.github.samyadaleh.cltoolbox.common.tag.Tag;

/**
 * If a node has a right sibling, move to that sibling.
 */
public class TagEarleyMoveRight extends AbstractDynamicDeductionRule {

  private final Tag tag;

  /**
   * Constructor needs the grammar to retrieve information about the
   * antecedence.
   */
  public TagEarleyMoveRight(Tag tag) {
    this.tag = tag;
    this.name = "move right";
    this.antNeeded = 1;
  }

  @Override public List<ChartItemInterface> getConsequences() {
    if (antecedences.size() == antNeeded) {
      String[] itemForm = antecedences.get(0).getItemForm();
      String treeName = itemForm[0];
      String siblingGorn =
          tag.getTree(treeName).getNodeByGornAdress(itemForm[1])
              .getGornAddressOfPotentialRightSibling();
      if (itemForm[2].equals("ra") && itemForm[7].equals("0")
          && tag.getTree(treeName).getNodeByGornAdress(siblingGorn) != null) {
        ChartItemInterface consequence =
            new DeductionChartItem(treeName, siblingGorn, "la", itemForm[3],
                itemForm[4], itemForm[5], itemForm[6], "0");
        consequence.setTrees(antecedences.get(0).getTrees());
        logItemGeneration(consequence);
        consequences.add(consequence);
      }
    }
    return consequences;
  }

  @Override public String toString() {
    return "[ɣ,p,ra,i,j,k,l,0]" + "\n______ ɣ(p+1) is defined\n"
        + "[ɣ,p+1,la,i,j,k,l,0]";
  }

}

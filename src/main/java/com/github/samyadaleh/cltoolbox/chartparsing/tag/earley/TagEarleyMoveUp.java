package com.github.samyadaleh.cltoolbox.chartparsing.tag.earley;

import java.util.List;

import com.github.samyadaleh.cltoolbox.chartparsing.dynamicdeductionrule.AbstractDynamicDeductionRule;
import com.github.samyadaleh.cltoolbox.chartparsing.item.DeductionChartItem;
import com.github.samyadaleh.cltoolbox.chartparsing.item.ChartItemInterface;
import com.github.samyadaleh.cltoolbox.common.tag.Tag;

import static com.github.samyadaleh.cltoolbox.common.Constants.DEDUCTION_RULE_TAG_EARLEY_MOVE_UP;

/**
 * If a node has no right sibling, move to the parent.
 */
public class TagEarleyMoveUp extends AbstractDynamicDeductionRule {

  private final Tag tag;

  /**
   * Constructor needs the grammar to retrieve information about the
   * antecedence.
   */
  public TagEarleyMoveUp(Tag tag) {
    this.tag = tag;
    this.name = DEDUCTION_RULE_TAG_EARLEY_MOVE_UP;
    this.antNeeded = 1;
  }

  @Override public List<ChartItemInterface> getConsequences() {
    if (antecedences.size() == antNeeded) {
      String[] itemForm = antecedences.get(0).getItemForm();
      String treeName = itemForm[0];
      String node = itemForm[1];
      String siblingGorn = tag.getTree(treeName).getNodeByGornAdress(node)
          .getGornAddressOfPotentialRightSibling();
      if (!node.equals("") && itemForm[2].equals("ra") && itemForm[7]
          .equals("0")
          && tag.getTree(treeName).getNodeByGornAdress(siblingGorn) == null) {
        String parentGorn = tag.getTree(treeName).getNodeByGornAdress(node)
            .getGornAddressOfParent();
        ChartItemInterface consequence =
            new DeductionChartItem(treeName, parentGorn, "rb", itemForm[3],
                itemForm[4], itemForm[5], itemForm[6], "0");
        consequence.setTrees(antecedences.get(0).getTrees());
        logItemGeneration(consequence);
        consequences.add(consequence);
      }
    }
    return consequences;
  }

  @Override public String toString() {
    return "[ɣ,p.m,ra,i,j,k,l,0]" + "\n______ ɣ(p.m+1) is not defined\n"
        + "[ɣ,p,rb,i,j,k,l,0]";
  }

}

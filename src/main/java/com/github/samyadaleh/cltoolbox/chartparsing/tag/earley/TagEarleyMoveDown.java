package com.github.samyadaleh.cltoolbox.chartparsing.tag.earley;

import java.util.List;

import com.github.samyadaleh.cltoolbox.chartparsing.dynamicdeductionrule.AbstractDynamicDeductionRule;
import com.github.samyadaleh.cltoolbox.chartparsing.item.DeductionChartItem;
import com.github.samyadaleh.cltoolbox.chartparsing.item.ChartItemInterface;
import com.github.samyadaleh.cltoolbox.common.tag.Tag;

import static com.github.samyadaleh.cltoolbox.common.Constants.DEDUCTION_RULE_TAG_EARLEY_MOVE_DOWN;

/**
 * If a node has a child, move to the fist child.
 */
public class TagEarleyMoveDown extends AbstractDynamicDeductionRule {

  private final Tag tag;

  /**
   * Constructor needs the grammar to retrieve information about the
   * antecedence.
   */
  public TagEarleyMoveDown(Tag tag) {
    this.tag = tag;
    this.name = DEDUCTION_RULE_TAG_EARLEY_MOVE_DOWN;
    this.antNeeded = 1;
  }

  @Override public List<ChartItemInterface> getConsequences() {
    if (antecedences.size() == antNeeded) {
      String[] itemForm = antecedences.get(0).getItemForm();
      String treeName = itemForm[0];
      String node = itemForm[1];
      if (itemForm[2].equals("lb") && itemForm[7].equals("0")
          && tag.getTree(treeName).getNodeByGornAddress(node + ".1") != null) {
        ChartItemInterface consequence =
            new DeductionChartItem(treeName, node + ".1", "la", itemForm[3],
                itemForm[4], itemForm[5], itemForm[6], "0");
        consequence.setTrees(antecedences.get(0).getTrees());
        logItemGeneration(consequence);
        consequences.add(consequence);
      }
    }
    return consequences;
  }

  @Override public String toString() {
    return "[ɣ,p,lb,i,j,k,l,0]" + "\n______ ɣ(p.1) is defined\n"
        + "[ɣ,p.1,la,i,j,k,l,0]";
  }

}

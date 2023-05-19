package com.github.samyadaleh.cltoolbox.chartparsing.tag.cyk;

import java.util.List;

import com.github.samyadaleh.cltoolbox.chartparsing.dynamicdeductionrule.AbstractDynamicDeductionRule;
import com.github.samyadaleh.cltoolbox.chartparsing.item.DeductionChartItem;
import com.github.samyadaleh.cltoolbox.chartparsing.item.ChartItemInterface;
import com.github.samyadaleh.cltoolbox.common.tag.Tag;

import static com.github.samyadaleh.cltoolbox.common.Constants.DEDUCTION_RULE_TAG_MOVE_UNARY;

/** From a single-child node move up to the parent node. */
public class TagCykMoveUnary extends AbstractDynamicDeductionRule {

  private final Tag tag;

  /**
   * Constructor needs the grammar to retrieve information about the
   * antecedences.
   */
  public TagCykMoveUnary(Tag tag) {
    this.tag = tag;
    this.name = DEDUCTION_RULE_TAG_MOVE_UNARY;
    this.antNeeded = 1;
  }

  @Override public List<ChartItemInterface> getConsequences() {
    if (antecedences.size() == antNeeded) {
      String[] itemForm = antecedences.get(0).getItemForm();
      String treeName = itemForm[0];
      String node = itemForm[1];
      String i = itemForm[2];
      String f1 = itemForm[3];
      String f2 = itemForm[4];
      String j = itemForm[5];
      if (node.endsWith(".1⊤")) {
        String nodeSib = tag.getTree(treeName)
          .getNodeByGornAddress(node.substring(0, node.length() - 1))
          .getGornAddressOfPotentialRightSibling();
        if (tag.getTree(treeName).getNodeByGornAddress(nodeSib) == null) {
          String parentNode = node.substring(0, node.length() - 3) + "⊥";
          ChartItemInterface consequence =
            new DeductionChartItem(treeName, parentNode, i, f1, f2, j);
          consequence.setTrees(antecedences.get(0).getTrees());
          logItemGeneration(consequence);
          consequences.add(consequence);
        }
      }
    }
    return consequences;
  }

  @Override public String toString() {
    return "[ɣ,(p.1)⊤,i,f1,f2,j]"
      + "\n______ node adress p.2 does not exist in ɣ\n" + "[ɣ,p⊥,i,f1,f2,j]";
  }

}

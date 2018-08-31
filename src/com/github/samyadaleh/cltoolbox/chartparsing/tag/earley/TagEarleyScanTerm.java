package com.github.samyadaleh.cltoolbox.chartparsing.tag.earley;

import java.util.List;

import com.github.samyadaleh.cltoolbox.chartparsing.dynamicdeductionrule.AbstractDynamicDeductionRule;
import com.github.samyadaleh.cltoolbox.chartparsing.item.DeductionChartItem;
import com.github.samyadaleh.cltoolbox.chartparsing.item.ChartItemInterface;
import com.github.samyadaleh.cltoolbox.common.tag.Tag;

/**
 * If the node's label is the next input symbol, consume it.
 */
public class TagEarleyScanTerm extends AbstractDynamicDeductionRule {

  private final String[] wSplit;
  private final Tag tag;

  /**
   * Constructor takes the input string to compare with the tree labels, also
   * needs the grammar to retrieve information about the antecedence.
   */
  public TagEarleyScanTerm(String[] wSplit, Tag tag) {
    this.wSplit = wSplit;
    this.tag = tag;
    this.name = "scan";
    this.antNeeded = 1;
  }

  @Override public List<ChartItemInterface> getConsequences() {
    if (antecedences.size() == antNeeded) {
      String[] itemForm = antecedences.get(0).getItemForm();
      String treeName = itemForm[0];
      String node = itemForm[1];
      int lInt = Integer.parseInt(itemForm[6]);
      if (lInt < wSplit.length && itemForm[2].equals("la") && itemForm[7]
          .equals("0") && tag.getTree(treeName).getNodeByGornAdress(node)
          .getLabel().equals(wSplit[lInt])) {
        this.name = "scan " + wSplit[lInt];
        ChartItemInterface consequence =
            new DeductionChartItem(treeName, node, "ra", itemForm[3],
                itemForm[4], itemForm[5], String.valueOf(lInt + 1), "0");
        consequence.setTrees(antecedences.get(0).getTrees());
        logItemGeneration(consequence);
        consequences.add(consequence);
      }
    }
    return consequences;
  }

  @Override public String toString() {
    return "[ɣ,p,la,i,j,k,l,0]" + "\n______ l(ɣ,p) = w_l\n"
        + "[ɣ,p,ra,i,j,k,l+1,0]";
  }

}

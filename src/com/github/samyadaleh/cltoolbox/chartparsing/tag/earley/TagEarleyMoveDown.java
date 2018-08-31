package com.github.samyadaleh.cltoolbox.chartparsing.tag.earley;

import java.util.List;

import com.github.samyadaleh.cltoolbox.chartparsing.dynamicdeductionrule.AbstractDynamicDeductionRule;
import com.github.samyadaleh.cltoolbox.chartparsing.item.DeductionChartItem;
import com.github.samyadaleh.cltoolbox.chartparsing.item.ChartItemInterface;
import com.github.samyadaleh.cltoolbox.common.tag.Tag;

/** If a node has a child, move to the fist child. */
public class TagEarleyMoveDown extends AbstractDynamicDeductionRule {

  private final Tag tag;

  /** Constructor needs the grammar to retrieve information about the
   * antecedence. */
  public TagEarleyMoveDown(Tag tag) {
    this.tag = tag;
    this.name = "move down";
    this.antNeeded = 1;
  }

  @Override public List<ChartItemInterface> getConsequences() {
    if (antecedences.size() == antNeeded) {
      String[] itemForm = antecedences.get(0).getItemForm();
      String treeName = itemForm[0];
      String node = itemForm[1];
      String pos = itemForm[2];
      String i = itemForm[3];
      String j = itemForm[4];
      String k = itemForm[5];
      String l = itemForm[6];
      String adj = itemForm[7];
      if (pos.equals("lb") && adj.equals("0")
        && tag.getTree(treeName).getNodeByGornAdress(node + ".1") != null) {
        ChartItemInterface consequence =
          new DeductionChartItem(treeName, node + ".1", "la", i, j, k, l, "0");
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

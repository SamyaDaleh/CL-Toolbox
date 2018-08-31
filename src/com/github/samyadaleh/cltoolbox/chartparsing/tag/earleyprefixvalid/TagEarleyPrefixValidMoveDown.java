package com.github.samyadaleh.cltoolbox.chartparsing.tag.earleyprefixvalid;

import java.util.List;

import com.github.samyadaleh.cltoolbox.chartparsing.dynamicdeductionrule.AbstractDynamicDeductionRule;
import com.github.samyadaleh.cltoolbox.chartparsing.item.DeductionChartItem;
import com.github.samyadaleh.cltoolbox.chartparsing.item.ChartItemInterface;
import com.github.samyadaleh.cltoolbox.common.tag.Tag;

public class TagEarleyPrefixValidMoveDown extends AbstractDynamicDeductionRule {

  private final Tag tag;

  public TagEarleyPrefixValidMoveDown(Tag tag) {
    this.tag = tag;
    this.name = "move down";
    this.antNeeded = 1;
  }

  @Override public List<ChartItemInterface> getConsequences() {
    if (antecedences.size() == antNeeded) {
      String[] itemForm = antecedences.get(0).getItemForm();
      String treeName = itemForm[0];
      String node = itemForm[1];
      String iGamma = itemForm[3];
      String i = itemForm[4];
      String j = itemForm[5];
      String k = itemForm[6];
      if (itemForm[2].equals("lb") && !iGamma.equals("~") && !i.equals("~")
          && !j.equals("~") && !k.equals("~") && itemForm[8].equals("0")
          && tag.getTree(treeName).getNodeByGornAdress(node + ".1") != null) {
        ChartItemInterface consequence =
            new DeductionChartItem(treeName, node + ".1", "la", iGamma, i, j, k,
                itemForm[7], "0");
        consequence.setTrees(antecedences.get(0).getTrees());
        logItemGeneration(consequence);
        consequences.add(consequence);
      }
    }
    return consequences;
  }

  @Override public String toString() {
    return "[ɣ,p,lb,i_ɣ,i,j,k,l,0]" + "\n______ \n"
        + "[ɣ,p.1,la,i_ɣ,i,j,k,l,0]";
  }

}

package com.github.samyadaleh.cltoolbox.chartparsing.tag.earleyprefixvalid;

import java.util.List;

import com.github.samyadaleh.cltoolbox.chartparsing.AbstractDynamicDeductionRule;
import com.github.samyadaleh.cltoolbox.chartparsing.DeductionChartItem;
import com.github.samyadaleh.cltoolbox.chartparsing.ChartItemInterface;
import com.github.samyadaleh.cltoolbox.common.tag.Tag;

public class TagEarleyPrefixValidMoveUp extends AbstractDynamicDeductionRule {

  private final Tag tag;

  public TagEarleyPrefixValidMoveUp(Tag tag) {
    this.tag = tag;
    this.name = "move up";
    this.antNeeded = 1;
  }

  @Override public List<ChartItemInterface> getConsequences() {
    if (antecedences.size() == antNeeded) {
      String[] itemForm = antecedences.get(0).getItemForm();
      String treeName = itemForm[0];
      String node = itemForm[1];
      String pos = itemForm[2];
      String iGamma = itemForm[3];
      String i = itemForm[4];
      String j = itemForm[5];
      String k = itemForm[6];
      String l = itemForm[7];
      String adj = itemForm[8];
      String siblingGorn = tag.getTree(treeName).getNodeByGornAdress(node)
          .getGornAddressOfPotentialRightSibling();
      if (!node.equals("") && pos.equals("ra") && !iGamma.equals("~") && !i
          .equals("~") && !j.equals("~") && !k.equals("~") && adj.equals("0")
          && tag.getTree(treeName).getNodeByGornAdress(siblingGorn) == null) {
        String parentGorn = tag.getTree(treeName).getNodeByGornAdress(node)
            .getGornAddressOfParent();
        ChartItemInterface consequence =
            new DeductionChartItem(treeName, parentGorn, "rb", "~", i, j, k, l,
                "0");
        consequence.setTrees(antecedences.get(0).getTrees());
        logItemGeneration(consequence);
        consequences.add(consequence);
      }
    }
    return consequences;
  }

  @Override public String toString() {
    return "[ɣ,p.m,ra,i_ɣ,i,j,k,l,0]" + "\n______ ɣ(p.m+1) is not defined\n"
        + "[ɣ,p,rb,i_ɣ,i,j,k,l,0]";
  }

}

package com.github.samyadaleh.cltoolbox.chartparsing.tag.earleyprefixvalid;

import java.util.List;

import com.github.samyadaleh.cltoolbox.chartparsing.dynamicdeductionrule.AbstractDynamicDeductionRule;
import com.github.samyadaleh.cltoolbox.chartparsing.item.DeductionChartItem;
import com.github.samyadaleh.cltoolbox.chartparsing.item.ChartItemInterface;
import com.github.samyadaleh.cltoolbox.common.tag.Tag;

import static com.github.samyadaleh.cltoolbox.common.Constants.DEDUCTION_RULE_TAG_EARLEY_MOVE_UP;

public class TagEarleyPrefixValidMoveUp extends AbstractDynamicDeductionRule {

  private final Tag tag;

  public TagEarleyPrefixValidMoveUp(Tag tag) {
    this.tag = tag;
    this.name = DEDUCTION_RULE_TAG_EARLEY_MOVE_UP;
    this.antNeeded = 1;
  }

  @Override public List<ChartItemInterface> getConsequences() {
    if (antecedences.size() == antNeeded) {
      String[] itemForm = antecedences.get(0).getItemForm();
      String treeName = itemForm[0];
      String node = itemForm[1];
      String i = itemForm[4];
      String j = itemForm[5];
      String k = itemForm[6];
      String siblingGorn = tag.getTree(treeName).getNodeByGornAdress(node)
          .getGornAddressOfPotentialRightSibling();
      if (!node.equals("") && itemForm[2].equals("ra") && !itemForm[3]
          .equals("~") && !i.equals("~") && !j.equals("~") && !k.equals("~")
          && itemForm[8].equals("0")
          && tag.getTree(treeName).getNodeByGornAdress(siblingGorn) == null) {
        String parentGorn = tag.getTree(treeName).getNodeByGornAdress(node)
            .getGornAddressOfParent();
        ChartItemInterface consequence =
            new DeductionChartItem(treeName, parentGorn, "rb", "~", i, j, k,
                itemForm[7], "0");
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

package com.github.samyadaleh.cltoolbox.chartparsing.tag.earleyprefixvalid;

import com.github.samyadaleh.cltoolbox.chartparsing.dynamicdeductionrule.AbstractDynamicDeductionRuleThreeAntecedences;
import com.github.samyadaleh.cltoolbox.chartparsing.item.ChartItemInterface;
import com.github.samyadaleh.cltoolbox.chartparsing.item.DeductionChartItem;
import com.github.samyadaleh.cltoolbox.common.ArrayUtils;
import com.github.samyadaleh.cltoolbox.common.tag.Tag;
import com.github.samyadaleh.cltoolbox.common.tag.Tree;
import com.github.samyadaleh.cltoolbox.common.tag.Vertex;

import java.util.Arrays;
import java.util.List;

public class TagEarleyPrefixValidCompleteFoot
    extends AbstractDynamicDeductionRuleThreeAntecedences {

  private final Tag tag;

  public TagEarleyPrefixValidCompleteFoot(Tag tag) {
    this.tag = tag;
    this.name = "complete foot";
    this.antNeeded = 3;
  }

  protected void calculateConsequences(String[] itemForm1, String[] itemForm2,
      String[] itemForm3) {
    String treeName1 = itemForm1[0];
    String node1 = itemForm1[1];
    String pos1 = itemForm1[2];
    String iGamma1 = itemForm1[3];
    String i1 = itemForm1[4];
    String j1 = itemForm1[5];
    String k1 = itemForm1[6];
    String l1 = itemForm1[7];
    String adj1 = itemForm1[8];
    String treeName2 = itemForm2[0];
    String node2 = itemForm2[1];
    String i2 = itemForm2[4];
    String[] itemForm2Goal =
        new String[] {"?", "?", "la", "?", "?", "-", "-", itemForm1[4], "0"};
    String[] itemForm3Goal =
        new String[] {itemForm1[0], itemForm1[1], "la", "~", "~", "~", "~",
            itemForm2[3], "0"};
    if (tag.getAuxiliaryTree(treeName2) != null) {
      Vertex pf = tag.getAuxiliaryTree(treeName2).getNodeByGornAdress(node2);
      if (pos1.equals("rb") && iGamma1.equals("~") && j1.equals("~") && k1
          .equals("~") && adj1.equals("0") && pf == tag
          .getAuxiliaryTree(treeName2).getFoot() && !i2.equals("~")
          && ArrayUtils.match(itemForm2, itemForm2Goal) && ArrayUtils
          .match(itemForm3, itemForm3Goal) && tag
          .isAdjoinable(treeName2, treeName1, node1)) {
        ChartItemInterface consequence =
            new DeductionChartItem(treeName2, node2, "rb", "~", i1, i1, l1, l1,
                "0");
        List<Tree> derivedTrees = generateDerivatedTrees(itemForm2);
        consequence.setTrees(derivedTrees);
        logItemGeneration(consequence);
        consequences.add(consequence);
      }
    }

  }

  private List<Tree> generateDerivatedTrees(String[] itemForm2) {
    List<Tree> derivedTrees;
    if (Arrays.equals(itemForm2, antecedences.get(0).getItemForm())) {
      derivedTrees = antecedences.get(0).getTrees();
    } else if (Arrays.equals(itemForm2, antecedences.get(1).getItemForm())) {
      derivedTrees = antecedences.get(1).getTrees();
    } else {
      derivedTrees = antecedences.get(2).getTrees();
    }
    return derivedTrees;
  }

  @Override public String toString() {
    return
        "[ɣ,p,rb,~,i,~,~,l,0], [β,p_f,la,i_β,m,-,-,i,0], [ɣ,p,la,~,~,~,~,i_β,0]"
            + "\n______ β(p_f) foot node, β ∈  f_SA(ɣ,p)\n"
            + "[β,p_f,rb,~,m,i,l,l,0]";
  }

}

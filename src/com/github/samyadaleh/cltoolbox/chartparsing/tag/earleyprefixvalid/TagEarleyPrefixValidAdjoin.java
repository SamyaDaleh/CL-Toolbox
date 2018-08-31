package com.github.samyadaleh.cltoolbox.chartparsing.tag.earleyprefixvalid;

import com.github.samyadaleh.cltoolbox.chartparsing.dynamicdeductionrule.AbstractDynamicDeductionRuleThreeAntecedences;
import com.github.samyadaleh.cltoolbox.chartparsing.item.ChartItemInterface;
import com.github.samyadaleh.cltoolbox.chartparsing.item.DeductionChartItem;
import com.github.samyadaleh.cltoolbox.common.ArrayUtils;
import com.github.samyadaleh.cltoolbox.common.tag.Tag;
import com.github.samyadaleh.cltoolbox.common.tag.Tree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TagEarleyPrefixValidAdjoin extends
    AbstractDynamicDeductionRuleThreeAntecedences {

  private final Tag tag;

  public TagEarleyPrefixValidAdjoin(Tag tag) {
    this.tag = tag;
    this.name = "adjoin";
    this.antNeeded = 3;
  }

  protected void calculateConsequences(String[] itemForm1, String[] itemForm2,
      String[] itemForm3) {
    String treeName1 = itemForm1[0];
    String node1 = itemForm1[1];
    String pos1 = itemForm1[2];
    String iGamma1 = itemForm1[3];
    String i1 = itemForm1[4];
    String k1 = itemForm1[6];
    String l1 = itemForm1[7];
    String adj1 = itemForm1[8];
    String treeName2 = itemForm2[0];
    String node2 = itemForm2[1];
    String j2 = itemForm2[5];
    String k2 = itemForm2[6];
    String[] itemForm2Goal =
        new String[] {"?", "?", "rb", "~", itemForm1[5], "?", "?", itemForm1[6],
            "0"};
    String[] itemForm3Goal =
        new String[] {itemForm2[0], itemForm2[1], "la", "~", "~", "~", "~",
            itemForm1[3], "0"};
    boolean adjoinable = tag.isAdjoinable(treeName1, treeName2, node2);
    if (adjoinable && node1.equals("") && iGamma1.equals(i1) && !iGamma1
        .equals("~") && !k1.equals("~") && adj1.equals("0") && pos1.equals("ra")
        && !j2.equals("~") && !k2.equals("~") && ArrayUtils
        .match(itemForm2, itemForm2Goal) && ArrayUtils
        .match(itemForm3, itemForm3Goal)) {
      ChartItemInterface consequence =
          new DeductionChartItem(treeName2, node2, "rb", "~", iGamma1, j2, k2,
              l1, "1");
      generateDerivatedTrees(itemForm1, node2, consequence);
      this.name = "adjoin " + treeName2 + "[" + node2 + "," + treeName1 + "]";
      logItemGeneration(consequence);
      consequences.add(consequence);
    }
  }

  private void generateDerivatedTrees(String[] itemForm1, String node2,
      ChartItemInterface consequence) {
    List<Tree> derivedTrees = new ArrayList<>();
    if (Arrays.equals(antecedences.get(0).getItemForm(), itemForm1)) {
      for (Tree tree1 : antecedences.get(1).getTrees()) {
        for (Tree tree2 : antecedences.get(0).getTrees()) {
          derivedTrees.add(tree1.adjoin(node2, tree2));
        }
      }
    } else if (Arrays.equals(antecedences.get(1).getItemForm(), itemForm1)) {
      for (Tree tree1 : antecedences.get(1).getTrees()) {
        for (Tree tree2 : antecedences.get(0).getTrees()) {
          derivedTrees.add(tree2.adjoin(node2, tree1));
        }
      }
    } else {
      for (Tree tree1 : antecedences.get(2).getTrees()) {
        for (Tree tree2 : antecedences.get(0).getTrees()) {
          derivedTrees.add(tree1.adjoin(node2, tree2));
        }
      }
    }
    consequence.setTrees(derivedTrees);
  }

  @Override public String toString() {
    return
        "[β,ε,ra,i_β,i_β,j,k,l,0], [ɣ,p,rb,~,j,g,h,k,0], [ɣ,p,la,~,~,~,~,i_β,0]"
            + "\n______ β ∈  f_SA(ɣ,p)\n" + "[ɣ,p,rb,~,i_β,g,h,l,1]";
  }

}

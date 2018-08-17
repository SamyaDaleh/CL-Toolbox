package com.github.samyadaleh.cltoolbox.chartparsing.tag.earley;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.github.samyadaleh.cltoolbox.chartparsing.AbstractDynamicDecutionRuleTwoAntecedences;
import com.github.samyadaleh.cltoolbox.chartparsing.DeductionChartItem;
import com.github.samyadaleh.cltoolbox.chartparsing.ChartItemInterface;
import com.github.samyadaleh.cltoolbox.common.tag.Tag;
import com.github.samyadaleh.cltoolbox.common.tag.Tree;

/**
 * Combines an auxiliary tree with another tree to get a new item in which has
 * been adjoined.
 */
public class TagEarleyAdjoin
  extends AbstractDynamicDecutionRuleTwoAntecedences {

  private final Tag tag;

  /**
   * Constructor needs the grammar to retrieve information about the
   * antecedences.
   */
  public TagEarleyAdjoin(Tag tag) {
    this.tag = tag;
    this.name = "adjoin";
    this.antNeeded = 2;
  }

  protected void calculateConsequences(String[] itemForm1, String[] itemForm2) {
    String treeName1 = itemForm1[0];
    String node1 = itemForm1[1];
    String pos1 = itemForm1[2];
    String i = itemForm1[3];
    String j1 = itemForm1[4];
    String l = itemForm1[6];
    String adj1 = itemForm1[7];
    String treeName2 = itemForm2[0];
    String node2 = itemForm2[1];
    String pos2 = itemForm2[2];
    String j2 = itemForm2[3];
    String g = itemForm2[4];
    String h = itemForm2[5];
    String adj2 = itemForm2[7];
    boolean adjoinable1 = tag.isAdjoinable(treeName1, treeName2, node2);
    if (adj1.equals("0") && adj2.equals("0")) {
      if (adjoinable1 && node1.equals("") && pos1.equals("ra")
        && pos2.equals("rb") && j1.equals(j2)) {
        ChartItemInterface consequence =
          new DeductionChartItem(treeName2, node2, "rb", i, g, h, l, "1");
        List<Tree> derivedTrees = new ArrayList<>();
        if (Arrays.equals(antecedences.get(0).getItemForm(), itemForm1)) {
          for (Tree tree1 : antecedences.get(1).getTrees()) {
            for (Tree tree2 : antecedences.get(0).getTrees()) {
              derivedTrees.add(tree1.adjoin(node2, tree2));
            }
          }
        } else {
          for (Tree tree1 : antecedences.get(1).getTrees()) {
            for (Tree tree2 : antecedences.get(0).getTrees()) {
              derivedTrees.add(tree2.adjoin(node2, tree1));
            }
          }
        }
        consequence.setTrees(derivedTrees);
        consequences.add(consequence);
        String node2name = node2.length() == 0 ? "ε" : node2;
        this.name =
          "adjoin " + treeName2 + "[" + node2name + "," + treeName1 + "]";
      }
    }
  }

  @Override public String toString() {
    return "[β,ε,ra,i,j,k,l,0], [ɣ,p,rb,j,g,h,k,0]" + "\n______ β ∈ f_SA(ɣ,p)\n"
      + "[ɣ,p,rb,i,g,h,l,1]";
  }

}

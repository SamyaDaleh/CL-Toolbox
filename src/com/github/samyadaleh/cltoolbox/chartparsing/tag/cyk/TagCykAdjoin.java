package com.github.samyadaleh.cltoolbox.chartparsing.tag.cyk;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.github.samyadaleh.cltoolbox.chartparsing.AbstractDynamicDecutionRuleTwoAntecedences;
import com.github.samyadaleh.cltoolbox.chartparsing.DeductionChartItem;
import com.github.samyadaleh.cltoolbox.chartparsing.ChartItemInterface;
import com.github.samyadaleh.cltoolbox.common.tag.Tag;
import com.github.samyadaleh.cltoolbox.common.tag.Tree;

/** Adjoin an auxiliary tree into an appropriate node in any other tree. */
public class TagCykAdjoin extends AbstractDynamicDecutionRuleTwoAntecedences {

  private final Tag tag;

  /** Rule needs grammar to check if adjoin is possible. */
  public TagCykAdjoin(Tag tag) {
    this.tag = tag;
    this.name = "adjoin";
    this.antNeeded = 2;
  }

  protected void calculateConsequences(String[] itemForm1, String[] itemForm2) {
    String treeName1 = itemForm1[0];
    String treeName2 = itemForm2[0];
    String node1 = itemForm1[1];
    String node2 = itemForm2[1];
    String i = itemForm1[2];
    String f12 = itemForm2[2];
    String f11 = itemForm1[3];
    String f21 = itemForm1[4];
    String f1b = itemForm2[3];
    String f2b = itemForm2[4];
    String j = itemForm1[5];
    String f22 = itemForm2[5];
    if (!f11.equals("-") && f11.equals(f12) && !f21.equals("-")
      && f21.equals(f22)
      && tag.isAdjoinable(treeName1, treeName2,
        node2.substring(0, node2.length() - 1))
      && node1.equals("⊤") && node2.endsWith("⊥")) {
      ChartItemInterface consequence = new DeductionChartItem(treeName2,
        node2.substring(0, node2.length() - 1) + "⊤", i, f1b, f2b, j);
      List<Tree> derivatedTrees = new ArrayList<>();
      if (Arrays.equals(itemForm1, antecedences.get(0).getItemForm())) {
        for (Tree tree1 : antecedences.get(0).getTrees()) {
          for (Tree tree2 : antecedences.get(1).getTrees()) {
            derivatedTrees
              .add(tree2.adjoin(node2.substring(0, node2.length() - 1), tree1));
          }
        }
      } else {
        for (Tree tree1 : antecedences.get(0).getTrees()) {
          for (Tree tree2 : antecedences.get(1).getTrees()) {
            derivatedTrees
              .add(tree1.adjoin(node2.substring(0, node2.length() - 1), tree2));
          }
        }
      }
      String node2Name =
          (node2.length() > 1) ? node2.substring(0, node2.length() - 1) : "ε";
      this.name =
          "adjoin " + treeName2 + "[" + node2Name + "," + treeName1 + "]";
      consequence.setTrees(derivatedTrees);
      logItemGeneration(consequence);
      consequences.add(consequence);
    }
  }

  @Override public String toString() {
    return "[β,ε⊤,i,f1,f2,j] [ɣ,p⊥,f1,f1',f2',f2]" + "\n______ β ∈ f_SA(ɣ,p)\n"
      + "[ɣ,p⊤,i,f1',f2',j]";
  }

}

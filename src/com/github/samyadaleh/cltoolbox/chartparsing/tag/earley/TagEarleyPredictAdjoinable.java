package com.github.samyadaleh.cltoolbox.chartparsing.tag.earley;

import java.util.ArrayList;
import java.util.List;

import com.github.samyadaleh.cltoolbox.chartparsing.dynamicdeductionrule.AbstractDynamicDeductionRule;
import com.github.samyadaleh.cltoolbox.chartparsing.item.DeductionChartItem;
import com.github.samyadaleh.cltoolbox.chartparsing.item.ChartItemInterface;
import com.github.samyadaleh.cltoolbox.common.tag.Tag;
import com.github.samyadaleh.cltoolbox.common.tag.Tree;

/**
 * If the dot is at a node where adjunction is possible, predict the auxiliary
 * tree that can be adjoined into that node.
 */
public class TagEarleyPredictAdjoinable extends AbstractDynamicDeductionRule {

  private final String auxTreeName;
  private final Tag tag;

  /**
   * Constructor takes an auxiliary tree for the items the rule shall derive,
   * also needs the grammar to retrieve information about the antecedence.
   */
  public TagEarleyPredictAdjoinable(String auxTreeName, Tag tag) {
    this.auxTreeName = auxTreeName;
    this.tag = tag;
    this.name = "predict adjoinable with " + auxTreeName;
    this.antNeeded = 1;
  }

  @Override public List<ChartItemInterface> getConsequences() {
    if (antecedences.size() == antNeeded) {
      String[] itemForm = antecedences.get(0).getItemForm();
      String treeName = itemForm[0];
      String node = itemForm[1];
      String l = itemForm[6];
      boolean adjoinable = tag.isAdjoinable(auxTreeName, treeName, node);
      if (adjoinable && itemForm[2].equals("la") && itemForm[7].equals("0")) {
        ChartItemInterface consequence =
          new DeductionChartItem(auxTreeName, "", "la", l, "-", "-", l, "0");
        List<Tree> derivedTrees = new ArrayList<>();
        derivedTrees.add(tag.getAuxiliaryTree(auxTreeName));
        consequence.setTrees(derivedTrees);
        logItemGeneration(consequence);
        consequences.add(consequence);
      }
    }
    return consequences;
  }

  @Override public String toString() {
    return "[ɣ,p,la,i,j,k,l,0]" + "\n______ " + auxTreeName + " ∈ f_SA(ɣ,p)\n"
      + "[" + auxTreeName + ",ε,la,l,-,-,l,0]";
  }

}

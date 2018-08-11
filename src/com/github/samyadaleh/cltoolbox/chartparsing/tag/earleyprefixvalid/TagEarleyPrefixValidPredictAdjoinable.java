package com.github.samyadaleh.cltoolbox.chartparsing.tag.earleyprefixvalid;

import java.util.ArrayList;
import java.util.List;

import com.github.samyadaleh.cltoolbox.chartparsing.AbstractDynamicDeductionRule;
import com.github.samyadaleh.cltoolbox.chartparsing.DeductionChartItem;
import com.github.samyadaleh.cltoolbox.chartparsing.ChartItemInterface;
import com.github.samyadaleh.cltoolbox.common.tag.Tag;
import com.github.samyadaleh.cltoolbox.common.tag.Tree;

public class TagEarleyPrefixValidPredictAdjoinable
  extends AbstractDynamicDeductionRule {

  private final Tag tag;
  private final String auxTreeName;

  public TagEarleyPrefixValidPredictAdjoinable(String auxTreeName, Tag tag) {
    this.tag = tag;
    this.name = "predict adjoinable";
    this.auxTreeName = auxTreeName;
    this.antNeeded = 1;
  }

  @Override public List<ChartItemInterface> getConsequences() {
    if (antecedences.size() == antNeeded) {
      String[] itemForm = antecedences.get(0).getItemform();
      String treeName = itemForm[0];
      String node = itemForm[1];
      String pos = itemForm[2];
      String iGamma = itemForm[3];
      String i = itemForm[4];
      String j = itemForm[5];
      String k = itemForm[6];
      String l = itemForm[7];
      String adj = itemForm[8];
      if (pos.equals("la") && adj.equals("0") && iGamma.equals("~")
        && i.equals("~") && j.equals("~") && k.equals("~") && !l.equals("~")
        && tag.isAdjoinable(auxTreeName, treeName, node)) {
        ChartItemInterface consequence =
          new DeductionChartItem(auxTreeName, "", "la", l, l, "-", "-", l, "0");
        List<Tree> derivedTrees = new ArrayList<Tree>();
        derivedTrees.add(tag.getAuxiliaryTree(auxTreeName));
        consequence.setTrees(derivedTrees);
        consequences.add(consequence);
      }
    }
    return consequences;
  }

  @Override public String toString() {
    return "[ɣ,p,la,~,~,~,~,l,0]" + "\n______ " + auxTreeName + " ∈ f_SA(ɣ,p)\n"
      + "[" + auxTreeName + ",ε,la,l,l,-,-,l,0]";
  }

}

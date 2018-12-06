package com.github.samyadaleh.cltoolbox.chartparsing.tag.earleyprefixvalid;

import com.github.samyadaleh.cltoolbox.chartparsing.dynamicdeductionrule.AbstractDynamicDeductionRule;
import com.github.samyadaleh.cltoolbox.chartparsing.item.ChartItemInterface;
import com.github.samyadaleh.cltoolbox.chartparsing.item.DeductionChartItem;
import com.github.samyadaleh.cltoolbox.common.tag.Tag;
import com.github.samyadaleh.cltoolbox.common.tag.Tree;

import java.util.ArrayList;
import java.util.List;

import static com.github.samyadaleh.cltoolbox.common.Constants.DEDUCTION_RULE_TAG_EARLEY_PREFIXVALID_PREDICTADJOINABLE;

public class TagEarleyPrefixValidPredictAdjoinable
    extends AbstractDynamicDeductionRule {

  private final Tag tag;
  private final String auxTreeName;

  public TagEarleyPrefixValidPredictAdjoinable(String auxTreeName, Tag tag) {
    this.tag = tag;
    this.name = DEDUCTION_RULE_TAG_EARLEY_PREFIXVALID_PREDICTADJOINABLE;
    this.auxTreeName = auxTreeName;
    this.antNeeded = 1;
  }

  @Override public List<ChartItemInterface> getConsequences() {
    if (antecedences.size() == antNeeded) {
      String[] itemForm = antecedences.get(0).getItemForm();
      String treeName = itemForm[0];
      String node = itemForm[1];
      String iGamma = itemForm[3];
      String l = itemForm[7];
      if (itemForm[2].equals("la") && itemForm[8].equals("0") && iGamma
          .equals("~") && itemForm[4].equals("~") && itemForm[5].equals("~")
          && itemForm[6].equals("~") && !l.equals("~") && tag
          .isAdjoinable(auxTreeName, treeName, node)) {
        ChartItemInterface consequence =
            new DeductionChartItem(auxTreeName, "", "la", l, l, "-", "-", l,
                "0");
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
    return "[ɣ,p,la,~,~,~,~,l,0]" + "\n______ " + auxTreeName + " ∈ f_SA(ɣ,p)\n"
        + "[" + auxTreeName + ",ε,la,l,l,-,-,l,0]";
  }

}

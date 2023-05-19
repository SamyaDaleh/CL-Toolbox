package com.github.samyadaleh.cltoolbox.chartparsing.tag.earleyprefixvalid;

import java.util.ArrayList;
import java.util.List;

import com.github.samyadaleh.cltoolbox.chartparsing.dynamicdeductionrule.AbstractDynamicDeductionRule;
import com.github.samyadaleh.cltoolbox.chartparsing.item.DeductionChartItem;
import com.github.samyadaleh.cltoolbox.chartparsing.item.ChartItemInterface;
import com.github.samyadaleh.cltoolbox.common.tag.Tag;
import com.github.samyadaleh.cltoolbox.common.tag.Tree;
import com.github.samyadaleh.cltoolbox.common.tag.Vertex;

import static com.github.samyadaleh.cltoolbox.common.Constants.DEDUCTION_RULE_TAG_EARLEY_PREFIXVALID_PREDICTSUBSTITUTED;

public class TagEarleyPrefixValidPredictSubst
    extends AbstractDynamicDeductionRule {

  private final Tag tag;
  private final String iniTreeName;

  public TagEarleyPrefixValidPredictSubst(String iniTreeName, Tag tag) {
    this.tag = tag;
    this.name = DEDUCTION_RULE_TAG_EARLEY_PREFIXVALID_PREDICTSUBSTITUTED;
    this.iniTreeName = iniTreeName;
    this.antNeeded = 1;
  }

  @Override public List<ChartItemInterface> getConsequences() {
    if (antecedences.size() == antNeeded) {
      String[] itemForm = antecedences.get(0).getItemForm();
      String treeName = itemForm[0];
      String l = itemForm[7];
      Vertex p = tag.getTree(treeName).getNodeByGornAddress(itemForm[1]);
      if (itemForm[2].equals("la") && itemForm[8].equals("0") && itemForm[3]
          .equals("~") && itemForm[4].equals("~") && itemForm[5].equals("~")
          && itemForm[6].equals("~") && !l.equals("~") && tag
          .isSubstitutionNode(p, treeName) && p.getLabel()
          .equals(tag.getTree(iniTreeName).getRoot().getLabel())) {
        ChartItemInterface consequence =
            new DeductionChartItem(iniTreeName, "", "la", l, l, "-", "-", l,
                "0");
        List<Tree> derivedTrees = new ArrayList<>();
        derivedTrees.add(tag.getInitialTree(iniTreeName));
        consequence.setTrees(derivedTrees);
        logItemGeneration(consequence);
        consequences.add(consequence);
      }
    }
    return consequences;
  }

  @Override public String toString() {
    return "[ɣ,p,la,~,~,~,~,l,0]" + "\n______ " + iniTreeName
        + " ∈ I, ɣ(p) substitution node, l(ɣ,p) = l(\" + iniTreeName + \",ε)\n"
        + "[" + iniTreeName + ",ε,la,l,l,-,-,l,0]";
  }

}

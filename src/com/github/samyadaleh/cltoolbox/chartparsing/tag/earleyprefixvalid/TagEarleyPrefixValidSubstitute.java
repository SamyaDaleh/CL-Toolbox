package com.github.samyadaleh.cltoolbox.chartparsing.tag.earleyprefixvalid;

import com.github.samyadaleh.cltoolbox.chartparsing.ChartParsingUtils;
import com.github.samyadaleh.cltoolbox.chartparsing.dynamicdeductionrule.AbstractDynamicDecutionRuleTwoAntecedences;
import com.github.samyadaleh.cltoolbox.chartparsing.item.ChartItemInterface;
import com.github.samyadaleh.cltoolbox.chartparsing.item.DeductionChartItem;
import com.github.samyadaleh.cltoolbox.common.ArrayUtils;
import com.github.samyadaleh.cltoolbox.common.tag.Tag;
import com.github.samyadaleh.cltoolbox.common.tag.Tree;
import com.github.samyadaleh.cltoolbox.common.tag.Vertex;

import java.util.List;

import static com.github.samyadaleh.cltoolbox.common.Constants.DEDUCTION_RULE_TAG_EARLEY_PREFIXVALID_SUBSTITUTE;

public class TagEarleyPrefixValidSubstitute
    extends AbstractDynamicDecutionRuleTwoAntecedences {

  private final Tag tag;

  public TagEarleyPrefixValidSubstitute(Tag tag) {
    this.tag = tag;
    this.name = DEDUCTION_RULE_TAG_EARLEY_PREFIXVALID_SUBSTITUTE;
    this.antNeeded = 2;
  }

  protected void calculateConsequences(String[] itemForm1, String[] itemForm2) {
    String treeName1 = itemForm1[0];
    String node1 = itemForm1[1];
    String l1 = itemForm1[7];
    String treeName2 = itemForm2[0];
    String[] itemForm2Goal =
        new String[] {"?", "", "ra", itemForm1[7], itemForm1[7], "-", "-", "?",
            "0"};
    Vertex p = tag.getTree(treeName1).getNodeByGornAdress(node1);
    if (itemForm1[2].equals("la") && itemForm1[3].equals("~") && itemForm1[4]
        .equals("~") && itemForm1[5].equals("~") && itemForm1[6].equals("~")
        && !l1.equals("~") && itemForm1[8].equals("0")
        && tag.getInitialTree(treeName2) != null && tag
        .isSubstitutionNode(p, treeName1) && p.getLabel()
        .equals(tag.getInitialTree(treeName2).getRoot().getLabel())
        && ArrayUtils.match(itemForm2, itemForm2Goal)) {
      ChartItemInterface consequence =
          new DeductionChartItem(treeName1, node1, "rb", "~", l1, "-", "-",
              itemForm2[7], "0");
      List<Tree> derivedTrees =
          ChartParsingUtils.generateDerivatedTrees(antecedences, itemForm1);
      consequence.setTrees(derivedTrees);
      this.name =
          DEDUCTION_RULE_TAG_EARLEY_PREFIXVALID_SUBSTITUTE + " " + treeName1
              + "[" + node1 + "," + treeName2 + "]";
      logItemGeneration(consequence);
      consequences.add(consequence);
    }
  }

  @Override public String toString() {
    return "[ɣ,p,la,~,~,~,~,i,0], [α,ε,ra,i,i,-,-,j,0]"
        + "\n______ α ∈ I, (ɣ,p) a substitution node, l(ɣ,p) = l(α,ε)\n"
        + "[ɣ,p,rb,~,i,-,-,j,0]";
  }

}

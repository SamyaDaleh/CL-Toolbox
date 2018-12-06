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

import static com.github.samyadaleh.cltoolbox.common.Constants.DEDUCTION_RULE_TAG_EARLEY_COMPLETEFOOT;

public class TagEarleyPrefixValidCompleteFoot
    extends AbstractDynamicDeductionRuleThreeAntecedences {

  private final Tag tag;

  public TagEarleyPrefixValidCompleteFoot(Tag tag) {
    this.tag = tag;
    this.name = DEDUCTION_RULE_TAG_EARLEY_COMPLETEFOOT;
    this.antNeeded = 3;
  }

  protected void calculateConsequences(String[] itemForm1, String[] itemForm2,
      String[] itemForm3) {
    String i1 = itemForm1[4];
    String l1 = itemForm1[7];
    String treeName2 = itemForm2[0];
    String node2 = itemForm2[1];
    String[] itemForm2Goal =
        new String[] {"?", "?", "la", "?", "?", "-", "-", itemForm1[4], "0"};
    String[] itemForm3Goal =
        new String[] {itemForm1[0], itemForm1[1], "la", "~", "~", "~", "~",
            itemForm2[3], "0"};
    if (tag.getAuxiliaryTree(treeName2) != null) {
      Vertex pf = tag.getAuxiliaryTree(treeName2).getNodeByGornAdress(node2);
      if (itemForm1[2].equals("rb") && itemForm1[3].equals("~") && itemForm1[5]
          .equals("~") && itemForm1[6].equals("~") && itemForm1[8].equals("0")
          && pf == tag.getAuxiliaryTree(treeName2).getFoot() && !itemForm2[4]
          .equals("~") && ArrayUtils.match(itemForm2, itemForm2Goal)
          && ArrayUtils.match(itemForm3, itemForm3Goal) && tag
          .isAdjoinable(treeName2, itemForm1[0], itemForm1[1])) {
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

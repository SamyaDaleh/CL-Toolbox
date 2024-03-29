package com.github.samyadaleh.cltoolbox.chartparsing.tag.earleyprefixvalid;

import com.github.samyadaleh.cltoolbox.chartparsing.dynamicdeductionrule.AbstractDynamicDecutionRuleTwoAntecedences;
import com.github.samyadaleh.cltoolbox.chartparsing.item.ChartItemInterface;
import com.github.samyadaleh.cltoolbox.chartparsing.item.DeductionChartItem;
import com.github.samyadaleh.cltoolbox.common.tag.Tag;
import com.github.samyadaleh.cltoolbox.common.tag.Tree;

import java.util.Arrays;
import java.util.List;

import static com.github.samyadaleh.cltoolbox.common.Constants.DEDUCTION_RULE_TAG_EARLEY_PREFIXVALID_PREDICTADJOINED;
import static com.github.samyadaleh.cltoolbox.common.Constants.EPSILON;

public class TagEarleyPrefixValidPredictAdjoined
    extends AbstractDynamicDecutionRuleTwoAntecedences {

  private final Tag tag;

  public TagEarleyPrefixValidPredictAdjoined(Tag tag) {
    this.tag = tag;
    this.name = DEDUCTION_RULE_TAG_EARLEY_PREFIXVALID_PREDICTADJOINED;
    this.antNeeded = 2;
  }

  protected void calculateConsequences(String[] itemForm1, String[] itemForm2) {
    String treeName1 = itemForm1[0];
    String m = itemForm1[7];
    String treeName2 = itemForm2[0];
    String node2 = itemForm2[1];
    String iGamma2 = itemForm2[3];
    boolean adjoinable1 = tag.isAdjoinable(treeName1, treeName2, node2);
    if (itemForm1[8].equals("0") && itemForm2[8].equals("0") && adjoinable1) {
      boolean isFootNode =
          tag.getAuxiliaryTree(treeName1).getFoot().getGornAddress()
              .equals(itemForm1[1]);
      if (isFootNode && itemForm1[2].equals("la") && itemForm2[2].equals("la")
          && itemForm1[5].equals("-") && itemForm1[6].equals("-")
          && itemForm1[3].equals(itemForm2[7]) && itemForm2[4].equals("~")
          && itemForm2[5].equals("~") && itemForm2[6].equals("~") && !iGamma2
          .equals("~")) {
        ChartItemInterface consequence =
            new DeductionChartItem(treeName2, node2, "lb", iGamma2, m, "-", "-",
                m, "0");
        List<Tree> derivedTrees = generateDerivatedTrees(itemForm1);
        String node2name = node2.length() == 0 ? EPSILON : node2;
        this.name =
            DEDUCTION_RULE_TAG_EARLEY_PREFIXVALID_PREDICTADJOINED + " " + treeName2 + "["
                + node2name + "," + treeName1 + "]";
        consequence.setTrees(derivedTrees);
        logItemGeneration(consequence);
        consequences.add(consequence);
      }
    }
  }

  private List<Tree> generateDerivatedTrees(String[] itemForm1) {
    List<Tree> derivedTrees;
    if (Arrays.equals(antecedences.get(0).getItemForm(), itemForm1)) {
      derivedTrees = antecedences.get(1).getTrees();
    } else {
      derivedTrees = antecedences.get(0).getTrees();
    }
    return derivedTrees;
  }

  @Override public String toString() {
    return "[β,p_f,ra,i,j,k,l,0], [ɣ,p,rb,j,g,h,k,0]"
        + "\n______ β(p_f) foot node, β ∈ f_SA(ɣ,p)\n" + "[ɣ,p,rb,i,g,h,l,1]";
  }

}

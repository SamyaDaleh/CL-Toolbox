package com.github.samyadaleh.cltoolbox.chartparsing.tag.earley;

import java.util.List;

import com.github.samyadaleh.cltoolbox.chartparsing.dynamicdeductionrule.AbstractDynamicDeductionRule;
import com.github.samyadaleh.cltoolbox.chartparsing.item.DeductionChartItem;
import com.github.samyadaleh.cltoolbox.chartparsing.item.ChartItemInterface;
import com.github.samyadaleh.cltoolbox.common.tag.Tag;

import static com.github.samyadaleh.cltoolbox.common.Constants.DEDUCTION_RULE_TAG_EARLEY_PREDICTNOADJOIN;

/**
 * If the dot is at a node where adjunction is not obligatory, just skip it.
 */
public class TagEarleyPredictNoAdj extends AbstractDynamicDeductionRule {

  private final Tag tag;

  /**
   * Constructor needs the grammar to retrieve information about the
   * antecedence.
   */
  public TagEarleyPredictNoAdj(Tag tag) {
    this.tag = tag;
    this.name = DEDUCTION_RULE_TAG_EARLEY_PREDICTNOADJOIN;
    this.antNeeded = 1;
  }

  @Override public List<ChartItemInterface> getConsequences() {
    if (antecedences.size() == antNeeded) {
      String[] itemForm = antecedences.get(0).getItemForm();
      String treeName = itemForm[0];
      String node = itemForm[1];
      String l = itemForm[6];
      boolean obligatoryAdjoin = tag.getTree(treeName).isInOA(node);
      if (!obligatoryAdjoin && itemForm[2].equals("la") && itemForm[7]
          .equals("0")) {
        ChartItemInterface consequence =
            new DeductionChartItem(treeName, node, "lb", l, "-", "-", l, "0");
        consequence.setTrees(antecedences.get(0).getTrees());
        logItemGeneration(consequence);
        consequences.add(consequence);
      }
    }
    return consequences;
  }

  @Override public String toString() {
    return "[ɣ,p,la,i,j,k,l,0]" + "\n______ f_OA(ɣ,p) = 0\n"
        + "[ɣ,p,lb,l,-,-,l,0]";
  }

}

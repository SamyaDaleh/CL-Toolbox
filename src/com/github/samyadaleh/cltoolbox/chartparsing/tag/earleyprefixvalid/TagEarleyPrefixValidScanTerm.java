package com.github.samyadaleh.cltoolbox.chartparsing.tag.earleyprefixvalid;

import java.util.List;

import com.github.samyadaleh.cltoolbox.chartparsing.dynamicdeductionrule.AbstractDynamicDeductionRule;
import com.github.samyadaleh.cltoolbox.chartparsing.item.DeductionChartItem;
import com.github.samyadaleh.cltoolbox.chartparsing.item.ChartItemInterface;
import com.github.samyadaleh.cltoolbox.common.tag.Tag;

import static com.github.samyadaleh.cltoolbox.common.Constants.DEDUCTION_RULE_TAG_EARLEY_SCANTERM;

public class TagEarleyPrefixValidScanTerm extends AbstractDynamicDeductionRule {

  private final String[] wSplit;
  private final Tag tag;

  public TagEarleyPrefixValidScanTerm(String[] wSplit, Tag tag) {
    this.wSplit = wSplit;
    this.tag = tag;
    this.name = DEDUCTION_RULE_TAG_EARLEY_SCANTERM;
    this.antNeeded = 1;
  }

  @Override public List<ChartItemInterface> getConsequences() {
    if (antecedences.size() == antNeeded && !"".equals(wSplit[0])) {
      String[] itemForm = antecedences.get(0).getItemForm();
      String treeName = itemForm[0];
      String node = itemForm[1];
      int lInt = Integer.parseInt(itemForm[7]);
      if (lInt < wSplit.length && itemForm[2].equals("la") && itemForm[8]
          .equals("0") && tag.getTree(treeName).getNodeByGornAdress(node)
          .getLabel().equals(wSplit[lInt])) {
        this.name = DEDUCTION_RULE_TAG_EARLEY_SCANTERM + " " + wSplit[lInt];
        ChartItemInterface consequence =
            new DeductionChartItem(treeName, node, "ra", itemForm[3],
                itemForm[4], itemForm[5], itemForm[6], String.valueOf(lInt + 1),
                "0");
        consequence.setTrees(antecedences.get(0).getTrees());
        logItemGeneration(consequence);
        consequences.add(consequence);
      }
    }
    return consequences;
  }

  @Override public String toString() {
    return "[ɣ,p,la,i_ɣ,i,j,k,l,0]" + "\n______ l(ɣ,p_ɣ . p) = w_l\n"
        + "[ɣ,p,ra,i_ɣ,i,j,k,l+1,0]";
  }

}

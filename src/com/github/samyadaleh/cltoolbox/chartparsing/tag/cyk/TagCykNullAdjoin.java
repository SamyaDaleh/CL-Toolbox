package com.github.samyadaleh.cltoolbox.chartparsing.tag.cyk;

import java.util.List;

import com.github.samyadaleh.cltoolbox.chartparsing.dynamicdeductionrule.AbstractDynamicDeductionRule;
import com.github.samyadaleh.cltoolbox.chartparsing.item.DeductionChartItem;
import com.github.samyadaleh.cltoolbox.chartparsing.item.ChartItemInterface;
import com.github.samyadaleh.cltoolbox.common.tag.Tag;

import static com.github.samyadaleh.cltoolbox.common.Constants.DEDUCTION_RULE_TAG_NULLADJOIN;

/** Goes from bottom into top position without adjoining. */
public class TagCykNullAdjoin extends AbstractDynamicDeductionRule {

  private final Tag tag;

  /** Constructor needs the grammar to check if adjoins is obligatory. */
  public TagCykNullAdjoin(Tag tag) {
    this.tag = tag;
    this.name = DEDUCTION_RULE_TAG_NULLADJOIN;
    this.antNeeded = 1;
  }

  @Override public List<ChartItemInterface> getConsequences() {
    if (antecedences.size() == antNeeded) {
      String[] itemForm = antecedences.get(0).getItemForm();
      String treeName = itemForm[0];
      String node = itemForm[1];
      String nodeGorn;
      if (node.length() == 1) {
        nodeGorn = "";
      } else {
        nodeGorn = node.substring(0, node.length() - 1);
      }
      String i = itemForm[2];
      String f1 = itemForm[3];
      String f2 = itemForm[4];
      boolean obligatoryAdjoin = tag.getTree(treeName).isInOA(nodeGorn);
      String j = itemForm[5];
      if (node.endsWith("⊥") && !obligatoryAdjoin) {
        String newNode = node.substring(0, node.length() - 1) + "⊤";
        ChartItemInterface consequence = new DeductionChartItem(treeName, newNode, i, f1, f2, j);
        consequence.setTrees(antecedences.get(0).getTrees());
        logItemGeneration(consequence);
        consequences.add(consequence);
      }
    }
    return consequences;
  }

  @Override public String toString() {
    return "[ɣ,p⊥,i,f1,f2,j]" + "\n______ f_OA(ɣ,p) = 0\n" + "[ɣ,p⊤,i,f1,f2,j]";
  }

}

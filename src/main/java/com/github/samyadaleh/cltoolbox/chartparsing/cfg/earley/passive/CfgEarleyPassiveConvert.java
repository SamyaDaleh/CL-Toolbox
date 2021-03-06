package com.github.samyadaleh.cltoolbox.chartparsing.cfg.earley.passive;

import java.util.List;

import com.github.samyadaleh.cltoolbox.chartparsing.dynamicdeductionrule.AbstractDynamicDeductionRule;
import com.github.samyadaleh.cltoolbox.chartparsing.item.DeductionChartItem;
import com.github.samyadaleh.cltoolbox.chartparsing.item.ChartItemInterface;

import static com.github.samyadaleh.cltoolbox.common.Constants.DEDUCTION_RULE_CFG_EARLEY_CONVERT;

/**
 * Converts an active item (with a dot) into a passive item that does not care
 * which rule led to its creation.
 *
 */
public class CfgEarleyPassiveConvert extends AbstractDynamicDeductionRule {

  public CfgEarleyPassiveConvert() {
    this.name = DEDUCTION_RULE_CFG_EARLEY_CONVERT;
    this.antNeeded = 1;
  }

  @Override public List<ChartItemInterface> getConsequences() {
    if (antecedences.size() == antNeeded) {
      String[] itemForm = antecedences.get(0).getItemForm();
      if (itemForm[0].endsWith("•")) {
        String lhsSym = itemForm[0].split(" ")[0];
        ChartItemInterface consequence = new DeductionChartItem(lhsSym, itemForm[1], itemForm[2]);
        consequence.setTrees(antecedences.get(0).getTrees());
        logItemGeneration(consequence);
        consequences.add(consequence);
      }
    }
    return consequences;
  }

  @Override public String toString() {
    return "[B → γ•, j, k]\n" + "_________\n" + "[B, j, k]";
  }

}

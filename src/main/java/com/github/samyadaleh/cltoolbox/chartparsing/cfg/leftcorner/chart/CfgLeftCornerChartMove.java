package com.github.samyadaleh.cltoolbox.chartparsing.cfg.leftcorner.chart;

import java.util.List;

import com.github.samyadaleh.cltoolbox.chartparsing.dynamicdeductionrule.AbstractDynamicDeductionRule;
import com.github.samyadaleh.cltoolbox.chartparsing.item.DeductionChartItem;
import com.github.samyadaleh.cltoolbox.chartparsing.item.ChartItemInterface;

import static com.github.samyadaleh.cltoolbox.common.Constants.DEDUCTION_RULE_CFG_LEFTCORNER_MOVE;

/**
 * If the end of a rhs is encountered, move the topmost nonterminal from the
 * stack of lhs to the stack of completed items.
 */
public class CfgLeftCornerChartMove extends AbstractDynamicDeductionRule {

  public CfgLeftCornerChartMove() {
    this.name = DEDUCTION_RULE_CFG_LEFTCORNER_MOVE;
    this.antNeeded = 1;
  }

  @Override public List<ChartItemInterface> getConsequences() {
    if (antecedences.size() == antNeeded) {
      String[] itemForm = antecedences.get(0).getItemForm();
      if (itemForm[0].length() > 0
        && itemForm[0].charAt(itemForm[0].length() - 1) == '•') {
        int posSpace = itemForm[0].indexOf(" ");
        ChartItemInterface consequence = new DeductionChartItem(
          itemForm[0].substring(0, posSpace), itemForm[1], itemForm[2]);
        consequence.setTrees(antecedences.get(0).getTrees());
        logItemGeneration(consequence);
        consequences.add(consequence);
      }
    }
    return consequences;
  }

  @Override public String toString() {
    return "[A -> α X •,i,l]" + "\n______ \n" + "[A,i,l]";
  }

}

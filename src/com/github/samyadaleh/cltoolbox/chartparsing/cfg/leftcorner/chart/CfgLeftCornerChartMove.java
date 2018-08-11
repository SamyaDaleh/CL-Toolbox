package com.github.samyadaleh.cltoolbox.chartparsing.cfg.leftcorner.chart;

import java.util.List;

import com.github.samyadaleh.cltoolbox.chartparsing.AbstractDynamicDeductionRule;
import com.github.samyadaleh.cltoolbox.chartparsing.DeductionChartItem;
import com.github.samyadaleh.cltoolbox.chartparsing.ChartItemInterface;

/**
 * If the end of a rhs is encountered, move the topmost nonterminal from the
 * stack of lhs to the stack of completed items.
 */
public class CfgLeftCornerChartMove extends AbstractDynamicDeductionRule {

  public CfgLeftCornerChartMove() {
    this.name = "move";
    this.antNeeded = 1;
  }

  @Override public List<ChartItemInterface> getConsequences() {
    if (antecedences.size() == antNeeded) {
      String[] itemForm = antecedences.get(0).getItemform();
      if (itemForm[0].length() > 0
        && itemForm[0].charAt(itemForm[0].length() - 1) == '•') {
        ChartItemInterface consequence = new DeductionChartItem(
          itemForm[0].substring(0, 1), itemForm[1], itemForm[2]);
        consequence.setTrees(antecedences.get(0).getTrees());
        consequences.add(consequence);
      }
    }
    return consequences;
  }

  @Override public String toString() {
    return "[A -> α X •,i,l]" + "\n______ \n" + "[A,i,l]";
  }

}

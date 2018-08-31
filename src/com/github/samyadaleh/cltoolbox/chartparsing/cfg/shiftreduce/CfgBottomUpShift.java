package com.github.samyadaleh.cltoolbox.chartparsing.cfg.shiftreduce;

import java.util.List;

import com.github.samyadaleh.cltoolbox.chartparsing.dynamicdeductionrule.AbstractDynamicDeductionRule;
import com.github.samyadaleh.cltoolbox.chartparsing.item.DeductionChartItem;
import com.github.samyadaleh.cltoolbox.chartparsing.item.ChartItemInterface;

/** Moves the next input symbol onto the stack */
public class CfgBottomUpShift extends AbstractDynamicDeductionRule {

  private final String[] wSplit;

  public CfgBottomUpShift(String[] wSplit) {
    this.wSplit = wSplit;
    this.name = "shift";
    this.antNeeded = 1;
  }

  @Override public List<ChartItemInterface> getConsequences() {
    if (antecedences.size() == antNeeded) {
      String[] itemForm = antecedences.get(0).getItemForm();
      String stack = itemForm[0];
      int i = Integer.parseInt(itemForm[1]);
      if (i < wSplit.length) {
        this.name = "shift " + wSplit[i];
        ChartItemInterface consequence;
        if (stack.length() == 0) {
          consequence = new DeductionChartItem(wSplit[i], String.valueOf(i + 1));
        } else {
          consequence =
            new DeductionChartItem(stack + " " + wSplit[i], String.valueOf(i + 1));
        }
        consequence.setTrees(antecedences.get(0).getTrees());
        logItemGeneration(consequence);
        consequences.add(consequence);
      }
    }
    return consequences;
  }

  @Override public String toString() {
    return "[Γ,i]" + "\n______ w_i = a\n" + "[Γa,i+1]";
  }

}

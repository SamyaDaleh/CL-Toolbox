package com.github.samyadaleh.cltoolbox.chartparsing.cfg.topdown;

import java.util.List;

import com.github.samyadaleh.cltoolbox.chartparsing.AbstractDynamicDeductionRule;
import com.github.samyadaleh.cltoolbox.chartparsing.DeductionChartItem;
import com.github.samyadaleh.cltoolbox.chartparsing.ChartItemInterface;
import com.github.samyadaleh.cltoolbox.common.ArrayUtils;

/** The scan rule for topdown removes a terminal if it is the next input
 * symbol. */
public class CfgTopDownScan extends AbstractDynamicDeductionRule {

  private final String[] wsplit;

  public CfgTopDownScan(String[] wsplit) {
    this.wsplit = wsplit;
    this.name = "scan";
    this.antNeeded = 1;
  }

  @Override public List<ChartItemInterface> getConsequences() {
    if (antecedences.size() == antNeeded) {
      String[] itemForm = antecedences.get(0).getItemform();
      String stack = itemForm[0];
      String[] stackSplit = stack.split(" ");
      int i = Integer.parseInt(itemForm[1]);
      if (i < wsplit.length && stackSplit[0].equals(wsplit[i])) {
        this.name = "scan " + wsplit[i];
        ChartItemInterface consequence = new DeductionChartItem(
          ArrayUtils.getSubSequenceAsString(stackSplit, 1, stackSplit.length),
          String.valueOf(i + 1));
        consequence.setTrees(antecedences.get(0).getTrees());
        consequences.add(consequence);
      }
    }
    return consequences;
  }

  @Override public String toString() {
    return "[aα,i]" + "\n______ w_i = a\n" + "[α,i+1]";
  }

}

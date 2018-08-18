package com.github.samyadaleh.cltoolbox.chartparsing.cfg.earley;

import java.util.List;

import com.github.samyadaleh.cltoolbox.chartparsing.AbstractDynamicDeductionRule;
import com.github.samyadaleh.cltoolbox.chartparsing.DeductionChartItem;
import com.github.samyadaleh.cltoolbox.chartparsing.ChartItemInterface;
import com.github.samyadaleh.cltoolbox.common.ArrayUtils;

/** The scan rule for topdown removes a terminal if it is the next input
 * symbol. */
public class CfgEarleyScan extends AbstractDynamicDeductionRule {

  private final String[] wsplit;

  public CfgEarleyScan(String[] wsplit) {
    this.wsplit = wsplit;
    this.name = "scan";
    this.antNeeded = 1;
  }

  @Override public List<ChartItemInterface> getConsequences() {
    if (antecedences.size() == antNeeded) {
      String[] itemForm = antecedences.get(0).getItemForm();
      String stack = itemForm[0];
      String[] stackSplit = stack.split(" ");
      int i = Integer.parseInt(itemForm[1]);
      int j = Integer.parseInt(itemForm[2]);
      for (int k = 0; k < stackSplit.length; k++) {
        if (stackSplit[k].startsWith("•") && j < wsplit.length && wsplit[j]
          .equals(stackSplit[k].substring(1, stackSplit[k].length()))) {
          this.name = "scan " + wsplit[j];
          StringBuilder newStack = new StringBuilder();
          newStack.append(ArrayUtils.getSubSequenceAsString(stackSplit, 0, k));
          if (k == stackSplit.length - 1) {
            newStack.append(" ").append(wsplit[j]).append(" •");
          } else {
            newStack.append(" ").append(wsplit[j]).append(" •")
              .append(ArrayUtils.getSubSequenceAsString(stackSplit, k + 1,
                stackSplit.length));
          }
          ChartItemInterface consequence = new DeductionChartItem(newStack.toString(),
            String.valueOf(i), String.valueOf(j + 1));
          consequence.setTrees(antecedences.get(0).getTrees());
          logItemGeneration(consequence);
          consequences.add(consequence);
        }
      }
    }
    return consequences;
  }

  @Override public String toString() {
    return "[A -> α •a β,i,j]" + "\n______ w_j = a\n" + "[A -> α a • β,i,j+1]";
  }

}

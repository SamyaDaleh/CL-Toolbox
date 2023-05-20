package com.github.samyadaleh.cltoolbox.chartparsing.cfg.shiftreduce;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.github.samyadaleh.cltoolbox.chartparsing.dynamicdeductionrule.AbstractDynamicDeductionRule;
import com.github.samyadaleh.cltoolbox.chartparsing.item.BottomUpChartItem;
import com.github.samyadaleh.cltoolbox.chartparsing.item.ChartItemInterface;
import com.github.samyadaleh.cltoolbox.chartparsing.item.Pair;
import com.github.samyadaleh.cltoolbox.common.tag.Tree;

import static com.github.samyadaleh.cltoolbox.common.Constants.DEDUCTION_RULE_CFG_SHIFTREDUCE_SHIFT;

/**
 * Moves the next input symbol onto the stack
 */
public class CfgBottomUpShift extends AbstractDynamicDeductionRule {

  private final String[] wSplit;

  public CfgBottomUpShift(String[] wSplit) {
    this.wSplit = wSplit;
    this.name = DEDUCTION_RULE_CFG_SHIFTREDUCE_SHIFT;
    this.antNeeded = 1;
  }

  @Override public List<ChartItemInterface> getConsequences() {
    if (antecedences.size() == antNeeded) {
      String[] itemForm = antecedences.get(0).getItemForm();
      String stack = itemForm[0];
      int i = Integer.parseInt(itemForm[1]);
      if (!"".equals(wSplit[0]) && i < wSplit.length) {
        this.name = DEDUCTION_RULE_CFG_SHIFTREDUCE_SHIFT + " " + wSplit[i];
        BottomUpChartItem consequence;
        if (stack.length() == 0) {
          consequence =
              new BottomUpChartItem(wSplit[i], String.valueOf(i + 1));
        } else {
          consequence = new BottomUpChartItem(stack + " " + wSplit[i],
              String.valueOf(i + 1));
        }
        List<Pair<String, Map<Integer, List<Tree>>>> trees =
            new ArrayList<>(
                ((BottomUpChartItem) antecedences.get(0)).getStackState());
        consequence.setStackState(trees);
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

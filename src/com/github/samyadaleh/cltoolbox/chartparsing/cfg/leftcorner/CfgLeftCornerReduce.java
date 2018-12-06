package com.github.samyadaleh.cltoolbox.chartparsing.cfg.leftcorner;

import com.github.samyadaleh.cltoolbox.chartparsing.dynamicdeductionrule.AbstractDynamicDeductionRule;
import com.github.samyadaleh.cltoolbox.chartparsing.item.ChartItemInterface;
import com.github.samyadaleh.cltoolbox.chartparsing.item.DeductionChartItem;
import com.github.samyadaleh.cltoolbox.common.ArrayUtils;
import com.github.samyadaleh.cltoolbox.common.cfg.CfgProductionRule;

import java.util.List;

import static com.github.samyadaleh.cltoolbox.common.Constants.DEDUCTION_RULE_CFG_LEFTCORNER_REDUCE;

/**
 * If the top of the completed stack is the left corner of a production rule,
 * pop that symbol, push the rest of the rhs to the stack to be predicted and
 * add the lhs to the stack of lhs
 */
public class CfgLeftCornerReduce extends AbstractDynamicDeductionRule {

  private final CfgProductionRule rule;

  public CfgLeftCornerReduce(CfgProductionRule rule) {
    this.name = DEDUCTION_RULE_CFG_LEFTCORNER_REDUCE + " " + rule.toString();
    this.rule = rule;
    this.antNeeded = 1;
  }

  @Override public List<ChartItemInterface> getConsequences() {
    if (antecedences.size() == antNeeded) {
      String[] itemForm = antecedences.get(0).getItemForm();
      String[] stackComplSplit = itemForm[0].split(" ");
      String[] stackPredSplit = itemForm[1].split(" ");
      if (!stackPredSplit[0].equals("$") && stackComplSplit[0]
          .equals(rule.getRhs()[0])) {
        String newCompl = ArrayUtils
            .getSubSequenceAsString(stackComplSplit, 1, itemForm[0].length());
        String newPred;
        if (rule.getRhs().length == 1) {
          newPred = "$ " + itemForm[1];
        } else {
          newPred = ArrayUtils
              .getSubSequenceAsString(rule.getRhs(), 1, rule.getRhs().length)
              + " $ " + itemForm[1];
        }
        String newLhs;
        if (itemForm[2].length() == 0) {
          newLhs = rule.getLhs();
        } else {
          newLhs = rule.getLhs() + " " + itemForm[2];
        }
        ChartItemInterface consequence =
            new DeductionChartItem(newCompl, newPred, newLhs);
        CfgLeftCornerUtils
            .generateDerivedTrees(rule, antecedences, consequence);
        logItemGeneration(consequence);
        consequences.add(consequence);
      }
    }
    return consequences;
  }

  @Override public String toString() {
    return "[" + rule.getRhs()[0] + "α,Bβ,ɣ]" + "\n______ " + ArrayUtils
        .toString(rule.getRhs()) + ", B ≠ $\n" + "[α," + ArrayUtils
        .getSubSequenceAsString(rule.getRhs(), 1, rule.getRhs().length) + "$Bβ,"
        + rule.getLhs() + "ɣ]";
  }

}

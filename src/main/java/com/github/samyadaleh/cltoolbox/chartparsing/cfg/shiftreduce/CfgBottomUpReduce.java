package com.github.samyadaleh.cltoolbox.chartparsing.cfg.shiftreduce;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import com.github.samyadaleh.cltoolbox.chartparsing.dynamicdeductionrule.AbstractDynamicDeductionRule;
import com.github.samyadaleh.cltoolbox.chartparsing.item.DeductionChartItem;
import com.github.samyadaleh.cltoolbox.chartparsing.item.ChartItemInterface;
import com.github.samyadaleh.cltoolbox.common.ArrayUtils;
import com.github.samyadaleh.cltoolbox.common.TreeUtils;
import com.github.samyadaleh.cltoolbox.common.cfg.CfgProductionRule;
import com.github.samyadaleh.cltoolbox.common.tag.Tree;

import static com.github.samyadaleh.cltoolbox.common.Constants.DEDUCTION_RULE_CFG_SHIFTREDUCE_REDUCE;

/**
 * If the top o the stack matches the rhs of a rule, replace it with the lhs.
 */
public class CfgBottomUpReduce extends AbstractDynamicDeductionRule {

  private final CfgProductionRule rule;

  public CfgBottomUpReduce(CfgProductionRule rule) {
    this.rule = rule;
    this.antNeeded = 1;
    this.name = DEDUCTION_RULE_CFG_SHIFTREDUCE_REDUCE + " " + rule.toString();
  }

  @Override public List<ChartItemInterface> getConsequences() {
    if (antecedences.size() == this.antNeeded) {
      String[] itemForm = antecedences.get(0).getItemForm();
      String stack = itemForm[0];
      String[] stackSplit = stack.split(" ");
      String i = itemForm[1];
      String gamma =
          ArrayUtils.getStringHeadIfEndsWith(stackSplit, rule.getRhs());
      if (gamma != null) {
        ChartItemInterface consequence;
        if (gamma.length() == 0) {
          consequence = new DeductionChartItem(rule.getLhs(), i);
        } else {
          consequence = new DeductionChartItem(gamma + " " + rule.getLhs(), i);
        }
        List<Tree> derivedTrees =
            new ArrayList<>(antecedences.get(0).getTrees());
        try {
          Tree derivedTreeBase = new Tree(rule);
          for (Tree tree : antecedences.get(0).getTrees()) {
            boolean found = false;
            for (String rhsSym : rule.getRhs()) {
              if (tree.getRoot().getLabel().equals(rhsSym)) {
                derivedTrees.remove(0);
                try {
                  derivedTreeBase = TreeUtils
                      .performLeftmostSubstitution(derivedTreeBase, tree);
                } catch (IndexOutOfBoundsException e) {
                  log.debug(e.getMessage(), e);
                }
                found = true;
                break;
              }
            }
            if (!found) {
              break;
            }
          }
          derivedTrees.add(0, derivedTreeBase);
          consequence.setTrees(derivedTrees);
          logItemGeneration(consequence);
          consequences.add(consequence);
        } catch (ParseException e) {
          throw new RuntimeException(e);
        }
      }
    }
    return consequences;
  }

  @Override public String toString() {
    return "[Γ " + ArrayUtils.toString(rule.getRhs()) + ",i]" + "\n______"
        + rule.toString() + "\n" + "[Γ " + rule.getLhs() + ",i]";
  }

}

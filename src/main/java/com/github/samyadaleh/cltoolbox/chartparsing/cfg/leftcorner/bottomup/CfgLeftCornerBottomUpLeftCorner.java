package com.github.samyadaleh.cltoolbox.chartparsing.cfg.leftcorner.bottomup;

import com.github.samyadaleh.cltoolbox.chartparsing.dynamicdeductionrule.AbstractDynamicDeductionRule;
import com.github.samyadaleh.cltoolbox.chartparsing.item.ChartItemInterface;
import com.github.samyadaleh.cltoolbox.chartparsing.item.DeductionChartItem;
import com.github.samyadaleh.cltoolbox.common.TreeUtils;
import com.github.samyadaleh.cltoolbox.common.cfg.CfgProductionRule;
import com.github.samyadaleh.cltoolbox.common.tag.Tree;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import static com.github.samyadaleh.cltoolbox.common.Constants.DEDUCTION_RULE_CFG_LEFTCORNER_BOTTOMUP_LC;

public class CfgLeftCornerBottomUpLeftCorner
    extends AbstractDynamicDeductionRule {
  private final String lc;
  private final String newStack;
  private final CfgProductionRule rule;

  public CfgLeftCornerBottomUpLeftCorner(CfgProductionRule rule) {
    this.name = DEDUCTION_RULE_CFG_LEFTCORNER_BOTTOMUP_LC;
    this.antNeeded = 1;
    this.lc = rule.getRhs()[0];
    int restStart = lc.length() + 1;
    String rhsRest = String.join(" ", rule.getRhs()).substring(restStart);
    newStack = rule.getLhs() + " -> " + lc + " •" + rhsRest;
    this.rule = rule;
  }

  @Override public List<ChartItemInterface> getConsequences() {
    String[] itemForm = antecedences.get(0).getItemForm();
    String stack1 = itemForm[0];
    String[] stackSplit = stack1.split(" ");
    if (!"•".equals(stackSplit[stackSplit.length - 1])) {
      return consequences;
    }
    String lhs = itemForm[0].split(" ")[0];
    if (!lhs.equals(lc)) {
      return consequences;
    }
    int i = Integer.parseInt(itemForm[1]);
    int j = Integer.parseInt(itemForm[2]);
    ChartItemInterface consequence =
        new DeductionChartItem(newStack, String.valueOf(i), String.valueOf(j));
    List<Tree> derivedTrees = new ArrayList<>();
    try {
      for (Tree tree : antecedences.get(0).getTrees()) {
        Tree baseTree = new Tree(rule);
        derivedTrees.add(TreeUtils.performLeftmostSubstitution(baseTree, tree));
      }
    } catch (ParseException e) {
      // should never happen
      throw new RuntimeException(e);
    }
    consequence.setTrees(derivedTrees);
    consequences.add(consequence);
    return consequences;
  }

  @Override public String toString() {
    return "[" + lc + " -> α •,i,l]" + "\n______ \n" + "[" + newStack + ",i,l]";
  }
}

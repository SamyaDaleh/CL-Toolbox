package com.github.samyadaleh.cltoolbox.chartparsing.cfg.cyk;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import com.github.samyadaleh.cltoolbox.chartparsing.dynamicdeductionrule.AbstractDynamicDeductionRule;
import com.github.samyadaleh.cltoolbox.chartparsing.item.DeductionChartItem;
import com.github.samyadaleh.cltoolbox.chartparsing.item.ChartItemInterface;
import com.github.samyadaleh.cltoolbox.common.TreeUtils;
import com.github.samyadaleh.cltoolbox.common.cfg.CfgProductionRule;
import com.github.samyadaleh.cltoolbox.common.tag.Tree;

import static com.github.samyadaleh.cltoolbox.common.Constants.DEDUCTION_RULE_CFG_CYK_COMPLETE;

/**
 * If two items match the rhs of a rule, get a new item that represents the lhs.
 */
public class CfgCykCompleteGeneral extends AbstractDynamicDeductionRule {

  private final CfgProductionRule rule;

  public CfgCykCompleteGeneral(CfgProductionRule rule) {
    this.rule = rule;
    this.name = DEDUCTION_RULE_CFG_CYK_COMPLETE + " " + rule.toString();
    this.antNeeded = rule.getRhs().length;
  }

  @Override public List<ChartItemInterface> getConsequences() {
    if (antecedences.size() == antNeeded) {
      int minI = Integer.MAX_VALUE;
      int prevItemStart = 0;
      for (ChartItemInterface mayFirstRhsItem : antecedences) {
        int i = Integer.parseInt(mayFirstRhsItem.getItemForm()[1]);
        if (i >= minI) {
          continue;
        }
        minI = i;
        prevItemStart = i;
      }
      int lSum = 0;
      List<Tree> derivedTrees = new ArrayList<>();
      try {
        derivedTrees.add(new Tree(rule));
        for (int j = 0; j < rule.getRhs().length; j++) {
          boolean found = false;
          for (ChartItemInterface mayRhsItem : antecedences) {
            int i = Integer.parseInt(mayRhsItem.getItemForm()[1]);
            if (i == prevItemStart && mayRhsItem.getItemForm()[0]
                .equals(rule.getRhs()[j])) {
              found = true;
              if (mayRhsItem.getTrees() != null) {
                List<Tree> derivedTreesNew = new ArrayList<>();
                for (Tree tree1 : mayRhsItem.getTrees()) {
                  for (Tree tree2 : derivedTrees) {
                    derivedTreesNew.add(
                        TreeUtils.performLeftmostSubstitution(tree2, tree1));
                  }
                }
                if (derivedTreesNew.size() > 0) {
                  derivedTrees = derivedTreesNew;
                }
              }
              int l = Integer.parseInt(mayRhsItem.getItemForm()[2]);
              prevItemStart = i + l;
              lSum += l;
              break;
            }
          }
          if (!found) {
            return this.consequences;
          }
        }
        ChartItemInterface consequence =
            new DeductionChartItem(rule.getLhs(), String.valueOf(minI),
                String.valueOf(lSum));
        consequence.setTrees(derivedTrees);
        logItemGeneration(consequence);
        consequences.add(consequence);
      } catch (ParseException e) {
        log.error(e.getMessage(), e);
      }
    }
    return this.consequences;
  }

  @Override public String toString() {
    StringBuilder repr = new StringBuilder();
    int i = 1;
    for (String rhsSym : rule.getRhs()) {
      repr.append('[').append(rhsSym).append(",i");
      if (i > 1) {
        repr.append("+l").append(i - 1);
      }
      repr.append(",l").append(i).append(']');
      i++;
    }
    repr.append("\n______ \n").append('[').append(rule.getLhs());
    return repr.toString();
  }
}

package com.github.samyadaleh.cltoolbox.chartparsing.cfg.cyk;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import com.github.samyadaleh.cltoolbox.chartparsing.AbstractDynamicDeductionRule;
import com.github.samyadaleh.cltoolbox.chartparsing.DeductionChartItem;
import com.github.samyadaleh.cltoolbox.chartparsing.ChartItemInterface;
import com.github.samyadaleh.cltoolbox.common.TreeUtils;
import com.github.samyadaleh.cltoolbox.common.cfg.CfgProductionRule;
import com.github.samyadaleh.cltoolbox.common.tag.Tree;

/**
 * If two items match the rhs of a rule, get a new item that represents the lhs.
 */
public class CfgCykCompleteGeneral extends AbstractDynamicDeductionRule {

  private final CfgProductionRule rule;

  public CfgCykCompleteGeneral(CfgProductionRule rule) {
    this.rule = rule;
    this.name = "complete " + rule.toString();
    this.antNeeded = rule.getRhs().length;
  }

  @Override public List<ChartItemInterface> getConsequences() throws ParseException {
    if (antecedences.size() == antNeeded) {
      int minI = Integer.MAX_VALUE;
      int prevItemStart = 0;
      for (ChartItemInterface mayFirstRhsItem : antecedences) {
        int i = Integer.parseInt(mayFirstRhsItem.getItemform()[1]);
        if (i >= minI) {
          continue;
        }
        minI = i;
        prevItemStart = i;
      }
      int lSum = 0;
      List<Tree> derivedTrees = new ArrayList<Tree>();
      derivedTrees.add(new Tree(rule));
      for (int j = 0; j < rule.getRhs().length; j++) {
        boolean found = false;
        for (ChartItemInterface mayRhsItem : antecedences) {
          int i = Integer.parseInt(mayRhsItem.getItemform()[1]);
          if (i == prevItemStart
            && mayRhsItem.getItemform()[0].equals(rule.getRhs()[j])) {
            found = true;
            if (mayRhsItem.getTrees() != null) {
              List<Tree> derivedTreesNew = new ArrayList<Tree>();
              for (Tree tree1 : mayRhsItem.getTrees()) {
                for (Tree tree2 : derivedTrees) {
                  derivedTreesNew
                    .add(TreeUtils.performLeftmostSubstitution(tree2, tree1));
                }
              }
              if (derivedTreesNew.size() > 0) {
                derivedTrees = derivedTreesNew;
              }
            }
            int l = Integer.parseInt(mayRhsItem.getItemform()[2]);
            prevItemStart = i + l;
            lSum += l;
            break;
          }
        }
        if (!found) {
          return this.consequences;
        }
      }
      ChartItemInterface consequence = new DeductionChartItem(rule.getLhs(), String.valueOf(minI),
        String.valueOf(lSum));
      consequence.setTrees(derivedTrees);
      consequences.add(consequence);
    }
    return this.consequences;
  }

  @Override public String toString() {
    return "[" + rule.getRhs()[0] + ",1,l1], [" + rule.getRhs()[1] + ",i+l1,l2]"
      + "\n______ \n" + "[" + rule.getLhs() + ",i,l1+l2]";
  }
}

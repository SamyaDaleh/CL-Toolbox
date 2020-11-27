package com.github.samyadaleh.cltoolbox.chartparsing.cfg.earley.passive;

import com.github.samyadaleh.cltoolbox.chartparsing.dynamicdeductionrule.AbstractDynamicDecutionRuleTwoAntecedences;
import com.github.samyadaleh.cltoolbox.chartparsing.item.DeductionChartItem;
import com.github.samyadaleh.cltoolbox.chartparsing.item.ChartItemInterface;
import com.github.samyadaleh.cltoolbox.common.ArrayUtils;
import com.github.samyadaleh.cltoolbox.common.TreeUtils;
import com.github.samyadaleh.cltoolbox.common.tag.Tree;

import java.util.ArrayList;
import java.util.List;

import static com.github.samyadaleh.cltoolbox.common.Constants.DEDUCTION_RULE_CFG_EARLEY_COMPLETE;

/**
 * Moves the dot over a nonterminal by using a passive item.
 */
public class CfgEarleyPassiveComplete
    extends AbstractDynamicDecutionRuleTwoAntecedences {

  public CfgEarleyPassiveComplete() {
    this.name = DEDUCTION_RULE_CFG_EARLEY_COMPLETE;
    this.antNeeded = 2;
  }

  @Override
  protected void calculateConsequences(String[] itemForm1, String[] itemForm2) {
    if (itemForm1[0].contains("•") && !itemForm2[0].contains("•")
        && itemForm1[2].equals(itemForm2[1])) {
      String stack1 = itemForm1[0];
      String[] stackSplit1 = stack1.split(" ");
      for (int l = 0; l < stackSplit1.length; l++) {
        if (stackSplit1[l].startsWith("•") && stackSplit1[l].substring(1)
            .equals(itemForm2[0])) {
          this.name = DEDUCTION_RULE_CFG_EARLEY_COMPLETE + " " + itemForm2[0];
          String newStack;
          if (l == stackSplit1.length - 1) {
            newStack =
                ArrayUtils.getSubSequenceAsString(stackSplit1, 0, l) + " "
                    + itemForm2[0] + " •";
          } else {
            newStack =
                ArrayUtils.getSubSequenceAsString(stackSplit1, 0, l) + " "
                    + itemForm2[0] + " •" + ArrayUtils
                    .getSubSequenceAsString(stackSplit1, l + 1,
                        stackSplit1.length);
          }
          ChartItemInterface consequence =
              new DeductionChartItem(newStack, itemForm1[1], itemForm2[2]);
          List<Tree> derivedTrees = new ArrayList<>();
          if (antecedences.get(0).getItemForm() == itemForm1) {
            for (Tree tree1 : antecedences.get(0).getTrees()) {
              for (Tree tree2 : antecedences.get(1).getTrees()) {
                derivedTrees
                    .add(TreeUtils.performLeftmostSubstitution(tree1, tree2));
              }
            }
          } else {
            for (Tree tree2 : antecedences.get(0).getTrees()) {
              for (Tree tree1 : antecedences.get(1).getTrees()) {
                derivedTrees
                    .add(TreeUtils.performLeftmostSubstitution(tree1, tree2));
              }
            }
          }
          consequence.setTrees(derivedTrees);
          logItemGeneration(consequence);
          consequences.add(consequence);
          break;
        }
      }
    }
  }

  @Override public String toString() {
    return "[A -> α •B β,i,j] [B,j,k]" + "\n______\n" + "[A -> α B •β,i,k]";
  }

}

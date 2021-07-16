package com.github.samyadaleh.cltoolbox.chartparsing.cfg.earley;

import com.github.samyadaleh.cltoolbox.chartparsing.dynamicdeductionrule.AbstractDynamicDecutionRuleTwoAntecedences;
import com.github.samyadaleh.cltoolbox.chartparsing.item.ChartItemInterface;
import com.github.samyadaleh.cltoolbox.chartparsing.item.DeductionChartItem;
import com.github.samyadaleh.cltoolbox.common.ArrayUtils;
import com.github.samyadaleh.cltoolbox.common.TreeUtils;
import com.github.samyadaleh.cltoolbox.common.tag.Tree;

import java.util.ArrayList;
import java.util.List;

import static com.github.samyadaleh.cltoolbox.common.Constants.DEDUCTION_RULE_CFG_EARLEY_COMPLETE;

/**
 * If in one item a dot is before a nonterminal and the other item is a rule
 * with that nonterminal as lhs and the dot at the end, move the dot over the
 * nonterminal.
 */
public class CfgEarleyComplete
    extends AbstractDynamicDecutionRuleTwoAntecedences {

  private String[] nonterminals;

  public CfgEarleyComplete(String[] nonterminals) {
    this.name = DEDUCTION_RULE_CFG_EARLEY_COMPLETE;
    this.antNeeded = 2;
    this.nonterminals = nonterminals;
  }

  protected void calculateConsequences(String[] itemForm1, String[] itemForm2) {
    String stack1 = itemForm1[0];
    String[] stackSplit1 = stack1.split(" ");
    int i1 = Integer.parseInt(itemForm1[1]);
    int j1 = Integer.parseInt(itemForm1[2]);
    String stack2 = itemForm2[0];
    String[] stackSplit2 = stack2.split(" ");
    int j2 = Integer.parseInt(itemForm2[1]);
    int k2 = Integer.parseInt(itemForm2[2]);

    if (j1 == j2 && stack2.endsWith("•")) {
      for (int l = 0; l < stackSplit1.length; l++) {
        if (stackSplit1[l].startsWith("•") && stackSplit1[l].substring(1)
            .equals(stackSplit2[0])) {
          this.name = DEDUCTION_RULE_CFG_EARLEY_COMPLETE + " " + stackSplit2[0];
          String newStack;
          if (l == stackSplit1.length - 1) {
            newStack =
                ArrayUtils.getSubSequenceAsString(stackSplit1, 0, l) + " "
                    + stackSplit2[0] + " •";
          } else {
            newStack =
                ArrayUtils.getSubSequenceAsString(stackSplit1, 0, l) + " "
                    + stackSplit2[0] + " •" + ArrayUtils
                    .getSubSequenceAsString(stackSplit1, l + 1,
                        stackSplit1.length);
          }
          ChartItemInterface consequence =
              new DeductionChartItem(newStack, String.valueOf(i1),
                  String.valueOf(k2));
          List<Tree> derivedTrees = new ArrayList<>();
          if (antecedences.get(0).getItemForm() == itemForm1) {
            for (Tree tree1 : antecedences.get(0).getTrees()) {
              for (Tree tree2 : antecedences.get(1).getTrees()) {
                if (tree2.contains(tree1) && (tree2.allLeavesAreEpsilon()
                    || tree1.oneSubstitutionNodeRestEpsilon(nonterminals))
                || TreeUtils.addsNothing(tree1, tree2, nonterminals)) {
                  continue;
                }
                derivedTrees
                    .add(TreeUtils.performLeftmostSubstitution(tree1, tree2));
              }
            }
          } else {
            for (Tree tree2 : antecedences.get(0).getTrees()) {
              for (Tree tree1 : antecedences.get(1).getTrees()) {
                if (tree2.contains(tree1) && (tree2.allLeavesAreEpsilon()
                    || tree1.oneSubstitutionNodeRestEpsilon(nonterminals))
                    || TreeUtils.addsNothing(tree1, tree2, nonterminals)) {
                  continue;
                }
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
    return "[A -> α •B β,i,j] [B -> ɣ •,j,k]" + "\n______\n"
        + "[A -> α B •β,i,k]";
  }

}

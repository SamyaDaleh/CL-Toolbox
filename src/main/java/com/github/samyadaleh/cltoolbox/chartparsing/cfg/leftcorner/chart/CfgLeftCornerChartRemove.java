package com.github.samyadaleh.cltoolbox.chartparsing.cfg.leftcorner.chart;

import com.github.samyadaleh.cltoolbox.chartparsing.ChartParsingUtils;
import com.github.samyadaleh.cltoolbox.chartparsing.dynamicdeductionrule.AbstractDynamicDecutionRuleTwoAntecedences;
import com.github.samyadaleh.cltoolbox.chartparsing.item.ChartItemInterface;
import com.github.samyadaleh.cltoolbox.chartparsing.item.DeductionChartItem;
import com.github.samyadaleh.cltoolbox.common.ArrayUtils;
import com.github.samyadaleh.cltoolbox.common.TreeUtils;
import com.github.samyadaleh.cltoolbox.common.tag.Tree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.github.samyadaleh.cltoolbox.common.Constants.DEDUCTION_RULE_CFG_LEFTCORNER_REMOVE;

/**
 * If topmost symbol on stacks completed and predicted are the same, remove
 * both.
 */
public class CfgLeftCornerChartRemove
    extends AbstractDynamicDecutionRuleTwoAntecedences {

  public CfgLeftCornerChartRemove() {
    this.name = DEDUCTION_RULE_CFG_LEFTCORNER_REMOVE;
    this.antNeeded = 2;
  }

  protected void calculateConsequences(String[] itemForm1, String[] itemForm2) {
    String[] mayDottedRuleSplit = itemForm1[0].split(" ");
    for (int k = 0; k < mayDottedRuleSplit.length; k++) {
      if (mayDottedRuleSplit[k].startsWith("•")) {
        int i = Integer.parseInt(itemForm1[1]);
        int l1 = Integer.parseInt(itemForm1[2]);
        int j = Integer.parseInt(itemForm2[1]);
        int l2 = Integer.parseInt(itemForm2[2]);
        if (mayDottedRuleSplit[k].substring(1).equals(itemForm2[0])
            && i + l1 == j && mayDottedRuleSplit[k].length() > 1) {
          this.name =
              DEDUCTION_RULE_CFG_LEFTCORNER_REMOVE + " " + mayDottedRuleSplit[k]
                  .substring(1);
          if (k == mayDottedRuleSplit.length - 1) {
            handleDotBeforeLastSymbol(itemForm1, mayDottedRuleSplit, k, i, l1,
                l2);
          } else {
            handleDotBeforeNotLastSymbol(itemForm1, mayDottedRuleSplit, k, i,
                l1, l2);
          }
        } else {
          return;
        }
      }
    }
  }

  private void handleDotBeforeNotLastSymbol(String[] itemForm1,
      String[] mayDottedRuleSplit, int k, int i, int l1, int l2) {
    ChartItemInterface consequence = new DeductionChartItem(
        ArrayUtils.getSubSequenceAsString(mayDottedRuleSplit, 0, k) + " "
            + mayDottedRuleSplit[k].substring(1) + " •" + ArrayUtils
            .getSubSequenceAsString(mayDottedRuleSplit, k + 1,
                mayDottedRuleSplit.length), String.valueOf(i),
        String.valueOf(l1 + l2));
    List<Tree> derivedTrees =
        ChartParsingUtils.generateDerivatedTrees(antecedences, itemForm1);
    if (derivedTrees.size() == 0) {
      if (antecedences.get(0).getTrees().size() == 0){
        derivedTrees = antecedences.get(1).getTrees();
      } else {
        derivedTrees = antecedences.get(0).getTrees();
      }
    }
    consequence.setTrees(derivedTrees);
    logItemGeneration(consequence);
    consequences.add(consequence);
  }

  private void handleDotBeforeLastSymbol(String[] itemForm1,
      String[] mayDottedRuleSplit, int k, int i, int l1, int l2) {
    ChartItemInterface consequence = new DeductionChartItem(
        ArrayUtils.getSubSequenceAsString(mayDottedRuleSplit, 0, k) + " "
            + mayDottedRuleSplit[k].substring(1) + " •", String.valueOf(i),
        String.valueOf(l1 + l2));
    List<Tree> derivedTrees = new ArrayList<>();
    if (Arrays.equals(antecedences.get(0).getItemForm(), itemForm1)) {
      if (antecedences.get(1).getTrees().size() > 0) {
        for (Tree tree1 : antecedences.get(0).getTrees()) {
          for (Tree tree2 : antecedences.get(1).getTrees()) {
            derivedTrees
                .add(TreeUtils.performLeftmostSubstitution(tree1, tree2));
          }
        }
      } else {
        derivedTrees.addAll(antecedences.get(0).getTrees());
      }
    } else {
      if (antecedences.get(0).getTrees().size() > 0) {
        for (Tree tree1 : antecedences.get(0).getTrees()) {
          for (Tree tree2 : antecedences.get(1).getTrees()) {
            try {
              derivedTrees
                  .add(TreeUtils.performLeftmostSubstitution(tree2, tree1));
            } catch(StringIndexOutOfBoundsException e){
              log.debug(e.getMessage(), e);
            }
          }
        }
      } else {
        derivedTrees.addAll(antecedences.get(0).getTrees());
      }
    }
    consequence.setTrees(derivedTrees);
    logItemGeneration(consequence);
    consequences.add(consequence);
  }

  @Override public String toString() {
    return "[A -> α •X β,i,l_1], [X,j,l_2]" + "\n______ j = i+l_1\n"
        + "[A -> α X •β,i,l_1+l_2]";
  }

}

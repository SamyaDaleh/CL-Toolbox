package com.github.samyadaleh.cltoolbox.chartparsing.cfg.leftcorner.chart;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.github.samyadaleh.cltoolbox.chartparsing.AbstractDynamicDecutionRuleTwoAntecedences;
import com.github.samyadaleh.cltoolbox.chartparsing.ChartItemInterface;
import com.github.samyadaleh.cltoolbox.chartparsing.DeductionChartItem;
import com.github.samyadaleh.cltoolbox.common.ArrayUtils;
import com.github.samyadaleh.cltoolbox.common.TreeUtils;
import com.github.samyadaleh.cltoolbox.common.tag.Tree;

/**
 * If topmost symbol on stacks completed and predicted are the same, remove
 * both.
 */
public class CfgLeftCornerChartRemove
  extends AbstractDynamicDecutionRuleTwoAntecedences {

  public CfgLeftCornerChartRemove() {
    this.name = "remove";
    this.antNeeded = 2;
  }

  protected void calculateConsequences(String[] itemForm1, String[] itemForm2)
    throws ParseException {
    String[] mayDottedRuleSplit = itemForm1[0].split(" ");
    for (int k = 0; k < mayDottedRuleSplit.length; k++) {
      if (mayDottedRuleSplit[k].startsWith("•")) {
        int i = Integer.parseInt(itemForm1[1]);
        int l1 = Integer.parseInt(itemForm1[2]);
        int j = Integer.parseInt(itemForm2[1]);
        int l2 = Integer.parseInt(itemForm2[2]);
        if (mayDottedRuleSplit[k].substring(1).equals(itemForm2[0])
          && i + l1 == j && mayDottedRuleSplit[k].length() > 1) {
          this.name = "remove " + mayDottedRuleSplit[k].substring(1);
          if (k == mayDottedRuleSplit.length - 1) {
            ChartItemInterface consequence = new DeductionChartItem(
              ArrayUtils.getSubSequenceAsString(mayDottedRuleSplit, 0, k) + " "
                + mayDottedRuleSplit[k].substring(1) + " •",
              String.valueOf(i), String.valueOf(l1 + l2));
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
                    derivedTrees
                      .add(TreeUtils.performLeftmostSubstitution(tree2, tree1));
                  }
                }
              } else {
                derivedTrees.addAll(antecedences.get(0).getTrees());
              }
            }
            consequence.setTrees(derivedTrees);
            logItemGeneration(consequence);
            consequences.add(consequence);
          } else {
            ChartItemInterface consequence = new DeductionChartItem(
              ArrayUtils.getSubSequenceAsString(mayDottedRuleSplit, 0, k) + " "
                + mayDottedRuleSplit[k].substring(1) + " •"
                + ArrayUtils.getSubSequenceAsString(mayDottedRuleSplit, k + 1,
                  mayDottedRuleSplit.length),
              String.valueOf(i), String.valueOf(l1 + l2));
            List<Tree> derivedTrees = new ArrayList<>();
            if (Arrays.equals(antecedences.get(0).getItemForm(), itemForm1)) {
              for (Tree tree1 : antecedences.get(0).getTrees()) {
                for (Tree tree2 : antecedences.get(1).getTrees()) {
                  derivedTrees
                    .add(TreeUtils.performLeftmostSubstitution(tree1, tree2));
                }

              }
            } else {
              for (Tree tree1 : antecedences.get(0).getTrees()) {
                for (Tree tree2 : antecedences.get(1).getTrees()) {
                  derivedTrees
                    .add(TreeUtils.performLeftmostSubstitution(tree2, tree1));
                }

              }
            }
            consequence.setTrees(derivedTrees);
            logItemGeneration(consequence);
            consequences.add(consequence);
          }
        } else {
          return;
        }
      }
    }
  }

  @Override public String toString() {
    return "[A -> α •X β,i,l_1], [X,j,l_2]" + "\n______ j = i+l_1\n"
      + "[A -> α X •β,i,l_1+l_2]";
  }

}

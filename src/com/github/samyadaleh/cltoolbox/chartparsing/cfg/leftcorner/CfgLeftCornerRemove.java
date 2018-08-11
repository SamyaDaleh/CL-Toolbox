package com.github.samyadaleh.cltoolbox.chartparsing.cfg.leftcorner;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import com.github.samyadaleh.cltoolbox.chartparsing.AbstractDynamicDeductionRule;
import com.github.samyadaleh.cltoolbox.chartparsing.DeductionChartItem;
import com.github.samyadaleh.cltoolbox.chartparsing.ChartItemInterface;
import com.github.samyadaleh.cltoolbox.common.ArrayUtils;
import com.github.samyadaleh.cltoolbox.common.TreeUtils;
import com.github.samyadaleh.cltoolbox.common.tag.Tree;

/**
 * If topmost symbol on stacks completed and predicted are the same, remove
 * both.
 */
public class CfgLeftCornerRemove extends AbstractDynamicDeductionRule {

  public CfgLeftCornerRemove() {
    this.name = "remove";
    this.antNeeded = 1;
  }

  @Override public List<ChartItemInterface> getConsequences()
    throws ParseException {
    if (antecedences.size() == antNeeded) {
      String[] itemForm = antecedences.get(0).getItemform();
      String stackCompl = itemForm[0];
      String[] stackComplSplit = stackCompl.split(" ");
      String stackPred = itemForm[1];
      String[] stackPredSplit = stackPred.split(" ");
      String stackLhs = itemForm[2];
      if (stackCompl.length() > 0 && stackPred.length() > 0
        && stackComplSplit[0].equals(stackPredSplit[0])) {
        String newCompl = ArrayUtils.getSubSequenceAsString(stackComplSplit, 1,
          stackComplSplit.length);
        String newPred = ArrayUtils.getSubSequenceAsString(stackPredSplit, 1,
          stackPredSplit.length);
        ChartItemInterface consequence =
          new DeductionChartItem(newCompl, newPred, stackLhs);
        List<Tree> derivedTrees = new ArrayList<Tree>();
        List<Tree> antDerivedTrees = antecedences.get(0).getTrees();
        if (antDerivedTrees.size() > 1) {
          for (int i = 0; i < antDerivedTrees.size() - 2; i++) {
            derivedTrees.add(antDerivedTrees.get(i));
          }
          try {
          derivedTrees.add(TreeUtils.performLeftmostSubstitution(
            antDerivedTrees.get(antDerivedTrees.size() - 2),
            antDerivedTrees.get(antDerivedTrees.size() - 1)));
          } catch (StringIndexOutOfBoundsException e) {
            // This probably doesn't lead to a successfull trace
            derivedTrees.addAll(antDerivedTrees);
          }
        } else {
          derivedTrees.addAll(antDerivedTrees);
        }
        consequence.setTrees(derivedTrees);
        consequences.add(consequence);
      }
    }
    return consequences;
  }

  @Override public String toString() {
    return "[Xα,Xβ,ɣ]" + "\n______\n" + "[α,β,ɣ]";
  }

}

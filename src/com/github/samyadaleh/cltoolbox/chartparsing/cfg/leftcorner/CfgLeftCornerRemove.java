package com.github.samyadaleh.cltoolbox.chartparsing.cfg.leftcorner;

import java.util.ArrayList;
import java.util.List;

import com.github.samyadaleh.cltoolbox.chartparsing.dynamicdeductionrule.AbstractDynamicDeductionRule;
import com.github.samyadaleh.cltoolbox.chartparsing.item.DeductionChartItem;
import com.github.samyadaleh.cltoolbox.chartparsing.item.ChartItemInterface;
import com.github.samyadaleh.cltoolbox.common.ArrayUtils;
import com.github.samyadaleh.cltoolbox.common.TreeUtils;
import com.github.samyadaleh.cltoolbox.common.tag.Tree;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * If topmost symbol on stacks completed and predicted are the same, remove
 * both.
 */
public class CfgLeftCornerRemove extends AbstractDynamicDeductionRule {
  private static final Logger log = LogManager.getLogger();

  public CfgLeftCornerRemove() {
    this.name = "remove";
    this.antNeeded = 1;
  }

  @Override public List<ChartItemInterface> getConsequences() {
    if (antecedences.size() == antNeeded) {
      String[] itemForm = antecedences.get(0).getItemForm();
      String[] stackComplSplit = itemForm[0].split(" ");
      String[] stackPredSplit = itemForm[1].split(" ");
      if (itemForm[0].length() > 0 && itemForm[1].length() > 0
          && stackComplSplit[0].equals(stackPredSplit[0])) {
        this.name = "remove " + stackComplSplit[0];
        String newCompl = ArrayUtils
            .getSubSequenceAsString(stackComplSplit, 1, stackComplSplit.length);
        String newPred = ArrayUtils
            .getSubSequenceAsString(stackPredSplit, 1, stackPredSplit.length);
        ChartItemInterface consequence =
            new DeductionChartItem(newCompl, newPred, itemForm[2]);
        List<Tree> derivedTrees = new ArrayList<>();
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
            log.debug(e.getMessage(), e);
            derivedTrees.addAll(antDerivedTrees);
          }
        } else {
          derivedTrees.addAll(antDerivedTrees);
        }
        consequence.setTrees(derivedTrees);
        logItemGeneration(consequence);
        consequences.add(consequence);
      }
    }
    return consequences;
  }

  @Override public String toString() {
    return "[Xα,Xβ,ɣ]" + "\n______\n" + "[α,β,ɣ]";
  }

}

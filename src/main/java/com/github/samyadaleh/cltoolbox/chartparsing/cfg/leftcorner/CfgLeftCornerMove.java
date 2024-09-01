package com.github.samyadaleh.cltoolbox.chartparsing.cfg.leftcorner;

import java.util.List;

import com.github.samyadaleh.cltoolbox.chartparsing.dynamicdeductionrule.AbstractDynamicDeductionRule;
import com.github.samyadaleh.cltoolbox.chartparsing.item.DeductionChartItem;
import com.github.samyadaleh.cltoolbox.chartparsing.item.ChartItemInterface;
import com.github.samyadaleh.cltoolbox.common.ArrayUtils;

import static com.github.samyadaleh.cltoolbox.common.Constants.DEDUCTION_RULE_CFG_LEFTCORNER_MOVE;

/**
 * If the end of a rhs is encountered, move the topmost nonterminal from the
 * stack of lhs to the stack of completed items.
 */
public class CfgLeftCornerMove extends AbstractDynamicDeductionRule {

  private final List<String> nonterminals;

  public CfgLeftCornerMove(List<String> nonterminals) {
    this.nonterminals = nonterminals;
    this.name = DEDUCTION_RULE_CFG_LEFTCORNER_MOVE;
    this.antNeeded = 1;
  }

  @Override public List<ChartItemInterface> getConsequences() {
    if (antecedences.size() == antNeeded) {
      String[] itemForm = antecedences.get(0).getItemForm();
      String stackCompl = itemForm[0];
      String stackPred = itemForm[1];
      String[] stackPredSplit = stackPred.split(" ");
      String stackLhs = itemForm[2];
      String[] stackLhsSplit = stackLhs.split(" ");

      if (stackPredSplit[0].equals("$")) {
        for (String nt : nonterminals) {
          if (stackLhsSplit[0].equals(nt)) {
            String newCompl;
            if (stackCompl.isEmpty()) {
              newCompl = nt;
            } else {
              newCompl = nt + " " + stackCompl;
            }
            this.name = DEDUCTION_RULE_CFG_LEFTCORNER_MOVE + " " + nt;
            String newPred = ArrayUtils
                .getSubSequenceAsString(stackPredSplit, 1,
                    stackPredSplit.length);
            String newLhs = ArrayUtils
                .getSubSequenceAsString(stackLhsSplit, 1, stackLhsSplit.length);
            ChartItemInterface consequence =
                new DeductionChartItem(newCompl, newPred, newLhs);
            consequence.setTrees(antecedences.get(0).getTrees());
            logItemGeneration(consequence);
            consequences.add(consequence);
            break;
          }
        }
      }
    }
    return consequences;
  }

  @Override public String toString() {
    return "[α,$β,Aɣ]" + "\n______ A ∈ N\n" + "[Aα,β,ɣ]";
  }

}

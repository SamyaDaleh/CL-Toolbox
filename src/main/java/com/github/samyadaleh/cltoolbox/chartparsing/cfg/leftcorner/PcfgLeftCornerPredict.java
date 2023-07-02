package com.github.samyadaleh.cltoolbox.chartparsing.cfg.leftcorner;

import com.github.samyadaleh.cltoolbox.chartparsing.cfg.cyk.PcfgCykItem;
import com.github.samyadaleh.cltoolbox.chartparsing.dynamicdeductionrule.AbstractDynamicDeductionRule;
import com.github.samyadaleh.cltoolbox.chartparsing.item.ChartItemInterface;
import com.github.samyadaleh.cltoolbox.common.ArrayUtils;
import com.github.samyadaleh.cltoolbox.common.TreeUtils;
import com.github.samyadaleh.cltoolbox.common.cfg.PcfgProductionRule;
import com.github.samyadaleh.cltoolbox.common.tag.Tree;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import static com.github.samyadaleh.cltoolbox.common.Constants.DEDUCTION_RULE_PCFG_LEFTCORNER_PREDICT;

public class PcfgLeftCornerPredict extends AbstractDynamicDeductionRule {
  private final PcfgProductionRule pRule;

  public PcfgLeftCornerPredict(PcfgProductionRule pRule) {
    this.pRule = pRule;
    this.name = DEDUCTION_RULE_PCFG_LEFTCORNER_PREDICT;
    this.antNeeded = 1;
  }

  @Override
  public List<ChartItemInterface> getConsequences() {
    String[] itemForm1 = antecedences.get(0).getItemForm();
    String nt = itemForm1[0];
    if (nt.equals(pRule.getRhs()[0])) {
      double p = ((PcfgCykItem) antecedences.get(0)).getProbability();
      String dottedRule = pRule.getLhs() + " -> " + nt + " •"
          + ArrayUtils.getSubSequenceAsString(
              pRule.getRhs(), 1, pRule.getRhs().length);
      int i = Integer.parseInt(itemForm1[1]);
      int j = Integer.parseInt(itemForm1[2]);
      PcfgCykItem consequence = new PcfgCykItem(p, dottedRule, i, j);
      try {
        List<Tree> existingTrees = antecedences.get(0).getTrees();
        List<Tree> derivedTrees = new ArrayList<>();
        if (existingTrees.size() > 0) {
          for (Tree tree : existingTrees) {
            Tree derivedTreeBase = new Tree(pRule);
            Tree derivedTree = TreeUtils.performLeftmostSubstitution(derivedTreeBase, tree);
            derivedTrees.add(derivedTree);
          }
        }  else {
          Tree derivedTreeBase = new Tree(pRule);
          derivedTrees.add(derivedTreeBase);
        }
        consequence.setTrees(derivedTrees);
        consequences.add(consequence);
      } catch (ParseException e) {
        // should never happen
        throw new RuntimeException(e);
      }
    }
    return consequences;
  }
}

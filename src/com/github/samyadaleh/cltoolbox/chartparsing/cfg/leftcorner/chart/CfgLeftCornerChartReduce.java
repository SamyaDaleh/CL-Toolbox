package com.github.samyadaleh.cltoolbox.chartparsing.cfg.leftcorner.chart;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import com.github.samyadaleh.cltoolbox.chartparsing.dynamicdeductionrule.AbstractDynamicDeductionRule;
import com.github.samyadaleh.cltoolbox.chartparsing.item.DeductionChartItem;
import com.github.samyadaleh.cltoolbox.chartparsing.item.ChartItemInterface;
import com.github.samyadaleh.cltoolbox.common.ArrayUtils;
import com.github.samyadaleh.cltoolbox.common.TreeUtils;
import com.github.samyadaleh.cltoolbox.common.cfg.CfgProductionRule;
import com.github.samyadaleh.cltoolbox.common.tag.Tree;

public class CfgLeftCornerChartReduce extends AbstractDynamicDeductionRule {

  private final CfgProductionRule rule;

  public CfgLeftCornerChartReduce(CfgProductionRule rule) {
    this.name = "reduce " + rule.toString();
    this.rule = rule;
    this.antNeeded = 1;
  }

  @Override public List<ChartItemInterface> getConsequences() {
    if (antecedences.size() == antNeeded) {
      String[] itemForm = antecedences.get(0).getItemForm();
      String i = itemForm[1];
      String l = itemForm[2];
      if (itemForm[0].equals(rule.getRhs()[0])) {
        ChartItemInterface consequence = new DeductionChartItem(
            rule.getLhs() + " -> " + rule.getRhs()[0] + " •" + ArrayUtils
                .getSubSequenceAsString(rule.getRhs(), 1, rule.getRhs().length),
            i, l);
        try {
          Tree derivedTreeBase = new Tree(rule);
          List<Tree> antDerivedTrees = antecedences.get(0).getTrees();
          List<Tree> derivedTrees = new ArrayList<>();
          if (antDerivedTrees.size() > 0 && antDerivedTrees
              .get(antDerivedTrees.size() - 1).getRoot().getLabel()
              .equals(rule.getRhs()[0])) {
            derivedTreeBase = TreeUtils
                .performLeftmostSubstitution(derivedTreeBase,
                    antDerivedTrees.get(antDerivedTrees.size() - 1));
            for (int j = 0;
                 j < antecedences.get(0).getTrees().size() - 1; j++) {
              derivedTrees.add(antecedences.get(0).getTrees().get(j));
            }
          } else {
            derivedTrees.addAll(antecedences.get(0).getTrees());
          }
          derivedTrees.add(derivedTreeBase);
          consequence.setTrees(derivedTrees);
          logItemGeneration(consequence);
          consequences.add(consequence);
        } catch (ParseException e) {
          log.error(e.getMessage(), e);
        }
      }
    }
    return this.consequences;
  }

  @Override public String toString() {
    return "[" + rule.getRhs()[0] + ",i,l]" + "\n______ \n" + "[" + rule
        .getLhs() + " - > " + rule.getRhs()[0] + " •" + ArrayUtils
        .getSubSequenceAsString(rule.getRhs(), 1, rule.getRhs().length)
        + ",i,l]";
  }
}

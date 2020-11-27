package com.github.samyadaleh.cltoolbox.chartparsing.cfg.leftcorner;

import com.github.samyadaleh.cltoolbox.chartparsing.item.ChartItemInterface;
import com.github.samyadaleh.cltoolbox.common.TreeUtils;
import com.github.samyadaleh.cltoolbox.common.cfg.CfgProductionRule;
import com.github.samyadaleh.cltoolbox.common.tag.Tree;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class CfgLeftCornerUtils {

  public static void generateDerivedChartTrees(CfgProductionRule rule,
      List<ChartItemInterface> antecedences, ChartItemInterface consequence) {
    try {
      List<Tree> antDerivedTrees = antecedences.get(0).getTrees();
      List<Tree> derivedTrees = new ArrayList<>();
      if (antDerivedTrees.size() > 0 && antDerivedTrees
          .get(antDerivedTrees.size() - 1).getRoot().getLabel()
          .equals(rule.getRhs()[0])) {
        for (int j = 0; j < antecedences.get(0).getTrees().size(); j++) {
          Tree derivedTreeBase = new Tree(rule);
          derivedTreeBase = TreeUtils
              .performLeftmostSubstitution(derivedTreeBase,
                  antecedences.get(0).getTrees().get(j));
          derivedTrees.add(derivedTreeBase);
        }
      } else {
        derivedTrees.add(new Tree(rule));
      }
      consequence.setTrees(derivedTrees);
    } catch (ParseException e) {
      throw new RuntimeException(e);
    }
  }

  public static void generateDerivedTrees(CfgProductionRule rule,
      List<ChartItemInterface> antecedences, ChartItemInterface consequence) {
    Tree derivedTreeBase;
    try {
      derivedTreeBase = new Tree(rule);
    } catch (ParseException e) {
      throw new RuntimeException(e);
    }
    List<Tree> antDerivedTrees = antecedences.get(0).getTrees();
    List<Tree> derivedTrees = new ArrayList<>();
    if (antDerivedTrees.size() > 0 && antDerivedTrees
        .get(antDerivedTrees.size() - 1).getRoot().getLabel()
        .equals(rule.getRhs()[0])) {
      derivedTreeBase = TreeUtils.performLeftmostSubstitution(derivedTreeBase,
          antDerivedTrees.get(antDerivedTrees.size() - 1));
      for (int j = 0; j < antecedences.get(0).getTrees().size() - 1; j++) {
        derivedTrees.add(antecedences.get(0).getTrees().get(j));
      }
    } else {
      derivedTrees.addAll(antecedences.get(0).getTrees());
    }
    derivedTrees.add(derivedTreeBase);
    consequence.setTrees(derivedTrees);
  }
}

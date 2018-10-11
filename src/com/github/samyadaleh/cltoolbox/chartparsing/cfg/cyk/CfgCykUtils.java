package com.github.samyadaleh.cltoolbox.chartparsing.cfg.cyk;

import com.github.samyadaleh.cltoolbox.chartparsing.item.ChartItemInterface;
import com.github.samyadaleh.cltoolbox.common.TreeUtils;
import com.github.samyadaleh.cltoolbox.common.cfg.CfgProductionRule;
import com.github.samyadaleh.cltoolbox.common.tag.Tree;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

class CfgCykUtils {

  public CfgCykUtils() {
  }

  static List<Tree> generateDerivedTrees(String i1,
      List<ChartItemInterface> antecedences, CfgProductionRule rule) {
    List<Tree> derivedTrees = new ArrayList<>();
    try {
      Tree derivedTreeBase = new Tree(rule);
      if (i1.equals(antecedences.get(0).getItemForm()[1])) {
        List<Tree> leftTrees = antecedences.get(0).getTrees();
        List<Tree> rightTrees = antecedences.get(1).getTrees();
        generateDerivedTrees(derivedTrees, derivedTreeBase, leftTrees,
            rightTrees);
      } else {
        List<Tree> leftTrees = antecedences.get(1).getTrees();
        List<Tree> rightTrees = antecedences.get(0).getTrees();
        generateDerivedTrees(derivedTrees, derivedTreeBase, leftTrees,
            rightTrees);
      }
    } catch (ParseException e) {
      throw new RuntimeException(e);
    }
    return derivedTrees;
  }

  private static void generateDerivedTrees(List<Tree> derivedTrees,
      Tree derivedTreeBase, List<Tree> leftTrees, List<Tree> rightTrees) {
    for (Tree tree1 : leftTrees) {
      for (Tree tree2 : rightTrees) {
        Tree derivedTree =
            TreeUtils.performLeftmostSubstitution(derivedTreeBase, tree1);
        derivedTree = TreeUtils.performLeftmostSubstitution(derivedTree, tree2);
        derivedTrees.add(derivedTree);
      }
    }
  }
}

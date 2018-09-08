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
      List<ChartItemInterface> antecedences, CfgProductionRule rule)
      throws ParseException {
    List<Tree> derivedTrees = new ArrayList<>();
    Tree derivedTreeBase = new Tree(rule);
    if (i1.equals(antecedences.get(0).getItemForm()[1])) {
      for (Tree tree1 : antecedences.get(0).getTrees()) {
        for (Tree tree2 : antecedences.get(1).getTrees()) {
          Tree derivedTree =
              TreeUtils.performLeftmostSubstitution(derivedTreeBase, tree1);
          derivedTree =
              TreeUtils.performLeftmostSubstitution(derivedTree, tree2);
          derivedTrees.add(derivedTree);
        }
      }
    } else {
      for (Tree tree1 : antecedences.get(0).getTrees()) {
        for (Tree tree2 : antecedences.get(1).getTrees()) {
          Tree derivedTree =
              TreeUtils.performLeftmostSubstitution(derivedTreeBase, tree2);
          derivedTree =
              TreeUtils.performLeftmostSubstitution(derivedTree, tree1);
          derivedTrees.add(derivedTree);
        }
      }
    }
    return derivedTrees;
  }
}

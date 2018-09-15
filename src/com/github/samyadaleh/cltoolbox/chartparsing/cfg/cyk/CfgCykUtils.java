package com.github.samyadaleh.cltoolbox.chartparsing.cfg.cyk;

import com.github.samyadaleh.cltoolbox.chartparsing.item.ChartItemInterface;
import com.github.samyadaleh.cltoolbox.common.TreeUtils;
import com.github.samyadaleh.cltoolbox.common.cfg.CfgProductionRule;
import com.github.samyadaleh.cltoolbox.common.tag.Tree;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

class CfgCykUtils {
  private static final Logger log = LogManager.getLogger();

  public CfgCykUtils() {
  }

  static List<Tree> generateDerivedTrees(String i1,
      List<ChartItemInterface> antecedences, CfgProductionRule rule) {
    List<Tree> derivedTrees = new ArrayList<>();
    try {
      Tree derivedTreeBase = new Tree(rule);
      if (i1.equals(antecedences.get(0).getItemForm()[1])) {
        for (Tree tree1 : antecedences.get(0).getTrees()) {
          for (Tree tree2 : antecedences.get(1).getTrees()) {
            Tree derivedTree =
                TreeUtils.performLeftmostSubstitution(derivedTreeBase, tree1);
            assert derivedTree != null;
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
            assert derivedTree != null;
            derivedTree =
                TreeUtils.performLeftmostSubstitution(derivedTree, tree1);
            derivedTrees.add(derivedTree);
          }
        }
      }
    } catch (ParseException e) {
      log.error(e.getMessage(), e);
    }
    return derivedTrees;
  }
}

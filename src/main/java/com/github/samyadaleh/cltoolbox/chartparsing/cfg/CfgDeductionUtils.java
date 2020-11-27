package com.github.samyadaleh.cltoolbox.chartparsing.cfg;

import com.github.samyadaleh.cltoolbox.common.TreeUtils;
import com.github.samyadaleh.cltoolbox.common.cfg.CfgProductionRule;
import com.github.samyadaleh.cltoolbox.common.tag.Tree;

import java.text.ParseException;
import java.util.List;

public class CfgDeductionUtils {

  public static void generateDerivedTrees(List<Tree> derivedTrees,
      List<Tree> derivedTreesNew, CfgProductionRule rule) {
    try {
      Tree derivedTreeBase = new Tree(rule);
      for (Tree tree : derivedTrees) {
        derivedTreesNew.add(TreeUtils
            .performLeftmostSubstitution(tree, derivedTreeBase));
      }
    } catch (ParseException e) {
      throw new RuntimeException(e);
    }
  }
}

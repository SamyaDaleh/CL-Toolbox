package com.github.samyadaleh.cltoolbox.chartparsing.cfg;

import com.github.samyadaleh.cltoolbox.common.TreeUtils;
import com.github.samyadaleh.cltoolbox.common.cfg.CfgProductionRule;
import com.github.samyadaleh.cltoolbox.common.tag.Tree;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.ParseException;
import java.util.List;

public class CfgDeductionUtils {

  private static final Logger log = LogManager.getLogger();

  public static void generateDerivedTrees(List<Tree> derivedTrees,
      List<Tree> derivedTreesNew, CfgProductionRule rule) {
    try {
      Tree derivedTreeBase = new Tree(rule);
      for (Tree tree : derivedTrees) {
        try {
          derivedTreesNew.add(
              TreeUtils.performLeftmostSubstitution(tree, derivedTreeBase));
        } catch (StringIndexOutOfBoundsException e) {
          log.trace(e);
        }
      }
    } catch (ParseException e) {
      throw new RuntimeException(e);
    }
  }
}

package com.github.samyadaleh.cltoolbox.chartparsing;

import com.github.samyadaleh.cltoolbox.chartparsing.item.ChartItemInterface;
import com.github.samyadaleh.cltoolbox.common.TreeUtils;
import com.github.samyadaleh.cltoolbox.common.tag.Tree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ChartParsingUtils {

  public static List<Tree> generateDerivatedTrees(
      List<ChartItemInterface> antecedences, String[] itemForm1) {
    List<Tree> derivedTrees = new ArrayList<>();
    if (Arrays.equals(itemForm1, antecedences.get(0).getItemForm())) {
      List<Tree> leftTrees = antecedences.get(0).getTrees();
      List<Tree> rightTrees = antecedences.get(1).getTrees();
      generateDerivedTrees(derivedTrees, leftTrees, rightTrees);
    } else {
      List<Tree> leftTrees = antecedences.get(1).getTrees();
      List<Tree> rightTrees = antecedences.get(0).getTrees();
      generateDerivedTrees(derivedTrees, leftTrees, rightTrees);
    }
    return derivedTrees;
  }

  private static void generateDerivedTrees(List<Tree> derivedTrees,
      List<Tree> leftTrees, List<Tree> rightTrees) {
    for (Tree tree1 : leftTrees) {
      for (Tree tree2 : rightTrees) {
        derivedTrees.add(TreeUtils.performLeftmostSubstitution(tree1, tree2));
      }
    }
  }
}

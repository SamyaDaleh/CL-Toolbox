package com.github.samyadaleh.cltoolbox.chartparsing.ccg;

import com.github.samyadaleh.cltoolbox.chartparsing.dynamicdeductionrule.DynamicDeductionRuleInterface;
import com.github.samyadaleh.cltoolbox.chartparsing.item.DeductionChartItem;
import com.github.samyadaleh.cltoolbox.common.ArrayUtils;
import com.github.samyadaleh.cltoolbox.common.tag.Tree;

import java.text.ParseException;
import java.util.List;

public class CcgDeductionUtils {

  public CcgDeductionUtils() {
  }

  static int findSymbolPos(String s, char symbol) {
    int bracketDepth = 0;
    int slashPos = -1;
    for (int i = 0; i < s.length(); i++) {
      switch (s.charAt(i)) {
      case '(':
        bracketDepth++;
        break;
      case ')':
        bracketDepth--;
      default:
        if (s.charAt(i) == symbol && bracketDepth == 0 && slashPos == -1) {
          slashPos = i;
        }
        break;
      }
    }
    return slashPos;
  }

  static void addNewTrees(DynamicDeductionRuleInterface rule,
      String[] itemForm1, DeductionChartItem consequence, String newMother) {
    newMother = newMother.replace('(', '[').replace(')', ']');
    if (ArrayUtils
        .match(rule.getAntecedences().get(0).getItemForm(), itemForm1)) {
      List<Tree> leftTrees = rule.getAntecedences().get(0).getTrees();
      List<Tree> rightTrees = rule.getAntecedences().get(1).getTrees();
      generateNewTrees(consequence, newMother, leftTrees, rightTrees);
    } else {
      List<Tree> leftTrees = rule.getAntecedences().get(1).getTrees();
      List<Tree> rightTrees = rule.getAntecedences().get(0).getTrees();
      generateNewTrees(consequence, newMother, leftTrees, rightTrees);
    }
  }

  private static void generateNewTrees(DeductionChartItem consequence,
      String newMother, List<Tree> leftTrees, List<Tree> rightTrees) {
    for (Tree tree1 : leftTrees) {
      for (Tree tree2 : rightTrees) {
        try {
          consequence.getTrees().add(
              new Tree("(" + newMother + " " + tree1 + " " + tree2 + " )"));
        } catch (ParseException e) {
          throw new RuntimeException(e);
        }
      }
    }
  }
}

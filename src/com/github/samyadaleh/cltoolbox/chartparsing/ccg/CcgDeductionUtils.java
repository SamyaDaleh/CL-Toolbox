package com.github.samyadaleh.cltoolbox.chartparsing.ccg;

import com.github.samyadaleh.cltoolbox.chartparsing.dynamicdeductionrule.DynamicDeductionRuleInterface;
import com.github.samyadaleh.cltoolbox.chartparsing.item.DeductionChartItem;
import com.github.samyadaleh.cltoolbox.common.ArrayUtils;
import com.github.samyadaleh.cltoolbox.common.tag.Tree;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.ParseException;

public class CcgDeductionUtils {
  private static final Logger log = LogManager.getLogger();

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
      for (Tree tree1 : rule.getAntecedences().get(0).getTrees()) {
        for (Tree tree2 : rule.getAntecedences().get(1).getTrees()) {
          try {
            consequence.getTrees().add(
                new Tree("(" + newMother + " " + tree1 + " " + tree2 + " )"));
          } catch (ParseException e) {
            log.error(e.getMessage(), e);
          }
        }
      }
    } else {
      for (Tree tree1 : rule.getAntecedences().get(0).getTrees()) {
        for (Tree tree2 : rule.getAntecedences().get(1).getTrees()) {
          try {
            consequence.getTrees().add(
                new Tree("(" + newMother + " " + tree2 + " " + tree1 + " )"));
          } catch (ParseException e) {
            log.error(e.getMessage(), e);
          }
        }
      }
    }
  }
}

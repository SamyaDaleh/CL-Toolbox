package com.github.samyadaleh.cltoolbox.chartparsing.lag;

import com.github.samyadaleh.cltoolbox.chartparsing.converter.lag.LagChartItem;
import com.github.samyadaleh.cltoolbox.chartparsing.dynamicdeductionrule.AbstractDynamicDeductionRule;
import com.github.samyadaleh.cltoolbox.chartparsing.item.ChartItemInterface;
import com.github.samyadaleh.cltoolbox.common.ArrayUtils;
import com.github.samyadaleh.cltoolbox.common.lag.LagRule;

import java.util.ArrayList;
import java.util.List;

/**
 * Applies a rule of a rule package, that must be part of the rule package the
 * item allows.
 */
public class LagRuleApplication extends AbstractDynamicDeductionRule {
  private final LagRule rule;

  public LagRuleApplication(String ruleName, LagRule lagRule) {
    super();
    this.name = ruleName;
    this.rule = lagRule;
    this.antNeeded = 1;
  }

  @Override public List<ChartItemInterface> getConsequences() {
    if (antecedences.size() != antNeeded) {
      return consequences;
    }
    List<ChartItemInterface> currentAntecendences = antecedences;
    LagChartItem currentAntecendence =
        (LagChartItem) currentAntecendences.get(0);
    if (!ArrayUtils.contains(currentAntecendence.getRulePackage(), name)) {
      return consequences;
    }
    if (currentAntecendence.getCategories().length < 2) {
      return consequences;
    }
    if (!ArrayUtils
        .match(currentAntecendence.getCategories()[1], rule.getCat2())) {
      return consequences;
    }
    String[] firstAntCat = currentAntecendence.getCategories()[0];
    String[] firstRuleCat = rule.getCat1();
    List<String> collectX = new ArrayList<>();
    boolean collectXStatus = false;
    int xFoundAt = -1;
    for (int i = 0; i < firstAntCat.length; i++) {
      if (collectXStatus) {
        collectX.add(firstAntCat[i]);
        continue;
      }
      if ("X".equals(firstRuleCat[i])) {
        collectXStatus = true;
        collectX.add(firstAntCat[i]);
        xFoundAt = i;
      } else {
        if (!firstAntCat[i].equals(firstRuleCat[i])) {
          return consequences;
        }
      }
    }
    int collectXSize = collectX.size();
    for (int i = 0; i < collectXSize && i < firstAntCat.length - xFoundAt
        && i < firstRuleCat.length; i++) {
      if (firstAntCat[firstAntCat.length - i - 1]
          .equals(firstRuleCat[firstRuleCat.length - i - 1])) {
        collectX.remove(collectX.size() - 1);
      } else if ("X".equals(firstRuleCat[firstRuleCat.length - i - 1])) {
        break;
      } else {
        return consequences;
      }
    }
    String[] newItemCategoryHead = replaceX(rule.getState().getCategory(),
        collectX.toArray(new String[0]));
    String[][] newCategories = getUpdatedCategories(newItemCategoryHead,
        currentAntecendence.getCategories());
    consequences
        .add(new LagChartItem(newCategories, rule.getState().getRulePackage()));
    return consequences;
  }

  /**
   * Category contains an entry 'X' somewhere. Replace its content by collectX
   * and return the new array.
   */
  String[] replaceX(String[] category, String[] collectX) {
    int xPos = -1;
    for (int i = 0; i < category.length; i++) {
      if ("X".equals(category[i])) {
        xPos = i;
        break;
      }
    }
    String[] beforeX = ArrayUtils.getSubSequenceAsArray(category, 0, xPos);
    String[] afterX =
        ArrayUtils.getSubSequenceAsArray(category, xPos + 1, category.length);
    String[] newArray = new String[collectX.length + category.length - 1];
    System.arraycopy(beforeX, 0, newArray, 0, beforeX.length);
    System.arraycopy(collectX, 0, newArray, beforeX.length, collectX.length);
    System.arraycopy(afterX, 0, newArray, beforeX.length + collectX.length,
        afterX.length);
    return newArray;
  }

  /**
   * Return a new array where newItemCategoryHead is the first entry and
   * categories is appended to it.
   */
  private String[][] getUpdatedCategories(String[] newItemCategoryHead,
      String[][] categories) {
    String[][] updatedCategories = new String[categories.length - 1][];
    updatedCategories[0] = newItemCategoryHead;
    System
        .arraycopy(categories, 2, updatedCategories, 1, categories.length - 2);
    return updatedCategories;
  }
}

package com.github.samyadaleh.cltoolbox.chartparsing.converter.lag;

import com.github.samyadaleh.cltoolbox.chartparsing.item.AbstractChartItem;
import com.github.samyadaleh.cltoolbox.chartparsing.item.ChartItemInterface;

/**
 * Chart Item for Left associative grammar that uses the usual itemForm for
 * display, but stores its own data structures for further use.
 */
public class LagChartItem extends AbstractChartItem
    implements ChartItemInterface {
  private final String[][] categories;
  private final String[] rulePackage;

  /**
   * CTOR generates itemForm for display from the passed variables beside
   * storing them.
   */
  LagChartItem(String[][] categories, String[] rulePackage) {
    this.categories = categories;
    this.rulePackage = rulePackage;
    StringBuilder categorieRepr = new StringBuilder();
    for (String[] category : categories) {
      categorieRepr.append("(");
      boolean notFirst = false;
      for (String cat : category) {
        if (notFirst) {
          categorieRepr.append(" ");
        }
        notFirst = true;
        categorieRepr.append(cat);
      }
      categorieRepr.append(")");
    }
    String rpRepr = "{" + String.join(", ", rulePackage) + "}";
    this.itemForm = new String[] {categorieRepr.toString(), rpRepr};
  }

  public String[][] getCategories() {
    return categories;
  }

  public String[] getRulePackage() {
    return rulePackage;
  }
}

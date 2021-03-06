package com.github.samyadaleh.cltoolbox.chartparsing.cfg.cyk.astar;

import com.github.samyadaleh.cltoolbox.chartparsing.item.AbstractChartItem;
import com.github.samyadaleh.cltoolbox.chartparsing.item.ProbabilisticChartItemInterface;
import com.github.samyadaleh.cltoolbox.common.ArrayUtils;

/** Item that stores a weight, which is like the cost of this item (bigger =
 * worse). */
public class PcfgAstarItem extends AbstractChartItem implements ProbabilisticChartItemInterface {

  private final Double ruleW;
  private final Double outW;

  /** Constructor takes the weight, a nonterminal, the beginning and length of
   * its span. */
  public PcfgAstarItem(double ruleW, double outW, String lhs, int i, int j) {
    this.ruleW = ruleW;
    this.outW = outW;
    this.itemForm = new String[] {lhs, String.valueOf(i), String.valueOf(j)};
  }

  @Override public String toString() {
    String ruleWeight = String.valueOf(Math.round(ruleW * 100.0) / 100.0);
    String outWeight = String.valueOf(Math.round(outW * 100.0) / 100.0);
    if (outW == Double.MAX_VALUE) {
      outWeight = "∞";
    }
    if (ruleW == Double.MAX_VALUE) {
      ruleWeight = "∞";
    }
    return ruleWeight + " + " + outWeight + " : " + ArrayUtils
        .toString(this.itemForm);
  }

  @Override public Double getProbability() {
    return ruleW + outW;
  }
}

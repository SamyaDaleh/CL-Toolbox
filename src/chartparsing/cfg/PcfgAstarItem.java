package chartparsing.cfg;

import chartparsing.AbstractItem;
import chartparsing.PItem;
import common.ArrayUtils;

/** Item that stores a weight, which is like the cost of this item (bigger =
 * worse). */
public class PcfgAstarItem extends AbstractItem implements PItem {

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
    return String.valueOf(ruleW) + " + " + String.valueOf(outW) + " : "
      + ArrayUtils.toString(this.itemForm);
  }

  @Override public Double getProbability() {
    return ruleW + outW;
  }
}

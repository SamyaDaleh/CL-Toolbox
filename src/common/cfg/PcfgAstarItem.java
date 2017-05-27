package common.cfg;

import common.AbstractItem;
import common.ArrayUtils;
import common.PItem;

/** Item that stores a weight, which is like the cost of this item (bigger =
 * worse). */
public class PcfgAstarItem extends AbstractItem implements PItem {

  private final Double rulew;
  private final Double outw;

  /** Constructor takes the weight, a nonterminal, the beginning and length of
   * its span. */
  public PcfgAstarItem(double rulew, double outw, String lhs, int i, int j) {
    this.rulew = rulew;
    this.outw = outw;
    this.itemform = new String[] {lhs, String.valueOf(i), String.valueOf(j)};
  }

  @Override public String toString() {
    return String.valueOf(rulew) + " + " + String.valueOf(outw) + " : "
      + ArrayUtils.toString(this.itemform);
  }

  @Override public Double getProbability() {
    return rulew + outw;
  }
}

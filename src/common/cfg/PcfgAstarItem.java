package common.cfg;

import common.ArrayUtils;
import common.Item;
import common.PItem;

/** Item that stores a weight, which is like the cost of this item (bigger =
 * worse). */
public class PcfgAstarItem implements PItem {

  String[] itemform;

  Double rulew;
  Double outw;

  /** Constructor takes the weight, a nonterminal, the beginning and length of
   * its span. */
  public PcfgAstarItem(double rulew, double outw, String lhs, int i, int j) {
    this.rulew = rulew;
    this.outw = outw;
    this.itemform = new String[] {lhs, String.valueOf(i), String.valueOf(j)};
  }

  @Override public void setItemform(String[] itemform) {
    this.itemform = itemform;
  }

  @Override public String[] getItemform() {
    return this.itemform;
  }

  @Override public String toString() {
    return String.valueOf(rulew) + " + " + String.valueOf(outw) + " : "
      + ArrayUtils.toString(this.itemform);
  }

  @Override public boolean equals(Object o) {
    return ArrayUtils.match(this.itemform, ((Item) o).getItemform());
  }

  @Override public Double getProbability() {
    return rulew + outw;
  }
}

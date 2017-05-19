package common.lcfrs;

import common.ArrayUtils;
import common.Item;

/** Active item that has not been fully seen yet. */
public class SrcgEarleyActiveItem implements Item {

  String[] itemform;
  RangeVector p;

  /** Constructor with a clause, where a dot marked up until which point the
   * clause has been macthed with the input. p is the position up to which we
   * have processed the input. We have processed up to the jith element of the
   * ith argument. RangeVector contains the bindings of the variables of the
   * lhs. */
  public SrcgEarleyActiveItem(String dottedclause, int pos, int i, int j,
    RangeVector p) {
    String[] pranges = p.getRangesAsPlainArray();
    this.itemform = new String[pranges.length + 4];
    this.itemform[0] = dottedclause;
    this.itemform[1] = String.valueOf(pos);
    this.itemform[2] = String.valueOf(i);
    this.itemform[3] = String.valueOf(j);
    System.arraycopy(pranges, 0, this.itemform, 4, pranges.length);
    this.p = p;
  }

  @Override public void setItemform(String[] itemform) {
    this.itemform = itemform;
  }

  @Override public String[] getItemform() {
    return this.itemform;
  }

  @Override public String toString() {
    return "[" + itemform[0] + ", " + itemform[1] + ", <" + itemform[2] + ","
      + itemform[3] + ">, " + p.toString() + "]";
  }

  @Override public boolean equals(Object o) {
    return ArrayUtils.match(this.itemform, ((Item) o).getItemform());
  }

}

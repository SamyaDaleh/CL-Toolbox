package common.lcfrs;

import common.ArrayUtils;
import common.Item;

/** An item we get if we have fully seen an active item. It is used to move the
 * dot further in active items. */
public class SrcgEarleyPassiveItem implements Item {

  String[] itemform;
  RangeVector p;

  /** Initialize the passive item with a nonterminal and a range vector. */
  public SrcgEarleyPassiveItem(String nt, RangeVector p) {
    String[] pranges = p.getRangesAsPlainArray();
    this.itemform = new String[pranges.length + 1];
    this.itemform[0] = nt;
    System.arraycopy(pranges, 0, this.itemform, 1, pranges.length);
    this.p = p;
  }

  @Override public void setItemform(String[] itemform) {
    this.itemform = itemform;
  }

  @Override public String[] getItemform() {
    return this.itemform;
  }

  @Override public String toString() {
    return "[" + itemform[0] + ", " + p.toString() + "]";
  }

  @Override public boolean equals(Object o) {
    return ArrayUtils.match(this.itemform, ((Item) o).getItemform());
  }

}

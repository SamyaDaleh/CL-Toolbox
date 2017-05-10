package common.cfg;

import common.ArrayUtils;
import common.Item;

/** Item of length 3 used for Earley parsing. */
public class CfgDottedItem implements Item {
  String[] itemform;

  /** Item is constructed by a dotted item (not checked) and indices
   * representing its range. */
  public CfgDottedItem(String ntdot, int from, int to) {
    itemform = new String[] {ntdot, String.valueOf(from), String.valueOf(to)};
  }

  @Override public void setItemform(String[] itemform) {
    this.itemform = itemform;
  }

  @Override public String[] getItemform() {
    return itemform;
  }

  @Override public String toString() {
    return ArrayUtils.toString(this.itemform);
  }

  @Override public boolean equals(Object o) {
    return ArrayUtils.match(this.itemform, ((Item) o).getItemform());
  }
}

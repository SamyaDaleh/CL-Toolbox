package common.cfg;

import common.ArrayUtils;
import common.Item;

/** Item of length 2 used for TopDown and ShiftReduce parsing. */
public class CfgItem implements Item {
  private String[] itemform;

  /** Item is constructed from a string and a span. */
  public CfgItem(String ntdot, int span) {
    itemform = new String[] {ntdot, String.valueOf(span)};
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

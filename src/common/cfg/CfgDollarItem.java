package common.cfg;

import common.ArrayUtils;
import common.Item;

/** Item used for LeftCorner parsing that has length 3. */
public class CfgDollarItem implements Item {
  String[] itemform;

  /**
   * Item is constructed by three stack strings.
   */
  public CfgDollarItem(String stackcompleted, String stackpredicted,
    String stacklhs) {
    itemform = new String[] {stackcompleted, stackpredicted, stacklhs};
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

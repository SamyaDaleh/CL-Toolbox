package common;

import common.ArrayUtils;
import common.Item;

/**
 * Abstract class that holds the functions commonly used by all items.
 */
public abstract class AbstractItem implements Item {

  protected String[] itemform;

  @Override public String[] getItemform() {
    return this.itemform;
  }

  @Override public boolean equals(Object o) {
    return ArrayUtils.match(this.itemform, ((Item) o).getItemform());
  }

  @Override public String toString() {
    return ArrayUtils.toString(this.itemform);
  }

}

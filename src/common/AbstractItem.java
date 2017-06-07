package common;

import common.ArrayUtils;
import common.Item;

/**
 * Abstract class that holds the functions commonly used by all items.
 */
public abstract class AbstractItem implements Item {

  protected String[] itemForm;

  @Override public String[] getItemform() {
    return this.itemForm;
  }

  @Override public boolean equals(Object o) {
    return ArrayUtils.match(this.itemForm, ((Item) o).getItemform());
  }

  @Override public String toString() {
    return ArrayUtils.toString(this.itemForm);
  }

}

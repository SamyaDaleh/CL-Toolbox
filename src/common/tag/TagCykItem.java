package common.tag;

import common.ArrayUtils;
import common.Item;

/** Item of length 6 used by TAG CYK parsing. */
public class TagCykItem implements Item {
  private String[] itemform;

  /** Constructor that replaces the foot node indices by '-' if they are
   * null. */
  public TagCykItem(String tree, String p, int i, Integer f1, Integer f2,
    int j) {
    String footfrom = (f1 == null) ? "-" : String.valueOf(f1);
    String footto = (f2 == null) ? "-" : String.valueOf(f2);
    itemform = new String[] {tree, p, String.valueOf(i), footfrom, footto,
      String.valueOf(j)};
  }

  @Override public void setItemform(String[] itemform) {
    this.itemform = itemform;
  }

  @Override public String[] getItemform() {
    return this.itemform;
  }

  @Override public String toString() {
    return ArrayUtils.toString(this.itemform);
  }

  @Override public boolean equals(Object o) {
    return ArrayUtils.match(this.itemform, ((Item) o).getItemform());
  }

}

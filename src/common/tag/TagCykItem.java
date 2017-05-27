package common.tag;

import common.AbstractItem;
import common.Item;

/** Item of length 6 used by TAG CYK parsing. */
public class TagCykItem extends AbstractItem implements Item {

  /** Constructor that replaces the foot node indices by '-' if they are
   * null. */
  public TagCykItem(String tree, String p, int i, Integer f1, Integer f2,
    int j) {
    String footfrom = (f1 == null) ? "-" : String.valueOf(f1);
    String footto = (f2 == null) ? "-" : String.valueOf(f2);
    itemform = new String[] {tree, p, String.valueOf(i), footfrom, footto,
      String.valueOf(j)};
  }

}

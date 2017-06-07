package common.cfg;

import common.AbstractItem;
import common.Item;

/** Item of length 3 used for Earley parsing. */
public class CfgDottedItem extends AbstractItem implements Item {

  /** Item is constructed by a dotted item (not checked) and indices
   * representing its range. */
  public CfgDottedItem(String ntDot, int from, int to) {
    itemForm = new String[] {ntDot, String.valueOf(from), String.valueOf(to)};
  }

}

package common.cfg;

import common.AbstractItem;
import common.Item;

/** Item of length 2 used for TopDown and ShiftReduce parsing. */
public class CfgItem extends AbstractItem implements Item {

  /** Item is constructed from a string and a span. */
  public CfgItem(String ntdot, int span) {
    itemform = new String[] {ntdot, String.valueOf(span)};
  }

  /** Constructor with two integers used for CYK parsing. Now Item has length
   * 3. */
  public CfgItem(String lhs, int i, int j) {
    itemform = new String[] {lhs, String.valueOf(i), String.valueOf(j)};
  }

}

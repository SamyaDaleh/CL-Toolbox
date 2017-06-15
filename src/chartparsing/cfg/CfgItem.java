package chartparsing.cfg;

import chartparsing.AbstractItem;
import chartparsing.Item;

/** Item of length 2 used for TopDown and ShiftReduce parsing. */
public class CfgItem extends AbstractItem implements Item {

  /** Item is constructed from a string and a span. */
  public CfgItem(String ntDot, int span) {
    itemForm = new String[] {ntDot, String.valueOf(span)};
  }

  /** Constructor with two integers used for CYK parsing. Now Item has length
   * 3. */
  public CfgItem(String lhs, int i, int j) {
    itemForm = new String[] {lhs, String.valueOf(i), String.valueOf(j)};
  }

  /** Like syntactic sugar, spares me some conversions. */
  CfgItem(String string, String i, String l) {
    itemForm = new String[] {string, i, l};
  }

}

package chartparsing.cfg;

import chartparsing.AbstractItem;
import chartparsing.Item;

/** Item used for LeftCorner parsing that has length 3. */
public class CfgDollarItem extends AbstractItem implements Item {

  /**
   * Item is constructed by three stack strings.
   */
  public CfgDollarItem(String stackCompleted, String stackPredicted,
    String stackLhs) {
    itemForm = new String[] {stackCompleted, stackPredicted, stackLhs};
  }

}

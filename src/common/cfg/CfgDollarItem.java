package common.cfg;

import common.AbstractItem;
import common.Item;

/** Item used for LeftCorner parsing that has length 3. */
public class CfgDollarItem extends AbstractItem implements Item {

  /**
   * Item is constructed by three stack strings.
   */
  public CfgDollarItem(String stackcompleted, String stackpredicted,
    String stacklhs) {
    itemform = new String[] {stackcompleted, stackpredicted, stacklhs};
  }

}

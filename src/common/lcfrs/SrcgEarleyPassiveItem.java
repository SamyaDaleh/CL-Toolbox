package common.lcfrs;

import common.AbstractItem;
import common.Item;

/** An item we get if we have fully seen an active item. It is used to move the
 * dot further in active items. */
public class SrcgEarleyPassiveItem extends AbstractItem implements Item {

  public SrcgEarleyPassiveItem(String nt,
    String[] newvector) {
    this.itemform = new String[newvector.length + 1];
    this.itemform[0] = nt;
    System.arraycopy(newvector, 0, this.itemform, 1, newvector.length);
  }

  @Override public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("[").append(itemform[0]);
    for (int i = 0; i*2+2 <itemform.length; i++){
        builder.append(", ");
      builder.append("<").append(itemform[i * 2 + 1]).append(",")
          .append(itemform[i * 2 + 2]).append(">");
    }
    builder.append("]");
    return builder.toString();
  }

}

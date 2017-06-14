package chartparsing.lcfrs;

import chartparsing.AbstractItem;
import chartparsing.Item;

/** An item we get if we have fully seen an active item. It is used to move the
 * dot further in active items. */
public class SrcgEarleyPassiveItem extends AbstractItem implements Item {

  public SrcgEarleyPassiveItem(String nt,
    String[] newVector) {
    this.itemForm = new String[newVector.length + 1];
    this.itemForm[0] = nt;
    System.arraycopy(newVector, 0, this.itemForm, 1, newVector.length);
  }

  @Override public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("[").append(itemForm[0]);
    for (int i = 0; i*2+2 <itemForm.length; i++){
        builder.append(", ");
      builder.append("<").append(itemForm[i * 2 + 1]).append(",")
          .append(itemForm[i * 2 + 2]).append(">");
    }
    builder.append("]");
    return builder.toString();
  }

}

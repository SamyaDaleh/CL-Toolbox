package common.lcfrs;

import common.AbstractItem;
import common.Item;

/** Item for CYK for sRCG, consisting of a nonterminal and an arbitrary long
 * range vector of at least length 2*1. */
public class SrcgCykItem extends AbstractItem implements Item {

  public SrcgCykItem(String nt, int i, int j) {
    this.itemForm = new String[3];
    this.itemForm[0] = nt;
    this.itemForm[1] = String.valueOf(i);
    this.itemForm[2] = String.valueOf(j);
  }

  public SrcgCykItem(String nt, Integer... ranges) {
    this.itemForm = new String[ranges.length + 1];
    this.itemForm[0] = nt;
    for (int i = 0; i < ranges.length; i++) {
      this.itemForm[i + 1] = String.valueOf(ranges[i]);
    }
  }

  @Override public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("[").append(itemForm[0]).append(", (");
    for (int i = 0; i * 2 + 2 < itemForm.length; i++) {
      if (i > 0) {
        builder.append(", ");
      }
      builder.append("<").append(itemForm[i * 2 + 1]).append(",")
        .append(itemForm[i * 2 + 2]).append(">");
    }
    builder.append(")]");
    return builder.toString();
  }
}

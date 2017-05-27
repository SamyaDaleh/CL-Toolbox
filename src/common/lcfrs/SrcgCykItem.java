package common.lcfrs;

import common.AbstractItem;
import common.Item;

/** Item for CYK for sRCG, consisting of a nonterminal and an arbitrary long
 * range vector of at least length 2*1. */
public class SrcgCykItem extends AbstractItem implements Item {

  public SrcgCykItem(String nt, int i, int j) {
    this.itemform = new String[3];
    this.itemform[0] = nt;
    this.itemform[1] = String.valueOf(i);
    this.itemform[2] = String.valueOf(j);
  }

  public SrcgCykItem(String nt, Integer... ranges) {
    this.itemform = new String[ranges.length + 1];
    this.itemform[0] = nt;
    for (int i = 0; i < ranges.length; i++) {
      this.itemform[i + 1] = String.valueOf(ranges[i]);
    }
  }

  @Override public String toString() {

    StringBuilder builder = new StringBuilder();
    builder.append("[").append(itemform[0]).append(", (");
    for (int i = 0; i * 2 + 2 < itemform.length; i++) {
      if (i > 0) {
        builder.append(", ");
      }
      builder.append("<").append(itemform[i * 2 + 1]).append(",")
        .append(itemform[i * 2 + 2]).append(">");
    }
    builder.append(")]");
    return builder.toString();

  }
}

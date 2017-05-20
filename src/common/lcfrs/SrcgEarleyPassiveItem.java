package common.lcfrs;

import common.ArrayUtils;
import common.Item;

/** An item we get if we have fully seen an active item. It is used to move the
 * dot further in active items. */
public class SrcgEarleyPassiveItem implements Item {

  private String[] itemform;
  private RangeVector p;

  /** Initialize the passive item with a nonterminal and a range vector. */
  public SrcgEarleyPassiveItem(String nt, RangeVector p) {
    String[] pranges = p.getRangesAsPlainArray();
    this.itemform = new String[pranges.length + 1];
    this.itemform[0] = nt;
    System.arraycopy(pranges, 0, this.itemform, 1, pranges.length);
    this.p = p;
  }

  public SrcgEarleyPassiveItem(String nt,
    String[] newvector) {
    this.itemform = new String[newvector.length + 1];
    this.itemform[0] = nt;
    System.arraycopy(newvector, 0, this.itemform, 1, newvector.length);
  }

  @Override public void setItemform(String[] itemform) {
    this.itemform = itemform;
  }

  @Override public String[] getItemform() {
    return this.itemform;
  }

  @Override public String toString() {
    if (p != null) {
      return "[" + itemform[0] + ", " + p.toString() + "]";
    } else {
      StringBuilder builder = new StringBuilder();
      builder.append("[" + itemform[0]);
      for (int i = 0; i*2+2 <itemform.length; i++){
          builder.append(", ");
        builder.append("<" + itemform[i*2+1] + "," + itemform[i*2+2] + ">");
      }
      builder.append("]");
      return builder.toString();
    }
  }

  @Override public boolean equals(Object o) {
    return ArrayUtils.match(this.itemform, ((Item) o).getItemform());
  }

}

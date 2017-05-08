package common.tag;

import java.util.Arrays;
import java.util.List;

import common.Item;

/** Item of length 8 used by TAG Earley parsing. */
public class TagEarleyItem implements Item {
  List<String> itemform;

  /** Constructor that replaces the foot node indices by '-' if they are
   * null. */
  public TagEarleyItem(String tree, String p, String pos, int i, Integer j,
    Integer k, int l, boolean adj) {
    String footfrom = (j == null) ? "-" : String.valueOf(j);
    String footto = (k == null) ? "-" : String.valueOf(k);
    String adjrep = (adj) ? "1" : "0";
    itemform = Arrays.asList(tree, p, pos, String.valueOf(i), footfrom, footto,
      String.valueOf(l), adjrep);
  }

  @Override public void setItemform(List<String> itemform) {
    this.itemform = itemform;
  }

  @Override public List<String> getItemform() {
    return this.itemform;
  }

  @Override public String toString() {
    StringBuilder representation = new StringBuilder();
    representation.append("[" + itemform.get(0) + "," + itemform.get(1) + ","
      + itemform.get(2) + "," + itemform.get(3) + "," + itemform.get(4) + ","
      + itemform.get(5) + "," + itemform.get(6) + "," + itemform.get(7) + "]");
    return representation.toString();
  }

  @Override public boolean equals(Object o) {
    Item item2 = (Item) o;
    try {
      if (this.getItemform().get(0).equals(item2.getItemform().get(0))
        && this.getItemform().get(1).equals(item2.getItemform().get(1))
        && this.getItemform().get(2).equals(item2.getItemform().get(2))
        && this.getItemform().get(3).equals(item2.getItemform().get(3))
        && this.getItemform().get(4).equals(item2.getItemform().get(4))
        && this.getItemform().get(5).equals(item2.getItemform().get(5))
        && this.getItemform().get(6).equals(item2.getItemform().get(6))
        && this.getItemform().get(7).equals(item2.getItemform().get(7)))
        return true;
    } catch (NullPointerException e) {
      return false;
    }
    return false;

  }

}

package common.tag;

import java.util.Arrays;
import java.util.List;

import common.Item;

public class TagItem implements Item {
  List<String> itemform;

  public TagItem(String auxtree, String node, int i, Integer i2, Integer j, int j2) {
    String footfrom = (i2 == null)? "-" : String.valueOf(i2);
    String footto = (j == null)? "-" : String.valueOf(j);
    itemform = Arrays.asList(auxtree, node, String.valueOf(i),
      footfrom, footto, String.valueOf(j2));
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
      + itemform.get(5) + "]");
    return representation.toString();
  }

  @Override public boolean equals(Object o) {
    Item item2 = (Item) o;
    if (this.getItemform().get(0).equals(item2.getItemform().get(0))
      && this.getItemform().get(1).equals(item2.getItemform().get(1))
      && this.getItemform().get(2).equals(item2.getItemform().get(2))
      && this.getItemform().get(3).equals(item2.getItemform().get(3))
      && this.getItemform().get(4).equals(item2.getItemform().get(4))
      && this.getItemform().get(5).equals(item2.getItemform().get(5)))
      return true;
    return false;

  }

}

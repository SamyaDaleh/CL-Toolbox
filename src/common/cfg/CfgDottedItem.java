package common.cfg;

import java.util.Arrays;
import java.util.List;

import common.Item;

public class CfgDottedItem implements Item {
	List<String> itemform;

	public CfgDottedItem(String ntdot, int from, int to) {
		itemform = Arrays.asList(ntdot, String.valueOf(from), String.valueOf(to));
	}

	@Override
	public void setItemform(List<String> itemform) {
		this.itemform = itemform;
	}

	@Override
	public List<String> getItemform() {
		return itemform;
	}
  
  @Override
  public String toString() {
    StringBuilder representation = new StringBuilder();
    representation.append("[" + itemform.get(0) + "," + itemform.get(1) + "," + itemform.get(2) + "]");
    return representation.toString();
  }
  
  @Override
  public boolean equals(Object o) {
    Item item2 = (Item) o;
    if (this.getItemform().get(0).equals(item2.getItemform().get(0))
        && this.getItemform().get(1).equals(item2.getItemform().get(1))
        && this.getItemform().get(2).equals(item2.getItemform().get(2))) return true;
    return false;
    
  }
}

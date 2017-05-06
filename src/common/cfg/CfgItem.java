package common.cfg;

import java.util.Arrays;
import java.util.List;

import common.Item;

public class CfgItem implements Item {
	List<String> itemform;

	public CfgItem(String ntdot, int span) {
		itemform = Arrays.asList(ntdot, String.valueOf(span));
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
		representation.append("[" + itemform.get(0) + "," + itemform.get(1) + "]");
		return representation.toString();
	}
	
	@Override
	public boolean equals(Object o) {
		Item item2 = (Item) o;
		if (this.getItemform().get(0).equals(item2.getItemform().get(0))
				&& this.getItemform().get(1).equals(item2.getItemform().get(1))) return true;
		return false;
		
	}
}

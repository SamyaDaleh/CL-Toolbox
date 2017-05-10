package common.cfg;

import java.util.List;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import common.Item;
import common.cfg.CfgItem;

public class CfgItemTest {
  public static void main(String[] args) {
		Item item1 = new CfgItem("S", 0);
		Item item2 = new CfgItem("S", 0);

		if (item1.equals(item2)) {
			System.out.println("Item equals success");
		} else
			System.out.println("Item equals fail");
		// test set contains item
		Set<Item> itemset = new HashSet<Item>();
		itemset.add(item1);
	/*	if (itemset.contains(item2)){ // fails, try second approach
			System.out.println("Item contains success");
		} else {
			System.out.println("Item contains fail");
		} //*/
		List<Item> itemssetlist = new ArrayList<Item>(itemset);
		if (itemssetlist.contains(item2)){
			System.out.println("Item contains success");
		} else {
			System.out.println("Item contains fail");
		}
		// test list contains Item
		List<Item> itemlist = new LinkedList<Item>();
		itemlist.add(item1);
		if (itemlist.contains(item2)){
			System.out.println("Item contains success");
		} else {
			System.out.println("Item contains fail");
		}
	}
}

package chartparsing.cfg;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import chartparsing.Item;
import chartparsing.cfg.CfgItem;

public class CfgItemTest {
  @Test public void testItemOperations() {
		Item item1 = new CfgItem("S", 0);
		Item item2 = new CfgItem("S", 0);

		assertTrue(item1.equals(item2));
		// test set contains item
		Set<Item> itemset = new HashSet<Item>();
		itemset.add(item1);

		List<Item> itemssetlist = new ArrayList<Item>(itemset);
		assertTrue(itemssetlist.contains(item2));
		// test list contains Item
		List<Item> itemlist = new LinkedList<Item>();
		itemlist.add(item1);
		assertTrue(itemlist.contains(item2));
	}
}

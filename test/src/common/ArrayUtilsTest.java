package common;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import chartparsing.DeductionItem;
import chartparsing.Item;

public class ArrayUtilsTest {

  @Test public void testItemEquals() {
    Item item1 = new DeductionItem("a", ".1", "la", "1", "?", "?", "3", "0");
    Item item2 = new DeductionItem("a", ".1", "la", "1", "-", "-", "3");
    Item item3 = new DeductionItem("a", ".1", "la", "1", "-", "-", "3", "0");
    assertTrue(!item1.equals(item2));
    assertTrue(item1.equals(item3));
  }

  @Test public void testListContainsItem() {
    Item item1 = new DeductionItem("a", ".1", "la", "1", "?", "?", "3", "0");
    Item item2 = new DeductionItem("a", ".1", "la", "1", "-", "-", "3", "0");

    List<Item> testchart = new ArrayList<Item>();
    testchart.add(item2);
    assertTrue(testchart.contains(item1));
    assertTrue(testchart.indexOf(item1) == 0);
  }

}

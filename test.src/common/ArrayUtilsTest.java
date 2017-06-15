package common;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import chartparsing.Item;
import chartparsing.tag.TagEarleyItem;

public class ArrayUtilsTest {
  
  @Test public void testItemEquals() {
    Item item1 = new TagEarleyItem("a", ".1", "la", 1, "?", "?", 3, false);
    Item item2 = new TagEarleyItem("a", ".1", "la", 1, (Integer) null, null, 3, false);
    assertTrue(item1.equals(item2));
  }
  @Test public void testListContainsItem() {
    Item item1 = new TagEarleyItem("a", ".1", "la", 1, "?", "?", 3, false);
    Item item2 = new TagEarleyItem("a", ".1", "la", 1, (Integer) null, null, 3, false);
    
    List<Item> testchart = new ArrayList<Item>();
    testchart.add(item2);
    assertTrue(testchart.contains(item1));
    assertTrue(testchart.indexOf(item1) == 0);
  }

}

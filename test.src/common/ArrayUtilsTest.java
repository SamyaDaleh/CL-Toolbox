package common;

import common.tag.TagEarleyItem;

public class ArrayUtilsTest {
  public static void main(String[] args) {
    Item item1 = new TagEarleyItem("a", ".1", "la", 1, "?", "?", 3, false);
    Item item2 = new TagEarleyItem("a", ".1", "la", 1, (Integer) null, null, 3, false);
    if (item1.equals(item2)) {
      System.out.println("Item1 equals Item2 true");
    } else{
    System.out.println("Item1 equals Item2 false");
    }
  }

}

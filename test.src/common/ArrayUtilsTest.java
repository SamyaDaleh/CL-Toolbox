package common;

import java.util.LinkedList;
import java.util.List;

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
    
    List<Item> testchart = new LinkedList<Item>();
    testchart.add(item2);
    if (testchart.contains(item1)){
      System.out.println("list contains item - good");
    } else {
      System.out.println("list doesn't contains item - bad");
    }
    if (testchart.indexOf(item1) == 0){
      System.out.println("list contains item - good");
    } else {
      System.out.println("list doesn't contains item - bad");
    }
  }

}

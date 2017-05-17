package common.tag;

import java.text.ParseException;

import common.tag.Tree;

public class TreeTest {
  public static void main(String[] args) throws ParseException {
   Tree tree = new Tree("(T (B (T a ε) ) (B T*))");
   if (tree.getRoot().label.equals("T") && tree.getFoot().label.equals("T")
       && tree.getFoot().gornaddress.equals(".2.1")) {
     System.out.println("Tree looks fine");
     System.out.println(tree);
   } else {
     System.out.println("Something went wrong when parsing the tree");
     System.out.println(tree);
   }
  }

}

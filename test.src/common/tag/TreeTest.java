package common.tag;

import java.text.ParseException;

import common.tag.Tree;
import gui.DisplayTree;

public class TreeTest {
  public static void main(String[] args) throws ParseException {
   Tree tree = new Tree("(T_NA (B (T a ε) ) (B_OA T*))");
   if (tree.getRoot().label.equals("T") && tree.getFoot().label.equals("T")
       && tree.getFoot().gornaddress.equals(".2.1")) {
     System.out.println("Tree looks fine");
     System.out.println(tree);
   } else {
     System.out.println("Something went wrong when parsing the tree");
     System.out.println(tree);
   }
   if(tree.getHeight() == 4 ){
     System.out.println("Correct height");
   } else {
     System.out.println("Wrong height: " + String.valueOf(tree.getHeight()));
   }
   if(tree.getWidth() == 2 ){
     System.out.println("Correct width");
   } else {
     System.out.println("Wrong width: " + String.valueOf(tree.getHeight()));
   }
   DisplayTree.main(new String[]{tree.toString()});
  }

}

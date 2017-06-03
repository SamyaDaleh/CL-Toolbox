package gui;

import static org.junit.Assert.assertTrue;

import java.text.ParseException;

import org.junit.Test;

import common.tag.Tree;

public class DisplayTreeTest {
  @Test public void testDisplayTree() throws ParseException {
    Tree tree = new Tree("(T_NA (B (T a ε) ) (B_OA T*))");
    DisplayTree.main(new String[] {tree.toString()});
    assertTrue(true);
  }
}

package common.tag;

import java.text.ParseException;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import common.tag.Tree;
import gui.DisplayTree;

public class TreeTest {
  @Test public void testTreeFunctions() throws ParseException {
    Tree tree = new Tree("(T_NA (B (T a ε) ) (B_OA T*))");
    assertTrue(tree.getRoot().label.equals("T"));
    assertTrue(tree.getFoot().label.equals("T"));
    assertTrue(tree.getFoot().gornaddress.equals(".2.1"));

    assertTrue(tree.getHeight() == 4);

    assertTrue(tree.getWidth() == 2);

    DisplayTree.main(new String[] {tree.toString()});
  }

  @Test public void testSubstitution() throws ParseException {
    Tree tree = new Tree("(S NP (VP (V walks) ) )");
    Tree initree = new Tree("(NP Mary)");
    Tree substres = tree.substitute(".1", initree);
    assertEquals("(S (NP (Mary ))(VP (V (walks ))))", substres.toString());
  }

  @Test public void testAdjoin() throws ParseException {
    Tree tree = new Tree("(S (NP Mary) (VP (V walks) ) )");
    Tree auxtree = new Tree("(VP (ADV sometimes) VP*)");
    Tree substres = tree.adjoin(".2", auxtree);
    assertEquals("(S (NP (Mary ))(VP (ADV (sometimes ))(VP (V (walks )))))",
      substres.toString());
  }

}

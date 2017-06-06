package common.tag;

import java.text.ParseException;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import common.tag.Tree;

public class TreeTest {
  @Test public void testTreeFunctions() throws ParseException {
    Tree tree = new Tree("(T_NA (B (T a ε) ) (B_OA T*))");
    assertTrue(tree.getRoot().getLabel().equals("T"));
    assertTrue(tree.getFoot().getLabel().equals("T"));
    assertTrue(tree.getFoot().getGornaddress().equals(".2.1"));
    assertTrue(tree.getHeight() == 4);
    assertTrue(tree.getWidth() == 2);
  }

  @Test public void testSubstitute() throws ParseException {
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

    Tree beta = new Tree("(T a T*)");
    Tree adjtree = beta.adjoin("", beta);
    assertEquals("(T (a )(T (a )(T* )))", adjtree.toString());
  }

  @Test public void testTreeWithCrossingEdges() throws ParseException {
    Tree tree1 = new Tree(
      "(S (Comp (dat<0> ))(VP (NP (Jan<1> ))(VP (NP (Piet<2> ))(VP (NP (de-kinderen<3> ))(V (zwemmen<6> )))(V (helpen<5> )))(V (zag<4> ))))");
    /* TreeWithCrossingEdges tree2 = new TreeWithCrossingEdges(
     * "(S (Comp <0>) (VP (NP <1>) (VP (NP <2>) (VP (NP <3>) (V <6>)) (V <5>)) (V <4>)))"
     * , "dat Jan Piet de_kinderen zag helpen zwemmen");
     * assertEquals(tree1.toString(), tree2.toString()); // */
    assertEquals(
      "(S (Comp (dat<0> ))(VP (NP (Jan<1> ))(VP (NP (Piet<2> ))(VP (NP (de-kinderen<3> ))(V (zwemmen<6> )))(V (helpen<5> )))(V (zag<4> ))))",
      tree1.toString());
  }
}

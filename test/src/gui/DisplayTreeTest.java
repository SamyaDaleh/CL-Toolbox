package gui;

import static org.junit.Assert.assertTrue;

import java.text.ParseException;

import org.junit.Test;

import common.tag.Tree;

public class DisplayTreeTest {
  @Test public void testDisplayTree() throws ParseException {
    Tree tree = new Tree("(T_NA (B (T a ε) ) (B_OA T*))");
    new DisplayTree(new String[] {tree.toString()});
    
    Tree beta = new Tree("(T a T*)");
    new DisplayTree(new String[] {beta.toString(), "[β,ε⊤,0,1,2,2]"});
    new DisplayTree(new String[] {beta.toString(), "[β,.2,rb,1,1,2,2,0]"});
    assertTrue(true);
  }

  @Test public void testDisplayTreeWithCrossingEdges() throws ParseException {
    Tree tree = new Tree("(S (Comp (dat<0> ))(VP (NP (Jan<1> ))(VP (NP (Piet<2> ))(VP (NP (de-kinderen<3> ))(V (zwemmen<6> )))(V (helpen<5> )))(V (zag<4> ))))");
    new DisplayTree(new String[] {tree.toString()});
    assertTrue(true);
  }
}

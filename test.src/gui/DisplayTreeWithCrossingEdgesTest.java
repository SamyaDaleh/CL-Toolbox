package gui;

import static org.junit.Assert.assertTrue;

import java.text.ParseException;

import org.junit.Test;

import common.lcfrs.TreeWithCrossingEdges;

public class DisplayTreeWithCrossingEdgesTest {
  @Test public void testDisplayTreeWithCrossingEdges() throws ParseException {
    TreeWithCrossingEdges tree = new TreeWithCrossingEdges("(S (Comp (dat<0> ))(VP (NP (Jan<1> ))(VP (NP (Piet<2> ))(VP (NP (de-kinderen<3> ))(V (zwemmen<6> )))(V (helpen<5> )))(V (zag<4> ))))");
    new DisplayTreeWithCrossingEdges(new String[] {tree.toString()});
    assertTrue(true);
  }
}

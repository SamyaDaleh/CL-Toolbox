package common.lcfrs;

import static org.junit.Assert.assertEquals;

import java.text.ParseException;

import org.junit.Test;

public class TreeWithCrossingEdgesTest {
  @Test public void testTreeWithCrossingEdges() throws ParseException {
    TreeWithCrossingEdges tree1 = new TreeWithCrossingEdges(
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

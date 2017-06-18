package common.lcfrs;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.text.ParseException;

import org.junit.Ignore;
import org.junit.Test;

import common.TestGrammarLibrary;

public class SrcgTest {

  @Test public void testOrder() throws ParseException {
    assertTrue(!TestGrammarLibrary.anbnUnorderedEpsSrcg().isOrdered());
  }

  @Test public void testEmptyProductions() throws ParseException {
    assertTrue(
      TestGrammarLibrary.anbnUnorderedEpsSrcg().hasEpsilonProductions());
  }

  @Test public void testCfgToSrcgConversion() throws ParseException {
    Srcg srcg = new Srcg(TestGrammarLibrary.epsCfg());
    assertEquals("G = <N, T, V, P, S>\n" + "N = {S, A, B, C}\n" + "T = {a, b}\n"
      + "V = {X1, X2, X3}\n" + "P = {A(ε) -> ε, S(ε) -> ε, C(ε) -> ε, "
      + "S(b X1 a X2 b X3) -> A(X1) S(X2) C(X3), A(a) -> ε, "
      + "A(b X1) -> B(X1), B(b) -> ε}\n" + "S = S\n", srcg.toString());
  }

  @Test public void testCfgToSrcgConversion2() throws ParseException {
    Srcg srcg = new Srcg(TestGrammarLibrary.anbnCnfProbCfg());
    assertEquals(
      "G = <N, T, V, P, S>\n" + "N = {S, X1, Y1, Y2}\n" + "T = {a, b}\n"
        + "V = {X2, X3}\n"
        + "P = {Y1(a) -> ε, S(X2 X3) -> Y1(X2) X1(X3), Y2(b) -> ε, "
        + "X1(X2 X3) -> S(X2) Y2(X3), S(X2 X3) -> Y1(X2) Y2(X3)}\n" + "S = S\n",
      srcg.toString());
  }

  @Test public void testSrcgOrdering() throws ParseException {
    assertTrue(!TestGrammarLibrary.unorderedSrcg().isOrdered());
    Srcg srcgOrd = TestGrammarLibrary.unorderedSrcg().getOrderedSrcg();
    assertTrue(srcgOrd.isOrdered());
    assertEquals(
      "G = <N, T, V, P, S>\n" + "N = {S, A, A^<2,1>}\n" + "T = {a, b}\n"
        + "V = {X, Y}\n"
        + "P = {S(X Y) -> A(X,Y), A(X,Y) -> A^<2,1>(X,Y), A(a X,b Y) -> A(X,Y), "
        + "A(a,b) -> ε, A^<2,1>(X,Y) -> A^<2,1>(X,Y), "
        + "A^<2,1>(a X,b Y) -> A(X,Y), A^<2,1>(a,b) -> ε}\n" + "S = S\n",
      srcgOrd.toString());
  }

  @Ignore("under construction") public void testSrcgRemoveEmptyProductions() throws ParseException {
    assertTrue(
      TestGrammarLibrary.withEmptyProductionsSrcg().hasEpsilonProductions());
    Srcg srcgWithoutEmptyProductions = TestGrammarLibrary
      .withEmptyProductionsSrcg().getSrcgWithoutEmptyProductions();
    assertTrue(!srcgWithoutEmptyProductions.hasEpsilonProductions());
    assertEquals(
      "G = <N, T, V, P, S>\n" + "N = {S, A, A^(10), A^(01)}\n" + "T = {a, b}\n"
        + "V = {X, Y}\n" + "P = {}\n" + "S = S\n",
      srcgWithoutEmptyProductions.toString());
  }

}

package common.lcfrs;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.text.ParseException;

import org.junit.Test;

import chartparsing.Deduction;
import chartparsing.ParsingSchema;
import chartparsing.converter.LcfrsToDeductionRulesConverter;
import common.GrammarParser;
import common.TestGrammarLibrary;
import common.lcfrs.util.Order;

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
      "G = <N, T, V, P, S>\n" + "N = {S^" + Order.ORDER_MARKING_LEFT + "1"
        + Order.ORDER_MARKING_RIGHT + ", A^" + Order.ORDER_MARKING_LEFT + "1,2"
        + Order.ORDER_MARKING_RIGHT + ", A^" + Order.ORDER_MARKING_LEFT + "2,1"
        + Order.ORDER_MARKING_RIGHT + "}\n" + "T = {a, b}\n" + "V = {X, Y}\n"
        + "P = {S^" + Order.ORDER_MARKING_LEFT + "1" + Order.ORDER_MARKING_RIGHT
        + "(X Y) -> A^" + Order.ORDER_MARKING_LEFT + "1,2"
        + Order.ORDER_MARKING_RIGHT + "(X,Y), A^" + Order.ORDER_MARKING_LEFT
        + "1,2" + Order.ORDER_MARKING_RIGHT + "(X,Y) -> A^"
        + Order.ORDER_MARKING_LEFT + "2,1" + Order.ORDER_MARKING_RIGHT
        + "(X,Y), " + "A^" + Order.ORDER_MARKING_LEFT + "1,2"
        + Order.ORDER_MARKING_RIGHT + "(a X,b Y) -> A^"
        + Order.ORDER_MARKING_LEFT + "1,2" + Order.ORDER_MARKING_RIGHT
        + "(X,Y), A^" + Order.ORDER_MARKING_LEFT + "1,2"
        + Order.ORDER_MARKING_RIGHT + "(a,b) -> ε, " + "A^"
        + Order.ORDER_MARKING_LEFT + "2,1" + Order.ORDER_MARKING_RIGHT
        + "(Y,X) -> A^" + Order.ORDER_MARKING_LEFT + "1,2"
        + Order.ORDER_MARKING_RIGHT + "(Y,X), A^" + Order.ORDER_MARKING_LEFT
        + "2,1" + Order.ORDER_MARKING_RIGHT + "(b Y,a X) -> A^"
        + Order.ORDER_MARKING_LEFT + "2,1" + Order.ORDER_MARKING_RIGHT
        + "(Y,X), " + "A^" + Order.ORDER_MARKING_LEFT + "2,1"
        + Order.ORDER_MARKING_RIGHT + "(b,a) -> ε}\n" + "S = S^"
        + Order.ORDER_MARKING_LEFT + "1" + Order.ORDER_MARKING_RIGHT + "\n",
      srcgOrd.toString());
  }

  @Test public void testSrcgRemoveEmptyProductions() throws ParseException {
    assertTrue(
      TestGrammarLibrary.withEmptyProductionsSrcg().hasEpsilonProductions());
    Srcg srcgWithoutEmptyProductions = TestGrammarLibrary
      .withEmptyProductionsSrcg().getSrcgWithoutEmptyProductions();
    assertTrue(!srcgWithoutEmptyProductions.hasEpsilonProductions());
    assertEquals("G = <N, T, V, P, S>\n" + "N = {A^10, A^01, A^11, S^1, S'}\n"
      + "T = {a, b}\n" + "V = {X, Y}\n"
      + "P = {S'(X) -> S^1(X), S^1(X) -> A^10(X), S^1(Y) -> A^01(Y), "
      + "S^1(X Y) -> A^11(X,Y), A^10(a) -> ε, A^01(b) -> ε, A^11(a,b) -> ε}\n"
      + "S = S'\n", srcgWithoutEmptyProductions.toString());
  }

  @Test public void testSrcgBinarize() throws ParseException {
    assertTrue(!TestGrammarLibrary.testBinarizationSrcg().isBinarized());
    Srcg binarizedSrcg =
      TestGrammarLibrary.testBinarizationSrcg().getBinarizedSrcg();
    assertTrue(binarizedSrcg.isBinarized());
    assertEquals(
      "G = <N, T, V, P, S>\n" + "N = {S, A, B, C, C1}\n" + "T = {a, b, c}\n"
        + "V = {X, Y, Z, U, V, W}\n"
        + "P = {S(X Y U V) -> A(X,U) C1(Y,V), C1(Y Z,V W) -> B(Y,V) C(Z,W), "
        + "A(a X,a Y) -> A(X,Y), B(b X,b Y) -> B(X,Y), C(c X,c Y) -> C(X,Y), "
        + "A(a,a) -> ε, B(b,b) -> ε, C(c,c) -> ε}\n" + "S = S\n",
      binarizedSrcg.toString());
  }

  @Test public void testRemoveEmptyProductionsForEarley()
    throws IOException, ParseException {
    Srcg srcg =
      GrammarParser.parseSrcgFile("./resources/grammars/anbmcndm.srcg");
    Srcg srcgEpsFree = srcg.getSrcgWithoutEmptyProductions();
    ParsingSchema schema =
      LcfrsToDeductionRulesConverter.srcgToEarleyRules(srcgEpsFree, "a c");
    Deduction deduction = new Deduction();
    assertTrue(deduction.doParse(schema, false));
  }

  @Test public void testRemoveUselessRules() throws ParseException {
    assertTrue(TestGrammarLibrary.testSrcgWUselessRules().hasUselessRules());
    Srcg srcgWOUselessRules =
      TestGrammarLibrary.testSrcgWUselessRules().getSrcgWithoutUselessRules();
    assertTrue(!srcgWOUselessRules.hasUselessRules());
    assertEquals("G = <N, T, V, P, S>\n" + "N = {S, A}\n" + "T = {a, b}\n"
      + "V = {X, Y, Z, U, V, W}\n" + "P = {S(X Y Z U V W) -> A(X,U), "
      + "A(a X,a Y) -> A(X,Y), A(b X,b Y) -> B(X,Y), A(a,a) -> ε}\n"
      + "S = S\n", srcgWOUselessRules.toString());
  }

}

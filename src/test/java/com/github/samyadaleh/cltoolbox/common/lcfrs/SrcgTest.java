package com.github.samyadaleh.cltoolbox.common.lcfrs;

import com.github.samyadaleh.cltoolbox.chartparsing.Deduction;
import com.github.samyadaleh.cltoolbox.chartparsing.ParsingSchema;
import com.github.samyadaleh.cltoolbox.chartparsing.converter.lcfrs.LcfrsToEarleyRulesConverter;
import com.github.samyadaleh.cltoolbox.common.GrammarLoader;
import com.github.samyadaleh.cltoolbox.common.cfg.Cfg;
import com.github.samyadaleh.cltoolbox.common.lcfrs.util.Order;
import com.github.samyadaleh.cltoolbox.common.tag.Tag;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.text.ParseException;
import java.util.Objects;

import static org.junit.Assert.*;

public class SrcgTest {

  @Test public void testOrder() throws FileNotFoundException, ParseException {
    Srcg srcg = GrammarLoader.readSrcg("anbnunorderedeps.srcg");
    assertFalse(srcg.isOrdered());
  }

  @Test public void testEmptyProductions()
      throws FileNotFoundException, ParseException {
    Srcg srcg = GrammarLoader.readSrcg("anbnunorderedeps.srcg");
    assertTrue(srcg.hasEpsilonProductions());
  }

  @Test public void testCfgToSrcgConversion()
      throws ParseException, FileNotFoundException {
    Cfg cfg = GrammarLoader.readCfg("eps.cfg");
    Srcg srcg = new Srcg(cfg);
    assertEquals("G = <N, T, V, P, S>\n" + "N = {S, A, B, C}\n" + "T = {a, b}\n"
        + "V = {X1, X2, X3}\n" + "P = {A(ε) -> ε, S(ε) -> ε, C(ε) -> ε, "
        + "S(b X1 a X2 b X3) -> A(X1) S(X2) C(X3), A(a) -> ε, "
        + "A(b X1) -> B(X1), B(b) -> ε}\n" + "S = S\n", srcg.toString());
  }

  @Test public void testCfgToSrcgConversion2()
      throws ParseException, FileNotFoundException {
    Cfg cfg = GrammarLoader.readCfg("anbncnfprob.cfg");
    Srcg srcg = new Srcg(cfg);
    assertEquals(
        "G = <N, T, V, P, S>\n" + "N = {S, X1, Y1, Y2}\n" + "T = {a, b}\n"
            + "V = {X2, X3}\n"
            + "P = {Y1(a) -> ε, S(X2 X3) -> Y1(X2) X1(X3), Y2(b) -> ε, "
            + "X1(X2 X3) -> S(X2) Y2(X3), S(X2 X3) -> Y1(X2) Y2(X3)}\n"
            + "S = S\n", srcg.toString());
  }

  @Test public void testSrcgOrdering()
      throws ParseException, FileNotFoundException {
    Srcg srcg = GrammarLoader.readSrcg("unordered.srcg");
    assertFalse(srcg.isOrdered());
    Srcg srcgOrd = srcg.getOrderedSrcg();
    assertTrue(srcgOrd.isOrdered());
    assertEquals(
        "G = <N, T, V, P, S>\n" + "N = {S^" + Order.ORDER_MARKING_LEFT + "1"
            + Order.ORDER_MARKING_RIGHT + ", A^" + Order.ORDER_MARKING_LEFT
            + "1,2" + Order.ORDER_MARKING_RIGHT + ", A^"
            + Order.ORDER_MARKING_LEFT + "2,1" + Order.ORDER_MARKING_RIGHT
            + "}\n" + "T = {a, b}\n" + "V = {X, Y}\n" + "P = {S^"
            + Order.ORDER_MARKING_LEFT + "1" + Order.ORDER_MARKING_RIGHT
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

  @Test public void testSrcgRemoveEmptyProductions()
      throws ParseException, FileNotFoundException {
    Srcg srcg = GrammarLoader.readSrcg("withemptyproductions.srcg");
    assertTrue(srcg.hasEpsilonProductions());
    Srcg srcgWithoutEmptyProductions = srcg.getSrcgWithoutEmptyProductions();
    assertFalse(srcgWithoutEmptyProductions.hasEpsilonProductions());
    assertEquals("G = <N, T, V, P, S>\n" + "N = {A^10, A^01, A^11, S^1, S'}\n"
        + "T = {a, b}\n" + "V = {X, Y}\n"
        + "P = {S'(X) -> S^1(X), S^1(X) -> A^10(X), S^1(Y) -> A^01(Y), "
        + "S^1(X Y) -> A^11(X,Y), A^10(a) -> ε, A^01(b) -> ε, A^11(a,b) -> ε}\n"
        + "S = S'\n", srcgWithoutEmptyProductions.toString());
  }

  @Test public void testSrcgRemoveEmptyProductionsEmptyWord()
      throws ParseException, FileNotFoundException {
    Cfg cfg = GrammarLoader.readCfg("emptyword.cfg");
    Srcg srcg = new Srcg(cfg);
    assertTrue(srcg.hasEpsilonProductions());
    Srcg srcgWithoutEmptyProductions = srcg.getSrcgWithoutEmptyProductions();
    assertFalse(srcgWithoutEmptyProductions.hasEpsilonProductions());
    assertEquals("G = <N, T, V, P, S>\n" + "N = {N0^1, S'}\n" + "T = {t0, t1}\n"
        + "V = {Z1}\n"
        + "P = {S'(ε) -> ε, S'(Z1) -> N0^1(Z1), N0^1(t0 t1) -> ε}\n"
        + "S = S'\n", srcgWithoutEmptyProductions.toString());
  }

  @Test public void testSrcgBinarize()
      throws ParseException, FileNotFoundException {
    Srcg srcg = GrammarLoader.readSrcg("testbinarization.srcg");
    assertFalse(srcg.isBinarized());
    Srcg binarizedSrcg = srcg.getBinarizedSrcg();
    assertTrue(binarizedSrcg.isBinarized());
    assertEquals(
        "G = <N, T, V, P, S>\n" + "N = {S, A, B, C, A1}\n" + "T = {a, b, c}\n"
            + "V = {X, Y, Z, U, V, W}\n"
            + "P = {S(X Y U V) -> A(X,U) A1(Y,V), A1(Y Z,V W) -> B(Y,V) C(Z,W), "
            + "A(a X,a Y) -> A(X,Y), B(b X,b Y) -> B(X,Y), C(c X,c Y) -> C(X,Y), "
            + "A(a,a) -> ε, B(b,b) -> ε, C(c,c) -> ε}\n" + "S = S\n",
        binarizedSrcg.toString());
  }

  @Test public void testSrcgOptimalBinarize()
      throws ParseException, FileNotFoundException {
    Srcg srcg = GrammarLoader.readSrcg("testoptimalbinarization.srcg");
    assertFalse(srcg.isBinarized());
    Srcg binarizedSrcg = srcg.getBinarizedSrcg();
    assertTrue(binarizedSrcg.isBinarized());
    assertEquals("G = <N, T, V, P, S>\n" + "N = {S, A, B, C, D, D1}\n"
        + "T = {a, c, d}\n" + "V = {X, Y, Z, U}\n"
        + "P = {S(X Y Z) -> A(X,Y,Z), A(a X,c Z,d U) -> D(U) D1(X,Z), "
        + "D1(X Y,Z) -> B(X) C(Y,Z), B(a) -> ε, C(c,c) -> ε, D(d) -> ε}\n"
        + "S = S\n", binarizedSrcg.toString());
  }

  @Test public void testSrcgOptimostBinarize()
      throws ParseException, FileNotFoundException {
    Srcg srcg = GrammarLoader.readSrcg("testoptimostbinarization.srcg");
    assertFalse(srcg.isBinarized());
    Srcg binarizedSrcg = srcg.getBinarizedSrcg();
    assertTrue(binarizedSrcg.isBinarized());
    assertEquals("G = <N, T, V, P, S>\n" + "N = {S, A, B, C, D, AB1, AB11}\n"
        + "T = {a, b, c, d}\n" + "V = {X, Y, V, U}\n"
        + "P = {S(X Y) -> A(X,Y), A(U,X) -> AB1(X) AB11(U), "
        + "AB1(X Y) -> C(X) D(Y), AB11(U V) -> A(U) B(V), A(a) -> ε, B(b) -> ε,"
        + " C(c) -> ε, D(d) -> ε}\n" + "S = S\n", binarizedSrcg.toString());
  }

  @Test public void testRemoveEmptyProductionsForEarley()
      throws ParseException, FileNotFoundException {
    Srcg srcg = GrammarLoader.readSrcg("anbmcndm.srcg");
    Srcg srcgEpsFree =
        Objects.requireNonNull(srcg).getSrcgWithoutEmptyProductions();
    ParsingSchema schema =
        LcfrsToEarleyRulesConverter.srcgToEarleyRules(srcgEpsFree, "a c");
    Deduction deduction = new Deduction();
    assertTrue(deduction.doParse(schema, false));
  }

  @Test public void testRemoveUselessRules()
      throws FileNotFoundException, ParseException {
    Srcg srcg = GrammarLoader.readSrcg("testsrcgwithuselessrules.srcg");
    Srcg srcg2 = srcg.getSrcgWithoutUselessRules();
    assertEquals("G = <N, T, V, P, S>\n" + "N = {S, A}\n" + "T = {a}\n"
        + "V = {X, Y, Z, U, V, W}\n"
        + "P = {S(X Y Z U V W) -> A(X,U), A(a X,a Y) -> A(X,Y), A(a,a) -> ε}\n"
        + "S = S\n", srcg2.toString());
  }

  @Test public void testConvertTagToLcfrs()
      throws FileNotFoundException, ParseException {
    Tag tag = GrammarLoader.readTag("converttolcfrs.tag");
    Srcg srcg = new Srcg(tag);
    assertEquals(
        "G = <N, T, V, P, S>\n" + "N = {α, β, γ}\n" + "T = {a, b, c, d}\n"
            + "V = {X1, X2, X3}\n"
            + "P = {α(a b c X3) -> γ(X3), α(a X1 b c X2 X3) -> β(X1,X2) γ(X3), "
            + "β(a,a) -> ε, β(X1 a,a X2) -> β(X1,X2), γ(d) -> ε}\n" + "S = S\n",
        srcg.toString());
  }

}

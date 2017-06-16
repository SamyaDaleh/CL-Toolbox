package common.cfg;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import common.TestGrammarLibrary;

public class CfgTest {

  @Test public void testBinarization() {
    assertTrue(!TestGrammarLibrary.longRhsCfg().isBinarized());
    Cfg cfgbin = TestGrammarLibrary.longRhsCfg().binarize();
    assertTrue(cfgbin.isBinarized());
  }

  @Test public void testRemoveEpsilon() {
    assertTrue(TestGrammarLibrary.epsCfg().hasEpsilonProductions());
    Cfg epsfree = TestGrammarLibrary.epsCfg().removeEmptyProductions();
    assertTrue(!epsfree.hasEpsilonProductions());
  }

  @Test public void testRemoveChainRules() {
    assertTrue(TestGrammarLibrary.eftCfg().hasChainRules());
    Cfg chainfree = TestGrammarLibrary.eftCfg().removeChainRules();
    assertTrue(!chainfree.hasChainRules());
  }

  @Test public void testReplaceTerminals() {
    Cfg treplaced = TestGrammarLibrary.eftCfg().replaceTerminals();
    assertEquals("G = <N, T, S, P>\n"
      + "N = {I, F, T, E, Y1, Y2, Y3, Y4, Y5, Y6, Y7, Y8}\n"
      + "T = {a, b, 0, 1, (, ), *, +}\n" + "S = E\n"
      + "P = {I -> a, I -> b, Y1 -> a, I -> I Y1, Y2 -> b, I -> I Y2, "
      + "Y3 -> 0, I -> I Y3, Y4 -> 1, I -> I Y4, F -> I, Y5 -> (, Y6 -> ), "
      + "F -> Y5 E Y6, T -> F, Y7 -> *, T -> T Y7 F, E -> T, Y8 -> +, "
      + "E -> E Y8 T}\n", treplaced.toString());
  }

  @Test public void testToCnf() {
    Cfg cfgcnf = TestGrammarLibrary.eftCfg().removeEmptyProductions()
      .removeNonGeneratingSymbols().removeNonReachableSymbols().binarize()
      .replaceTerminals().removeChainRules();
    assertTrue(cfgcnf.isInChomskyNormalForm());
    assertEquals("G = <N, T, S, P>\n"
      + "N = {I, F, T, E, X1, X2, X3, Y1, Y2, Y3, Y4, Y5, Y6, Y7, Y8}\n"
      + "T = {a, b, 0, 1, (, ), *, +}\n" + "S = E\n"
      + "P = {I -> a, I -> b, Y1 -> a, I -> I Y1, Y2 -> b, I -> I Y2, "
      + "Y3 -> 0, I -> I Y3, Y4 -> 1, I -> I Y4, Y5 -> (, F -> Y5 X1, "
      + "Y6 -> ), X1 -> E Y6, T -> T X2, Y7 -> *, X2 -> Y7 F, E -> E X3, "
      + "Y8 -> +, X3 -> Y8 T, F -> a, F -> b, F -> I Y1, F -> I Y2, "
      + "F -> I Y3, F -> I Y4, T -> Y5 X1, E -> T X2}\n", cfgcnf.toString());
  }

  @Test public void testToC2f() {
    assertTrue(TestGrammarLibrary.eftCfg().removeEmptyProductions()
      .removeNonGeneratingSymbols().removeNonReachableSymbols().binarize()
      .replaceTerminals().isInCanonicalTwoForm());
  }

  @Test public void testRemoveLeftRecursion() {
    Cfg cfgwlr = TestGrammarLibrary.leftRecursionCfg().removeLeftRecursion();
    assertEquals(
      "G = <N, T, S, P>\n" + "N = {S, S1}\n" + "T = {a, b, c, d}\n" + "S = S\n"
        + "P = {S1 -> ε, S -> a S1, S -> b S1, S -> c S1, S -> d S1}\n",
      cfgwlr.toString());
  }

  @Test public void testRemoveNotReachableSymbols() {
    Cfg after =
      TestGrammarLibrary.nonReachableSymbolsCfg().removeNonReachableSymbols();
    assertEquals("G = <N, T, S, P>\n" + "N = {S}\n" + "T = {a}\n" + "S = S\n"
      + "P = {S -> a}\n", after.toString());
  }

  @Test public void testRemoveNonGeneratingSymbols() {
    Cfg after =
      TestGrammarLibrary.nonGeneratingSymbolsCfg().removeNonGeneratingSymbols();
    assertEquals("G = <N, T, S, P>\n" + "N = {S}\n" + "T = {a}\n" + "S = S\n"
      + "P = {S -> a}\n", after.toString());
  }

  @Test public void testCreateCfgFromPcfg() {
    Cfg cfg = new Cfg(TestGrammarLibrary.banPcfg());
    assertEquals("G = <N, T, S, P>\n" + "N = {S, A, B}\n" + "T = {a, b}\n"
      + "S = S\n" + "P = {S -> A B, A -> b, A -> a, B -> B B, B -> a}\n",
      cfg.toString());
  }
}

package com.github.samyadaleh.cltoolbox.common.cfg;

import java.io.StringReader;
import java.text.ParseException;
import java.util.Objects;

import com.github.samyadaleh.cltoolbox.cli.GrammarToGrammarConverter;
import com.github.samyadaleh.cltoolbox.common.finiteautomata.NondeterministicFiniteAutomaton;
import com.github.samyadaleh.cltoolbox.common.parser.CfgParser;
import org.junit.Ignore;
import org.junit.Test;

import com.github.samyadaleh.cltoolbox.common.TestGrammarLibrary;

import static org.junit.Assert.*;

public class CfgTest {

  @Test
  public void testBinarization() {
    assertFalse(
        Objects.requireNonNull(TestGrammarLibrary.longRhsCfg()).isBinarized());
    Cfg cfgbin = Objects.requireNonNull(TestGrammarLibrary.longRhsCfg()).getBinarizedCfg();
    assertTrue(cfgbin.isBinarized());
  }

  @Test
  public void testRemoveEpsilon() {
    assertTrue(Objects.requireNonNull(TestGrammarLibrary.epsCfg()).hasEpsilonProductions());
    Cfg epsfree = Objects.requireNonNull(TestGrammarLibrary.epsCfg()).getCfgWithoutEmptyProductions();
    assertFalse(epsfree.hasEpsilonProductions());
    assertEquals(
      "G = <N, T, S, P>\n" + "N = {S, A, B, C, S1}\n" + "T = {a, b}\n"
        + "S = S1\n"
        + "P = {S -> b A a S b C, A -> a, A -> b B, B -> b, S -> b a S b C,"
        + " S -> b A a b C, S -> b a b C, S1 -> S, S1 -> ε, S -> b A a S b,"
        + " S -> b a S b, S -> b A a b, S -> b a b}\n" + "",
      epsfree.toString());
  }

  @Test
  public void testReplaceTerminals() {
    Cfg treplaced = Objects.requireNonNull(TestGrammarLibrary.eftCfg())
      .getCfgWithEitherOneTerminalOrNonterminalsOnRhs();
    assertEquals("G = <N, T, S, P>\n"
      + "N = {I, F, T, E, Y1, Y2, Y3, Y4, Y5, Y6, Y7, Y8}\n"
      + "T = {a, b, 0, 1, (, ), *, +}\n" + "S = E\n"
      + "P = {I -> a, I -> b, Y1 -> a, I -> I Y1, Y2 -> b, I -> I Y2, "
      + "Y3 -> 0, I -> I Y3, Y4 -> 1, I -> I Y4, F -> I, Y5 -> (, Y6 -> ), "
      + "F -> Y5 E Y6, T -> F, Y7 -> *, T -> T Y7 F, E -> T, Y8 -> +, "
      + "E -> E Y8 T}\n", treplaced.toString());
  }

  @Test
  public void testToCnf() {
    Cfg cfgcnf = Objects.requireNonNull(TestGrammarLibrary.eftCfg()).getCfgWithoutEmptyProductions()
      .getCfgWithoutNonGeneratingSymbols().getCfgWithoutNonReachableSymbols()
      .getBinarizedCfg().getCfgWithEitherOneTerminalOrNonterminalsOnRhs()
      .getCfgWithoutChainRules();
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

  @Test
  public void testToC2f() {
    assertTrue(Objects.requireNonNull(TestGrammarLibrary.eftCfg()).getCfgWithoutEmptyProductions()
      .getCfgWithoutNonGeneratingSymbols().getCfgWithoutNonReachableSymbols()
      .getBinarizedCfg().getCfgWithEitherOneTerminalOrNonterminalsOnRhs()
      .isInCanonicalTwoForm());
  }

  @Test
  public void testRemoveDirectLeftRecursion() {
    Cfg cfgwlr = Objects
        .requireNonNull(TestGrammarLibrary.directLeftRecursionCfg())
      .getCfgWithoutDirectLeftRecursion();
    assertEquals(
      "G = <N, T, S, P>\n" + "N = {S, S1}\n" + "T = {a, b, c, d}\n" + "S = S\n"
        + "P = {S1 -> ε, S -> d S1, S -> c S1, S1 -> b S1, S1 -> a S1}\n",
      cfgwlr.toString());
  }

  @Test
  public void testRemoveDirectLeftRecursion2() throws ParseException {
    Cfg cfgwlr =
      Objects.requireNonNull(TestGrammarLibrary.directLeftRecursionCfg()).getCfgWithoutLeftRecursion();
    assertEquals(
      "G = <N, T, S, P>\n" + "N = {S, S1}\n" + "T = {a, b, c, d}\n" + "S = S\n"
        + "P = {S1 -> ε, S -> d S1, S -> c S1, S1 -> b S1, S1 -> a S1}\n",
      cfgwlr.toString());
  }

  @Test
  public void testRemoveIndirectLeftRecursion() throws ParseException {
    Cfg cfgwlr = Objects
        .requireNonNull(TestGrammarLibrary.indirectLeftRecursionCfg())
      .getCfgWithoutEmptyProductions().getCfgWithoutNonGeneratingSymbols()
      .getCfgWithoutNonReachableSymbols().getCfgWithoutLeftRecursion();
    assertEquals("G = <N, T, S, P>\n" +
      "N = {S, A, A1}\n" +
      "T = {a, b}\n" +
      "S = S\n" +
      "P = {S -> A a, S -> b, A1 -> ε, A -> b a A1, A1 -> a a A1}\n" +
      "", cfgwlr.toString());
  }

  @Test
  public void testRemoveLeftRecursionNoTermination()
    throws ParseException {
    Cfg cfgwlr = Objects
        .requireNonNull(TestGrammarLibrary.leftRecursionNoTerminationCfg())
      .getCfgWithoutLeftRecursion();
    assertNull(cfgwlr);
  }

  @Test
  public void testCrazyLeftRecursionRemoval()
      throws ParseException {
    Cfg cfgwlr = Objects
        .requireNonNull(TestGrammarLibrary.crazyLeftRecursionRemovalCfg())
        .getCfgWithoutLeftRecursion();
    assertEquals("G = <N, T, S, P>\n" + "N = {S1, N2, S, N1, S2, N11}\n"
        + "T = {t0}\n" + "S = S1\n"
        + "P = {N2 -> t0, S -> N1 S, S -> N1, S1 -> S, S1 -> ε, S -> N1 S2, S -> N1 S2, S -> N1 S S2, S2 -> N1 S2, S -> N1 S2, S -> N1 N1 S2, S -> N1 S S2, S -> N1 S N1 S2, N1 -> N1 S2, N1 -> N1 S2, N1 -> N1 S S2, N11 -> N1 S2 N11, N11 -> S2 N11, N11 -> S S2 N11, N11 -> S2 N11, N11 -> S2 N11, N11 -> S N11, N1 -> t0 N11}\n",
        cfgwlr.toString());
  }

  @Test
  public void testLeftRecursionNotRemoved()
      throws ParseException {
    Cfg cfgwlr = GrammarToGrammarConverter.checkAndMayConvertToCfg(
        TestGrammarLibrary.leftRecursionNotRemovedCfg(), "cfg-topdown", true);
    assertFalse(cfgwlr.hasLeftRecursion());
    assertEquals("G = <N, T, S, P>\n" + "N = {S, N1, S1}\n" + "T = {t0}\n"
            + "S = S\n"
            + "P = {N1 -> t0, N1 -> S S, S1 -> ε, S1 -> S S1, S -> t0 S1}\n",
        cfgwlr.toString());
  }

  @Test
  public void testLeftRecursionEndlessRemovalLeftCorner()
      throws ParseException {
    Cfg cfgwlr = GrammarToGrammarConverter.checkAndMayConvertToCfg(
        TestGrammarLibrary.leftRecursionEndlessRemovalCfg(), "cfg-leftcorner", true);
    assertEquals("G = <N, T, S, P>\n" + "N = {S3}\n" + "T = {}\n" + "S = S3\n"
            + "P = {S3 -> ε}\n",
        cfgwlr.toString());
  }

  @Test
  public void testRemoveNotReachableSymbols() {
    Cfg after = Objects
        .requireNonNull(TestGrammarLibrary.nonReachableSymbolsCfg())
      .getCfgWithoutNonReachableSymbols();
    assertEquals("G = <N, T, S, P>\n" + "N = {S}\n" + "T = {a}\n" + "S = S\n"
      + "P = {S -> a}\n", after.toString());
  }

  @Test
  public void testRemoveNonGeneratingSymbols() {
    Cfg after = Objects
        .requireNonNull(TestGrammarLibrary.nonGeneratingSymbolsCfg())
      .getCfgWithoutNonGeneratingSymbols();
    assertEquals("G = <N, T, S, P>\n" + "N = {S}\n" + "T = {a}\n" + "S = S\n"
      + "P = {S -> a}\n", after.toString());

    assertNull(
        Objects.requireNonNull(TestGrammarLibrary.noUsefulNonterminalCfg())
      .getCfgWithoutNonGeneratingSymbols());

    after = Objects
        .requireNonNull(TestGrammarLibrary.nonGeneratingSymbolsEpsilonCfg())
        .getCfgWithoutNonGeneratingSymbols();
    assertEquals(
        "G = <N, T, S, P>\n" + "N = {A, B, S}\n" + "T = {a, b, c}\n" + "S = S\n"
            + "P = {S -> A B, A -> ε, B -> ε}\n", after.toString());
  }

  @Test
  public void testCreateCfgFromPcfg() {
    Cfg cfg = new Cfg(TestGrammarLibrary.banPcfg());
    assertEquals("G = <N, T, S, P>\n" + "N = {S, A, B}\n" + "T = {a, b}\n"
      + "S = S\n" + "P = {S -> A B, A -> b, A -> a, B -> B B, B -> a}\n",
      cfg.toString());
  }

  @Test
  public void testHasGeneratingSymbols() throws ParseException {
    StringReader reader = new StringReader(
        "N = {N0, N1, N2, N3, N4, N5, N6, N7}\n"
            + "T = {t0, t1, t2, t3, t4, t5}\n" + "S = N0\n"
            + "P = {N2 -> ε, N0 -> N1 t0 t1 N2, N1 -> N2, N0 -> N1}");
    Cfg cfg = CfgParser.parseCfgReader(reader);
    assertEquals("G = <N, T, S, P>\n" + "N = {S1, N0}\n"
            + "T = {t0, t1, t2, t3, t4, t5}\n" + "S = S1\n"
            + "P = {N0 -> t0 t1, S1 -> N0, S1 -> ε}\n",
        cfg.getCfgWithoutEmptyProductions().getCfgWithoutNonGeneratingSymbols()
            .toString());
  }

  @Test
  public void testDoubling() throws ParseException {
    StringReader reader = new StringReader(
        "N = {S, A, B, U}\n" + "T = {a, b}\n" + "S = S\n"
            + "P = {S -> A | B U, A -> a A | a, B -> b B | b, U -> a U a | a a}");
    Cfg cfg = CfgParser.parseCfgReader(reader);
    assertEquals(
        "G = <N, T, S, P>\n" + "N = {S, A, B, U, Y1, Y2, A', B', Y1'}\n"
            + "T = {a, b}\n" + "S = S\n"
            + "P = {S -> A, S -> B U, Y1 -> a, A -> Y1 A', A -> a, Y2 -> b, "
            + "B -> Y2 B', B -> b, U -> Y1 U Y1', U -> Y1 Y1', A' -> Y1 A', "
            + "A' -> a, B' -> Y2 B', B' -> b, Y1' -> a}\n",
        cfg.getCfgWithDoubledRules().toString());
  }

  @Ignore
  public void testLeftFactoring() throws ParseException {
    StringReader reader = new StringReader(
        "N = {S, A, B}\n" + "T = {a, b}\n" + "S = S\n"
            + "P = {S -> A B, A -> A B, A -> a, B -> b}\n\n");
    Cfg cfg = CfgParser.parseCfgReader(reader);
    assertEquals(
        "",
        cfg.getLeftFactoredCfg().toString());
  }

  @Test
  public void testLeftToRightLinearGrammar() throws ParseException {
    StringReader reader = new StringReader(
        "N = {S, A, B}\n" + "T = {a, b, c}\n" + "S = S\n"
            + "P = {S -> a b S | c A, A -> b B, B -> c b B | ε}\n\n");
    Cfg cfg = CfgParser.parseCfgReader(reader);
    assertTrue(cfg.isRightLinear());
    NondeterministicFiniteAutomaton nfa = new NondeterministicFiniteAutomaton(cfg);
    NondeterministicFiniteAutomaton nfaRev = nfa.getReversedLanguageAutomaton();
    Cfg cfgRev = new Cfg(nfaRev);
    Cfg cfgLeftLinear = cfgRev.getCfgWithReversedProductionRules();
    assertFalse(cfgLeftLinear.isRightLinear());
    System.out.println(cfgLeftLinear);
    // comparison with string represemtation fails, because it looks different in every run.
    assertTrue(cfgLeftLinear.toString().contains("A -> S c"));
    assertTrue(cfgLeftLinear.toString().contains("q2 -> B c"));
    assertTrue(cfgLeftLinear.toString().contains("B -> A b"));
    assertTrue(cfgLeftLinear.toString().contains("B -> q2 b"));
    assertTrue(cfgLeftLinear.toString().contains("q0 -> B"));
    assertTrue(cfgLeftLinear.toString().contains("q1 -> S a"));
    assertTrue(cfgLeftLinear.toString().contains("S -> q1 b"));
    assertTrue(cfgLeftLinear.toString().contains("S -> ε"));
  }

  @Test public void testRemoveLoops() {
    Cfg cfg = TestGrammarLibrary.loopRemovalCfg();
    Cfg cfgNoLoops = cfg.getCfgWithoutLoops();
    assertEquals("G = <N, T, S, P>\n" + "N = {S, N1}\n" + "T = {a, b}\n"
        + "S = S1\n" + "P = {S -> a a N1, S -> b N1, N1 -> b N1, N1 -> a}\n",
        cfgNoLoops.toString());
  }
}

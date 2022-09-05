package com.github.samyadaleh.cltoolbox.common.cfg;

import com.github.samyadaleh.cltoolbox.chartparsing.Deduction;
import com.github.samyadaleh.cltoolbox.chartparsing.ParsingSchema;
import com.github.samyadaleh.cltoolbox.chartparsing.converter.cfg.CfgToTopDownRulesConverter;
import com.github.samyadaleh.cltoolbox.cli.GrammarToGrammarConverter;
import com.github.samyadaleh.cltoolbox.common.GrammarLoader;
import com.github.samyadaleh.cltoolbox.common.finiteautomata.NondeterministicFiniteAutomaton;
import com.github.samyadaleh.cltoolbox.common.parser.CfgParser;
import org.junit.Ignore;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.StringReader;
import java.text.ParseException;

import static org.junit.Assert.*;

public class CfgTest {

  @Test
  public void testBinarization() throws FileNotFoundException, ParseException {
    Cfg cfg = GrammarLoader.readCfg("longrhs.cfg");
    assertFalse(cfg.isBinarized());
    Cfg cfgbin = cfg.getBinarizedCfg();
    assertTrue(cfgbin.isBinarized());
  }

  @Test
  public void testRemoveEpsilon() throws FileNotFoundException, ParseException {
    Cfg cfg = GrammarLoader.readCfg("eps.cfg");
    assertTrue(cfg.hasEpsilonProductions());
    Cfg epsfree = cfg.getCfgWithoutEmptyProductions();
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
  public void testReplaceTerminals()
      throws FileNotFoundException, ParseException {
    Cfg cfg = GrammarLoader.readCfg("eft.cfg");
    Cfg treplaced = cfg.getCfgWithEitherOneTerminalOrNonterminalsOnRhs();
    assertEquals("G = <N, T, S, P>\n"
      + "N = {I, F, T, E, Y1, Y2, Y3, Y4, Y5, Y6, Y7, Y8}\n"
      + "T = {a, b, 0, 1, (, ), *, +}\n" + "S = E\n"
      + "P = {I -> a, I -> b, Y1 -> a, I -> I Y1, Y2 -> b, I -> I Y2, "
      + "Y3 -> 0, I -> I Y3, Y4 -> 1, I -> I Y4, F -> I, Y5 -> (, Y6 -> ), "
      + "F -> Y5 E Y6, T -> F, Y7 -> *, T -> T Y7 F, E -> T, Y8 -> +, "
      + "E -> E Y8 T}\n", treplaced.toString());
  }

  @Test
  public void testToCnf() throws FileNotFoundException, ParseException {
    Cfg cfg = GrammarLoader.readCfg("eft.cfg");
    Cfg cfgcnf = cfg.getCfgWithoutEmptyProductions()
      .getCfgWithoutNonGeneratingSymbols().getCfgWithoutNonReachableSymbols()
      .getBinarizedCfg().getCfgWithEitherOneTerminalOrNonterminalsOnRhs()
      .getCfgWithoutChainRules();
    assertTrue(cfgcnf.isInChomskyNormalForm());
    assertEquals("G = <N, T, S, P>\n"
        + "N = {I, F, T, E, X1, X2, X3, Y1, Y2, Y3, Y4, Y5, Y6, Y7, Y8}\n"
        + "T = {a, b, 0, 1, (, ), *, +}\n" + "S = E\n"
        + "P = {I -> a, I -> b, Y1 -> a, I -> I Y1, Y2 -> b, I -> I Y2, Y3 -> 0, I -> I Y3, Y4 -> 1, I -> I Y4, Y5 -> (, F -> Y5 X1, Y6 -> ), X1 -> E Y6, T -> T X2, Y7 -> *, X2 -> Y7 F, E -> E X3, Y8 -> +, X3 -> Y8 T, F -> a, F -> b, F -> I Y1, F -> I Y2, F -> I Y3, F -> I Y4, T -> Y5 X1, E -> T X2, T -> a, T -> b, T -> I Y1, T -> I Y2, T -> I Y3, T -> I Y4, E -> Y5 X1, E -> a, E -> b, E -> I Y1, E -> I Y2, E -> I Y3, E -> I Y4}\n",
        cfgcnf.toString());
  }

  @Test
  public void testToC2f() throws FileNotFoundException, ParseException {
    Cfg cfg = GrammarLoader.readCfg("eft.cfg");
    assertTrue(cfg.getCfgWithoutEmptyProductions()
      .getCfgWithoutNonGeneratingSymbols().getCfgWithoutNonReachableSymbols()
      .getBinarizedCfg().getCfgWithEitherOneTerminalOrNonterminalsOnRhs()
      .isInCanonicalTwoForm());
  }

  @Test
  public void testRemoveDirectLeftRecursion()
      throws FileNotFoundException, ParseException {
    Cfg cfg = GrammarLoader.readCfg("directleftrecursion.cfg");
    Cfg cfgwlr = cfg.getCfgWithoutDirectLeftRecursion();
    assertEquals(
      "G = <N, T, S, P>\n" + "N = {S, S1}\n" + "T = {a, b, c, d}\n" + "S = S\n"
        + "P = {S1 -> ε, S -> d S1, S -> c S1, S1 -> b S1, S1 -> a S1}\n",
      cfgwlr.toString());
  }

  @Test
  public void testRemoveDirectLeftRecursion2()
      throws ParseException, FileNotFoundException {
    Cfg cfg = GrammarLoader.readCfg("directleftrecursion.cfg");
    Cfg cfgwlr = cfg.getCfgWithoutLeftRecursion();
    assertEquals(
      "G = <N, T, S, P>\n" + "N = {S, S1}\n" + "T = {a, b, c, d}\n" + "S = S\n"
        + "P = {S1 -> ε, S -> d S1, S -> c S1, S1 -> b S1, S1 -> a S1}\n",
      cfgwlr.toString());
  }

  @Test
  public void testRemoveIndirectLeftRecursion()
      throws ParseException, FileNotFoundException {
    Cfg cfg = GrammarLoader.readCfg("indirectleftrecursion.cfg");
    Cfg cfgwlr = cfg
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
      throws ParseException, FileNotFoundException {
    Cfg cfg = GrammarLoader.readCfg("leftrecursionnotermination.cfg");
    Cfg cfgwlr = cfg.getCfgWithoutLeftRecursion();
    assertNull(cfgwlr);
  }

  @Test
  public void testCrazyLeftRecursionRemoval()
      throws ParseException, FileNotFoundException {
    Cfg cfg = GrammarLoader.readCfg("crazyleftrecursionremoval.cfg");
    Cfg cfgwlr = cfg.getCfgWithoutLeftRecursion();
    assertEquals("G = <N, T, S, P>\n" + "N = {S1, N2, S, N1, S2, N11}\n"
        + "T = {t0}\n" + "S = S1\n"
        + "P = {N2 -> t0, S -> N1 S, S -> N1, S1 -> S, S1 -> ε, S -> N1 S2, S -> N1 S2, S -> N1 S S2, S2 -> N1 S2, S -> N1 S2, S -> N1 N1 S2, S -> N1 S S2, S -> N1 S N1 S2, N1 -> N1 S2, N1 -> N1 S2, N1 -> N1 S S2, N11 -> N1 S2 N11, N11 -> S2 N11, N11 -> S S2 N11, N11 -> S2 N11, N11 -> S2 N11, N11 -> S N11, N1 -> t0 N11}\n",
        cfgwlr.toString());
  }

  @Test
  public void testLeftRecursionNotRemoved()
      throws ParseException, FileNotFoundException {
    Cfg cfg = GrammarLoader.readCfg("leftrecursionnotremoved.cfg");
    Cfg cfgwlr = GrammarToGrammarConverter.checkAndMayConvertToCfg(
        cfg, "cfg-topdown", true);
    assertFalse(cfgwlr.hasLeftRecursion());
    assertEquals("G = <N, T, S, P>\n" + "N = {N2, N21}\n" + "T = {t0}\n"
            + "S = N2\n" + "P = {N21 -> ε, N21 -> N2 N21, N2 -> t0 N21}\n",
        cfgwlr.toString());
  }

  @Test
  public void testLeftRecursionEndlessRemovalLeftCorner()
      throws ParseException, FileNotFoundException {
    Cfg cfg = GrammarLoader.readCfg("leftrecursionendlessremoval.cfg");
    Cfg cfgwlr = GrammarToGrammarConverter.checkAndMayConvertToCfg(
        cfg, "cfg-leftcorner", true);
    assertEquals("G = <N, T, S, P>\n" + "N = {S1}\n" + "T = {}\n" + "S = S1\n"
            + "P = {S1 -> ε}\n",
        cfgwlr.toString());
  }

  private static Cfg nonReachableSymbolsCfg() {
    Cfg cfg = new Cfg();
    cfg.setTerminals(new String[] {"a"});
    cfg.setNonterminals(new String[] {"S", "G"});
    cfg.setStartSymbol("S");
    try {
      cfg.addProductionRule("S -> a");
      cfg.addProductionRule("G -> b");
      return cfg;
    } catch (ParseException e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  public void testRemoveNotReachableSymbols() {
    Cfg cfg = nonReachableSymbolsCfg();
    Cfg after = cfg.getCfgWithoutNonReachableSymbols();
    assertEquals("G = <N, T, S, P>\n" + "N = {S}\n" + "T = {a}\n" + "S = S\n"
      + "P = {S -> a}\n", after.toString());
  }

  private static Cfg nonGeneratingSymbolsCfg() {
    Cfg cfg = new Cfg();
    cfg.setTerminals(new String[] {"a"});
    cfg.setNonterminals(new String[] {"S", "G"});
    cfg.setStartSymbol("S");
    try {
      cfg.addProductionRule("S -> a");
      cfg.addProductionRule("S -> G");
      cfg.addProductionRule("G -> G b");
      return cfg;
    } catch (ParseException e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  public void testRemoveNonGeneratingSymbols()
      throws FileNotFoundException, ParseException {
    Cfg cfg = nonGeneratingSymbolsCfg();
    Cfg after = cfg.getCfgWithoutNonGeneratingSymbols();
    assertEquals("G = <N, T, S, P>\n" + "N = {S}\n" + "T = {a}\n" + "S = S\n"
      + "P = {S -> a}\n", after.toString());

    cfg = GrammarLoader.readCfg("nousefulnonterminal.cfg");
    assertNull(cfg.getCfgWithoutNonGeneratingSymbols());

    cfg = GrammarLoader.readCfg("nongeneratingsymbolsepsilon.cfg");
    after = cfg.getCfgWithoutNonGeneratingSymbols();
    assertEquals(
        "G = <N, T, S, P>\n" + "N = {A, B, S}\n" + "T = {a, b, c}\n" + "S = S\n"
            + "P = {S -> A B, A -> ε, B -> ε}\n", after.toString());
  }

  @Test
  public void testCreateCfgFromPcfg()
      throws FileNotFoundException, ParseException {
    Pcfg pcfg = GrammarLoader.readPcfg("ban.pcfg");
    Cfg cfg = new Cfg(pcfg);
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

  @Test
  public void testRemoveChainRules() throws ParseException {
    StringReader reader = new StringReader(
        "N = {N0, N1, N2}\n" + "T = {t0, t1}\n" + "S = N1\n"
            + "P = {N1 -> ε, N1 -> t1, N1 -> t0 N2, N0 -> t1 N2 t1, N2 -> N0, N0 -> N1, N2 -> t0}");
    Cfg cfg = CfgParser.parseCfgReader(reader);
    Cfg cfgWithoutChainRules = cfg.getCfgWithoutChainRules();
    assertEquals("G = <N, T, S, P>\n" + "N = {N0, N1, N2}\n" + "T = {t0, t1}\n"
        + "S = N1\n"
        + "P = {N1 -> ε, N1 -> t1, N1 -> t0 N2, N0 -> t1 N2 t1, N2 -> t0, N0 -> ε, N0 -> t1, N0 -> t0 N2, N2 -> t1 N2 t1, N2 -> ε, N2 -> t1, N2 -> t0 N2}\n", cfgWithoutChainRules.toString());

    String w = "t0 t1";
    ParsingSchema schema = CfgToTopDownRulesConverter
        .cfgToTopDownRules(cfgWithoutChainRules, w);
    Deduction deduction = new Deduction();
    assertTrue(deduction.doParse(schema, false));
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

  @Test public void testRemoveLoops()
      throws FileNotFoundException, ParseException {
    Cfg cfg = GrammarLoader.readCfg("loopremoval.cfg");
    Cfg cfgNoLoops = cfg.getCfgWithoutLoops();
    assertEquals("G = <N, T, S, P>\n" + "N = {S, S1, N1}\n" + "T = {a, b}\n"
        + "S = S1\n" + "P = {S -> a a N1, S -> b N1, N1 -> b N1, N1 -> a}\n",
        cfgNoLoops.toString());
  }
}

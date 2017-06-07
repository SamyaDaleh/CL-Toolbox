package common.cfg;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class CfgTest {
  private static Cfg gen_cfgbintest() {
    Cfg G = new Cfg();
    G.setTerminals(new String[] {"a", "b"});
    G.setNonterminals(new String[] {"S"});
    G.setProductionrules(
      new String[][] {{"S", "a S b S S a S b a b"}, {"S", "a b"}});
    G.setStartSymbol("S");
    return G;
  }

  @Test public void testBinarization() {
    assertTrue(!gen_cfgbintest().isBinarized());
    Cfg cfgbin = gen_cfgbintest().binarize();
    assertTrue(cfgbin.isBinarized());
  }

  @Test public void testRemoveEpsilon() {
    Cfg cfgeps = new Cfg();
    cfgeps.setTerminals(new String[] {"a", "b"});
    cfgeps.setNonterminals(new String[] {"S", "A", "B", "C"});
    cfgeps.setProductionrules(new String[][] {{"A", "ε"}, {"S", ""}, {"C", ""},
      {"S", "b A a S b C"}, {"A", "a"}, {"A", "b B"}, {"B", "b"}});
    cfgeps.setStartSymbol("S");
    assertTrue(cfgeps.hasEpsilonProductions());
    Cfg epsfree = cfgeps.removeEmptyProductions();
    assertTrue(!epsfree.hasEpsilonProductions());
  }

  @Test public void testRemoveChainRules() {
    Cfg cfg = new Cfg();
    cfg.setTerminals(new String[] {"a", "b", "0", "1", "(", ")", "*", "+"});
    cfg.setNonterminals(new String[] {"I", "F", "T", "E"});
    cfg.setProductionrules(new String[][] {{"I", "a"}, {"I", "b"}, {"I", "I a"},
      {"I", "I b"}, {"I", "I 0"}, {"I", "I 1"}, {"F", "I"}, {"F", "( E )"},
      {"T", "F"}, {"T", "T * F"}, {"E", "T"}, {"E", "E + T"}});
    cfg.setStartSymbol("E");
    assertTrue(cfg.hasChainRules());
    Cfg chainfree = cfg.removeChainRules();
    assertTrue(!chainfree.hasChainRules());
  }

  @Test public void testReplaceTerminals() {
    Cfg cfg = new Cfg();
    cfg.setTerminals(new String[] {"a", "b", "0", "1", "(", ")", "*", "+"});
    cfg.setNonterminals(new String[] {"I", "F", "T", "E"});
    cfg.setProductionrules(new String[][] {{"I", "a"}, {"I", "b"}, {"I", "I a"},
      {"I", "I b"}, {"I", "I 0"}, {"I", "I 1"}, {"F", "I"}, {"F", "( E )"},
      {"T", "F"}, {"T", "T * F"}, {"E", "T"}, {"E", "E + T"}});
    cfg.setStartSymbol("E");
    Cfg treplaced = cfg.replaceTerminals();
    assertEquals("G = <N, T, S, P>\n"
      + "N = {I, F, T, E, Y1, Y2, Y3, Y4, Y5, Y6, Y7, Y8}\n"
      + "T = {a, b, 0, 1, (, ), *, +}\n" + "S = E\n"
      + "P = {I -> a, I -> b, Y1 -> a, I -> I Y1, Y2 -> b, I -> I Y2, "
      + "Y3 -> 0, I -> I Y3, Y4 -> 1, I -> I Y4, F -> I, Y5 -> (, Y6 -> ), "
      + "F -> Y5 E Y6, T -> F, Y7 -> *, T -> T Y7 F, E -> T, Y8 -> +, "
      + "E -> E Y8 T}\n", treplaced.toString());
  }

  @Test public void testToCnf() {
    Cfg cfg = new Cfg();
    cfg.setTerminals(new String[] {"a", "b", "0", "1", "(", ")", "*", "+"});
    cfg.setNonterminals(new String[] {"I", "F", "T", "E"});
    cfg.setProductionrules(new String[][] {{"I", "a"}, {"I", "b"}, {"I", "I a"},
      {"I", "I b"}, {"I", "I 0"}, {"I", "I 1"}, {"F", "I"}, {"F", "( E )"},
      {"T", "F"}, {"T", "T"}, {"T", "T * F"}, {"E", "T"}, {"E", "E + T"}});
    cfg.setStartSymbol("E");
    Cfg cfgcnf = cfg.removeEmptyProductions().removeNonGeneratingSymbols()
      .removeNonReachableSymbols().binarize().replaceTerminals()
      .removeChainRules();
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
    Cfg cfg = new Cfg();
    cfg.setTerminals(new String[] {"a", "b", "0", "1", "(", ")", "*", "+"});
    cfg.setNonterminals(new String[] {"I", "F", "T", "E"});
    cfg.setProductionrules(new String[][] {{"I", "a"}, {"I", "b"}, {"I", "I a"},
      {"I", "I b"}, {"I", "I 0"}, {"I", "I 1"}, {"F", "I"}, {"F", "( E )"},
      {"T", "F"}, {"T", "T * F"}, {"E", "T"}, {"E", "E + T"}});
    cfg.setStartSymbol("E");
    assertTrue(cfg.removeEmptyProductions().removeNonGeneratingSymbols()
      .removeNonReachableSymbols().binarize().replaceTerminals()
      .isInCanonicalTwoForm());
  }

  @Test public void testRemoveLeftRecursion() {
    Cfg cfg = new Cfg();
    cfg.setTerminals(new String[] {"a", "b", "c", "d"});
    cfg.setNonterminals(new String[] {"S"});
    cfg.setProductionrules(new String[][] {{"S", "S"}, {"S", "S a"},
      {"S", "S b"}, {"S", "c"}, {"S", "d"}});
    cfg.setStartSymbol("S");
    Cfg cfgwlr = cfg.removeLeftRecursion();
    assertEquals(
      "G = <N, T, S, P>\n" + "N = {S, S1}\n" + "T = {a, b, c, d}\n" + "S = S\n"
        + "P = {S1 -> ε, S -> a S1, S -> b S1, S -> c S1, S -> d S1}\n",
      cfgwlr.toString());
  }

  @Test public void testRemoveNotReachableSymbols() {
    Cfg cfg = new Cfg();
    cfg.setTerminals(new String[] {"a"});
    cfg.setNonterminals(new String[] {"S", "G"});
    cfg.setStartSymbol("S");
    cfg.setProductionrules(new String[][] {{"S", "a"}, {"G", "b"}});
    Cfg after = cfg.removeNonReachableSymbols();
    assertEquals("G = <N, T, S, P>\n" + "N = {S}\n" + "T = {a}\n" + "S = S\n"
      + "P = {S -> a}\n", after.toString());
  }

  @Test public void testRemoveNonGeneratingSymbols() {
    Cfg cfg = new Cfg();
    cfg.setTerminals(new String[] {"a"});
    cfg.setNonterminals(new String[] {"S", "G"});
    cfg.setStartSymbol("S");
    cfg
      .setProductionrules(new String[][] {{"S", "a"}, {"S", "G"}, {"G", "Gb"}});
    Cfg after = cfg.removeNonGeneratingSymbols();
    assertEquals("G = <N, T, S, P>\n" + "N = {S}\n" + "T = {a}\n" + "S = S\n"
      + "P = {S -> a}\n", after.toString());
  }

  @Test public void testCreateCfgFromPcfg() {
    Pcfg pcfg = new Pcfg();
    pcfg.setTerminals(new String[] {"a", "b"});
    pcfg.setNonterminals(new String[] {"S", "A", "B"});
    pcfg
      .setProductionRules(new String[][] {{"S", "A B", "1"}, {"A", "b", "0.7"},
        {"A", "a", "0.3"}, {"B", "B B", "0.6"}, {"B", "a", "0.4"}});
    pcfg.setStartSymbol("S");
    Cfg cfg = new Cfg(pcfg);
    assertEquals("G = <N, T, S, P>\n"
      + "N = {S, A, B}\n"
      + "T = {a, b}\n"
      + "S = S\n"
      + "P = {S -> A B, A -> b, A -> a, B -> B B, B -> a}\n", cfg.toString());
  }
}

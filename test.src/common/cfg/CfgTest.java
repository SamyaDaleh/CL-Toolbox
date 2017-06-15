package common.cfg;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class CfgTest {
  private static Cfg gen_cfgbintest() {
    Cfg cfg = new Cfg();
    cfg.setTerminals(new String[] {"a", "b"});
    cfg.setNonterminals(new String[] {"S"});
    cfg.addProductionRule("S -> a S b S S a S b a b");
    cfg.addProductionRule("S -> a b");
    cfg.setStartSymbol("S");
    return cfg;
  }
  
  private static Cfg gen_cfgEFT() {
    Cfg cfg = new Cfg();
    cfg.setTerminals(new String[] {"a", "b", "0", "1", "(", ")", "*", "+"});
    cfg.setNonterminals(new String[] {"I", "F", "T", "E"});
    cfg.addProductionRule("I -> a");
    cfg.addProductionRule("I -> b");
    cfg.addProductionRule("I -> I a");
    cfg.addProductionRule("I -> I b");
    cfg.addProductionRule("I -> I 0");
    cfg.addProductionRule("I -> I 1");
    cfg.addProductionRule("F -> I");
    cfg.addProductionRule("F -> ( E )");
    cfg.addProductionRule("T -> F");
    cfg.addProductionRule("T -> T * F");
    cfg.addProductionRule("E -> T");
    cfg.addProductionRule("E -> E + T");
    cfg.setStartSymbol("E");
    return cfg;
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
    cfgeps.addProductionRule("A -> ε");
    cfgeps.addProductionRule("S -> ");
    cfgeps.addProductionRule("C -> ");
    cfgeps.addProductionRule("S -> b A a S b C");
    cfgeps.addProductionRule("A -> a");
    cfgeps.addProductionRule("A -> b B");
    cfgeps.addProductionRule("B -> b");
    cfgeps.setStartSymbol("S");
    assertTrue(cfgeps.hasEpsilonProductions());
    Cfg epsfree = cfgeps.removeEmptyProductions();
    assertTrue(!epsfree.hasEpsilonProductions());
  }

  @Test public void testRemoveChainRules() {
    assertTrue(gen_cfgEFT().hasChainRules());
    Cfg chainfree = gen_cfgEFT().removeChainRules();
    assertTrue(!chainfree.hasChainRules());
  }

  @Test public void testReplaceTerminals() {
    Cfg treplaced = gen_cfgEFT().replaceTerminals();
    assertEquals("G = <N, T, S, P>\n"
      + "N = {I, F, T, E, Y1, Y2, Y3, Y4, Y5, Y6, Y7, Y8}\n"
      + "T = {a, b, 0, 1, (, ), *, +}\n" + "S = E\n"
      + "P = {I -> a, I -> b, Y1 -> a, I -> I Y1, Y2 -> b, I -> I Y2, "
      + "Y3 -> 0, I -> I Y3, Y4 -> 1, I -> I Y4, F -> I, Y5 -> (, Y6 -> ), "
      + "F -> Y5 E Y6, T -> F, Y7 -> *, T -> T Y7 F, E -> T, Y8 -> +, "
      + "E -> E Y8 T}\n", treplaced.toString());
  }

  @Test public void testToCnf() {
    Cfg cfgcnf = gen_cfgEFT().removeEmptyProductions().removeNonGeneratingSymbols()
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
    assertTrue(gen_cfgEFT().removeEmptyProductions().removeNonGeneratingSymbols()
      .removeNonReachableSymbols().binarize().replaceTerminals()
      .isInCanonicalTwoForm());
  }

  @Test public void testRemoveLeftRecursion() {
    Cfg cfg = new Cfg();
    cfg.setTerminals(new String[] {"a", "b", "c", "d"});
    cfg.setNonterminals(new String[] {"S"});
    cfg.addProductionRule("S -> S");
    cfg.addProductionRule("S -> S a");
    cfg.addProductionRule("S -> S b");
    cfg.addProductionRule("S -> c");
    cfg.addProductionRule("S -> d");
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
    cfg.addProductionRule("S -> a");
    cfg.addProductionRule("G -> b");
    Cfg after = cfg.removeNonReachableSymbols();
    assertEquals("G = <N, T, S, P>\n" + "N = {S}\n" + "T = {a}\n" + "S = S\n"
      + "P = {S -> a}\n", after.toString());
  }

  @Test public void testRemoveNonGeneratingSymbols() {
    Cfg cfg = new Cfg();
    cfg.setTerminals(new String[] {"a"});
    cfg.setNonterminals(new String[] {"S", "G"});
    cfg.setStartSymbol("S");
    cfg.addProductionRule("S -> a");
    cfg.addProductionRule("S -> G");
    cfg.addProductionRule("G -> G b");
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

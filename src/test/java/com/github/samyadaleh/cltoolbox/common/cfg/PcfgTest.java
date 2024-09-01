package com.github.samyadaleh.cltoolbox.common.cfg;

import com.github.samyadaleh.cltoolbox.common.GrammarLoader;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.text.ParseException;
import java.util.Arrays;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

public class PcfgTest {

  @Test public void testPcfgCNF() throws FileNotFoundException, ParseException {
    Pcfg pcfg = GrammarLoader.readPcfg("testcnf.pcfg");
    Cfg cfg = new Cfg(pcfg);
    assertTrue(cfg.isInChomskyNormalForm());
  }
  @Test public void testCreatePcfgFromCfg() throws ParseException {
    Cfg cfg = new Cfg();
    cfg.setTerminals(Arrays.asList("a", "b", "c", "d"));
    cfg.setNonterminals(Arrays.asList("S"));
    cfg.addProductionRule("S -> S");
    cfg.addProductionRule("S -> S a");
    cfg.addProductionRule("S -> S b");
    cfg.addProductionRule("S -> c");
    cfg.addProductionRule("S -> d");
    cfg.setStartSymbol("S");
    Pcfg pcfg = new Pcfg(cfg);
    assertEquals(
      "G = <N, T, S, P>\n" + "N = {S}\n" + "T = {a, b, c, d}\n" + "S = S\n"
        + "P = {0.2 : S -> S, 0.2 : S -> S a, 0.2 : S -> S b, 0.2 : S -> c, "
        + "0.2 : S -> d}\n",
      pcfg.toString());
  }
}

package common.cfg;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class PcfgTest {
  @Test public void testCreatePcfgFromCfg() {
    Cfg cfg = new Cfg();
    cfg.setTerminals(new String[] {"a", "b", "c", "d"});
    cfg.setNonterminals(new String[] {"S"});
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
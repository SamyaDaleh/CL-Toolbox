package com.github.samyadaleh.cltoolbox.chartparsing.cfg.util;

import com.github.samyadaleh.cltoolbox.common.GrammarLoader;
import com.github.samyadaleh.cltoolbox.common.cfg.Cfg;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.text.ParseException;

import static junit.framework.TestCase.assertEquals;

public class FactoringTest {
  @Test public void testFactoring()
      throws FileNotFoundException, ParseException {
    Cfg cfg = GrammarLoader.readCfg("leftfactor.cfg");
    Cfg cfgOut = cfg.getLeftFactoredCfg();
    assertEquals(
        "G = <N, T, S, P>\n" + "N = {A, S, N1, N2, N3}\n"
            + "T = {a, b, c, d, e}\n" + "S = S\n"
            + "P = {A -> a b c, N1 -> e, N1 -> d, N2 -> c N1, N2 -> d, N3 -> b N2, N3 -> ε, S -> a N3}\n",
        cfgOut.toString());
  }
}

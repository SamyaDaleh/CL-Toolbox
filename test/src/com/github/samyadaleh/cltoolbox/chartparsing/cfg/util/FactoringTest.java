package com.github.samyadaleh.cltoolbox.chartparsing.cfg.util;

import com.github.samyadaleh.cltoolbox.common.TestGrammarLibrary;
import com.github.samyadaleh.cltoolbox.common.cfg.Cfg;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

public class FactoringTest {
  @Test public void testFactoring() {
    Cfg cfg = TestGrammarLibrary.leftFactorCfg().getLeftFactoredCfg();
    assertEquals(
        "G = <N, T, S, P>\n" + "N = {A, S, N1, N2}\n" + "T = {a, b, c, d, e}\n"
            + "S = S\n"
            + "P = {A -> a b c, S -> a, N1 -> e, N1 -> d, N2 -> c N1, N2 -> d, S -> a b N2}\n",
        cfg.toString());
  }
}

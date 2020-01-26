package com.github.samyadaleh.cltoolbox.common.lcfrs.util;

import com.github.samyadaleh.cltoolbox.common.cfg.Cfg;
import com.github.samyadaleh.cltoolbox.common.lcfrs.Srcg;
import com.github.samyadaleh.cltoolbox.common.parser.CfgParser;
import org.junit.Test;

import java.io.StringReader;
import java.text.ParseException;

import static org.junit.Assert.assertEquals;

public class UselessRulesTest {

  @Test public void testRemoveUselessRules() throws ParseException {
    StringReader reader = new StringReader(
        "N = {N0, N1, N2, N3}\n" + "T = {t0, t1}\n" + "S = N2\n"
            + "P = {N2 -> t0 N3 t1, N1 -> t0 N3, N0 -> ε, N2 -> ε, N3 -> t1, "
            + "N2 -> ε, N3 -> ε}");
    Cfg cfg = CfgParser.parseCfgReader(reader);
    Srcg srcg = new Srcg(cfg).getSrcgWithoutUselessRules();
    assertEquals("G = <N, T, V, P, S>\n" + "N = {N2, N3}\n" + "T = {t0, t1}\n"
            + "V = {X1}\n" + "P = {N2(t0 X1 t1) -> N3(X1), N2(ε) -> ε, "
            + "N3(t1) -> ε, N2(ε) -> ε, N3(ε) -> ε}\n" + "S = N2\n",
        srcg.toString());
  }
}

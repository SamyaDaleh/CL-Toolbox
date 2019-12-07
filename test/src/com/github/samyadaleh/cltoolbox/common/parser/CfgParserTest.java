package com.github.samyadaleh.cltoolbox.common.parser;

import com.github.samyadaleh.cltoolbox.common.cfg.Cfg;
import org.junit.Test;

import java.io.StringReader;
import java.text.ParseException;

import static org.junit.Assert.assertEquals;

public class CfgParserTest {

  @Test public void testParseCfg() throws ParseException {
    StringReader reader = new StringReader(
        "N = {N0, N1, N2}\n" + "T = {t0, t1}\n" + "S = N2\n"
            + "P = {N2 -> ε, N0 -> ε, N2 -> N0 N1, N1 -> ε, N2 -> t0 t1, "
            + "N1 -> ε}\n\n");
    Cfg cfg = CfgParser.parseCfgReader(reader);
    assertEquals("N2", cfg.getStartSymbol());
  }
}

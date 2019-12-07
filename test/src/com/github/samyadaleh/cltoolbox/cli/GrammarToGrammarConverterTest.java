package com.github.samyadaleh.cltoolbox.cli;

import com.github.samyadaleh.cltoolbox.common.cfg.Cfg;
import com.github.samyadaleh.cltoolbox.common.parser.CfgParser;
import org.junit.Test;

import java.io.StringReader;
import java.text.ParseException;

public class GrammarToGrammarConverterTest {

  @Test(expected = ParseException.class) public void testCykConversion()
      throws ParseException {
    StringReader reader = new StringReader(
        "N = {N0, N1, N2}\n" + "T = {t0, t1}\n" + "S = N1\n"
            + "P = {N1 -> ε, N1 -> t1, N1 -> t0 N2, N0 -> t1 N2 t1, "
            + "N2 -> N0, N1 -> ε, N1 -> ε, N0 -> N1, N1 -> ε, N2 -> t0, "
            + "N2 -> N0}");
    Cfg cfg = CfgParser.parseCfgReader(reader);
    GrammarToGrammarConverter.checkAndMayConvertToCfg(cfg, "cfg-cyk", false);

  }
}

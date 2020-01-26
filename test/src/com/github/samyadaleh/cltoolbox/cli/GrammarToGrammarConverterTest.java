package com.github.samyadaleh.cltoolbox.cli;

import com.github.samyadaleh.cltoolbox.common.cfg.Cfg;
import com.github.samyadaleh.cltoolbox.common.lcfrs.Srcg;
import com.github.samyadaleh.cltoolbox.common.parser.CfgParser;
import com.github.samyadaleh.cltoolbox.common.parser.SrcgParser;
import org.junit.Test;

import java.io.StringReader;
import java.text.ParseException;

import static junit.framework.TestCase.assertEquals;

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

  @Test public void testSrcgCykExtendedConversion() throws ParseException {
    StringReader reader = new StringReader(
        "N = {N0, N1, N2}\n" + "T = {t0, t1}\n" + "V = {X1, X2, X3}\n"
            + "P = {N1(t0 t1) -> ε, N2(t1 X1) -> N0(X1), N1(ε) -> ε, "
            + "N2(X1) -> N0(X1), N2(t0 X1 X2 X3 t1) -> N0(X1) N0(X2) N1(X3)}\n"
            + "S = N1");
    Srcg srcg = SrcgParser.parseSrcgReader(reader);
    srcg = GrammarToGrammarConverter
        .checkAndMayConvertToSrcg(srcg, "srcg-cyk-extended", true);
    assertEquals("G = <N, T, V, P, S>\n" + "N = {N1^1, S'}\n" + "T = {t0, t1}\n"
        + "V = {Z1}\n" + "P = {S'(Z1) -> N1^1(Z1), S'(ε) -> ε, "
        + "N1^1(t0 t1) -> ε}\n" + "S = S'\n", srcg.toString());
  }
}

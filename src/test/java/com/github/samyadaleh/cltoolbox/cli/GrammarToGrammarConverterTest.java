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

  @Test public void testCfgShiftReduceConversion() throws ParseException {
    StringReader reader = new StringReader(
        "G = <N, T, S, P>\n" + "N = {N0, N1}\n" + "T = {t0, t1}\n" + "S = N1\n"
            + "P = {N1 -> ε, N1 -> t1 t0, N0 -> ε, N0 -> t1 t0 N0 N1 t0, "
            + "N0 -> ε, N0 -> ε, N1 -> N0, N0 -> t0 N1 N0 t1 N0}\n");
    Cfg cfg = CfgParser.parseCfgReader(reader);
    cfg = GrammarToGrammarConverter
        .checkAndMayConvertToCfg(cfg, "cfg-shiftreduce", true);
    assertEquals("G = <N, T, S, P>\n" + "N = {S1, N1, N0}\n" + "T = {t0, t1}\n"
            + "S = S1\n" + "P = {N1 -> t1 t0, N0 -> t1 t0 N0 N1 t0, N1 -> N0, "
            + "N0 -> t0 N1 N0 t1 N0, N0 -> t1 t0 N0 t0, N0 -> t0 N0 t1 N0, "
            + "S1 -> N1, S1 -> ε, N0 -> t1 t0 N1 t0, N0 -> t0 N1 t1 N0, "
            + "N0 -> t0 N1 N0 t1, N0 -> t1 t0 t0, N0 -> t0 t1 N0, N0 -> t0 N0 t1, "
            + "N0 -> t0 N1 t1, N0 -> t0 N1 t1, N0 -> t0 t1, N0 -> t0 t1}\n",
        cfg.toString());
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

  @Test public void testSrcgCykExtended2ConversionSimple() throws ParseException {
    StringReader reader = new StringReader(
        "N = {N0}\n" + "T = {t0, t1}\n" + "S = N0\n"
            + "P = {N0 -> ε, N0 -> t0 N0 t1}");
    Cfg cfg = CfgParser.parseCfgReader(reader);
    Srcg srcg = GrammarToGrammarConverter
        .checkAndMayConvertToSrcg(cfg, "srcg-cyk-extended", true);
    assertEquals("G = <N, T, V, P, S>\n" + "N = {N0^1, S'}\n" + "T = {t0, t1}\n"
        + "V = {X1}\n"
        + "P = {S'(ε) -> ε, S'(X1) -> N0^1(X1), N0^1(t0 t1) -> ε, N0^1(t0 X1 t1) -> N0^1(X1)}\n"
        + "S = S'\n", srcg.toString());
  }

  @Test public void testSrcgCykExtended2Conversion() throws ParseException {
    StringReader reader = new StringReader(
        "N = {N0}\n" + "T = {t0, t1}\n" + "S = N0\n"
            + "P = {N0 -> ε, N0 -> t0 N0 N0 N0 t1}");
    Cfg cfg = CfgParser.parseCfgReader(reader);
    Srcg srcg = GrammarToGrammarConverter
        .checkAndMayConvertToSrcg(cfg, "srcg-cyk-extended", true);
    assertEquals(
        "G = <N, T, V, P, S>\n" + "N = {N0^1, N01^1, S'}\n" + "T = {t0, t1}\n"
            + "V = {X1, X2, X3}\n"
            + "P = {S'(ε) -> ε, S'(X1) -> N0^1(X1), N0^1(t0 t1) -> ε, "
            + "N0^1(t0 X1 t1) -> N0^1(X1), N0^1(t0 X2 t1) -> N01^1(X2), "
            + "N0^1(t0 X1 X2 t1) -> N0^1(X1) N01^1(X2), N01^1(X2) -> N0^1(X2), "
            + "N01^1(X3) -> N0^1(X3), N01^1(X2 X3) -> N0^1(X2) N0^1(X3)}\n"
            + "S = S'\n", srcg.toString());
  }

  @Test(expected = ParseException.class)
  public void testParseSrcgUndeclaredSymbol() throws ParseException {
    StringReader reader = new StringReader(
        "N = {N0^1, N1^1, N11^1, S'}\n" + "T = {, t0, t1}\n"
            + "V = {X1, X2, X3}\n"
            + "P = {S'(ε) -> ε, S'(X1) -> N1^1(X1), N1^1(t0 X2) -> N11(X2), "
            + "N1^1(t0 X1 X2) -> N1^1(X1) N11^1(X2), N11^1(X3) -> N1(X3), "
            + "N11^1(X2) -> N1^1(X2), N11^1(X3) -> N1(X3), "
            + "N11^1(X2 X3) -> N1^1(X2) N1^1(X3), N0^1(t1) -> ε, "
            + "N1^1(t1 t0) -> ε, N1^1(X1) -> N0^1(X1)}\n" + "S = S'");
    Srcg srcg = SrcgParser.parseSrcgReader(reader);
    GrammarToGrammarConverter
        .checkAndMayConvertToSrcg(srcg, "srcg-cyk-extended", true);
  }
}

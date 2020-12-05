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

  @Test(expected = ParseException.class)
  public void testParseCfgLowercaseStartSymbol() throws ParseException {
    StringReader reader = new StringReader(
        "G = <N, T, S, P>\n" + "N = {S}\n" + "T = {a, b}\n" + "S = s\n"
            + "P = {S -> a S b, S -> a b}\n\n");
    Cfg cfg = CfgParser.parseCfgReader(reader);
  }

  @Test public void testParseCfgAndLeftFactor() throws ParseException {
    StringReader reader = new StringReader(
        "N = {S1, N0, X1, X2, X3, X4, X5, Y1, Y2}\n" + "T = {t0, t1, t2}\n"
            + "S = S1\n"
            + "P = {S1 -> ε, S1 -> t1, S1 -> Y1 X1, S1 -> Y2 N0, S1 -> t0, S1 -> Y1 X3, S1 -> Y1 X4, S1 -> Y1 X5, S1 -> t0, S1 -> Y1 N0, S1 -> Y1 N0, S1 -> Y1 Y2, S1 -> Y1 Y2, S1 -> t2, S1 -> t2, N0 -> t1, N0 -> Y1 X1, N0 -> Y2 N0, N0 -> t0, N0 -> Y1 X3, N0 -> Y1 X4, N0 -> Y1 X5, N0 -> t0, N0 -> Y1 N0, N0 -> Y1 N0, N0 -> Y1 Y2, N0 -> Y1 Y2, N0 -> t2, N0 -> t2, X1 -> Y2 X2, X2 -> N0 N0, X3 -> N0 N0, X4 -> Y2 N0, X5 -> Y2 N0, Y1 -> t2, Y2 -> t0}\n");
    Cfg cfg = CfgParser.parseCfgReader(reader);
    assertEquals("G = <N, T, S, P>\n"
        + "N = {S1, N0, X1, X2, X3, X4, X5, Y1, Y2, N1, N2}\n"
        + "T = {t0, t1, t2}\n" + "S = S1\n"
        + "P = {S1 -> ε, S1 -> t1, S1 -> Y2 N0, S1 -> t0, S1 -> t2, N0 -> t1, N0 -> Y2 N0, N0 -> t0, N0 -> t2, X1 -> Y2 X2, X2 -> N0 N0, X3 -> N0 N0, X4 -> Y2 N0, X5 -> Y2 N0, Y1 -> t2, Y2 -> t0, N1 -> Y2, N1 -> N0, N1 -> X5, N1 -> X4, N1 -> X3, N1 -> X1, S1 -> Y1 N1, N2 -> Y2, N2 -> N0, N2 -> X5, N2 -> X4, N2 -> X3, N2 -> X1, N0 -> Y1 N2}\n", cfg.getLeftFactoredCfg().toString());
  }

  @Test public void testParseCfgAndLeftFactor2() throws ParseException {
    StringReader reader = new StringReader(
        "N = {S, A, B}\n" + "T = {a, b}\n" + "S = S\n"
            + "P = {S -> a B | b A, A -> a S | b A A | a, B -> b S | a B B | b}");
    Cfg cfg = CfgParser.parseCfgReader(reader);
    assertEquals("G = <N, T, S, P>\n" + "N = {S, A, B, N1, N2}\n"
        + "T = {a, b}\n" + "S = S\n"
        + "P = {S -> a B, S -> b A, A -> b A A, B -> a B B, N1 -> ε, N1 -> S, A -> a N1, N2 -> ε, N2 -> S, B -> b N2}\n", cfg.getLeftFactoredCfg().toString());
  }

  @Test(expected = ParseException.class)
  public void testParseCfgWithSpecialCharacters() throws ParseException {
    StringReader reader = new StringReader(
        "G = <N, T, S, P>\n" + "N = {S, q_0}\n" + "T = {a, b}\n" + "S = q_0\n"
            + "P = {S -> S a, S -> S b, q_0 -> S a, q_0 -> S b, S -> ε}");
    CfgParser.parseCfgReader(reader);
  }

  @Test(expected = ParseException.class)
  public void testParseCfgWithMalFormedGrammar() throws ParseException {
    StringReader reader = new StringReader(
        "G = <N, T, S, P>\n" + "N = {N0, N1\n" + "T = {t0, t1}\n" + "S = N1\n"
            + "P = {N1 -> t0 t1}");
    CfgParser.parseCfgReader(reader);
  }
}

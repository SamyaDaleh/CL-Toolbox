package com.github.samyadaleh.cltoolbox.common;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

import com.github.samyadaleh.cltoolbox.common.GrammarParser;
import com.github.samyadaleh.cltoolbox.common.cfg.Cfg;
import com.github.samyadaleh.cltoolbox.common.cfg.Pcfg;
import com.github.samyadaleh.cltoolbox.common.lcfrs.Srcg;
import com.github.samyadaleh.cltoolbox.common.tag.Tag;

public class GrammarParserTest {

  @Test public void testparseCfgFile() throws IOException {
    Cfg cfg = GrammarParser.parseCfgFile("./resources/grammars/anbn.cfg");
    assert cfg != null;
    assertEquals("G = <N, T, S, P>\n" + "N = {S}\n" + "T = {a, b}\n" + "S = S\n"
      + "P = {S -> a S b, S -> a b}\n", cfg.toString());
  }

  @Test public void testparsePcfgFile() throws IOException {
    Pcfg pcfg = GrammarParser.parsePcfgFile("./resources/grammars/a0n.pcfg");
    assert pcfg != null;
    assertEquals(
      "G = <N, T, S, P>\n" + "N = {S, A, B}\n" + "T = {0, 1}\n" + "S = S\n"
        + "P = {1.0 : S -> A B, 0.7 : A -> 1, 0.3 : A -> 0, "
        + "1.0 : B -> 0}\n",
      pcfg.toString());
  }

  @Test public void testparseSrcgFile() throws IOException {
    Srcg srcg =
      GrammarParser.parseSrcgFile("./resources/grammars/anbmcndm.srcg");
    assert srcg != null;
    assertEquals("G = <N, T, V, P, S>\n" + "N = {A, B, S}\n"
      + "T = {a, b, c, d}\n" + "V = {X, Y, V, W}\n"
      + "P = {S(X V Y W) -> A(X,Y) B(V,W), B(ε,ε) -> ε, B(b X,d Y) -> B(X,Y), "
      + "A(a,c) -> ε, A(a X,c Y) -> A(X,Y)}\n" + "S = S\n", srcg.toString());
  }

  @Test public void testparseTagFile() throws IOException {
    Tag tag = GrammarParser.parseTagFile("./resources/grammars/anbncndn.tag");
    assert tag != null;
    assertEquals(
      "G = <N, T, I, A, S>\n" + "N = {S}\n" + "T = {a, b, c, d}\n"
        + "I = {α1 : (S_OA (ε ))}\n"
        + "A = {β : (S_NA (a )(S (b )(S* )(c ))(d ))}\n" + "S = S\n",
      tag.toString());
  }

}

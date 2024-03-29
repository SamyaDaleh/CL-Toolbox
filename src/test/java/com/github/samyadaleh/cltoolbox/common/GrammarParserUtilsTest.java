package com.github.samyadaleh.cltoolbox.common;

import static org.junit.Assert.assertEquals;

import java.io.*;
import java.text.ParseException;

import com.github.samyadaleh.cltoolbox.common.parser.CfgParser;
import com.github.samyadaleh.cltoolbox.common.parser.PcfgParser;
import com.github.samyadaleh.cltoolbox.common.parser.SrcgParser;
import com.github.samyadaleh.cltoolbox.common.parser.TagParser;
import org.junit.Test;

import com.github.samyadaleh.cltoolbox.common.cfg.Cfg;
import com.github.samyadaleh.cltoolbox.common.cfg.Pcfg;
import com.github.samyadaleh.cltoolbox.common.lcfrs.Srcg;
import com.github.samyadaleh.cltoolbox.common.tag.Tag;

public class GrammarParserUtilsTest {

  @Test public void testparseCfgFile() throws IOException, ParseException {
    BufferedReader grammarReader =
        new BufferedReader(new FileReader("./src/test/resources/grammars/cfg/anbn.cfg"));
    Cfg cfg = CfgParser.parseCfgReader(grammarReader);
    assertEquals("G = <N, T, S, P>\n" + "N = {S}\n" + "T = {a, b}\n" + "S = S\n"
        + "P = {S -> a S b, S -> a b}\n", cfg.toString());
  }

  @Test public void testparsePcfgFile() throws IOException, ParseException {
    BufferedReader grammarReader =
        new BufferedReader(new FileReader("./src/test/resources/grammars/pcfg/a0n.pcfg"));
    Pcfg pcfg = PcfgParser.parsePcfgReader(grammarReader);
    assertEquals(
        "G = <N, T, S, P>\n" + "N = {S, A, B}\n" + "T = {0, 1}\n" + "S = S\n"
            + "P = {1.0 : S -> A B, 0.7 : A -> 1, 0.3 : A -> 0, "
            + "1.0 : B -> 0}\n", pcfg.toString());
  }

  @Test public void testparseSrcgFile() throws IOException, ParseException {
    BufferedReader grammarReader = new BufferedReader(
        new FileReader("./src/test/resources/grammars/srcg/anbmcndm.srcg"));
    Srcg srcg = SrcgParser.parseSrcgReader(grammarReader);
    assertEquals(
        "G = <N, T, V, P, S>\n" + "N = {A, B, S}\n" + "T = {a, b, c, d}\n"
            + "V = {X, Y, V, W}\n"
            + "P = {S(X V Y W) -> A(X,Y) B(V,W), B(ε,ε) -> ε, B(b X,d Y) -> B(X,Y), "
            + "A(a,c) -> ε, A(a X,c Y) -> A(X,Y)}\n" + "S = S\n",
        srcg.toString());
  }

  @Test public void testparseTagFile() throws IOException, ParseException {
    Reader reader = new FileReader("./src/test/resources/grammars/tag/anbncndn.tag");
    Tag tag = TagParser.parseTagReader(reader);
    assertEquals("G = <N, T, I, A, S>\n" + "N = {S}\n" + "T = {a, b, c, d}\n"
            + "I = {α1 : (S_OA (ε ))}\n"
            + "A = {β : (S_NA (a )(S (b )(S* )(c ))(d ))}\n" + "S = S\n",
        tag.toString());
  }

}

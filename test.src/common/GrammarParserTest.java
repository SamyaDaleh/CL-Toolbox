package common;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.text.ParseException;

import org.junit.Test;

import common.cfg.Cfg;
import common.lcfrs.Srcg;
import common.tag.Tag;

public class GrammarParserTest {

  @Test public void testparseCfgFile() throws IOException {
    Cfg cfg = GrammarParser.parseCfgFile("./resources/grammars/anbn.cfg");
    assertEquals("G = <N, T, S, P>\n" + "N = {S}\n" + "T = {a, b}\n" + "S = S\n"
      + "P = {S -> a S b, S -> a b}\n", cfg.toString());
  }

  @Test public void testparseTagFile() throws IOException, ParseException {
    Tag tag = GrammarParser.parseTagFile("./resources/grammars/anbncndn.tag");
    assertEquals("G = <N, T, I, A, S>\n"
        + "N = {S}\n"
        + "T = {a, b, c, d}\n"
        + "I = {α1 : (S_OA (ε ))}\n"
        + "A = {β : (S_NA (a )(S (b )(S* )(c ))(d ))}\n"
        + "S = S\n", tag.toString());
  }

  @Test public void testparseSrcgFile() throws IOException {
    Srcg srcg =
      GrammarParser.parseSrcgFile("./resources/grammars/anbmcndm.srcg");
    assertEquals("G = <N, T, V, P, S>\n" + "N = {A, B, S}\n"
        + "T = {a, b, c, d}\n" + "V = {X, Y, V, W}\n"
        + "P = {S(X V Y W) -> A(X,Y) B(V,W), B(ε,ε) -> ε, B(b X,d Y) -> B(X,Y), " 
        + "A(a,c) -> ε, A(a X,c Y) -> A(X,Y)}\n"
        + "S = S\n", srcg.toString());
  }

}

package com.github.samyadaleh.cltoolbox.common;

import com.github.samyadaleh.cltoolbox.common.cfg.Cfg;
import com.github.samyadaleh.cltoolbox.common.parser.CfgParser;
import org.junit.Test;

import java.io.Reader;
import java.io.StringReader;
import java.text.ParseException;

import static org.junit.Assert.assertEquals;

public class GrammarParserUtilsStringTest {

  @Test public void testparseCfgString() throws ParseException {
    String cfgString =
        "G = <N, T, S, P>\n" + "N = {S}\n" + "T = {a, b}\n" + "S = S\n"
            + "P = {S -> a S b, S -> a b}\n";
    Reader reader = new StringReader(cfgString);
    Cfg cfg = CfgParser.parseCfgReader(reader);
    assertEquals(cfgString, cfg.toString());
  }
}

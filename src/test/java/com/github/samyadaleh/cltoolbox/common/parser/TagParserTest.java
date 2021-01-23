package com.github.samyadaleh.cltoolbox.common.parser;

import com.github.samyadaleh.cltoolbox.common.tag.Tag;
import org.junit.Test;

import java.io.StringReader;
import java.text.ParseException;

import static org.junit.Assert.assertEquals;

public class TagParserTest {

  @Test public void testParseTag() throws ParseException {
    StringReader reader = new StringReader(
        "G = <N, T, I, A, S>\n" + "I = {α1 : (N0 (t0 )(t1 ))}\n" + "A = {}\n"
            + "N = {N0}\n" + "T = {t0, t1}\n" + "S = N0\n");
    Tag tag = TagParser.parseTagReader(reader);
    assertEquals("N0", tag.getStartSymbol());
  }

  @Test public void testParseEpsilonTag() throws ParseException {
    StringReader reader = new StringReader(
        "G = <N, T, I, A, S>\n" + "N = {N0}\n" + "T = {t0, t1}\n"
            + "I = {α1 : (N0 (ε )), α3 : (N0 (t0 )(N0 )), α5 : (N0 (t1 ))}\n"
            + "A = {}\n" + "S = N0");
    Tag tag = TagParser.parseTagReader(reader);
    assertEquals("N0", tag.getStartSymbol());
  }

}

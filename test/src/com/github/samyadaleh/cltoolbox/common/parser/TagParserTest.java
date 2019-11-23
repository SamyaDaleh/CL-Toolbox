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
    assert tag != null;
    assertEquals("N0", tag.getStartSymbol());
  }
}

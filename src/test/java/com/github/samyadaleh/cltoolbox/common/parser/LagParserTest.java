package com.github.samyadaleh.cltoolbox.common.parser;

import com.github.samyadaleh.cltoolbox.common.lag.Lag;
import org.junit.Test;

import java.io.StringReader;
import java.text.ParseException;

import static org.junit.Assert.assertEquals;

public class LagParserTest {

  @Test public void testParseLag() throws ParseException {
    StringReader reader = new StringReader(
        "G = <W, C, LX, CO, RP, ST_S, ST_F>\n"
            + "LX = {[a (b c)], [b (b)], [c(c)]}\n"
            + "ST_S = {({r1, r2} (b c))}\n" + "RP = {\n"
            + "r1 : [(X) (b c)] -> [{r1, r2} (b X c)],\n"
            + "r2 : [(b X c) (b)] -> [{r2, r3} (X c)],\n"
            + "r3 : [(c X) (c)] -> [{r3} (X)] }\n" + "ST_F = {({r3} ε)}\n");
    Lag lag = LagParser.parseLagReader(reader);
    assertEquals("r3", lag.getFinalStates()[0].getCategory()[0]);
  }

}

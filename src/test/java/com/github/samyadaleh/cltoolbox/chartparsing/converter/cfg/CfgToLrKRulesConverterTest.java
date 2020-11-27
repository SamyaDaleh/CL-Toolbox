package com.github.samyadaleh.cltoolbox.chartparsing.converter.cfg;

import com.github.samyadaleh.cltoolbox.chartparsing.ParsingSchema;
import com.github.samyadaleh.cltoolbox.common.TestGrammarLibrary;
import com.github.samyadaleh.cltoolbox.common.cfg.Cfg;
import com.github.samyadaleh.cltoolbox.common.parser.CfgParser;
import org.junit.Test;

import java.io.StringReader;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class CfgToLrKRulesConverterTest {

  @Test public void testBasicMethods() {
    Cfg cfg = TestGrammarLibrary.lrCfg();
    String[] initialState = CfgToLrKRulesConverter.computeInitialState(cfg, 0);
    assertEquals("NP' -> •NP", initialState[0]);
    List<List<String[]>> states =
        CfgToLrKRulesConverter.computeStates(cfg, initialState, 0);
    assertEquals(7,states.size());
    assertEquals(4,states.get(0).size());
    Map<String, String> parseTable = CfgToLrKRulesConverter
        .computeParseTable(states, initialState, 0, new ParsingSchema(), cfg, 0);
    assertEquals(11,parseTable.size());
  }

  @Test public void testReduceReduceConflict() throws ParseException {
    StringReader reader = new StringReader(
        "N = {S, A, B}\n" + "T = {a}\n" + "S = S\n"
            + "P = {S -> A, S -> B, A -> a, B -> a}");
    Cfg cfg = CfgParser.parseCfgReader(reader);
    String[] initialState = CfgToLrKRulesConverter.computeInitialState(cfg, 0);
    assertEquals("S' -> •S", initialState[0]);
    List<List<String[]>> states =
        CfgToLrKRulesConverter.computeStates(cfg, initialState, 0);
    assertEquals("S' -> •S", states.get(0).get(0)[0]);
    assertEquals("S -> •A", states.get(0).get(1)[0]);
    assertEquals("S -> •B", states.get(0).get(2)[0]);
    assertEquals("A -> •a", states.get(0).get(3)[0]);
    assertEquals("B -> •a", states.get(0).get(4)[0]);
    assertEquals(5,states.size());
    assertEquals(5,states.get(0).size());
    Map<String, String> parseTable = CfgToLrKRulesConverter
        .computeParseTable(states, initialState, 0, new ParsingSchema(), cfg, 0);
    assertEquals(8,parseTable.size());
    assertEquals("r3, r4", parseTable.get("4 $"));
  }
}

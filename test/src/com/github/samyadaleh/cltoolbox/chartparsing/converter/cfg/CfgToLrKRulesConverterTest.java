package com.github.samyadaleh.cltoolbox.chartparsing.converter.cfg;

import com.github.samyadaleh.cltoolbox.chartparsing.ParsingSchema;
import com.github.samyadaleh.cltoolbox.common.TestGrammarLibrary;
import com.github.samyadaleh.cltoolbox.common.cfg.Cfg;
import org.junit.Test;

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
}

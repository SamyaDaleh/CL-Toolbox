package com.github.samyadaleh.cltoolbox.chartparsing.cfg.util;

import com.github.samyadaleh.cltoolbox.common.TestGrammarLibrary;
import com.github.samyadaleh.cltoolbox.common.cfg.Cfg;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

public class FactoringTest {
  @Test public void testFactoring() {
    Cfg cfg = TestGrammarLibrary.leftFactorCfg().getLeftFactoredCfg();
    assertEquals("", cfg.toString());
  }
}

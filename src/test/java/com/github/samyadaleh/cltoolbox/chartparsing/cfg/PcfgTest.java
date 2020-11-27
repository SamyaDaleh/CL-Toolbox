package com.github.samyadaleh.cltoolbox.chartparsing.cfg;

import com.github.samyadaleh.cltoolbox.common.TestGrammarLibrary;
import com.github.samyadaleh.cltoolbox.common.cfg.Cfg;
import com.github.samyadaleh.cltoolbox.common.cfg.Pcfg;
import org.junit.Test;

import static junit.framework.TestCase.assertTrue;

public class PcfgTest {

  @Test public void testPcfgCNF() {
    Pcfg pcfg = TestGrammarLibrary.testCnfPcfg();
    Cfg cfg = new Cfg(pcfg);
    assertTrue(cfg.isInChomskyNormalForm());
  }
}

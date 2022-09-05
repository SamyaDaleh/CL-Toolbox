package com.github.samyadaleh.cltoolbox.chartparsing.cfg;

import com.github.samyadaleh.cltoolbox.common.GrammarLoader;
import com.github.samyadaleh.cltoolbox.common.cfg.Cfg;
import com.github.samyadaleh.cltoolbox.common.cfg.Pcfg;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.text.ParseException;

import static junit.framework.TestCase.assertTrue;

public class PcfgTest {

  @Test public void testPcfgCNF() throws FileNotFoundException, ParseException {
    Pcfg pcfg = GrammarLoader.readPcfg("testcnf.pcfg");
    Cfg cfg = new Cfg(pcfg);
    assertTrue(cfg.isInChomskyNormalForm());
  }
}

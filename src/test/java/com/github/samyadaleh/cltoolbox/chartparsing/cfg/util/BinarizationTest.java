package com.github.samyadaleh.cltoolbox.chartparsing.cfg.util;

import com.github.samyadaleh.cltoolbox.common.GrammarLoader;
import com.github.samyadaleh.cltoolbox.common.cfg.Cfg;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.text.ParseException;

import static junit.framework.TestCase.assertEquals;

public class BinarizationTest {
  @Test public void testBinarization()
      throws FileNotFoundException, ParseException {
    Cfg cfg = GrammarLoader.readCfg("exceptiononbinarize.cfg");
    Cfg cfgOut = cfg.getBinarizedCfg();
    assertEquals("G = <N, T, S, P>\n" + "N = {N0, N2, N1}\n"
        + "T = {t0, t1, t2}\n" + "S = N2\n"
        + "P = {N0 -> ε, N0 -> t1, N0 -> t0 N0, N2 -> N0}\n", cfgOut.toString());
  }
}

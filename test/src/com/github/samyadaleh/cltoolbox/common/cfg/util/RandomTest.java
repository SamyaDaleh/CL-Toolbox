package com.github.samyadaleh.cltoolbox.common.cfg.util;

import java.text.ParseException;

import org.junit.Test;

import com.github.samyadaleh.cltoolbox.common.cfg.Cfg;
import com.github.samyadaleh.cltoolbox.common.cfg.util.Random;

public class RandomTest {
  @Test public void testRandomCfg() throws ParseException {
    Cfg cfg = Random.getRandomCfg();
    System.out.println(cfg);
  }
}

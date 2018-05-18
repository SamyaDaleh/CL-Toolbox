package common.cfg.util;

import java.text.ParseException;

import org.junit.Test;

import common.cfg.Cfg;

public class RandomTest {
  @Test public void testRandomCfg() throws ParseException {
    Cfg cfg = Random.getRandomCfg();
    System.out.println(cfg);
  }
}

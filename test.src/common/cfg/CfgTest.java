package common.cfg;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class CfgTest {
  static Cfg gen_cfgbintest() {
    Cfg G = new Cfg();

    G.setTerminals(new String[] {"a", "b"});
    G.setVars(new String[] {"S"});
    G.setR(new String[][] {{"S", "a S b S S a S b a b"}, {"S", "a b"}});
    G.setStart_var("S");

    return G;
  }

  @Test public void testBinarization() {
    assertTrue(!gen_cfgbintest().isBinarized());
    Cfg cfgbin = gen_cfgbintest().binarize();
    assertTrue(cfgbin.isBinarized());
  }

  @Test public void testRemoveEpsilon() {

    Cfg cfgeps = new Cfg();
    cfgeps.setTerminals(new String[] {"a", "b"});
    cfgeps.setVars(new String[] {"S", "A", "B", "C"});
    cfgeps.setR(new String[][] {{"A", "ε"}, {"S", ""}, {"C", ""},
      {"S", "b A a S b C"}, {"A", "a"}, {"A", "b B"}, {"B", "b"}});
    cfgeps.setStart_var("S");
    assertTrue(cfgeps.hasEpsilonProductions());

    Cfg epsfree = cfgeps.removeEmptyProductions();

    assertTrue(!epsfree.hasEpsilonProductions());
  }
}
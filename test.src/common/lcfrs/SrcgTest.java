package common.lcfrs;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import common.cfg.Cfg;

public class SrcgTest {

  @Test public void testOrder() {
    Srcg srcg1 = new Srcg();
    srcg1.setNonterminals(new String[] {"S", "A"});
    srcg1.setTerminals(new String[] {"a"});
    srcg1.setVariables(new String[] {"X1", "X2"});
    srcg1.setStartSymbol("S");
    srcg1.addClause("S (X1 X2)", "A(X2, X1)");
    srcg1.addClause("A (a X1, b X2)", "A(X1, X2)");
    srcg1.addClause("A (ε,ε)", "ε");
    assertTrue(!srcg1.isOrdered());

  }

  @Test public void testEmptyProductions() {
    Srcg srcg1 = new Srcg();
    srcg1.setNonterminals(new String[] {"S", "A"});
    srcg1.setTerminals(new String[] {"a"});
    srcg1.setVariables(new String[] {"X1", "X2"});
    srcg1.setStartSymbol("S");
    srcg1.addClause("S (X1 X2)", "A(X2, X1)");
    srcg1.addClause("A (a X1, b X2)", "A(X1, X2)");
    srcg1.addClause("A (ε,ε)", "ε");
    assertTrue(srcg1.hasEpsilonProductions());
  }

  @Test public void testCfgToSrcgConversion() {

    Cfg cfgeps = new Cfg();
    cfgeps.setTerminals(new String[] {"a", "b"});
    cfgeps.setVars(new String[] {"S", "A", "B", "C"});
    cfgeps.setR(new String[][] {{"A", "ε"}, {"S", ""}, {"C", ""},
      {"S", "b A a S b C"}, {"A", "a"}, {"A", "b B"}, {"B", "b"}});
    cfgeps.setStart_var("S");

    Srcg srcg = new Srcg(cfgeps);
    assertEquals("G = <N, T, V, P, S>\n" + "N = {S, A, B, C}\n"
      + "T = {a, b}\n" + "V = {X1}\n"
      + "P = {A(ε) -> ε, S(ε) -> ε, C(ε) -> ε, " 
      + "S(b X1 a X1 b X1) -> A(X1) S(X1) C(X1), A(a) -> ε, " 
      + "A(b X1) -> B(X1), B(b) -> ε}\n"
      + "S = S\n", srcg.toString());
  }

}

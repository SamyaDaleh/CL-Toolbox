package common.lcfrs;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.text.ParseException;

import org.junit.Test;

import common.cfg.Cfg;

public class SrcgTest {

  @Test public void testOrder() throws ParseException {
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

  @Test public void testEmptyProductions() throws ParseException {
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

  @Test public void testCfgToSrcgConversion() throws ParseException {

    Cfg cfgeps = new Cfg();
    cfgeps.setTerminals(new String[] {"a", "b"});
    cfgeps.setNonterminals(new String[] {"S", "A", "B", "C"});
    cfgeps.setProductionrules(new String[][] {{"A", "ε"}, {"S", ""}, {"C", ""},
      {"S", "b A a S b C"}, {"A", "a"}, {"A", "b B"}, {"B", "b"}});
    cfgeps.setStartSymbol("S");

    Srcg srcg = new Srcg(cfgeps);
    assertEquals("G = <N, T, V, P, S>\n" + "N = {S, A, B, C}\n"
      + "T = {a, b}\n" + "V = {X1, X2, X3}\n"
      + "P = {A(ε) -> ε, S(ε) -> ε, C(ε) -> ε, " 
      + "S(b X1 a X2 b X3) -> A(X1) S(X2) C(X3), A(a) -> ε, " 
      + "A(b X1) -> B(X1), B(b) -> ε}\n"
      + "S = S\n", srcg.toString());
  }

  @Test public void testCfgToSrcgConversion2() throws ParseException {

    Cfg cfgeps = new Cfg();
    cfgeps.setTerminals(new String[] {"a", "b"});
    cfgeps.setNonterminals(new String[] {"S", "X1", "Y1", "Y2"});
    cfgeps.setProductionrules(new String[][] {{"Y1", "a"}, {"S", "Y1 X1"}, {"Y2", "b"},
      {"X1", "S Y2"}, {"S", "Y1 Y2"}});
    cfgeps.setStartSymbol("S");

    Srcg srcg = new Srcg(cfgeps);
    assertEquals("G = <N, T, V, P, S>\n" + "N = {S, X1, Y1, Y2}\n"
      + "T = {a, b}\n" + "V = {X2, X3}\n"
      + "P = {Y1(a) -> ε, S(X2 X3) -> Y1(X2) X1(X3), Y2(b) -> ε, "
      + "X1(X2 X3) -> S(X2) Y2(X3), S(X2 X3) -> Y1(X2) Y2(X3)}\n"
      + "S = S\n", srcg.toString());
    
  }
  

}

package com.github.samyadaleh.cltoolbox.chartparsing.cfg;

import java.util.Map;

import org.junit.Test;

import com.github.samyadaleh.cltoolbox.chartparsing.cfg.cyk.astar.SxCalc;
import com.github.samyadaleh.cltoolbox.common.cfg.Pcfg;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SxCalcTest {

  private static Pcfg gen_pcfg0() {
    Pcfg pcfg = new Pcfg();

    pcfg.setTerminals(new String[] {"a", "b"});
    pcfg.setNonterminals(new String[] {"S", "A", "B"});
    pcfg.setProductionRules(new String[][] {{"S", "A B", "1"}, {"A", "b", "0.7"},
      {"A", "a", "0.3"}, {"B", "B B", "0.6"}, {"B", "a", "0.4"}});
    pcfg.setStartSymbol("S");

    return pcfg;
  }

  @Test public void testInAndOutsodes() {
    Map<String, Double> insides = SxCalc.getInsides(gen_pcfg0(), 4);
    assertEquals(0.35667494393873245, insides.get("in(A,1)"), 0.0);
    assertEquals(0.35667494393873245, insides.get("in(A,1)"), 0.0);
    assertEquals(1.7976931348623157E308, insides.get("in(A,2)"), 0.0);
    assertEquals(1.7976931348623157E308, insides.get("in(A,3)"), 0.0);
    assertEquals(1.7976931348623157E308, insides.get("in(A,4)"), 0.0);
    assertEquals(0.916290731874155, insides.get("in(B,1)"), 0.0);
    assertEquals(2.3434070875143007, insides.get("in(B,2)"), 0.0);
    assertEquals(3.7705234431544463, insides.get("in(B,3)"), 0.0);
    assertEquals(5.197639798794592, insides.get("in(B,4)"), 0.0);
    assertEquals(1.7976931348623157E308, insides.get("in(S,1)"), 0.0);
    assertEquals(1.2729656758128876, insides.get("in(S,2)"), 0.0);
    assertEquals(2.700082031453033, insides.get("in(S,3)"), 0.0);
    assertEquals(4.127198387093179, insides.get("in(S,4)"), 0.0);

    Map<String, Double> outsides = SxCalc.getOutsides(insides, 4, gen_pcfg0());
    assertEquals(0.0, outsides.get("out(S,0,4,0)"), 0.0);
    assertEquals(1.7976931348623157E308, outsides.get("out(A,0,4,0)"), 0.0);
    assertEquals(1.7976931348623157E308, outsides.get("out(B,0,4,0)"), 0.0);
    assertEquals(1.7976931348623157E308, outsides.get("out(S,0,3,1)"), 0.0);
    assertEquals(1.7976931348623157E308, outsides.get("out(S,1,3,0)"), 0.0);
    assertEquals(0.916290731874155, outsides.get("out(A,0,3,1)"), 0.0);
    assertEquals(1.7976931348623157E308, outsides.get("out(A,1,3,0)"), 0.0);
    assertEquals(1.7976931348623157E308, outsides.get("out(B,0,3,1)"), 0.0);
    assertEquals(0.35667494393873245, outsides.get("out(B,1,3,0)"), 0.0);
    assertEquals(1.7976931348623157E308, outsides.get("out(S,0,2,2)"), 0.0);
    assertEquals(1.7976931348623157E308, outsides.get("out(S,1,2,1)"), 0.0);
    assertEquals(1.7976931348623157E308, outsides.get("out(S,2,2,0)"), 0.0);
    assertEquals(2.3434070875143007, outsides.get("out(A,0,2,2)"), 0.0);
    assertEquals(1.7976931348623157E308, outsides.get("out(A,1,2,1)"), 0.0);
    assertEquals(1.7976931348623157E308, outsides.get("out(A,2,2,0)"), 0.0);
    assertEquals(1.7976931348623157E308, outsides.get("out(B,0,2,2)"), 0.0);
    assertEquals(1.783791299578878, outsides.get("out(B,1,2,1)"), 0.0);
    assertEquals(1.783791299578878, outsides.get("out(B,2,2,0)"), 0.0);
    assertEquals(1.7976931348623157E308, outsides.get("out(S,0,1,3)"), 0.0);
    assertEquals(1.7976931348623157E308, outsides.get("out(S,1,1,2)"), 0.0);
    assertEquals(1.7976931348623157E308, outsides.get("out(S,2,1,1)"), 0.0);
    assertEquals(1.7976931348623157E308, outsides.get("out(S,3,1,0)"), 0.0);
    assertEquals(3.7705234431544463, outsides.get("out(A,0,1,3)"), 0.0);
    assertEquals(1.7976931348623157E308, outsides.get("out(A,1,1,2)"), 0.0);
    assertEquals(1.7976931348623157E308, outsides.get("out(A,2,1,1)"), 0.0);
    assertEquals(1.7976931348623157E308, outsides.get("out(A,3,1,0)"), 0.0);
    assertEquals(1.7976931348623157E308, outsides.get("out(B,0,1,3)"), 0.0);
    assertEquals(3.2109076552190237, outsides.get("out(B,1,1,2)"), 0.0);
    assertEquals(3.2109076552190237, outsides.get("out(B,2,1,1)"), 0.0);
    assertEquals(3.2109076552190237, outsides.get("out(B,3,1,0)"), 0.0);

  }

}

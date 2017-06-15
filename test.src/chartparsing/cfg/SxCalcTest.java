package chartparsing.cfg;

import java.util.Map;

import org.junit.Test;

import chartparsing.cfg.SxCalc;

import static org.junit.Assert.assertTrue;
import common.cfg.Pcfg;

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
    assertTrue(insides.get("in(A,1)") == 0.35667494393873245);
    assertTrue(insides.get("in(A,1)") == 0.35667494393873245);
    assertTrue(insides.get("in(A,2)") == 1.7976931348623157E308);
    assertTrue(insides.get("in(A,3)") == 1.7976931348623157E308);
    assertTrue(insides.get("in(A,4)") == 1.7976931348623157E308);
    assertTrue(insides.get("in(B,1)") == 0.916290731874155);
    assertTrue(insides.get("in(B,2)") == 2.3434070875143007);
    assertTrue(insides.get("in(B,3)") == 3.7705234431544463);
    assertTrue(insides.get("in(B,4)") == 5.197639798794592);
    assertTrue(insides.get("in(S,1)") == 1.7976931348623157E308);
    assertTrue(insides.get("in(S,2)") == 1.2729656758128876);
    assertTrue(insides.get("in(S,3)") == 2.700082031453033);
    assertTrue(insides.get("in(S,4)") == 4.127198387093179);

    Map<String, Double> outsides = SxCalc.getOutsides(insides, 4, gen_pcfg0());
    assertTrue(outsides.get("out(S,0,4,0)") == 0.0);
    assertTrue(outsides.get("out(A,0,4,0)") == 1.7976931348623157E308);
    assertTrue(outsides.get("out(B,0,4,0)") == 1.7976931348623157E308);
    assertTrue(outsides.get("out(S,0,3,1)") == 1.7976931348623157E308);
    assertTrue(outsides.get("out(S,1,3,0)") == 1.7976931348623157E308);
    assertTrue(outsides.get("out(A,0,3,1)") == 0.916290731874155);
    assertTrue(outsides.get("out(A,1,3,0)") == 1.7976931348623157E308);
    assertTrue(outsides.get("out(B,0,3,1)") == 1.7976931348623157E308);
    assertTrue(outsides.get("out(B,1,3,0)") == 0.35667494393873245);
    assertTrue(outsides.get("out(S,0,2,2)") == 1.7976931348623157E308);
    assertTrue(outsides.get("out(S,1,2,1)") == 1.7976931348623157E308);
    assertTrue(outsides.get("out(S,2,2,0)") == 1.7976931348623157E308);
    assertTrue(outsides.get("out(A,0,2,2)") == 2.3434070875143007);
    assertTrue(outsides.get("out(A,1,2,1)") == 1.7976931348623157E308);
    assertTrue(outsides.get("out(A,2,2,0)") == 1.7976931348623157E308);
    assertTrue(outsides.get("out(B,0,2,2)") == 1.7976931348623157E308);
    assertTrue(outsides.get("out(B,1,2,1)") == 1.783791299578878);
    assertTrue(outsides.get("out(B,2,2,0)") == 1.783791299578878);
    assertTrue(outsides.get("out(S,0,1,3)") == 1.7976931348623157E308);
    assertTrue(outsides.get("out(S,1,1,2)") == 1.7976931348623157E308);
    assertTrue(outsides.get("out(S,2,1,1)") == 1.7976931348623157E308);
    assertTrue(outsides.get("out(S,3,1,0)") == 1.7976931348623157E308);
    assertTrue(outsides.get("out(A,0,1,3)") == 3.7705234431544463);
    assertTrue(outsides.get("out(A,1,1,2)") == 1.7976931348623157E308);
    assertTrue(outsides.get("out(A,2,1,1)") == 1.7976931348623157E308);
    assertTrue(outsides.get("out(A,3,1,0)") == 1.7976931348623157E308);
    assertTrue(outsides.get("out(B,0,1,3)") == 1.7976931348623157E308);
    assertTrue(outsides.get("out(B,1,1,2)") == 3.2109076552190237);
    assertTrue(outsides.get("out(B,2,1,1)") == 3.2109076552190237);
    assertTrue(outsides.get("out(B,3,1,0)") == 3.2109076552190237);

  }

}

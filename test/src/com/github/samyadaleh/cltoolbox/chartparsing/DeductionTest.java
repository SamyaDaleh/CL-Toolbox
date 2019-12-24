package com.github.samyadaleh.cltoolbox.chartparsing;

import com.github.samyadaleh.cltoolbox.chartparsing.converter.ccg.CcgToDeductionRulesConverter;
import com.github.samyadaleh.cltoolbox.chartparsing.converter.cfg.*;
import com.github.samyadaleh.cltoolbox.chartparsing.converter.lcfrs.LcfrsToCykRulesConverter;
import com.github.samyadaleh.cltoolbox.chartparsing.converter.lcfrs.LcfrsToEarleyRulesConverter;
import com.github.samyadaleh.cltoolbox.chartparsing.converter.pcfg.PcfgToAstarRulesConverter;
import com.github.samyadaleh.cltoolbox.chartparsing.converter.pcfg.PcfgToCykRulesConverter;
import com.github.samyadaleh.cltoolbox.chartparsing.converter.tag.TagToCykRulesConverter;
import com.github.samyadaleh.cltoolbox.chartparsing.converter.tag.TagToEarleyPrefixValidRulesConverter;
import com.github.samyadaleh.cltoolbox.chartparsing.converter.tag.TagToEarleyRulesConverter;
import com.github.samyadaleh.cltoolbox.common.TestGrammarLibrary;
import com.github.samyadaleh.cltoolbox.common.cfg.Cfg;
import com.github.samyadaleh.cltoolbox.common.lcfrs.Srcg;
import org.junit.Test;

import java.io.IOException;
import java.text.ParseException;
import java.util.Objects;

import static org.junit.Assert.*;

public class DeductionTest {

  @Test public void testCfgTopdown() throws ParseException {
    String w = "a a b b";
    ParsingSchema schema = CfgToTopDownRulesConverter
        .cfgToTopDownRules(Objects.requireNonNull(TestGrammarLibrary.anBnCfg()),
            w);
    Deduction deduction = new Deduction();
    assertTrue(deduction.doParse(schema, false));
    deduction.printTrace();
    assertEquals("(S (a )(S (a )(b ))(b ))",
        deduction.getDerivedTrees().get(0).toString());
    assertEquals(13, deduction.getChart().size());
    assertEquals(13, deduction.getDeductedFrom().size());
    assertEquals(13, deduction.getAppliedRule().size());
    assertEquals(13, deduction.getUsefulItem().length);
    assertEquals(13, deduction.getGoalItem().length);
    assertTrue(deduction.getUsefulItem()[0]);
    // depending on where the test runs one or the other prediction is performed first.
    assertTrue(deduction.getUsefulItem()[1] || deduction.getUsefulItem()[2]);
    assertTrue(deduction.getUsefulItem()[3] || deduction.getUsefulItem()[4]);
    assertTrue(deduction.getUsefulItem()[5] | deduction.getUsefulItem()[6]);
    assertTrue(deduction.getUsefulItem()[7]);
    assertFalse(deduction.getUsefulItem()[8]);
    assertTrue(deduction.getUsefulItem()[9]);
    assertFalse(deduction.getUsefulItem()[10]);
    assertFalse(deduction.getUsefulItem()[11]);
    assertTrue(deduction.getUsefulItem()[12]);
  }

  @Test public void testCfgTopdownEpsilon() throws ParseException {
    String w = "a a b b";
    ParsingSchema schema = CfgToTopDownRulesConverter.cfgToTopDownRules(
        Objects.requireNonNull(TestGrammarLibrary.anBnEpsilonCfg()), w);
    Deduction deduction = new Deduction();
    assertTrue(deduction.doParse(schema, false));
    deduction.printTrace();
    assertEquals("(S (a )(S (a )(S (ε ))(b ))(b ))",
        deduction.getDerivedTrees().get(0).toString());
  }

  @Test public void testCfgShiftreduce() throws ParseException {
    String w = "a a b b";
    ParsingSchema schema = CfgToShiftReduceRulesConverter.cfgToShiftReduceRules(
        Objects.requireNonNull(TestGrammarLibrary.anBnCfg()), w);
    Deduction deduction = new Deduction();
    assertTrue(deduction.doParse(schema, false));
    deduction.printTrace();
    assertEquals("(S (a )(S (a )(b ))(b ))",
        deduction.getDerivedTrees().get(0).toString());
  }

  @Test public void testCfgShiftreduceEpsilon() throws ParseException {
    String w = "t0 t1";
    Cfg cfg = new Cfg();
    cfg.setStartSymbol("N0");
    cfg.setTerminals(new String[] {"t0", "t1", "t2"});
    cfg.setNonterminals(new String[] {"N0"});
    try {
      cfg.addProductionRule("N0 -> ε");
      cfg.addProductionRule("N0 -> t0 t1");
    } catch (ParseException e) {
      throw new RuntimeException(e);
    }
    ParsingSchema schema =
        CfgToShiftReduceRulesConverter.cfgToShiftReduceRules(cfg, w);
    Deduction deduction = new Deduction();
    assertTrue(deduction.doParse(schema, false));
    deduction.printTrace();
    assertEquals("(N0 (t0 )(t1 ))",
        deduction.getDerivedTrees().get(0).toString());
  }

  @Test public void testCfgEarley() throws ParseException {
    String w = "a a b b";
    ParsingSchema schema = CfgToEarleyRulesConverter
        .cfgToEarleyRules(TestGrammarLibrary.anBnCfg(), w);
    Deduction deduction = new Deduction();
    assertTrue(deduction.doParse(schema, false));
    deduction.printTrace();
    assertEquals("(S (a )(S (a )(b ))(b ))",
        deduction.getDerivedTrees().get(0).toString());
  }

  @Test public void testCfgEarleyBottomup() throws ParseException {
    String w = "a a b b";
    ParsingSchema schema = CfgToEarleyRulesConverter
        .cfgToEarleyBottomupRules(TestGrammarLibrary.anBnCfg(), w);
    Deduction deduction = new Deduction();
    assertTrue(deduction.doParse(schema, false));
    deduction.printTrace();
    assertEquals("(S (a )(S (a )(b ))(b ))",
        deduction.getDerivedTrees().get(0).toString());
  }

  @Test public void testCfgEarleyPassive() throws ParseException {
    String w = "t0 t1";
    ParsingSchema schema = CfgToEarleyPassiveRulesConverter
        .cfgToEarleyPassiveRules(TestGrammarLibrary.earleyPassiveCfg(), w);
    Deduction deduction = new Deduction();
    assertTrue(deduction.doParse(schema, false));
    deduction.printTrace();
    assertEquals("(N1 (t0 )(t1 ))",
        deduction.getDerivedTrees().get(0).toString());
  }

  @Test public void testCfgEarleyBottomupMissingInitialize()
      throws ParseException {
    String w = "t0 t1";
    ParsingSchema schema = CfgToEarleyRulesConverter.cfgToEarleyBottomupRules(
        TestGrammarLibrary.earleyBottomUpMissingAxiomsCfg(), w);
    Deduction deduction = new Deduction();
    assertTrue(deduction.doParse(schema, false));
    deduction.printTrace();
    assertEquals("(N0 (t0 )(N0 (ε ))(t1 )(N0 (ε ))(N0 (ε )))",
        deduction.getDerivedTrees().get(0).toString());
  }

  @Test public void testCfgEarleyEpsilon() throws ParseException {
    String w = "t0 t1";
    Cfg cfg = new Cfg();
    cfg.setNonterminals(new String[] {"N0"});
    cfg.setTerminals(new String[] {"t0", "t1"});
    cfg.setStartSymbol("N0");
    cfg.addProductionRule("N0 -> N0 N0");
    cfg.addProductionRule("N0 -> ε");
    ParsingSchema schema = CfgToEarleyRulesConverter.cfgToEarleyRules(cfg, w);
    Deduction deduction = new Deduction();
    assertFalse(deduction.doParse(schema, false));
  }

  @Test public void testCfgLeftcorner() throws ParseException {
    String w = "a a b b";
    ParsingSchema schema = CfgToLeftCornerRulesConverter.cfgToLeftCornerRules(
        Objects.requireNonNull(TestGrammarLibrary.anBnCfg()), w);
    Deduction deduction = new Deduction();
    assertTrue(deduction.doParse(schema, false));
    deduction.printTrace();
    assertEquals("(S (a )(S (a )(b ))(b ))",
        deduction.getDerivedTrees().get(0).toString());
  }

  @Test public void testCfgLeftcornerBreak() throws ParseException {
    String w = "a b c d e f g h i";
    ParsingSchema schema = CfgToLeftCornerRulesConverter.cfgToLeftCornerRules(
        Objects.requireNonNull(TestGrammarLibrary.leftCornerBreak()), w);
    Deduction deduction = new Deduction();
    assertTrue(deduction.doParse(schema, false));
    deduction.printTrace();
    assertEquals(
        "(S (A (D (a ))(E (b ))(F (c )))(B (G (d ))(H (e ))(I (f )))(C (J (g ))(K (h ))(L (i ))))",
        deduction.getDerivedTrees().get(0).toString());
  }

  @Test public void testCfgLeftcornerChart() throws ParseException {
    String w = "a b c b a";
    ParsingSchema schema = CfgToLeftCornerChartRulesConverter
        .cfgToLeftCornerChartRules(TestGrammarLibrary.wwRCfg(), w);
    Deduction deduction = new Deduction();
    assertTrue(deduction.doParse(schema, false));
    deduction.printTrace();
    assertEquals("(S (a )(S (b )(S (c ))(b ))(a ))",
        deduction.getDerivedTrees().get(0).toString());
  }

  @Test public void testCfgLeftcornerChartBreak() throws ParseException {
    String w = "a b c d e f g h i";
    ParsingSchema schema = CfgToLeftCornerChartRulesConverter
        .cfgToLeftCornerChartRules(TestGrammarLibrary.leftCornerBreak(), w);
    Deduction deduction = new Deduction();
    assertTrue(deduction.doParse(schema, false));
    deduction.printTrace();
    assertEquals(
        "(S (A (D (a ))(E (b ))(F (c )))(B (G (d ))(H (e ))(I (f )))(C (J (g ))(K (h ))(L (i ))))",
        deduction.getDerivedTrees().get(0).toString());
  }

  @Test public void testCfgCyk() throws ParseException {
    String w = "a a b b";
    ParsingSchema schema = CfgToCykRulesConverter
        .cfgToCykRules(Objects.requireNonNull(TestGrammarLibrary.anbnCnfCfg()),
            w);
    Deduction deduction = new Deduction();
    assertTrue(deduction.doParse(schema, false));
    deduction.printTrace();
    assertEquals("(S (A (a ))(X1 (S (A (a ))(B (b )))(B (b ))))",
        deduction.getDerivedTrees().get(0).toString());
  }

  @Test public void testCfgCykExtended() throws ParseException {
    String w = "a a b b";
    ParsingSchema schema = CfgToCykRulesConverter.cfgToCykExtendedRules(
        Objects.requireNonNull(TestGrammarLibrary.anbnC2fCfg()), w);
    Deduction deduction = new Deduction();
    assertTrue(deduction.doParse(schema, false));
    deduction.printTrace();
    assertEquals("(S (A (C (a )))(X1 (S (A (C (a )))(B (b )))(B (b ))))",
        deduction.getDerivedTrees().get(0).toString());
  }

  @Test public void testEmptyInput() throws ParseException {
    String w = "";
    ParsingSchema schema = CfgToCykRulesConverter.cfgToCykGeneralRules(
        Objects.requireNonNull(TestGrammarLibrary.anBnCfg()), w);
    Deduction deduction = new Deduction();
    assertFalse(deduction.doParse(schema, false));
    deduction.printTrace();
    assertEquals(1, deduction.getChart().size());
  }

  @Test public void testTreeAmount() throws ParseException {
    String w = "a a b a b b";
    ParsingSchema schema = CfgToTopDownRulesConverter.cfgToTopDownRules(
        TestGrammarLibrary.diffTreeAmountCfg(), w);
    Deduction deduction = new Deduction();
    assertTrue(deduction.doParse(schema, false));
    assertEquals(3,
        deduction.getDerivedTrees().size());
    schema = CfgToEarleyRulesConverter.cfgToEarleyRules(
        TestGrammarLibrary.diffTreeAmountCfg(), w);
    assertTrue(deduction.doParse(schema, false));
    assertEquals(3,
        deduction.getDerivedTrees().size());
  }

  @Test public void testTagCyk() throws ParseException {
    String w2 = "a c b";
    ParsingSchema schema = TagToCykRulesConverter
        .tagToCykExtendedRules(TestGrammarLibrary.anCBTag(), w2);
    Deduction deduction = new Deduction();
    assertTrue(deduction.doParse(schema, false));
    deduction.printTrace();
    assertEquals("(S (T (a )(T (c )))(b ))",
        deduction.getDerivedTrees().get(0).toString());
  }

  @Test public void testTagCykGeneral() throws ParseException {
    String w2 = "a c b";
    ParsingSchema schema = TagToCykRulesConverter
        .tagToCykGeneralRules(TestGrammarLibrary.anCBTag(), w2);
    Deduction deduction = new Deduction();
    assertTrue(deduction.doParse(schema, false));
    deduction.printTrace();
    assertEquals("(S (T (a )(T (c )))(b ))",
        deduction.getDerivedTrees().get(0).toString());
  }

  @Test public void testTagEarley() throws ParseException {
    String w2 = "a c b";
    ParsingSchema schema = TagToEarleyRulesConverter
        .tagToEarleyRules(TestGrammarLibrary.anCBTag(), w2);
    Deduction deduction = new Deduction();
    try {
      assertTrue(deduction.doParse(schema, false));
    } finally {
      deduction.printTrace();
      assertEquals("(S (T (a )(T (c )))(b ))",
          deduction.getDerivedTrees().get(0).toString());
    }
  }

  @Test public void testTagEarleyPrefixValid() throws ParseException {
    String w2 = "a c b";
    ParsingSchema schema = TagToEarleyPrefixValidRulesConverter
        .tagToEarleyPrefixValidRules(TestGrammarLibrary.anCBTag(), w2);
    Deduction deduction = new Deduction();
    try {
      assertTrue(deduction.doParse(schema, false));
    } finally {
      deduction.printTrace();
      assertEquals("(S (T (a )(T (c )))(b ))",
          deduction.getDerivedTrees().get(0).toString());
    }
  }

  @Test public void testSrcgCykUnary() throws ParseException {
    String w3 = "a a b b";
    ParsingSchema schema = LcfrsToCykRulesConverter
        .srcgToCykExtendedRules(TestGrammarLibrary.anBnSrcg(), w3);
    Deduction deduction = new Deduction();
    assertTrue(deduction.doParse(schema, false));
    deduction.printTrace();
    assertEquals("(S (A (a<0> )(A (a<1> )(b<3> ))(b<2> )))",
        deduction.getDerivedTrees().get(0).toString());
  }

  @Test public void testSrcgCykBinary() throws ParseException {
    String w = "a a b b a c b b a c";
    ParsingSchema schema = LcfrsToCykRulesConverter
        .srcgToCykExtendedRules(TestGrammarLibrary.longStringsSrcg(), w);
    Deduction deduction = new Deduction();
    assertTrue(deduction.doParse(schema, false));
    deduction.printTrace();
    assertEquals(
        "(S (A (A (a<0> )(a<4> )(a<8> ))(C (a<1> )(c<5> )(c<9> )))(B (B (b<3> )(b<6> ))(B (b<2> )(b<7> ))))",
        deduction.getDerivedTrees().get(0).toString());
  }

  @Test public void testSrcgCykGeneral() throws ParseException {
    String w = "a a b b a c b b a c";
    ParsingSchema schema = LcfrsToCykRulesConverter
        .srcgToCykGeneralRules(TestGrammarLibrary.longStringsSrcg(), w);
    Deduction deduction = new Deduction();
    assertTrue(deduction.doParse(schema, false));
    deduction.printTrace();
    assertTrue(
        "(S (A (A (a<0> )(a<4> )(a<8> ))(C (a<1> )(c<5> )(c<9> )))(B (B (b<2> )(b<6> ))(B (b<3> )(b<7> ))))"
            .equals(deduction.getDerivedTrees().get(0).toString())
            || "(S (A (A (a<0> )(a<4> )(a<8> ))(C (a<1> )(c<5> )(c<9> )))(B (B (b<2> )(b<7> ))(B (b<3> )(b<6> ))))"
            .equals(deduction.getDerivedTrees().get(0).toString()));
  }

  @Test public void testSrcgCykGeneral2() throws ParseException {
    String w = "a a b b";
    ParsingSchema schema = LcfrsToCykRulesConverter.srcgToCykGeneralRules(
        new Srcg(Objects.requireNonNull(TestGrammarLibrary.anBnCfg())), w);
    Deduction deduction = new Deduction();
    assertTrue(deduction.doParse(schema, false));
    deduction.printTrace();
    assertEquals("(S (a<0> )(S (a<1> )(b<2> ))(b<3> ))",
        deduction.getDerivedTrees().get(0).toString());
  }

  @Test public void testSrcgEarley() throws ParseException {
    String w3 = "a a b b";
    ParsingSchema schema = LcfrsToEarleyRulesConverter
        .srcgToEarleyRules(TestGrammarLibrary.anBnSrcg(), w3);
    Deduction deduction = new Deduction();
    assertTrue(deduction.doParse(schema, false));
    deduction.printTrace();
    assertEquals("(S (A (A (a<1> )(b<2> ))(a<0> )(b<3> )))",
        deduction.getDerivedTrees().get(0).toString());
  }

  @Test public void testPcfgAstar() throws ParseException {
    String w = "red nice ugly car";
    ParsingSchema schema = PcfgToAstarRulesConverter
        .pcfgToAstarRules(TestGrammarLibrary.niceUglyCarPcfg(), w);
    Deduction deduction = new Deduction();
    deduction.setReplace('l');
    assertTrue(deduction.doParse(schema, false));
    deduction.printTrace();
    assertEquals("(N (A (red ))(N (A (nice ))(N (A (ugly ))(N (car )))))",
        deduction.getDerivedTrees().get(0).toString());
  }

  @Test public void testPcfgCyk() throws ParseException {
    String w = "red nice ugly car";
    ParsingSchema schema = PcfgToCykRulesConverter
        .pcfgToCykRules(TestGrammarLibrary.niceUglyCarPcfg(), w);
    Deduction deduction = new Deduction();
    deduction.setReplace('l');
    assertTrue(deduction.doParse(schema, false));
    deduction.printTrace();
    assertEquals("(N (A (red ))(N (A (nice ))(N (A (ugly ))(N (car )))))",
        deduction.getDerivedTrees().get(0).toString());
  }

  @Test public void testCfgUnger() throws ParseException {
    String w = "a a b b";
    ParsingSchema schema = CfgToUngerRulesConverter
        .cfgToUngerRules(Objects.requireNonNull(TestGrammarLibrary.anBnCfg()),
            w);
    Deduction deduction = new Deduction();
    assertTrue(deduction.doParse(schema, false));
    deduction.printTrace();
    assertEquals("(S (a )(S (a )(b ))(b ))",
        deduction.getDerivedTrees().get(0).toString());
  }

  @Test public void testCfgUngerGoalTrees() throws ParseException {
    String w = "t0 t1";
    ParsingSchema schema = CfgToUngerRulesConverter.cfgToUngerRules(
        Objects.requireNonNull(TestGrammarLibrary.ungerWrongGoalTreesCfg()), w);
    Deduction deduction = new Deduction();
    assertTrue(deduction.doParse(schema, false));
    deduction.printTrace();
    assertEquals("(N1 (t0 )(t1 ))",
        deduction.getDerivedTrees().get(0).toString());
    assertEquals(1, deduction.getDerivedTrees().size());
  }

  @Test public void testCfgCykGeneral() throws ParseException {
    String w = "a a b b";
    ParsingSchema schema = CfgToCykRulesConverter
        .cfgToCykGeneralRules(TestGrammarLibrary.anBnCfg(), w);
    Deduction deduction = new Deduction();
    assertTrue(deduction.doParse(schema, false));
    deduction.printTrace();
    assertEquals("(S (a )(S (a )(b ))(b ))",
        deduction.getDerivedTrees().get(0).toString());
  }

  @Test public void testCfgLr() throws ParseException {
    String w = "the apple";
    ParsingSchema schema =
        CfgToLrKRulesConverter.cfgToLrKRules(TestGrammarLibrary.lrCfg(), w, 0);
    Deduction deduction = new Deduction();
    assertTrue(deduction.doParse(schema, false));
    assertEquals("(NP (Det (the ))(N (apple )))",
        deduction.getDerivedTrees().get(0).toString());
    schema =
        CfgToLrKRulesConverter.cfgToLrKRules(TestGrammarLibrary.lrCfg(), w, 1);
    assertTrue(deduction.doParse(schema, false));
   /* schema = cfgToLrKRules(TestGrammarLibrary.lrCfg(), w, 2);
    assertTrue(deduction.doParse(schema, false)); //*/
  }

  @Test public void testCcg() throws IOException, ParseException {
    String w = "Trip certainly likes merengue";
    ParsingSchema schema = CcgToDeductionRulesConverter
        .ccgToDeductionRules(TestGrammarLibrary.dedCcg(), w);
    Deduction deduction = new Deduction();
    assertTrue(deduction.doParse(schema, false));
    assertEquals(
        "(S (NP (Trip ))(S\\NP ([S\\NP]/NP ([S\\NP]/[S\\NP] (certainly ))([S\\NP]/NP (likes )))(NP (merengue ))))",
        deduction.getDerivedTrees().get(0).toString());
  }
}

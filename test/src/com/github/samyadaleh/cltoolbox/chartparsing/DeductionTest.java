package com.github.samyadaleh.cltoolbox.chartparsing;

import static com.github.samyadaleh.cltoolbox.chartparsing.converter.CfgToDeductionRulesConverter.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.text.ParseException;

import org.junit.Test;

import com.github.samyadaleh.cltoolbox.chartparsing.Deduction;
import com.github.samyadaleh.cltoolbox.chartparsing.ParsingSchema;
import com.github.samyadaleh.cltoolbox.chartparsing.converter.LcfrsToDeductionRulesConverter;
import com.github.samyadaleh.cltoolbox.chartparsing.converter.PcfgToDeductionRulesConverter;
import com.github.samyadaleh.cltoolbox.chartparsing.converter.TagToDeductionRulesConverter;
import com.github.samyadaleh.cltoolbox.common.TestGrammarLibrary;

public class DeductionTest {

  @Test public void testCfgTopdown() throws ParseException {
    String w = "a a b b";
    ParsingSchema schema = cfgToTopDownRules(TestGrammarLibrary.anBnCfg(), w);
    Deduction deduction = new Deduction();
    assertTrue(deduction.doParse(schema, false));
    deduction.printTrace();
    assertEquals("(S (a )(S (a )(b ))(b ))",
        deduction.getDerivedTrees().get(0).toString());
  }

  @Test public void testCfgTopdownEpsilon() throws ParseException {
    String w = "a a b b";
    ParsingSchema schema =
        cfgToTopDownRules(TestGrammarLibrary.anBnEpsilonCfg(), w);
    Deduction deduction = new Deduction();
    assertTrue(deduction.doParse(schema, false));
    deduction.printTrace();
    assertEquals("(S (a )(S (a )(S (ε ))(b ))(b ))",
        deduction.getDerivedTrees().get(0).toString());
  }

  @Test public void testCfgShiftreduce() throws ParseException {
    String w = "a a b b";
    ParsingSchema schema =
        cfgToShiftReduceRules(TestGrammarLibrary.anBnCfg(), w);
    Deduction deduction = new Deduction();
    assertTrue(deduction.doParse(schema, false));
    deduction.printTrace();
    assertEquals("(S (a )(S (a )(b ))(b ))",
        deduction.getDerivedTrees().get(0).toString());
  }

  @Test public void testCfgEarley() throws ParseException {
    String w = "a a b b";
    ParsingSchema schema = cfgToEarleyRules(TestGrammarLibrary.anBnCfg(), w);
    Deduction deduction = new Deduction();
    assertTrue(deduction.doParse(schema, false));
    deduction.printTrace();
    assertEquals("(S (a )(S (a )(b ))(b ))",
        deduction.getDerivedTrees().get(0).toString());
  }

  @Test public void testCfgLeftcorner() throws ParseException {
    String w = "a a b b";
    ParsingSchema schema =
        cfgToLeftCornerRules(TestGrammarLibrary.anBnCfg(), w);
    Deduction deduction = new Deduction();
    assertTrue(deduction.doParse(schema, false));
    deduction.printTrace();
    assertEquals("(S (a )(S (a )(b ))(b ))",
        deduction.getDerivedTrees().get(0).toString());
  }

  @Test public void testCfgLeftcornerBreak() throws ParseException {
    String w = "a b c d e f g h i";
    ParsingSchema schema =
        cfgToLeftCornerRules(TestGrammarLibrary.leftCornerBreak(), w);
    Deduction deduction = new Deduction();
    assertTrue(deduction.doParse(schema, false));
    deduction.printTrace();
    assertEquals(
        "(S (A (D (a ))(E (b ))(F (c )))(B (G (d ))(H (e ))(I (f )))(C (J (g ))(K (h ))(L (i ))))",
        deduction.getDerivedTrees().get(0).toString());
  }

  @Test public void testCfgLeftcornerChart() throws ParseException {
    String w = "a b c b a";
    ParsingSchema schema =
        cfgToLeftCornerChartRules(TestGrammarLibrary.wwRCfg(), w);
    Deduction deduction = new Deduction();
    assertTrue(deduction.doParse(schema, false));
    deduction.printTrace();
    assertEquals("(S (a )(S (b )(S (c ))(b ))(a ))",
        deduction.getDerivedTrees().get(0).toString());
  }

  @Test public void testCfgLeftcornerChartBreak() throws ParseException {
    String w = "a b c d e f g h i";
    ParsingSchema schema =
        cfgToLeftCornerChartRules(TestGrammarLibrary.leftCornerBreak(), w);
    Deduction deduction = new Deduction();
    assertTrue(deduction.doParse(schema, false));
    deduction.printTrace();
    assertEquals(
        "(S (A (D (a ))(E (b ))(F (c )))(B (G (d ))(H (e ))(I (f )))(C (J (g ))(K (h ))(L (i ))))",
        deduction.getDerivedTrees().get(0).toString());
  }

  @Test public void testCfgCyk() throws ParseException {
    String w = "a a b b";
    ParsingSchema schema = cfgToCykRules(TestGrammarLibrary.anbnCnfCfg(), w);
    Deduction deduction = new Deduction();
    assertTrue(deduction.doParse(schema, false));
    deduction.printTrace();
    assertEquals("(S (A (a ))(X1 (S (A (a ))(B (b )))(B (b ))))",
        deduction.getDerivedTrees().get(0).toString());
  }

  @Test public void testCfgCykExtended() throws ParseException {
    String w = "a a b b";
    ParsingSchema schema =
        cfgToCykExtendedRules(TestGrammarLibrary.anbnC2fCfg(), w);
    Deduction deduction = new Deduction();
    assertTrue(deduction.doParse(schema, false));
    deduction.printTrace();
    assertEquals("(S (A (C (a )))(X1 (S (A (C (a )))(B (b )))(B (b ))))",
        deduction.getDerivedTrees().get(0).toString());
  }

  @Test public void testTagCyk() throws ParseException {
    String w2 = "a c b";
    ParsingSchema schema = TagToDeductionRulesConverter
        .tagToCykExtendedRules(TestGrammarLibrary.anCBTag(), w2);
    Deduction deduction = new Deduction();
    assertTrue(deduction.doParse(schema, false));
    deduction.printTrace();
    assertEquals("(S (T (a )(T (c )))(b ))",
        deduction.getDerivedTrees().get(0).toString());
  }

  @Test public void testTagCykGeneral() throws ParseException {
    String w2 = "a c b";
    ParsingSchema schema = TagToDeductionRulesConverter
        .tagToCykGeneralRules(TestGrammarLibrary.anCBTag(), w2);
    Deduction deduction = new Deduction();
    assertTrue(deduction.doParse(schema, false));
    deduction.printTrace();
    assertEquals("(S (T (a )(T (c )))(b ))",
        deduction.getDerivedTrees().get(0).toString());
  }

  @Test public void testTagEarley() throws ParseException {
    String w2 = "a c b";
    ParsingSchema schema = TagToDeductionRulesConverter
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
    ParsingSchema schema = TagToDeductionRulesConverter
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
    ParsingSchema schema = LcfrsToDeductionRulesConverter
        .srcgToCykExtendedRules(TestGrammarLibrary.anBnSrcg(), w3);
    Deduction deduction = new Deduction();
    assertTrue(deduction.doParse(schema, false));
    deduction.printTrace();
    assertEquals("(S (A (a<0> )(A (a<1> )(b<3> ))(b<2> )))",
        deduction.getDerivedTrees().get(0).toString());
  }

  @Test public void testSrcgCykBinary() throws ParseException {
    String w = "a a b b a c b b a c";
    ParsingSchema schema = LcfrsToDeductionRulesConverter
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
    ParsingSchema schema = LcfrsToDeductionRulesConverter
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

  @Test public void testSrcgEarley() throws ParseException {
    String w3 = "a a b b";
    ParsingSchema schema = LcfrsToDeductionRulesConverter
        .srcgToEarleyRules(TestGrammarLibrary.anBnSrcg(), w3);
    Deduction deduction = new Deduction();
    assertTrue(deduction.doParse(schema, false));
    deduction.printTrace();
    assertEquals("(S (A (A (a<1> )(b<2> ))(a<0> )(b<3> )))",
        deduction.getDerivedTrees().get(0).toString());
  }

  @Test public void testPcfgAstar() throws ParseException {
    String w = "red nice ugly car";
    ParsingSchema schema = PcfgToDeductionRulesConverter
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
    ParsingSchema schema = PcfgToDeductionRulesConverter
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
    ParsingSchema schema = cfgToUngerRules(TestGrammarLibrary.anBnCfg(), w);
    Deduction deduction = new Deduction();
    assertTrue(deduction.doParse(schema, false));
    deduction.printTrace();
    assertEquals("(S (a )(S (a )(b ))(b ))",
        deduction.getDerivedTrees().get(0).toString());
  }

  @Test public void testCfgCykGeneral() throws ParseException {
    String w = "a a b b";
    ParsingSchema schema =
        cfgToCykGeneralRules(TestGrammarLibrary.anBnCfg(), w);
    Deduction deduction = new Deduction();
    assertTrue(deduction.doParse(schema, false));
    deduction.printTrace();
    assertEquals("(S (a )(S (a )(b ))(b ))",
        deduction.getDerivedTrees().get(0).toString());
  }

  @Test public void testCfgLr() throws ParseException {
    String w = "the apple";
    ParsingSchema schema = cfgToLrKRules(TestGrammarLibrary.lrCfg(), w, 0);
    Deduction deduction = new Deduction();
    assertTrue(deduction.doParse(schema, false));
    assertEquals("(NP (Det (the ))(N (apple )))",
        deduction.getDerivedTrees().get(0).toString());
    schema = cfgToLrKRules(TestGrammarLibrary.lrCfg(), w, 1);
    assertTrue(deduction.doParse(schema, false));
   /* schema = cfgToLrKRules(TestGrammarLibrary.lrCfg(), w, 2);
    assertTrue(deduction.doParse(schema, false)); //*/
  }

}

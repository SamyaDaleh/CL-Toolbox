package chartparsing;

import static org.junit.Assert.assertTrue;

import java.text.ParseException;

import org.junit.Test;

import chartparsing.converter.CfgToDeductionRulesConverter;
import chartparsing.converter.LcfrsToDeductionRulesConverter;
import chartparsing.converter.PcfgToDeductionRulesConverter;
import chartparsing.converter.TagToDeductionRulesConverter;
import common.TestGrammarLibrary;

public class DeductionTest {

  @Test public void testCfgTopdown() {
    String w = "a a b b";
    ParsingSchema schema = CfgToDeductionRulesConverter
      .cfgToTopDownRules(TestGrammarLibrary.anBnCfg(), w);
    Deduction deduction = new Deduction();
    assertTrue(deduction.doParse(schema, false));
    deduction.printTrace();
  }

  @Test public void testCfgShiftreduce() {
    String w = "a a b b";
    ParsingSchema schema = CfgToDeductionRulesConverter
      .cfgToShiftReduceRules(TestGrammarLibrary.anBnCfg(), w);
    Deduction deduction = new Deduction();
    assertTrue(deduction.doParse(schema, false));
    deduction.printTrace();
  }

  @Test public void testCfgEarley() {
    String w = "a a b b";
    ParsingSchema schema = CfgToDeductionRulesConverter
      .cfgToEarleyRules(TestGrammarLibrary.anBnCfg(), w);
    Deduction deduction = new Deduction();
    assertTrue(deduction.doParse(schema, false));
    deduction.printTrace();
  }

  @Test public void testCfgLeftcorner() {
    String w = "a a b b";
    ParsingSchema schema = CfgToDeductionRulesConverter
      .cfgToLeftCornerRules(TestGrammarLibrary.anBnCfg(), w);
    Deduction deduction = new Deduction();
    assertTrue(deduction.doParse(schema, false));
    deduction.printTrace();
  }

  @Test public void testCfgLeftcornerChart() {
    String w = "a b c b a";
    ParsingSchema schema = CfgToDeductionRulesConverter
      .cfgToLeftCornerChartRules(TestGrammarLibrary.wwRCfg(), w);
    Deduction deduction = new Deduction();
    assertTrue(deduction.doParse(schema, false));
    deduction.printTrace();
  }

  @Test public void testCfgCyk() {

    String w = "a a b b";
    ParsingSchema schema = CfgToDeductionRulesConverter
      .cfgToCykRules(TestGrammarLibrary.anbnCnfCfg(), w);
    Deduction deduction = new Deduction();
    assertTrue(deduction.doParse(schema, false));
    deduction.printTrace();
  }

  @Test public void testCfgCykExtended() {
    String w = "a a b b";
    ParsingSchema schema = CfgToDeductionRulesConverter
      .cfgToCykExtendedRules(TestGrammarLibrary.anbnC2fCfg(), w);
    Deduction deduction = new Deduction();
    assertTrue(deduction.doParse(schema, false));
    deduction.printTrace();
  }

  @Test public void testTagCyk() throws ParseException {
    String w2 = "a c b";
    ParsingSchema schema = TagToDeductionRulesConverter
      .tagToCykExtendedRules(TestGrammarLibrary.anCBTag(), w2);
    Deduction deduction = new Deduction();
    assertTrue(deduction.doParse(schema, false));
    deduction.printTrace();
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
    }
  }

  @Test public void testSrcgCykUnary() throws ParseException {
    String w3 = "a a b b";
    ParsingSchema schema = LcfrsToDeductionRulesConverter
      .srcgToCykExtendedRules(TestGrammarLibrary.anBnSrcg(), w3);
    Deduction deduction = new Deduction();
    assertTrue(deduction.doParse(schema, false));
    deduction.printTrace();
  }

  @Test public void testSrcgCykBinary() throws ParseException {
    String w = "a a b b a c b b a c";
    ParsingSchema schema = LcfrsToDeductionRulesConverter
      .srcgToCykExtendedRules(TestGrammarLibrary.longStringsSrcg(), w);
    Deduction deduction = new Deduction();
    assertTrue(deduction.doParse(schema, false));
    deduction.printTrace();
  }

  @Test public void testSrcgEarley() throws ParseException {
    String w3 = "a a b b";
    ParsingSchema schema = LcfrsToDeductionRulesConverter
      .srcgToEarleyRules(TestGrammarLibrary.anBnSrcg(), w3);
    Deduction deduction = new Deduction();
    assertTrue(deduction.doParse(schema, false));
    deduction.printTrace();
  }

  @Test public void testPcfgAstar() throws ParseException {
    String w = "red nice ugly car";
    ParsingSchema schema = PcfgToDeductionRulesConverter
      .pcfgToAstarRules(TestGrammarLibrary.niceUglyCarPcfg(), w);
    Deduction deduction = new Deduction();
    assertTrue(deduction.doParse(schema, false));
    deduction.printTrace();
  }

  @Test public void testCfgUnger() {
    String w = "a a b b";
    ParsingSchema schema = CfgToDeductionRulesConverter
      .cfgToUngerRules(TestGrammarLibrary.anBnCfg(), w);
    Deduction deduction = new Deduction();
    assertTrue(deduction.doParse(schema, false));
    deduction.printTrace();
  }

  @Test public void testCfgCykGeneral() {
    String w = "a a b b";
    ParsingSchema schema = CfgToDeductionRulesConverter
      .cfgToCykGeneralRules(TestGrammarLibrary.anBnCfg(), w);
    Deduction deduction = new Deduction();
    assertTrue(deduction.doParse(schema, false));
    deduction.printTrace();
  }

}

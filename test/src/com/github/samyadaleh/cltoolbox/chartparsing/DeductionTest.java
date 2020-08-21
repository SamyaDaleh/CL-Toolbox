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
import com.github.samyadaleh.cltoolbox.common.parser.SrcgParser;
import com.github.samyadaleh.cltoolbox.common.tag.Tree;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.io.StringReader;
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
    assertEquals(3, deduction.getMaxAgendaSize());
    String[][] data = deduction.getTraceTable();
    deduction.printTrace(data);
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
    assertTrue(deduction.getUsefulItem()[5] || deduction.getUsefulItem()[6]);
    assertTrue(deduction.getUsefulItem()[7] || deduction.getUsefulItem()[8]);
    assertTrue(deduction.getUsefulItem()[12]);
  }

  @Test public void testCfgTopdownEpsilon() throws ParseException {
    String w = "a a b b";
    ParsingSchema schema = CfgToTopDownRulesConverter.cfgToTopDownRules(
        Objects.requireNonNull(TestGrammarLibrary.anBnEpsilonCfg()), w);
    Deduction deduction = new Deduction();
    assertTrue(deduction.doParse(schema, false));
    assertEquals(2, deduction.getMaxAgendaSize());
    String[][] data = deduction.getTraceTable();
    deduction.printTrace(data);
    assertEquals("(S (a )(S (a )(S (ε ))(b ))(b ))",
        deduction.getDerivedTrees().get(0).toString());
  }

  @Test public void testCfgShiftreduce() throws ParseException {
    String w = "a a b b";
    ParsingSchema schema = CfgToShiftReduceRulesConverter.cfgToShiftReduceRules(
        Objects.requireNonNull(TestGrammarLibrary.anBnCfg()), w);
    Deduction deduction = new Deduction();
    assertTrue(deduction.doParse(schema, false));
    assertEquals(2, deduction.getMaxAgendaSize());
    String[][] data = deduction.getTraceTable();
    deduction.printTrace(data);
    assertEquals("(S (a )(S (a )(b ))(b ))",
        deduction.getDerivedTrees().get(0).toString());
  }

  @Ignore("#258") public void testCfgShiftreduceRecursiveTrees() throws ParseException {
    String w = "a a a";
    ParsingSchema schema = CfgToShiftReduceRulesConverter.cfgToShiftReduceRules(
        Objects.requireNonNull(TestGrammarLibrary.highlyRecursiveCfg()), w);
    Deduction deduction = new Deduction();
    assertTrue(deduction.doParse(schema, false));
    String[][] data = deduction.getTraceTable();
    deduction.printTrace(data);
    assertEquals(2, deduction.getDerivedTrees().size());
    assertEquals("(S (S (S (a ))(S (a )))(S (a )))",
        deduction.getDerivedTrees().get(0).toString());
    assertEquals("(S (S (a ))(S (S (a ))(S (a ))))",
        deduction.getDerivedTrees().get(1).toString());
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
    assertEquals(2, deduction.getMaxAgendaSize());
    String[][] data = deduction.getTraceTable();
    deduction.printTrace(data);
    assertEquals("(N0 (t0 )(t1 ))",
        deduction.getDerivedTrees().get(0).toString());
  }

  @Test public void testCfgEarley() throws ParseException {
    String w = "a a b b";
    ParsingSchema schema = CfgToEarleyRulesConverter
        .cfgToEarleyRules(TestGrammarLibrary.anBnCfg(), w);
    Deduction deduction = new Deduction();
    assertTrue(deduction.doParse(schema, false));
    assertEquals(3, deduction.getMaxAgendaSize());
    String[][] data = deduction.getTraceTable();
    deduction.printTrace(data);
    assertEquals("(S (a )(S (a )(b ))(b ))",
        deduction.getDerivedTrees().get(0).toString());
  }

  @Test public void testCfgEarleyBottomup() throws ParseException {
    String w = "a a b b";
    ParsingSchema schema = CfgToEarleyRulesConverter
        .cfgToEarleyBottomupRules(TestGrammarLibrary.anBnCfg(), w);
    Deduction deduction = new Deduction();
    assertTrue(deduction.doParse(schema, false));
    assertEquals(8, deduction.getMaxAgendaSize());
    String[][] data = deduction.getTraceTable();
    deduction.printTrace(data);
    assertEquals("(S (a )(S (a )(b ))(b ))",
        deduction.getDerivedTrees().get(0).toString());
  }

  @Test public void testCfgEarleyBottomupEmpty() throws ParseException {
    String w = "";
    ParsingSchema schema = CfgToEarleyRulesConverter
        .cfgToEarleyBottomupRules(TestGrammarLibrary.earleyBottomUpProblemCfg(),
            w);
    Deduction deduction = new Deduction();
    assertTrue(deduction.doParse(schema, false));
    String[][] data = deduction.getTraceTable();
    deduction.printTrace(data);
    assertEquals("(N0 (ε ))",
        deduction.getDerivedTrees().get(0).toString());
  }

  @Test public void testCfgEarleyPassive() throws ParseException {
    String w = "t0 t1";
    ParsingSchema schema = CfgToEarleyPassiveRulesConverter
        .cfgToEarleyPassiveRules(TestGrammarLibrary.earleyPassiveCfg(), w);
    Deduction deduction = new Deduction();
    assertTrue(deduction.doParse(schema, false));
    assertEquals(2, deduction.getMaxAgendaSize());
    String[][] data = deduction.getTraceTable();
    deduction.printTrace(data);
    assertEquals("(N1 (t0 )(t1 ))",
        deduction.getDerivedTrees().get(0).toString());
  }

  @Test public void testCfgEarleyPassiveEmptyWord() throws ParseException {
    String w = "";
    ParsingSchema schema = CfgToEarleyPassiveRulesConverter
        .cfgToEarleyPassiveRules(TestGrammarLibrary.emptyWordNothingElseCfg(),
            w);
    Deduction deduction = new Deduction();
    assertTrue(deduction.doParse(schema, false));
    String[][] data = deduction.getTraceTable();
    deduction.printTrace(data);
    assertEquals(2, deduction.getDerivedTrees().size());
    assertEquals("(S (ε ))",
        deduction.getDerivedTrees().get(0).toString());
  }

  @Test public void testCfgLeftCornerChartGoalItems() throws ParseException {
    String w = "t0 t1";
    ParsingSchema schema = CfgToLeftCornerChartRulesConverter
        .cfgToLeftCornerChartRules(TestGrammarLibrary.earleyPassiveCfg(), w);
    Deduction deduction = new Deduction();
    assertTrue(deduction.doParse(schema, false));
    String[][] data = deduction.getTraceTable();
    deduction.printTrace(data);
    assertEquals("(N1 (t0 )(t1 ))",
        deduction.getDerivedTrees().get(0).toString());
  }

  @Test public void testCfgLeftCornerChartEmptyWord() throws ParseException {
    String w = "";
    ParsingSchema schema = CfgToLeftCornerChartRulesConverter
        .cfgToLeftCornerChartRules(TestGrammarLibrary.emptyWordNothingElseCfg(), w);
    Deduction deduction = new Deduction();
    assertTrue(deduction.doParse(schema, false));
    String[][] data = deduction.getTraceTable();
    deduction.printTrace(data);
    assertEquals(2, deduction.getDerivedTrees().size());
    assertEquals("(S (ε ))", deduction.getDerivedTrees().get(0).toString());
  }

  @Test public void testCfgLeftCornerScan() throws ParseException {
    String w = "";
    ParsingSchema schema = CfgToLeftCornerChartRulesConverter
        .cfgToLeftCornerChartRules(TestGrammarLibrary.leftCornerChartScanCfg(),
            w);
    Deduction deduction = new Deduction();
    assertTrue(deduction.doParse(schema, false));
    assertEquals(1, deduction.getMaxAgendaSize());
    String[][] data = deduction.getTraceTable();
    deduction.printTrace(data);
    assertEquals("(N0 (ε ))", deduction.getDerivedTrees().get(0).toString());
  }

  @Test public void testCfgEarleyBottomupMissingInitialize()
      throws ParseException {
    String w = "t0 t1";
    ParsingSchema schema = CfgToEarleyRulesConverter.cfgToEarleyBottomupRules(
        TestGrammarLibrary.earleyBottomUpMissingAxiomsCfg(), w);
    Deduction deduction = new Deduction();
    assertTrue(deduction.doParse(schema, false));
    assertEquals(5, deduction.getMaxAgendaSize());
    String[][] data = deduction.getTraceTable();
    deduction.printTrace(data);
    assertEquals("(N0 (t0 )(N0 (ε ))(t1 )(N0 (ε ))(N0 (ε )))",
        deduction.getDerivedTrees().get(0).toString());
  }

  @Test public void testCfgEarleyBottomupEmptyWord()
      throws ParseException {
    String w = "";
    ParsingSchema schema = CfgToEarleyRulesConverter
        .cfgToEarleyBottomupRules(TestGrammarLibrary.emptyWordNothingElseCfg(),
            w);
    Deduction deduction = new Deduction();
    assertTrue(deduction.doParse(schema, false));
    String[][] data = deduction.getTraceTable();
    deduction.printTrace(data);
    assertTrue(deduction.getDerivedTrees().contains(new Tree("(S (ε ))")));
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
    assertEquals(2, deduction.getMaxAgendaSize());
  }

  @Test public void testCfgEarleyEpsilonNothingElse() throws ParseException {
    String w = "";
    ParsingSchema schema = CfgToEarleyRulesConverter
        .cfgToEarleyRules(TestGrammarLibrary.emptyWordNothingElseCfg(), w);
    Deduction deduction = new Deduction();
    assertTrue(deduction.doParse(schema, false));
    Tree tree = new Tree("(S (S (S (ε ))(S (ε )))(S (S (ε ))(S (ε ))))");
    assertTrue(deduction.getDerivedTrees().contains(tree));
  }

  @Test public void testCfgLeftcorner() throws ParseException {
    String w = "a a b b";
    ParsingSchema schema = CfgToLeftCornerRulesConverter.cfgToLeftCornerRules(
        Objects.requireNonNull(TestGrammarLibrary.anBnCfg()), w);
    Deduction deduction = new Deduction();
    assertTrue(deduction.doParse(schema, false));
    assertEquals(4, deduction.getMaxAgendaSize());
    String[][] data = deduction.getTraceTable();
    deduction.printTrace(data);
    assertEquals("(S (a )(S (a )(b ))(b ))",
        deduction.getDerivedTrees().get(0).toString());
  }

  @Test public void testCfgLeftcornerBreak() throws ParseException {
    String w = "a b c d e f g h i";
    ParsingSchema schema = CfgToLeftCornerRulesConverter.cfgToLeftCornerRules(
        Objects.requireNonNull(TestGrammarLibrary.leftCornerBreak()), w);
    Deduction deduction = new Deduction();
    assertTrue(deduction.doParse(schema, false));
    assertEquals(1, deduction.getMaxAgendaSize());
    String[][] data = deduction.getTraceTable();
    deduction.printTrace(data);
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
    assertEquals(11, deduction.getMaxAgendaSize());
    String[][] data = deduction.getTraceTable();
    deduction.printTrace(data);
    assertEquals("(S (a )(S (b )(S (c ))(b ))(a ))",
        deduction.getDerivedTrees().get(0).toString());
  }

  @Test public void testCfgLeftcornerChartBreak() throws ParseException {
    String w = "a b c d e f g h i";
    ParsingSchema schema = CfgToLeftCornerChartRulesConverter
        .cfgToLeftCornerChartRules(TestGrammarLibrary.leftCornerBreak(), w);
    Deduction deduction = new Deduction();
    assertTrue(deduction.doParse(schema, false));
    assertEquals(19, deduction.getMaxAgendaSize());
    String[][] data = deduction.getTraceTable();
    deduction.printTrace(data);
    assertEquals(
        "(S (A (D (a ))(E (b ))(F (c )))(B (G (d ))(H (e ))(I (f )))(C (J (g ))(K (h ))(L (i ))))",
        deduction.getDerivedTrees().get(0).toString());
  }

  @Test public void testCfgLeftcornerChartExceptionBreak() throws ParseException {
    String w = "t0 t1";
    ParsingSchema schema = CfgToLeftCornerChartRulesConverter
        .cfgToLeftCornerChartRules(TestGrammarLibrary.leftCornerChartExceptionCfg(), w);
    Deduction deduction = new Deduction();
    assertTrue(deduction.doParse(schema, false));
    String[][] data = deduction.getTraceTable();
    deduction.printTrace(data);
    assertEquals(16, deduction.getDerivedTrees().size());
    assertEquals(
        "(N0 (N1 (ε ))(N1 (t0 )(N0 (ε ))(N0 (t1 )(N0 (N1 (ε ))(N1 (ε ))))))",
        deduction.getDerivedTrees().get(0).toString());
  }


  @Test public void testCfgCyk() throws ParseException {
    String w = "a a b b";
    ParsingSchema schema = CfgToCykRulesConverter
        .cfgToCykRules(Objects.requireNonNull(TestGrammarLibrary.anbnCnfCfg()),
            w);
    Deduction deduction = new Deduction();
    assertTrue(deduction.doParse(schema, false));
    assertEquals(4, deduction.getMaxAgendaSize());
    String[][] data = deduction.getTraceTable();
    deduction.printTrace(data);
    assertEquals("(S (A (a ))(X1 (S (A (a ))(B (b )))(B (b ))))",
        deduction.getDerivedTrees().get(0).toString());
  }

  @Test public void testCfgCykExtended() throws ParseException {
    String w = "a a b b";
    ParsingSchema schema = CfgToCykRulesConverter.cfgToCykExtendedRules(
        Objects.requireNonNull(TestGrammarLibrary.anbnC2fCfg()), w);
    Deduction deduction = new Deduction();
    assertTrue(deduction.doParse(schema, false));
    assertEquals(4, deduction.getMaxAgendaSize());
    String[][] data = deduction.getTraceTable();
    deduction.printTrace(data);
    assertEquals("(S (A (C (a )))(X1 (S (A (C (a )))(B (b )))(B (b ))))",
        deduction.getDerivedTrees().get(0).toString());
  }

  @Test public void testEmptyInput() throws ParseException {
    String w = "";
    ParsingSchema schema = CfgToCykRulesConverter.cfgToCykGeneralRules(
        Objects.requireNonNull(TestGrammarLibrary.anBnCfg()), w);
    Deduction deduction = new Deduction();
    assertFalse(deduction.doParse(schema, false));
    assertEquals(1, deduction.getMaxAgendaSize());
    String[][] data = deduction.getTraceTable();
    deduction.printTrace(data);
    assertEquals(1, deduction.getChart().size());
  }

  @Test public void testTreeAmount() throws ParseException {
    String w = "a a b a b b";
    ParsingSchema schema = CfgToTopDownRulesConverter
        .cfgToTopDownRules(TestGrammarLibrary.diffTreeAmountCfg(), w);
    Deduction deduction = new Deduction();
    assertTrue(deduction.doParse(schema, false));
    assertEquals(3, deduction.getDerivedTrees().size());
    schema = CfgToEarleyRulesConverter
        .cfgToEarleyRules(TestGrammarLibrary.diffTreeAmountCfg(), w);
    assertTrue(deduction.doParse(schema, false));
    assertEquals(8, deduction.getMaxAgendaSize());
    assertEquals(3, deduction.getDerivedTrees().size());
  }

  @Test public void testTagCyk() throws ParseException {
    String w2 = "a c b";
    ParsingSchema schema = TagToCykRulesConverter
        .tagToCykExtendedRules(TestGrammarLibrary.anCBTag(), w2);
    Deduction deduction = new Deduction();
    assertTrue(deduction.doParse(schema, false));
    String[][] data = deduction.getTraceTable();
    deduction.printTrace(data);
    assertEquals("(S (T (a )(T (c )))(b ))",
        deduction.getDerivedTrees().get(0).toString());
  }

  @Test public void testTagCykGeneral() throws ParseException {
    String w2 = "a c b";
    ParsingSchema schema = TagToCykRulesConverter
        .tagToCykGeneralRules(TestGrammarLibrary.anCBTag(), w2);
    Deduction deduction = new Deduction();
    assertTrue(deduction.doParse(schema, false));
    String[][] data = deduction.getTraceTable();
    deduction.printTrace(data);
    assertEquals("(S (T (a )(T (c )))(b ))",
        deduction.getDerivedTrees().get(0).toString());
  }

  @Test public void testTagCykGeneralTooManyTrees() throws ParseException {
    String w2 = "t0 t1";
    ParsingSchema schema = TagToCykRulesConverter
        .tagToCykGeneralRules(TestGrammarLibrary.tooManyTreesTag(), w2);
    Deduction deduction = new Deduction();
    assertTrue(deduction.doParse(schema, false));
    String[][] data = deduction.getTraceTable();
    deduction.printTrace(data);
    assertEquals(1, deduction.getDerivedTrees().size());
    assertEquals("(N0 (t0 )(t1 ))",
        deduction.getDerivedTrees().get(0).toString());
  }

  @Test public void testTagEarley() throws ParseException {
    String w2 = "a c b";
    ParsingSchema schema = TagToEarleyRulesConverter
        .tagToEarleyRules(TestGrammarLibrary.anCBTag(), w2);
    Deduction deduction = new Deduction();
    try {
      assertTrue(deduction.doParse(schema, false));
      assertEquals(3, deduction.getMaxAgendaSize());
    } finally {
      String[][] data = deduction.getTraceTable();
      deduction.printTrace(data);
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
      assertEquals(7, deduction.getMaxAgendaSize());
    } finally {
      String[][] data = deduction.getTraceTable();
      deduction.printTrace(data);
      assertEquals("(S (T (a )(T (c )))(b ))",
          deduction.getDerivedTrees().get(0).toString());
    }
  }

  @Test public void testTagEarleyPrefixValidNPE() throws ParseException {
    String w2 = "";
    ParsingSchema schema = TagToEarleyPrefixValidRulesConverter
        .tagToEarleyPrefixValidRules(
            TestGrammarLibrary.EarleyPrefixValidNPETag(), w2);
    Deduction deduction = new Deduction();
    try {
      assertTrue(deduction.doParse(schema, false));
      assertEquals(10, deduction.getMaxAgendaSize());
    } finally {
      String[][] data = deduction.getTraceTable();
      deduction.printTrace(data);
      assertEquals("(N0 (ε ))", deduction.getDerivedTrees().get(0).toString());
    }
  }

  @Test public void testSrcgCykUnary() throws ParseException {
    String w3 = "a a b b";
    ParsingSchema schema = LcfrsToCykRulesConverter
        .srcgToCykExtendedRules(TestGrammarLibrary.anBnSrcg(), w3);
    Deduction deduction = new Deduction();
    assertTrue(deduction.doParse(schema, false));
    assertEquals(4, deduction.getMaxAgendaSize());
    String[][] data = deduction.getTraceTable();
    deduction.printTrace(data);
    assertEquals("(S (A (a<0> )(A (a<1> )(b<3> ))(b<2> )))",
        deduction.getDerivedTrees().get(0).toString());
  }

  @Test public void testSrcgCykBinary() throws ParseException {
    String w = "a a b b a c b b a c";
    ParsingSchema schema = LcfrsToCykRulesConverter
        .srcgToCykExtendedRules(TestGrammarLibrary.longStringsSrcg(), w);
    Deduction deduction = new Deduction();
    assertTrue(deduction.doParse(schema, false));
    assertEquals(13, deduction.getMaxAgendaSize());
    String[][] data = deduction.getTraceTable();
    deduction.printTrace(data);
    assertEquals(
        "(S (A (A (a<0> )(a<4> )(a<8> ))(C (a<1> )(c<5> )(c<9> )))(B (B (b<3> )(b<6> ))(B (b<2> )(b<7> ))))",
        deduction.getDerivedTrees().get(0).toString());
  }

  @Test public void testSrcgVectorMatchFail() throws ParseException {
    String w = "t0 t1 t0 t1 t0 t1 t0";
    ParsingSchema schema = LcfrsToCykRulesConverter
        .srcgToCykExtendedRules(TestGrammarLibrary.vectorMatchFailSrcg(), w);
    Deduction deduction = new Deduction();
    assertFalse(deduction.doParse(schema, false));
  }

  @Test public void testSrcgCykGeneral() throws ParseException {
    String w = "a a b b a c b b a c";
    ParsingSchema schema = LcfrsToCykRulesConverter
        .srcgToCykGeneralRules(TestGrammarLibrary.longStringsSrcg(), w);
    Deduction deduction = new Deduction();
    assertTrue(deduction.doParse(schema, false));
    assertEquals(13, deduction.getMaxAgendaSize());
    String[][] data = deduction.getTraceTable();
    deduction.printTrace(data);
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
    assertEquals(1, deduction.getMaxAgendaSize());
    String[][] data = deduction.getTraceTable();
    deduction.printTrace(data);
    assertEquals("(S (a<0> )(S (a<1> )(b<2> ))(b<3> ))",
        deduction.getDerivedTrees().get(0).toString());
  }

  @Test public void testSrcgCykExtended() throws ParseException {
    StringReader reader = new StringReader(
        "N = {S'}\n" + "T = {}\n" + "V = {}\n" + "P = {S'(ε) -> ε}\n"
            + "S = S'");
    Srcg srcg = SrcgParser.parseSrcgReader(reader);
    String w = "";
    ParsingSchema schema =
        LcfrsToCykRulesConverter.srcgToCykExtendedRules(srcg, w);
    Deduction deduction = new Deduction();
    assertTrue(deduction.doParse(schema, false));
    assertEquals(1, deduction.getMaxAgendaSize());
    String[][] data = deduction.getTraceTable();
    deduction.printTrace(data);
    assertEquals("(S' (ε<0> ))", deduction.getDerivedTrees().get(0).toString());
  }

  @Test public void testSrcgEarley() throws ParseException {
    String w3 = "a a b b";
    ParsingSchema schema = LcfrsToEarleyRulesConverter
        .srcgToEarleyRules(TestGrammarLibrary.anBnSrcg(), w3);
    Deduction deduction = new Deduction();
    assertTrue(deduction.doParse(schema, false));
    String[][] data = deduction.getTraceTable();
    deduction.printTrace(data);
    assertEquals("(S (A (A (a<1> )(b<2> ))(a<0> )(b<3> )))",
        deduction.getDerivedTrees().get(0).toString());
  }

  @Test public void testSrcgEarleyGetSymAtFail() throws ParseException {
    String w3 = "t0 t0 t0 t1";
    ParsingSchema schema = LcfrsToEarleyRulesConverter
        .srcgToEarleyRules(TestGrammarLibrary.earleyGetSymAtFailSrcg(), w3);
    Deduction deduction = new Deduction();
    assertTrue(deduction.doParse(schema, false));
    String[][] data = deduction.getTraceTable();
    deduction.printTrace(data);
    assertEquals(
        "(S' (N0^[1]^1 (N0^[1]^1 (N0^[1]^1 (t0<2> )(t1<3> ))(t0<1> )(t1 ))(t0<0> )(t1 )))",
        deduction.getDerivedTrees().get(0).toString());
  }


  @Test public void testPcfgAstar() throws ParseException {
    String w = "red nice ugly car";
    ParsingSchema schema = PcfgToAstarRulesConverter
        .pcfgToAstarRules(TestGrammarLibrary.niceUglyCarPcfg(), w);
    Deduction deduction = new Deduction();
    deduction.setReplace('l');
    assertTrue(deduction.doParse(schema, false));
    assertEquals(5, deduction.getMaxAgendaSize());
    String[][] data = deduction.getTraceTable();
    deduction.printTrace(data);
    assertEquals("(N (A (red ))(N (A (nice ))(N (A (ugly ))(N (car )))))",
        deduction.getDerivedTrees().get(0).toString());
  }

  @Test public void testPcfgAstarProbabilityDisplay() throws ParseException {
    String w = "t0";
    ParsingSchema schema = PcfgToAstarRulesConverter
        .pcfgToAstarRules(TestGrammarLibrary.uglyProbabilitiesPcfg(), w);
    Deduction deduction = new Deduction();
    deduction.setReplace('l');
    assertTrue(deduction.doParse(schema, false));
    assertEquals(3, deduction.getMaxAgendaSize());
    String[][] data = deduction.getTraceTable();
    deduction.printTrace(data);
    for (int i = 0; i < deduction.getAppliedRule().size(); i++) {
      String rule = deduction.getAppliedRule().get(i).get(0);
      assertTrue(rule.equals("scan 0.09 : S1 -> t0") || rule
          .equals("scan 0.1 : N0 -> t0") || rule.equals("scan 1.0 : Y1 -> t0"));
      String item = deduction.getChart().get(i).toString();
      assertTrue(item.equals("2.4 + 0.0 : [S1,0,1]") || item
          .equals("2.3 + ∞ : [N0,0,1]") || item.equals("0.0 + ∞ : [Y1,0,1]"));
    }
  }

  @Test public void testPcfgCyk() throws ParseException {
    String w = "red nice ugly car";
    ParsingSchema schema = PcfgToCykRulesConverter
        .pcfgToCykRules(TestGrammarLibrary.niceUglyCarPcfg(), w);
    Deduction deduction = new Deduction();
    deduction.setReplace('l');
    assertTrue(deduction.doParse(schema, false));
    assertEquals(5, deduction.getMaxAgendaSize());
    String[][] data = deduction.getTraceTable();
    deduction.printTrace(data);
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
    assertEquals(4, deduction.getMaxAgendaSize());
    String[][] data = deduction.getTraceTable();
    deduction.printTrace(data);
    assertEquals("(S (a )(S (a )(b ))(b ))",
        deduction.getDerivedTrees().get(0).toString());
  }

  @Test public void testCfgUngerGoalTrees() throws ParseException {
    String w = "t0 t1";
    ParsingSchema schema = CfgToUngerRulesConverter.cfgToUngerRules(
        Objects.requireNonNull(TestGrammarLibrary.ungerWrongGoalTreesCfg()), w);
    Deduction deduction = new Deduction();
    assertTrue(deduction.doParse(schema, false));
    assertEquals(2, deduction.getMaxAgendaSize());
    String[][] data = deduction.getTraceTable();
    deduction.printTrace(data);
    assertEquals("(N1 (t0 )(t1 ))",
        deduction.getDerivedTrees().get(0).toString());
    assertEquals(1, deduction.getDerivedTrees().size());
  }

  @Test public void testCfgUngerComplete() throws ParseException {
    String w = "t1";
    ParsingSchema schema = CfgToUngerRulesConverter.cfgToUngerRules(
        Objects.requireNonNull(TestGrammarLibrary.ungerCompleteCfg()), w);
    Deduction deduction = new Deduction();
    assertTrue(deduction.doParse(schema, false));
    assertEquals(2, deduction.getMaxAgendaSize());
    String[][] data = deduction.getTraceTable();
    deduction.printTrace(data);
    assertEquals(1, deduction.getDerivedTrees().size());
    assertEquals("(S2 (S1 (N0 (t1 ))))",
        deduction.getDerivedTrees().get(0).toString());
  }

  @Test public void testCfgCykGeneral() throws ParseException {
    String w = "a a b b";
    ParsingSchema schema = CfgToCykRulesConverter
        .cfgToCykGeneralRules(TestGrammarLibrary.anBnCfg(), w);
    Deduction deduction = new Deduction();
    assertTrue(deduction.doParse(schema, false));
    assertEquals(9, deduction.getMaxAgendaSize());
    String[][] data = deduction.getTraceTable();
    deduction.printTrace(data);
    assertEquals("(S (a )(S (a )(b ))(b ))",
        deduction.getDerivedTrees().get(0).toString());
  }

  @Test public void testCfgCykGeneralEmptyWord() throws ParseException {
    String w = "";
    ParsingSchema schema = CfgToCykRulesConverter
        .cfgToCykGeneralRules(TestGrammarLibrary.emptyWordCfg(), w);
    Deduction deduction = new Deduction();
    assertTrue(deduction.doParse(schema, false));
    assertEquals(2, deduction.getMaxAgendaSize());
    String[][] data = deduction.getTraceTable();
    deduction.printTrace(data);
    assertEquals("(N0 (ε ))",
        deduction.getDerivedTrees().get(0).toString());
  }

  @Test public void testCfgLr() throws ParseException {
    String w = "the apple";
    ParsingSchema schema =
        CfgToLrKRulesConverter.cfgToLrKRules(TestGrammarLibrary.lrCfg(), w, 0);
    Deduction deduction = new Deduction();
    assertTrue(deduction.doParse(schema, false));
    assertEquals(1, deduction.getMaxAgendaSize());
    assertEquals("(NP (Det (the ))(N (apple )))",
        deduction.getDerivedTrees().get(0).toString());
    schema =
        CfgToLrKRulesConverter.cfgToLrKRules(TestGrammarLibrary.lrCfg(), w, 1);
    assertTrue(deduction.doParse(schema, false));
    assertEquals(1, deduction.getMaxAgendaSize());
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

  @Test public void testLatexComputationGraphCfgTopdown() {
    String[][] data = new String[][] {{"1", "[S,0]", "axiom", "{}"},
        {"2", "[A X,0]", "predict S $\rightarrow$ A X", "{1}"},
        {"3", "[a X,0]", "predict A -> a", "{2}"},
        {"4", "[X,1]", "scan a", "{3}"},
        {"5", "[B C,1]", "predict X -> B C", "{4}"},
        {"6", "[b C,1]", "predict B -> b", "{5}"},
        {"7", "[C,2]", "scan b", "{6}"},
        {"8", "[c,2]", "predict C -> c", "{7}"},
        {"9", "[ε,3]", "scan c", "{8}"}};
    String expectedGraph =
        "\\begin{dependency}\n" + "   \\begin{deptext}[column sep=1em]\n"
            + "      0 \\& a \\& 1 \\& b \\& 2 \\& c \\& 3 \\\\\n"
            + "   \\end{deptext}\n" + "   \\depedge{1}{7}{1, 2, 3}\n"
            + "   \\depedge{3}{7}{4, 5, 6}\n" + "   \\depedge{5}{7}{7, 8}\n"
            + "   \\depedge[edge height=3ex]{7}{7}{9}\n" + "\\end{dependency}";
    String graph = Deduction.printLatexGraph(data, "cfg-topdown", "a b c");
    assertEquals(expectedGraph, graph);
  }

  @Test public void testLatexComputationGraphCfgShiftreduce() {
    String[][] data = new String[][] {{"1", "[ε,0]", "axiom", "{}"},
        {"2", "[a,1]", "shift a", "{1}"},
        {"4", "[A,1]", "reduce A -> a", "{2}"},
        {"7", "[A b,2]", "shift b", "{4}"},
        {"11", "[A B,2]", "reduce B -> b", "{7}"},
        {"14", "[A B c,3]", "shift c", "{11}"},
        {"16", "[A B C,3]", "reduce C -> c", "{14}"},
        {"17", "[A X,3]", "reduce X -> B C", "{16}"},
        {"18", "[S,3]", "reduce S -> A X", "{17}"}};
    String expectedGraph =
        "\\begin{dependency}\n" + "   \\begin{deptext}[column sep=1em]\n"
            + "      0 \\& a \\& 1 \\& b \\& 2 \\& c \\& 3 \\\\\n"
            + "   \\end{deptext}\n" + "   \\depedge[edge height=3ex]{1}{1}{1}\n"
            + "   \\depedge{1}{3}{2, 4}\n" + "   \\depedge{1}{5}{7, 11}\n"
            + "   \\depedge{1}{7}{14, 16, 17, 18}\n" + "\\end{dependency}";
    String graph = Deduction.printLatexGraph(data, "cfg-shiftreduce", "a b c");
    assertEquals(expectedGraph, graph);
  }

  @Test public void testLatexComputationGraphCfgCyk() {
    String[][] data = new String[][] {
        {"1", "[C,2,1]",  "scan C -> c", "{}"},
        {"2", "[B,1,1]", "scan B -> b", "{}"},
        {"3", "[A,0,1]", "scan A -> a", "{}"},
        {"4", "[X,1,2]", "complete X -> B C",  "{1, 2}"},
        {"5", "[S,0,3]", "complete S -> A X", "{3, 4}"}
    };
    String expectedGraph =
        "\\begin{dependency}\n" + "   \\begin{deptext}[column sep=1em]\n"
            + "      0 \\& a \\& 1 \\& b \\& 2 \\& c \\& 3 \\\\\n"
            + "   \\end{deptext}\n" + "   \\depedge{1}{3}{3}\n"
            + "   \\depedge{1}{7}{5}\n" + "   \\depedge{3}{5}{2}\n"
            + "   \\depedge{3}{7}{4}\n" + "   \\depedge{5}{7}{1}\n"
            + "\\end{dependency}";
    String graph = Deduction.printLatexGraph(data, "cfg-cyk", "a b c");
    assertEquals(expectedGraph, graph);
  }

  @Test public void testLatexComputationGraphCfgEarley() {
    String[][] data = new String[][] {
        {"1", "[S -> •A X,0,0]", "axiom", "{}"},
        {"2", "[A -> •a,0,0]", "predict A -> a", "{1}"},
        {"3", "[A -> a •,0,1]", "scan a", "{2}"},
        {"4", "[S -> A •X,0,1]", "complete A", "{1, 3}"},
        {"5", "[X -> •B C,1,1]", "predict X -> B C", "{4}"},
        {"6", "[B -> •b,1,1]", "predict B -> b", "{5}"},
        {"7", "[B -> b •,1,2]", "scan b", "{6}"},
        {"8", "[X -> B •C,1,2]", "complete B", "{5, 7}"},
        {"9", "[C -> •c,2,2]", "predict C -> c", "{8}"},
        {"10", "[C -> c •,2,3]", "scan c", "{9}"},
        {"11", "[X -> B C •,1,3]", "complete C", "{8, 10}"},
        {"12", "[S -> A X •,0,3]", "complete X", "{4, 11}"}
    };
    String expectedGraph =
        "\\begin{dependency}\n" + "   \\begin{deptext}[column sep=1em]\n"
            + "      0 \\& a \\& 1 \\& b \\& 2 \\& c \\& 3 \\\\\n"
            + "   \\end{deptext}\n"
            + "   \\depedge[edge height=3ex]{1}{1}{1, 2}\n"
            + "   \\depedge{1}{3}{3, 4}\n" + "   \\depedge{1}{7}{12}\n"
            + "   \\depedge[edge height=3ex]{3}{3}{5, 6}\n"
            + "   \\depedge{3}{5}{7, 8}\n" + "   \\depedge{3}{7}{11}\n"
            + "   \\depedge[edge height=3ex]{5}{5}{9}\n"
            + "   \\depedge{5}{7}{10}\n" + "\\end{dependency}";
    String graph = Deduction.printLatexGraph(data, "cfg-earley", "a b c");
    assertEquals(expectedGraph, graph);
  }
}

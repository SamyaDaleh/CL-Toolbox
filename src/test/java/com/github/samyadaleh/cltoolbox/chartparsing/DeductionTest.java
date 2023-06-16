package com.github.samyadaleh.cltoolbox.chartparsing;

import com.github.samyadaleh.cltoolbox.chartparsing.converter.ccg.CcgToDeductionRulesConverter;
import com.github.samyadaleh.cltoolbox.chartparsing.converter.cfg.*;
import com.github.samyadaleh.cltoolbox.chartparsing.converter.lag.LagToDeductionRulesConverter;
import com.github.samyadaleh.cltoolbox.chartparsing.converter.lcfrs.LcfrsToCykRulesConverter;
import com.github.samyadaleh.cltoolbox.chartparsing.converter.lcfrs.LcfrsToEarleyRulesConverter;
import com.github.samyadaleh.cltoolbox.chartparsing.converter.pcfg.PcfgToAstarRulesConverter;
import com.github.samyadaleh.cltoolbox.chartparsing.converter.pcfg.PcfgToCykRulesConverter;
import com.github.samyadaleh.cltoolbox.chartparsing.converter.tag.TagToCykRulesConverter;
import com.github.samyadaleh.cltoolbox.chartparsing.converter.tag.TagToEarleyPrefixValidRulesConverter;
import com.github.samyadaleh.cltoolbox.chartparsing.converter.tag.TagToEarleyRulesConverter;
import com.github.samyadaleh.cltoolbox.cli.GrammarToGrammarConverter;
import com.github.samyadaleh.cltoolbox.common.GrammarLoader;
import com.github.samyadaleh.cltoolbox.common.ccg.Ccg;
import com.github.samyadaleh.cltoolbox.common.cfg.Cfg;
import com.github.samyadaleh.cltoolbox.common.cfg.Pcfg;
import com.github.samyadaleh.cltoolbox.common.lag.Lag;
import com.github.samyadaleh.cltoolbox.common.lcfrs.Srcg;
import com.github.samyadaleh.cltoolbox.common.parser.LagParser;
import com.github.samyadaleh.cltoolbox.common.parser.SrcgParser;
import com.github.samyadaleh.cltoolbox.common.tag.Tag;
import com.github.samyadaleh.cltoolbox.common.tag.Tree;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.text.ParseException;

import static org.junit.Assert.*;

public class DeductionTest {

  @Test public void testCfgTopdown()
      throws ParseException, FileNotFoundException {
    String w = "a a b b";
    Cfg cfg = GrammarLoader.readCfg("anbn.cfg");
    ParsingSchema schema = CfgToTopDownRulesConverter
        .cfgToTopDownRules(cfg, w);
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

  @Test public void testCfgTopdownEpsilon()
      throws ParseException, FileNotFoundException {
    String w = "a a b b";
    Cfg cfg = GrammarLoader.readCfg("anbnepsilon.cfg");
    ParsingSchema schema = CfgToTopDownRulesConverter.cfgToTopDownRules(
        cfg, w);
    Deduction deduction = new Deduction();
    assertTrue(deduction.doParse(schema, false));
    assertEquals(2, deduction.getMaxAgendaSize());
    String[][] data = deduction.getTraceTable();
    deduction.printTrace(data);
    assertEquals("(S (a )(S (a )(S (ε ))(b ))(b ))",
        deduction.getDerivedTrees().get(0).toString());
  }

  @Test public void testCfgShiftreduce()
      throws ParseException, FileNotFoundException {
    Cfg cfg = GrammarLoader.readCfg("anbn.cfg");
    String w = "a a b b";
    ParsingSchema schema = CfgToShiftReduceRulesConverter.cfgToShiftReduceRules(
        cfg, w);
    Deduction deduction = new Deduction();
    assertTrue(deduction.doParse(schema, false));
    assertEquals(2, deduction.getMaxAgendaSize());
    String[][] data = deduction.getTraceTable();
    deduction.printTrace(data);
    assertEquals("(S (a )(S (a )(b ))(b ))",
        deduction.getDerivedTrees().get(0).toString());
  }

  @Test public void testCfgShiftreduceRecursiveTrees()
      throws ParseException, FileNotFoundException {
    String w = "a a a";
    Cfg cfg = GrammarLoader.readCfg("highlyrecursive.cfg");
    ParsingSchema schema = CfgToShiftReduceRulesConverter.cfgToShiftReduceRules(
        cfg, w);
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

  @Test public void testCfgShiftreduceMinimalEpsilon()
      throws ParseException, FileNotFoundException {
    String w = "";
    Cfg cfg = GrammarLoader.readCfg("emptywordminimal.cfg");
    ParsingSchema schema = CfgToShiftReduceRulesConverter.cfgToShiftReduceRules(
        cfg, w);
    Deduction deduction = new Deduction();
    assertTrue(deduction.doParse(schema, false));
    String[][] data = deduction.getTraceTable();
    deduction.printTrace(data);
    assertEquals("(S (ε ))",
        deduction.getDerivedTrees().get(0).toString());
    assertEquals(2, data.length);
  }

  @Test public void testCfgShiftreduceIndexOOB()
          throws ParseException, FileNotFoundException {
    Cfg cfg = GrammarLoader.readCfg("sr-ioob.cfg");
    String w = "t0 t0";
    ParsingSchema schema = CfgToShiftReduceRulesConverter.cfgToShiftReduceRules(
            cfg, w);
    Deduction deduction = new Deduction();
    assertTrue(deduction.doParse(schema, false));
    String[][] data = deduction.getTraceTable();
    deduction.printTrace(data);
    assertEquals(16, deduction.getDerivedTrees().size());
  }

  @Test public void testCfgEarley() throws ParseException,
      FileNotFoundException {
    Cfg cfg = GrammarLoader.readCfg("anbn.cfg");
    String w = "a a b b";
    ParsingSchema schema = CfgToEarleyRulesConverter
        .cfgToEarleyRules(cfg, w);
    Deduction deduction = new Deduction();
    assertTrue(deduction.doParse(schema, false));
    assertEquals(3, deduction.getMaxAgendaSize());
    String[][] data = deduction.getTraceTable();
    deduction.printTrace(data);
    assertEquals("(S (a )(S (a )(b ))(b ))",
        deduction.getDerivedTrees().get(0).toString());
  }

  @Test public void testCfgEarleyBottomup()
      throws ParseException, FileNotFoundException {
    Cfg cfg = GrammarLoader.readCfg("anbn.cfg");
    String w = "a a b b";
    ParsingSchema schema = CfgToEarleyRulesConverter
        .cfgToEarleyBottomupRules(cfg, w);
    Deduction deduction = new Deduction();
    assertTrue(deduction.doParse(schema, false));
    assertEquals(8, deduction.getMaxAgendaSize());
    String[][] data = deduction.getTraceTable();
    deduction.printTrace(data);
    assertEquals("(S (a )(S (a )(b ))(b ))",
        deduction.getDerivedTrees().get(0).toString());
  }

  @Test public void testCfgEarleyBottomupEmpty()
      throws ParseException, FileNotFoundException {
    String w = "";
    Cfg cfg = GrammarLoader.readCfg("earleybottomupproblem.cfg");
    ParsingSchema schema = CfgToEarleyRulesConverter
        .cfgToEarleyBottomupRules(cfg, w);
    Deduction deduction = new Deduction();
    assertTrue(deduction.doParse(schema, false));
    String[][] data = deduction.getTraceTable();
    deduction.printTrace(data);
    assertEquals("(N0 (ε ))",
        deduction.getDerivedTrees().get(0).toString());
  }

  @Test public void testCfgEarleyPassive()
      throws ParseException, FileNotFoundException {
    String w = "t0 t1";
    Cfg cfg = GrammarLoader.readCfg("earleypassive.cfg");
    ParsingSchema schema = CfgToEarleyPassiveRulesConverter
        .cfgToEarleyPassiveRules(cfg, w);
    Deduction deduction = new Deduction();
    assertTrue(deduction.doParse(schema, false));
    assertEquals(2, deduction.getMaxAgendaSize());
    String[][] data = deduction.getTraceTable();
    deduction.printTrace(data);
    assertEquals("(N1 (t0 )(t1 ))",
        deduction.getDerivedTrees().get(0).toString());
  }

  @Test public void testCfgEarleyPassiveEmptyWord()
      throws ParseException, FileNotFoundException {
    String w = "";
    Cfg cfg = GrammarLoader.readCfg("emptywordnothingelse.cfg");
    ParsingSchema schema = CfgToEarleyPassiveRulesConverter
        .cfgToEarleyPassiveRules(cfg, w);
    Deduction deduction = new Deduction();
    assertTrue(deduction.doParse(schema, false));
    String[][] data = deduction.getTraceTable();
    deduction.printTrace(data);
    assertEquals(2, deduction.getDerivedTrees().size());
    assertEquals("(S (ε ))",
        deduction.getDerivedTrees().get(0).toString());
  }

  @Test public void testCfgLeftCornerChartGoalItems()
      throws ParseException, FileNotFoundException {
    String w = "t0 t1";
    Cfg cfg = GrammarLoader.readCfg("earleypassive.cfg");
    ParsingSchema schema = CfgToLeftCornerChartRulesConverter
        .cfgToLeftCornerChartRules(cfg, w);
    Deduction deduction = new Deduction();
    assertTrue(deduction.doParse(schema, false));
    String[][] data = deduction.getTraceTable();
    deduction.printTrace(data);
    assertEquals("(N1 (t0 )(t1 ))",
        deduction.getDerivedTrees().get(0).toString());
  }

  @Test public void testCfgLeftCornerChartEmptyWord()
      throws ParseException, FileNotFoundException {
    String w = "";
    Cfg cfg = GrammarLoader.readCfg("emptywordnothingelse.cfg");
    ParsingSchema schema = CfgToLeftCornerChartRulesConverter
        .cfgToLeftCornerChartRules(cfg, w);
    Deduction deduction = new Deduction();
    assertTrue(deduction.doParse(schema, false));
    String[][] data = deduction.getTraceTable();
    deduction.printTrace(data);
    assertEquals(2, deduction.getDerivedTrees().size());
    assertEquals("(S (ε ))", deduction.getDerivedTrees().get(0).toString());
  }

  @Test public void testCfgLeftCornerScan()
      throws ParseException, FileNotFoundException {
    String w = "";
    Cfg cfg = GrammarLoader.readCfg("leftcornerchartscan.cfg");
    ParsingSchema schema = CfgToLeftCornerChartRulesConverter
        .cfgToLeftCornerChartRules(cfg, w);
    Deduction deduction = new Deduction();
    assertTrue(deduction.doParse(schema, false));
    assertEquals(1, deduction.getMaxAgendaSize());
    String[][] data = deduction.getTraceTable();
    deduction.printTrace(data);
    assertEquals("(N0 (ε ))", deduction.getDerivedTrees().get(0).toString());
  }

  @Test public void testCfgLeftCornerMissingGoalTree() throws ParseException {
    String w = "a b c b c b";
    String grammar = "N = {S, A, B}\n" + "T = {a, b, c}\n" + "S = S\n"
        + "P = {S -> a b S | c A, A -> b B, B -> c b B | ε}";
    Cfg cfg = new Cfg(new BufferedReader(new StringReader(grammar)));
    ParsingSchema schema = CfgToLeftCornerChartRulesConverter
        .cfgToLeftCornerChartRules(cfg, w);
    Deduction deduction = new Deduction();
    assertTrue(deduction.doParse(schema, false));
    String[][] data = deduction.getTraceTable();
    deduction.printTrace(data);
    assertEquals(1, deduction.getDerivedTrees().size());
    assertEquals("(S (a )(b )(S (c )(A (b )(B (c )(b )(B (ε ))))))",
        deduction.getDerivedTrees().get(0).toString());
  }

  @Test public void testConversion() throws ParseException {
    String grammar = "G = <N, T, S, P>\n" + "N = {S}\n" + "T = {a, b, c}\n"
        + "S = S\n" + "P = {S -> a, S -> c b a S a a}\n";
    Cfg cfg = new Cfg(new BufferedReader(new StringReader(grammar)));
    String binarizedGrammar = "G = <N, T, S, P>\n" + "N = {S, X1, X2, X3, X4}\n"
        + "T = {a, b, c}\n" + "S = S\n"
        + "P = {S -> a, S -> c X1, X1 -> b X2, X2 -> a X3, X3 -> S X4, X4 -> a a}\n";
    assertEquals(binarizedGrammar, cfg.getBinarizedCfg().toString());
  }

  @Test public void testCfgEarleyBottomupMissingInitialize()
      throws ParseException, FileNotFoundException {
    String w = "t0 t1";
    Cfg cfg = GrammarLoader.readCfg("earleybottomupmissingaxioms.cfg");
    ParsingSchema schema = CfgToEarleyRulesConverter.cfgToEarleyBottomupRules(
        cfg, w);
    Deduction deduction = new Deduction();
    assertTrue(deduction.doParse(schema, false));
    assertEquals(5, deduction.getMaxAgendaSize());
    String[][] data = deduction.getTraceTable();
    deduction.printTrace(data);
    assertEquals("(N0 (t0 )(N0 (ε ))(t1 )(N0 (ε ))(N0 (ε )))",
        deduction.getDerivedTrees().get(0).toString());
  }

  @Test public void testCfgEarleyBottomupEmptyWord()
      throws ParseException, FileNotFoundException {
    String w = "";
    Cfg cfg = GrammarLoader.readCfg("emptywordnothingelse.cfg");
    ParsingSchema schema = CfgToEarleyRulesConverter
        .cfgToEarleyBottomupRules(cfg, w);
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

  @Test public void testCfgEarleyEpsilonNothingElse()
      throws ParseException, FileNotFoundException {
    String w = "";
    Cfg cfg = GrammarLoader.readCfg("emptywordnothingelse.cfg");
    ParsingSchema schema = CfgToEarleyRulesConverter
        .cfgToEarleyRules(cfg, w);
    Deduction deduction = new Deduction();
    assertTrue(deduction.doParse(schema, false));
    Tree tree1 = new Tree("(S (ε )");
    Tree tree2 = new Tree("(S (S (ε ))(S (ε )))");
    assertTrue(deduction.getDerivedTrees().contains(tree1));
    assertTrue(deduction.getDerivedTrees().contains(tree2));
    assertEquals(2, deduction.getDerivedTrees().size());
  }

  @Test public void testCfgEarleyEpsilonMoreComplicated()
      throws ParseException, FileNotFoundException {
    String w = "";
    Cfg cfg = GrammarLoader.readCfg("emptywordmorecomplicated.cfg");
    ParsingSchema schema = CfgToEarleyRulesConverter
        .cfgToEarleyRules(cfg, w);
    Deduction deduction = new Deduction();
    assertTrue(deduction.doParse(schema, false));
    Tree tree1 = new Tree("(S (ε )");
    Tree tree2 = new Tree("(S (N1 (S (ε )))(N1 (S (ε ))))");
    assertTrue(deduction.getDerivedTrees().contains(tree1));
    assertTrue(deduction.getDerivedTrees().contains(tree2));
    assertEquals(2, deduction.getDerivedTrees().size());
  }

  @Test public void testCfgEarleyPruneTrees()
      throws ParseException, FileNotFoundException {
    String w = "t0 t0";
    Cfg cfg = GrammarLoader.readCfg("prunetrees.cfg");
    ParsingSchema schema = CfgToEarleyRulesConverter
        .cfgToEarleyRules(cfg, w);
    Deduction deduction = new Deduction();
    assertTrue(deduction.doParse(schema, false));
  }

  @Test public void testUngerOutOfBounds()
      throws ParseException, FileNotFoundException {
    String w = "a b";
    Cfg cfg = GrammarLoader.readCfg("ungeroob.cfg");
    ParsingSchema schema = CfgToUngerRulesConverter
        .cfgToUngerRules(cfg, w);
    Deduction deduction = new Deduction();
    assertTrue(deduction.doParse(schema, false));
    assertEquals(3, deduction.getDerivedTrees().size());
  }

  @Test public void testCfgEarleyPruneTrees2()
      throws ParseException, FileNotFoundException {
    String w = "t2 t1 t0";
    Cfg cfg = GrammarLoader.readCfg("earleycomplicated.cfg");
    ParsingSchema schema = CfgToEarleyRulesConverter
        .cfgToEarleyRules(cfg, w);
    Deduction deduction = new Deduction();
    assertTrue(deduction.doParse(schema, false));
    // amount varies with execution
    assertTrue(deduction.getDerivedTrees().size() > 0);
  }

  @Test public void testCfgLeftcorner()
      throws ParseException, FileNotFoundException {
    Cfg cfg = GrammarLoader.readCfg("anbn.cfg");
    String w = "a a b b";
    ParsingSchema schema = CfgToLeftCornerRulesConverter.cfgToLeftCornerRules(
        cfg, w);
    Deduction deduction = new Deduction();
    assertTrue(deduction.doParse(schema, false));
    assertEquals(4, deduction.getMaxAgendaSize());
    String[][] data = deduction.getTraceTable();
    deduction.printTrace(data);
    assertEquals("(S (a )(S (a )(b ))(b ))",
        deduction.getDerivedTrees().get(0).toString());
  }

  @Test public void testCfgLeftcornerBreak()
      throws ParseException, FileNotFoundException {
    String w = "a b c d e f g h i";
    Cfg cfg = GrammarLoader.readCfg("leftcornerbreak.cfg");
    ParsingSchema schema = CfgToLeftCornerRulesConverter.cfgToLeftCornerRules(
       cfg, w);
    Deduction deduction = new Deduction();
    assertTrue(deduction.doParse(schema, false));
    assertEquals(1, deduction.getMaxAgendaSize());
    String[][] data = deduction.getTraceTable();
    deduction.printTrace(data);
    assertEquals(
        "(S (A (D (a ))(E (b ))(F (c )))(B (G (d ))(H (e ))(I (f )))(C (J (g ))(K (h ))(L (i ))))",
        deduction.getDerivedTrees().get(0).toString());
  }

  @Test public void testCfgLeftcornerChart()
      throws ParseException, FileNotFoundException {
    String w = "a b c b a";
    Cfg cfg = GrammarLoader.readCfg("wwr.cfg");
    ParsingSchema schema = CfgToLeftCornerChartRulesConverter
        .cfgToLeftCornerChartRules(cfg, w);
    Deduction deduction = new Deduction();
    assertTrue(deduction.doParse(schema, false));
    assertEquals(11, deduction.getMaxAgendaSize());
    String[][] data = deduction.getTraceTable();
    deduction.printTrace(data);
    assertEquals("(S (a )(S (b )(S (c ))(b ))(a ))",
        deduction.getDerivedTrees().get(0).toString());
  }

  @Test public void testCfgLeftcornerChartBreak()
      throws ParseException, FileNotFoundException {
    String w = "a b c d e f g h i";
    Cfg cfg = GrammarLoader.readCfg("leftcornerbreak.cfg");
    ParsingSchema schema = CfgToLeftCornerChartRulesConverter
        .cfgToLeftCornerChartRules(cfg, w);
    Deduction deduction = new Deduction();
    assertTrue(deduction.doParse(schema, false));
    assertEquals(19, deduction.getMaxAgendaSize());
    String[][] data = deduction.getTraceTable();
    deduction.printTrace(data);
    assertEquals(
        "(S (A (D (a ))(E (b ))(F (c )))(B (G (d ))(H (e ))(I (f )))(C (J (g ))(K (h ))(L (i ))))",
        deduction.getDerivedTrees().get(0).toString());
  }

  @Test public void testCfgLeftcornerChartExceptionBreak()
      throws ParseException, FileNotFoundException {
    String w = "t0 t1";
    Cfg cfg = GrammarLoader.readCfg("leftcornerchartexception.cfg");
    ParsingSchema schema = CfgToLeftCornerChartRulesConverter
        .cfgToLeftCornerChartRules(cfg, w);
    Deduction deduction = new Deduction();
    assertTrue(deduction.doParse(schema, false));
    String[][] data = deduction.getTraceTable();
    deduction.printTrace(data);
    // TODO sometimes 8, sometimes 16, please investigate
    // assertEquals(8, deduction.getDerivedTrees().size());
  }

  @Test public void testCfgLeftcornerBottomUp()
      throws ParseException, FileNotFoundException {
    Cfg cfg = GrammarLoader.readCfg("anbn.cfg");
    String w = "a a b b";
    ParsingSchema schema = CfgToLeftCornerBottomUpRulesConverter
        .cfgToLeftCornerBottomUpRules(cfg, w);
    Deduction deduction = new Deduction();
    assertTrue(deduction.doParse(schema, false));
    assertEquals(4, deduction.getMaxAgendaSize());
    String[][] data = deduction.getTraceTable();
    deduction.printTrace(data);
    assertEquals("(S (a )(S (a )(b ))(b ))",
        deduction.getDerivedTrees().get(0).toString());
  }

  @Test public void testCfgLeftcornerBottomUpChallenging()
      throws ParseException, FileNotFoundException {
    Cfg cfg = GrammarLoader.readCfg("highlyrecursive.cfg");
    String w = "a a a";
    ParsingSchema schema = CfgToLeftCornerBottomUpRulesConverter
        .cfgToLeftCornerBottomUpRules(cfg, w);
    Deduction deduction = new Deduction();
    assertTrue(deduction.doParse(schema, false));
    String[][] data = deduction.getTraceTable();
    deduction.printTrace(data);
    assertEquals(2, deduction.getDerivedTrees().size());
  }

  @Test public void testCfgCyk() throws ParseException, FileNotFoundException {
    String w = "a a b b";
    Cfg cfg = GrammarLoader.readCfg("anbncnf.cfg");
    ParsingSchema schema = CfgToCykRulesConverter
        .cfgToCykRules(cfg, w);
    Deduction deduction = new Deduction();
    assertTrue(deduction.doParse(schema, false));
    assertEquals(4, deduction.getMaxAgendaSize());
    String[][] data = deduction.getTraceTable();
    deduction.printTrace(data);
    assertEquals("(S (A (a ))(X1 (S (A (a ))(B (b )))(B (b ))))",
        deduction.getDerivedTrees().get(0).toString());
  }

  @Test public void testCfgCnfConversionAndCyk() throws ParseException, FileNotFoundException {
    String w = "t0 t1";
    Cfg cfg = GrammarLoader.readCfg("notCnf.cfg");
    cfg = GrammarToGrammarConverter.checkAndMayConvertToCfg(cfg, "cfg-cyk", true);
    ParsingSchema schema = CfgToCykRulesConverter
        .cfgToCykRules(cfg, w);
    Deduction deduction = new Deduction();
    assertTrue(deduction.doParse(schema, false));
  }

  @Test public void testCfgEpsilon()
      throws ParseException, FileNotFoundException {
    String w = "";
    Cfg cfg = GrammarLoader.readCfg("epsilonnothingelse.cfg");
    ParsingSchema schema = CfgToCykRulesConverter
        .cfgToCykRules(cfg,  w);
    Deduction deduction = new Deduction();
    assertTrue(deduction.doParse(schema, false));
    assertEquals(1, deduction.getChart().size());
    String[][] data = deduction.getTraceTable();
    deduction.printTrace(data);
    assertEquals("(S (ε ))",
        deduction.getDerivedTrees().get(0).toString());
  }

  @Test public void testCfgCykExtended()
      throws ParseException, FileNotFoundException {
    String w = "a a b b";
    Cfg cfg = GrammarLoader.readCfg("anbnc2f.cfg");
    ParsingSchema schema = CfgToCykRulesConverter.cfgToCykExtendedRules(
        cfg, w);
    Deduction deduction = new Deduction();
    assertTrue(deduction.doParse(schema, false));
    assertEquals(4, deduction.getMaxAgendaSize());
    String[][] data = deduction.getTraceTable();
    deduction.printTrace(data);
    assertEquals("(S (A (C (a )))(X1 (S (A (C (a )))(B (b )))(B (b ))))",
        deduction.getDerivedTrees().get(0).toString());
  }

  @Test public void testEmptyInput()
      throws ParseException, FileNotFoundException {
    Cfg cfg = GrammarLoader.readCfg("anbn.cfg");
    String w = "";
    ParsingSchema schema = CfgToCykRulesConverter.cfgToCykGeneralRules(
        cfg, w);
    Deduction deduction = new Deduction();
    assertFalse(deduction.doParse(schema, false));
    assertEquals(1, deduction.getMaxAgendaSize());
    String[][] data = deduction.getTraceTable();
    deduction.printTrace(data);
    assertEquals(1, deduction.getChart().size());
  }

  @Test public void testTreeAmount()
      throws ParseException, FileNotFoundException {
    String w = "a a b a b b";
    Cfg cfg = GrammarLoader.readCfg("difftreeamount.cfg");
    ParsingSchema schema = CfgToTopDownRulesConverter
        .cfgToTopDownRules(cfg, w);
    Deduction deduction = new Deduction();
    assertTrue(deduction.doParse(schema, false));
    assertEquals(3, deduction.getDerivedTrees().size());
    schema = CfgToEarleyRulesConverter
        .cfgToEarleyRules(cfg, w);
    assertTrue(deduction.doParse(schema, false));
    assertEquals(8, deduction.getMaxAgendaSize());
    assertEquals(3, deduction.getDerivedTrees().size());
  }

  @Test public void testTagCyk() throws ParseException, FileNotFoundException {
    String w2 = "a c b";
    Tag tag = GrammarLoader.readTag("ancb.tag");
    ParsingSchema schema = TagToCykRulesConverter
        .tagToCykExtendedRules(tag, w2);
    Deduction deduction = new Deduction();
    assertTrue(deduction.doParse(schema, false));
    String[][] data = deduction.getTraceTable();
    deduction.printTrace(data);
    assertEquals("(S (T (a )(T (c )))(b ))",
        deduction.getDerivedTrees().get(0).toString());
  }

  @Test public void testTagCykBrokenTrees()
      throws ParseException, FileNotFoundException {
    String w2 = "t0 t0";
    Tag tag = GrammarLoader.readTag("brokentrees.tag");
    ParsingSchema schema = TagToCykRulesConverter
        .tagToCykExtendedRules(tag, w2);
    Deduction deduction = new Deduction();
    assertTrue(deduction.doParse(schema, false));
    String[][] data = deduction.getTraceTable();
    deduction.printTrace(data);
    for (Tree tree : deduction.getDerivedTrees()) {
      if (tree.toString().contains("(N1 )")) {
        fail();
      }
    }
  }

  @Test public void testTagCykGeneral()
      throws ParseException, FileNotFoundException {
    String w2 = "a c b";
    Tag tag = GrammarLoader.readTag("ancb.tag");
    ParsingSchema schema = TagToCykRulesConverter
        .tagToCykGeneralRules(tag, w2);
    Deduction deduction = new Deduction();
    assertTrue(deduction.doParse(schema, false));
    String[][] data = deduction.getTraceTable();
    deduction.printTrace(data);
    assertEquals("(S (T (a )(T (c )))(b ))",
        deduction.getDerivedTrees().get(0).toString());
  }

  @Test public void testTagCykGeneralBrokenTrees()
      throws ParseException, FileNotFoundException {
    String w2 = "t0 t0";
    Tag tag = GrammarLoader.readTag("brokentrees.tag");
    ParsingSchema schema = TagToCykRulesConverter
        .tagToCykGeneralRules(tag, w2);
    Deduction deduction = new Deduction();
    assertTrue(deduction.doParse(schema, false));
    String[][] data = deduction.getTraceTable();
    deduction.printTrace(data);
    for (Tree tree : deduction.getDerivedTrees()) {
      if (tree.toString().contains("(N1 )")) {
        fail();
      }
    }
  }

  @Test public void testTagCykGeneralTooManyTrees()
      throws ParseException, FileNotFoundException {
    String w2 = "t0 t1";
    Tag tag = GrammarLoader.readTag("toomanytrees.tag");
    ParsingSchema schema = TagToCykRulesConverter
        .tagToCykGeneralRules(tag, w2);
    Deduction deduction = new Deduction();
    assertTrue(deduction.doParse(schema, false));
    String[][] data = deduction.getTraceTable();
    deduction.printTrace(data);
    assertEquals(1, deduction.getDerivedTrees().size());
    assertEquals("(N0 (t0 )(t1 ))",
        deduction.getDerivedTrees().get(0).toString());
  }

  @Test public void testTagEarley() throws ParseException,
      FileNotFoundException {
    String w2 = "a c b";
    Tag tag = GrammarLoader.readTag("ancb.tag");
    ParsingSchema schema = TagToEarleyRulesConverter
        .tagToEarleyRules(tag, w2);
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

  @Test public void testTagEarleyPrefixValid()
      throws ParseException, FileNotFoundException {
    String w2 = "a c b";
    Tag tag = GrammarLoader.readTag("ancb.tag");
    ParsingSchema schema = TagToEarleyPrefixValidRulesConverter
        .tagToEarleyPrefixValidRules(tag, w2);
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

  @Test public void testTagEarleyPrefixValidNPE()
      throws ParseException, FileNotFoundException {
    String w2 = "";
    Tag tag = GrammarLoader.readTag("earleyprefixvalidnpe.tag");
    ParsingSchema schema = TagToEarleyPrefixValidRulesConverter
        .tagToEarleyPrefixValidRules(tag, w2);
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

  @Test public void testSrcgCykUnary()
      throws ParseException, FileNotFoundException {
    String w3 = "a a b b";
    Srcg srcg = GrammarLoader.readSrcg("anbn.srcg");
    ParsingSchema schema = LcfrsToCykRulesConverter
        .srcgToCykExtendedRules(srcg, w3);
    Deduction deduction = new Deduction();
    assertTrue(deduction.doParse(schema, false));
    assertEquals(4, deduction.getMaxAgendaSize());
    String[][] data = deduction.getTraceTable();
    deduction.printTrace(data);
    assertEquals("(S (A (a<0> )(A (a<1> )(b<3> ))(b<2> )))",
        deduction.getDerivedTrees().get(0).toString());
  }

  @Test public void testSrcgCykBinary()
      throws ParseException, FileNotFoundException {
    String w = "a a b b a c b b a c";
    Srcg srcg = GrammarLoader.readSrcg("longstring.srcg");
    ParsingSchema schema = LcfrsToCykRulesConverter
        .srcgToCykExtendedRules(srcg, w);
    Deduction deduction = new Deduction();
    assertTrue(deduction.doParse(schema, false));
    assertEquals(13, deduction.getMaxAgendaSize());
    String[][] data = deduction.getTraceTable();
    deduction.printTrace(data);
    assertEquals(
        "(S (A (A (a<0> )(a<4> )(a<8> ))(C (a<1> )(c<5> )(c<9> )))(B (B (b<3> )(b<6> ))(B (b<2> )(b<7> ))))",
        deduction.getDerivedTrees().get(0).toString());
  }

  @Test public void testSrcgVectorMatchFail()
      throws ParseException, FileNotFoundException {
    String w = "t0 t1 t0 t1 t0 t1 t0";
    Srcg srcg = GrammarLoader.readSrcg("vectormatchfail.srcg");
    ParsingSchema schema = LcfrsToCykRulesConverter
        .srcgToCykExtendedRules(srcg, w);
    Deduction deduction = new Deduction();
    assertFalse(deduction.doParse(schema, false));
  }

  @Test public void testSrcgCykGeneral()
      throws ParseException, FileNotFoundException {
    String w = "a a b b a c b b a c";
    Srcg srcg = GrammarLoader.readSrcg("longstring.srcg");
    ParsingSchema schema = LcfrsToCykRulesConverter
        .srcgToCykGeneralRules(srcg, w);
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

  @Test public void testSrcgCykGeneral2()
      throws ParseException, FileNotFoundException {
    Cfg cfg = GrammarLoader.readCfg("anbn.cfg");
    String w = "a a b b";
    ParsingSchema schema = LcfrsToCykRulesConverter.srcgToCykGeneralRules(
        new Srcg(cfg), w);
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

  @Test public void testSrcgEarley()
      throws ParseException, FileNotFoundException {
    String w3 = "a a b b";
    Srcg srcg = GrammarLoader.readSrcg("anbn.srcg");
    ParsingSchema schema = LcfrsToEarleyRulesConverter
        .srcgToEarleyRules(srcg, w3);
    Deduction deduction = new Deduction();
    assertTrue(deduction.doParse(schema, false));
    String[][] data = deduction.getTraceTable();
    deduction.printTrace(data);
    assertEquals("(S (A (A (a<1> )(b<2> ))(a<0> )(b<3> )))",
        deduction.getDerivedTrees().get(0).toString());
  }

  @Test public void testSrcgEarleyGetSymAtFail()
      throws ParseException, FileNotFoundException {
    String w3 = "t0 t0 t0 t1";
    Srcg srcg = GrammarLoader.readSrcg("earleygetsymatfail.srcg");
    ParsingSchema schema = LcfrsToEarleyRulesConverter
        .srcgToEarleyRules(srcg, w3);
    Deduction deduction = new Deduction();
    assertTrue(deduction.doParse(schema, false));
    String[][] data = deduction.getTraceTable();
    deduction.printTrace(data);
    assertEquals(
        "(S' (N0^[1]^1 (N0^[1]^1 (N0^[1]^1 (t0<2> )(t1<3> ))(t0<1> )(t1 ))(t0<0> )(t1 )))",
        deduction.getDerivedTrees().get(0).toString());
  }


  @Test public void testPcfgAstar() throws ParseException,
      FileNotFoundException {
    String w = "red nice ugly car";
    Pcfg pcfg = GrammarLoader.readPcfg("niceuglycar.pcfg");
    ParsingSchema schema = PcfgToAstarRulesConverter
        .pcfgToAstarRules(pcfg, w);
    Deduction deduction = new Deduction();
    deduction.setReplace('l');
    assertTrue(deduction.doParse(schema, false));
    assertEquals(5, deduction.getMaxAgendaSize());
    String[][] data = deduction.getTraceTable();
    deduction.printTrace(data);
    assertEquals("(N (A (red ))(N (A (nice ))(N (A (ugly ))(N (car )))))",
        deduction.getDerivedTrees().get(0).toString());
  }

  @Test public void testPcfgAstarProbabilityDisplay()
      throws ParseException, FileNotFoundException {
    String w = "t0";
    Pcfg pcfg = GrammarLoader.readPcfg("uglyprobabilities.pcfg");
    ParsingSchema schema = PcfgToAstarRulesConverter
        .pcfgToAstarRules(pcfg, w);
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
      assertTrue(item.equals("2.41 + 0.0 : [S1,0,1]") || item
          .equals("2.3 + ∞ : [N0,0,1]") || item.equals("0.0 + ∞ : [Y1,0,1]"));
    }
  }

  @Test public void testPcfgCyk() throws ParseException, FileNotFoundException {
    String w = "red nice ugly car";
    Pcfg pcfg = GrammarLoader.readPcfg("niceuglycar.pcfg");
    ParsingSchema schema = PcfgToCykRulesConverter
        .pcfgToCykRules(pcfg, w);
    Deduction deduction = new Deduction();
    deduction.setReplace('l');
    assertTrue(deduction.doParse(schema, false));
    assertEquals(5, deduction.getMaxAgendaSize());
    String[][] data = deduction.getTraceTable();
    deduction.printTrace(data);
    assertEquals("(N (A (red ))(N (A (nice ))(N (A (ugly ))(N (car )))))",
        deduction.getDerivedTrees().get(0).toString());
  }

  @Test public void testCfgUnger() throws ParseException,
      FileNotFoundException {
    Cfg cfg = GrammarLoader.readCfg("anbn.cfg");
    String w = "a a b b";
    ParsingSchema schema = CfgToUngerRulesConverter
        .cfgToUngerRules(cfg, w);
    Deduction deduction = new Deduction();
    assertTrue(deduction.doParse(schema, false));
    assertEquals(4, deduction.getMaxAgendaSize());
    String[][] data = deduction.getTraceTable();
    deduction.printTrace(data);
    assertEquals("(S (a )(S (a )(b ))(b ))",
        deduction.getDerivedTrees().get(0).toString());
  }

  @Test public void testCfgUngerGoalTrees()
      throws ParseException, FileNotFoundException {
    String w = "t0 t1";
    Cfg cfg = GrammarLoader.readCfg("ungerwronggoaltrees.cfg");
    ParsingSchema schema = CfgToUngerRulesConverter.cfgToUngerRules(
        cfg, w);
    Deduction deduction = new Deduction();
    assertTrue(deduction.doParse(schema, false));
    assertEquals(2, deduction.getMaxAgendaSize());
    String[][] data = deduction.getTraceTable();
    deduction.printTrace(data);
    assertEquals("(N1 (t0 )(t1 ))",
        deduction.getDerivedTrees().get(0).toString());
    assertEquals(1, deduction.getDerivedTrees().size());
  }

  @Test public void testCfgUngerComplete()
      throws ParseException, FileNotFoundException {
    String w = "t1";
    Cfg cfg = GrammarLoader.readCfg("ungercomplete.cfg");
    ParsingSchema schema = CfgToUngerRulesConverter.cfgToUngerRules(
        cfg, w);
    Deduction deduction = new Deduction();
    assertTrue(deduction.doParse(schema, false));
    assertEquals(2, deduction.getMaxAgendaSize());
    String[][] data = deduction.getTraceTable();
    deduction.printTrace(data);
    assertEquals(1, deduction.getDerivedTrees().size());
    assertEquals("(S2 (S1 (N0 (t1 ))))",
        deduction.getDerivedTrees().get(0).toString());
  }

  @Test public void testCfgCykGeneral()
      throws ParseException, FileNotFoundException {
    Cfg cfg = GrammarLoader.readCfg("anbn.cfg");
    String w = "a a b b";
    ParsingSchema schema = CfgToCykRulesConverter
        .cfgToCykGeneralRules(cfg, w);
    Deduction deduction = new Deduction();
    assertTrue(deduction.doParse(schema, false));
    assertEquals(9, deduction.getMaxAgendaSize());
    String[][] data = deduction.getTraceTable();
    deduction.printTrace(data);
    assertEquals("(S (a )(S (a )(b ))(b ))",
        deduction.getDerivedTrees().get(0).toString());
  }

  @Test public void testCfgCykGeneralEmptyWord()
      throws ParseException, FileNotFoundException {
    String w = "";
    Cfg cfg = GrammarLoader.readCfg("emptyword.cfg");
    ParsingSchema schema = CfgToCykRulesConverter
        .cfgToCykGeneralRules(cfg, w);
    Deduction deduction = new Deduction();
    assertTrue(deduction.doParse(schema, false));
    assertEquals(2, deduction.getMaxAgendaSize());
    String[][] data = deduction.getTraceTable();
    deduction.printTrace(data);
    assertEquals("(N0 (ε ))",
        deduction.getDerivedTrees().get(0).toString());
  }

  @Test public void testCfgLr() throws ParseException, FileNotFoundException {
    String w = "the apple";
    Cfg cfg = GrammarLoader.readCfg("lr.cfg");
    ParsingSchema schema =
        CfgToLrKRulesConverter.cfgToLrKRules(cfg, w, 0);
    Deduction deduction = new Deduction();
    assertTrue(deduction.doParse(schema, false));
    assertEquals(1, deduction.getMaxAgendaSize());
    assertEquals("(NP (Det (the ))(N (apple )))",
        deduction.getDerivedTrees().get(0).toString());
    schema =
        CfgToLrKRulesConverter.cfgToLrKRules(cfg, w, 1);
    assertTrue(deduction.doParse(schema, false));
    assertEquals(1, deduction.getMaxAgendaSize());
   /* schema = cfgToLrKRules(TestGrammarLibrary.lrCfg(), w, 2);
    assertTrue(deduction.doParse(schema, false)); //*/
  }

  @Test public void testCcg() throws IOException, ParseException {
    String w = "Trip certainly likes merengue";
    Ccg ccg = GrammarLoader.readCcg("trip.ccg");
    ParsingSchema schema = CcgToDeductionRulesConverter
        .ccgToDeductionRules(ccg, w);
    Deduction deduction = new Deduction();
    assertTrue(deduction.doParse(schema, false));
    assertEquals(
        "(S (NP (Trip ))(S\\NP ([S\\NP]/NP ([S\\NP]/[S\\NP] (certainly ))([S\\NP]/NP (likes )))(NP (merengue ))))",
        deduction.getDerivedTrees().get(0).toString());
  }

  @Test public void testLag() throws ParseException {
    String w = "a a a b b b c c c";
    String lagGrammar = "G = <W, C, LX, CO, RP, ST_S, ST_F>\n"
        + "LX = {[a (b c)], [b (b)], [c (c)]}\n"
        + "ST_S = {[{r1, r2} (b c)]}\n" + "RP = {"
        + "r1 : [(X) (b c)] -> [{r1, r2} (b X c)],\n"
        + "r2 : [(b X c) (b)] -> [{r2, r3} (X c)],\n"
        + "r3 : [(c X) (c)] -> [{r3} (X)]}\n" + "ST_F = {[{r3} ε]}\n";
    StringReader reader = new StringReader(lagGrammar);
    Lag lag = LagParser.parseLagReader(reader);
    ParsingSchema schema = LagToDeductionRulesConverter.lagToDeductionRules(lag, w);
    Deduction deduction = new Deduction();
    assertTrue(deduction.doParse(schema, false));
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

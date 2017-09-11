package chartparsing.converter;

import static org.junit.Assert.assertEquals;

import java.text.ParseException;

import org.junit.Ignore;
import org.junit.Test;

import chartparsing.Deduction;
import chartparsing.ParsingSchema;
import common.TestGrammarLibrary;
import common.tag.Tree;

public class ChartToTreeConverterTest {

  @Test public void testTagCykToDerivatedTree() throws ParseException {
    String w2 = "a a c b";
    ParsingSchema schema = TagToDeductionRulesConverter
      .tagToCykRules(TestGrammarLibrary.gentag(), w2);
    Deduction deduction = new Deduction();
    deduction.doParse(schema, false);
    Tree derivatedTree = ChartToTreeConverter.tagToDerivatedTree(deduction,
      schema.getGoals(), TestGrammarLibrary.gentag());
    assertEquals("(S (T (a )(T (a )(T (c ))))(b ))", derivatedTree.toString());
  }

  @Test public void testTagEarleyToDerivatedTree() throws ParseException {
    String w2 = "a a c b";
    ParsingSchema schema = TagToDeductionRulesConverter
      .tagToEarleyRules(TestGrammarLibrary.gentag(), w2);
    Deduction deduction = new Deduction();
    deduction.doParse(schema, false);
    Tree derivatedTree = ChartToTreeConverter.tagToDerivatedTree(deduction,
      schema.getGoals(), TestGrammarLibrary.gentag());
    assertEquals("(S (T (a )(T (a )(T (c ))))(b ))", derivatedTree.toString());
  }

  @Ignore("waits to get fixed") public void testTagUnusedItemsToDerivedTree()
    throws ParseException {
    String w = "a c b";
    ParsingSchema schema = TagToDeductionRulesConverter
      .tagToEarleyRules(TestGrammarLibrary.acbTag(), w);
    Deduction deduction = new Deduction();
    deduction.doParse(schema, false);
    Tree derivatedTree = ChartToTreeConverter.tagToDerivatedTree(deduction,
      schema.getGoals(), TestGrammarLibrary.acbTag());
    assertEquals("(S (A (a ))(S (c ))(B (b )))", derivatedTree.toString());
  }

  @Test public void testCfgTopdownToDerivationTree() throws ParseException {
    String w = "a c b";
    ParsingSchema schema = CfgToDeductionRulesConverter
      .cfgToTopDownRules(TestGrammarLibrary.gencfg(), w);
    Deduction deduction = new Deduction();
    deduction.doParse(schema, false);
    Tree derivationTree = ChartToTreeConverter.cfgToDerivatedTree(deduction,
      schema.getGoals(), "topdown");
    assertEquals("(S (A (a ))(S (c ))(B (b )))", derivationTree.toString());

    w = "a a a b b b";
    schema = CfgToDeductionRulesConverter
      .cfgToTopDownRules(TestGrammarLibrary.gen_cfgdedtest(), w);
    deduction = new Deduction();
    deduction.doParse(schema, false);
    derivationTree = ChartToTreeConverter.cfgToDerivatedTree(deduction,
      schema.getGoals(), "topdown");
    assertEquals("(S (a )(S (a )(S (a )(b ))(b ))(b ))",
      derivationTree.toString());
  }

  @Test public void testCfgEarleyToDerivationTree() throws ParseException {
    String w = "a c b";
    ParsingSchema schema = CfgToDeductionRulesConverter
      .cfgToEarleyRules(TestGrammarLibrary.gencfg(), w);
    Deduction deduction = new Deduction();
    deduction.doParse(schema, false);
    Tree derivationTree = ChartToTreeConverter.cfgToDerivatedTree(deduction,
      schema.getGoals(), "earley");
    assertEquals("(S (A (a ))(S (c ))(B (b )))", derivationTree.toString());

    w = "a a a b b b";
    schema = CfgToDeductionRulesConverter
      .cfgToEarleyRules(TestGrammarLibrary.gen_cfgdedtest(), w);
    deduction = new Deduction();
    deduction.doParse(schema, false);
    derivationTree = ChartToTreeConverter.cfgToDerivatedTree(deduction,
      schema.getGoals(), "earley");
    assertEquals("(S (a )(S (a )(S (a )(b ))(b ))(b ))",
      derivationTree.toString());
  }

  @Test public void testCfgShiftReduceToDerivationTree() throws ParseException {
    String w = "a c b";
    ParsingSchema schema = CfgToDeductionRulesConverter
      .cfgToShiftReduceRules(TestGrammarLibrary.gencfg(), w);
    Deduction deduction = new Deduction();
    deduction.doParse(schema, false);
    Tree derivationTree = ChartToTreeConverter.cfgToDerivatedTree(deduction,
      schema.getGoals(), "shiftreduce");
    assertEquals("(S (A (a ))(S (c ))(B (b )))", derivationTree.toString());
  }

  @Test public void testCfgUngerToDerivationTree() throws ParseException {
    String w = "a a a b b b";
    ParsingSchema schema = CfgToDeductionRulesConverter
      .cfgToUngerRules(TestGrammarLibrary.gen_cfgdedtest(), w);
    Deduction deduction = new Deduction();
    deduction.doParse(schema, false);
    Tree derivationTree = ChartToTreeConverter.cfgToDerivatedTree(deduction,
      schema.getGoals(), "unger");
    assertEquals("(S (a )(S (a )(S (a )(b ))(b ))(b ))",
      derivationTree.toString());
  }

  @Ignore public void testSrcgCykExtendedToDerivatiobTree()
    throws ParseException {
    String w = "a b c d";
    ParsingSchema schema = LcfrsToDeductionRulesConverter
      .srcgToCykExtendedRules(TestGrammarLibrary.snbmcndmSrcg(), w);
    Deduction deduction = new Deduction();
    deduction.doParse(schema, false);
    Tree derivationTree = ChartToTreeConverter.srcgToDerivatedTree(deduction,
      schema.getGoals(), "cyk");
    assertEquals("(S (a )(S (a )(S (a )(b ))(b ))(b ))",
      derivationTree.toString());
  }

  @Test public void testSrcgEarleyToDerivationTree() throws ParseException {
    String w = "a b c d";
    ParsingSchema schema = LcfrsToDeductionRulesConverter
      .srcgToEarleyRules(TestGrammarLibrary.snbmcndmSrcg(), w);
    Deduction deduction = new Deduction();
    deduction.doParse(schema, false);
    Tree derivationTree = ChartToTreeConverter.srcgToDerivatedTree(deduction,
      schema.getGoals(), "earley");
    assertEquals("(S (A (a<0> )(c<2> ))(B (b<1> )(d<3> )))",
      derivationTree.toString());

    w = "a a b b c c d d";
    schema = LcfrsToDeductionRulesConverter
      .srcgToEarleyRules(TestGrammarLibrary.snbmcndmSrcg(), w);
    deduction = new Deduction();
    deduction.doParse(schema, false);
    derivationTree = ChartToTreeConverter.srcgToDerivatedTree(deduction,
      schema.getGoals(), "earley");
    assertEquals(
      "(S (A (a<0> )(A (a<1> )(c<5> ))(c<4> ))(B (b<2> )(B (b<3> )(d<7> ))(d<6> )))",
      derivationTree.toString());
  }
}

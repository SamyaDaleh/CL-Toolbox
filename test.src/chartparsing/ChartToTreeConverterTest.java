package chartparsing;

import static org.junit.Assert.assertEquals;

import java.text.ParseException;

import org.junit.Test;

import chartparsing.converter.CfgToDeductionRulesConverter;
import chartparsing.converter.ChartToTreeConverter;
import chartparsing.converter.TagToDeductionRulesConverter;
import common.cfg.Cfg;
import common.tag.Tag;
import common.tag.Tree;

public class ChartToTreeConverterTest {

  private static Tag gentag() throws ParseException {
    Tag g = new Tag();
    g.setNonterminals(new String[] {"S", "T"});
    g.setTerminals(new String[] {"a", "b", "c"});
    g.setStartsymbol("S");
    g.addInitialTree("α1", "(S T b)");
    g.addInitialTree("α2", "(T c)");
    g.addAuxiliaryTree("β", "(T a T*)");
    return g;
  }

  private static Cfg gencfg() {
    Cfg cfg = new Cfg();

    cfg.setTerminals(new String[] {"a", "b", "c"});
    cfg.setNonterminals(new String[] {"A", "B", "S"});
    cfg.setProductionrules(new String[][] {{"S", "A S B"}, {"S", "a b"},
      {"S", "c"}, {"A", "a"}, {"B", "b"}});
    cfg.setStartsymbol("S");
    return cfg;
  }

  private static Cfg gen_cfgdedtest() {
    Cfg cfg = new Cfg();

    cfg.setTerminals(new String[] {"a", "b"});
    cfg.setNonterminals(new String[] {"S"});
    cfg.setProductionrules(new String[][] {{"S", "a S b"}, {"S", "a b"}});
    cfg.setStartsymbol("S");

    return cfg;
  }

  @Test public void testTagCykToDerivatedTree() throws ParseException {
    String w2 = "a a c b";
    ParsingSchema schema =
      TagToDeductionRulesConverter.tagToParsingSchema(gentag(), w2, "cyk");
    Deduction deduction = new Deduction();
    deduction.doParse(schema, false);
    Tree derivatedTree = ChartToTreeConverter.tagToDerivatedTree(deduction,
      schema.getGoals(), gentag());
    assertEquals("(S (T (a )(T (a )(T (c ))))(b ))", derivatedTree.toString());
  }

  @Test public void testTagEarleyToDerivatedTree() throws ParseException {
    String w2 = "a a c b";
    ParsingSchema schema =
      TagToDeductionRulesConverter.tagToParsingSchema(gentag(), w2, "earley");
    Deduction deduction = new Deduction();
    deduction.doParse(schema, false);
    Tree derivatedTree = ChartToTreeConverter.tagToDerivatedTree(deduction,
      schema.getGoals(), gentag());
    assertEquals("(S (T (a )(T (a )(T (c ))))(b ))", derivatedTree.toString());

  }

  @Test public void testCfgTopdownToDerivationTree() throws ParseException {
    String w = "a c b";
    ParsingSchema schema =
      CfgToDeductionRulesConverter.cfgToTopDownRules(gencfg(), w);
    Deduction deduction = new Deduction();
    deduction.doParse(schema, false);
    Tree derivationTree = ChartToTreeConverter.cfgToDerivatedTree(deduction,
      schema.getGoals(), "topdown");
    assertEquals("(S (A (a ))(S (c ))(B (b )))", derivationTree.toString());

    w = "a a a b b b";
    schema =
      CfgToDeductionRulesConverter.cfgToTopDownRules(gen_cfgdedtest(), w);
    deduction = new Deduction();
    deduction.doParse(schema, false);
    derivationTree = ChartToTreeConverter.cfgToDerivatedTree(deduction,
      schema.getGoals(), "topdown");
    assertEquals("(S (a )(S (a )(S (a )(b ))(b ))(b ))",
      derivationTree.toString());
  }

  @Test public void testCfgEarleyToDerivationTree() throws ParseException {
    String w = "a c b";
    ParsingSchema schema =
      CfgToDeductionRulesConverter.cfgToEarleyRules(gencfg(), w);
    Deduction deduction = new Deduction();
    deduction.doParse(schema, false);
    Tree derivationTree = ChartToTreeConverter.cfgToDerivatedTree(deduction,
      schema.getGoals(), "earley");
    assertEquals("(S (A (a ))(S (c ))(B (b )))", derivationTree.toString());

    w = "a a a b b b";
    schema = CfgToDeductionRulesConverter.cfgToEarleyRules(gen_cfgdedtest(), w);
    deduction = new Deduction();
    deduction.doParse(schema, false);
    derivationTree = ChartToTreeConverter.cfgToDerivatedTree(deduction,
      schema.getGoals(), "earley");
    assertEquals("(S (a )(S (a )(S (a )(b ))(b ))(b ))",
      derivationTree.toString());
  }

  @Test public void testCfgShiftReduceToDerivationTree() throws ParseException {
    String w = "a c b";
    ParsingSchema schema =
      CfgToDeductionRulesConverter.cfgToShiftReduceRules(gencfg(), w);
    Deduction deduction = new Deduction();
    deduction.doParse(schema, false);
    Tree derivationTree = ChartToTreeConverter.cfgToDerivatedTree(deduction,
      schema.getGoals(), "shiftreduce");
    assertEquals("(S (A (a ))(S (c ))(B (b )))", derivationTree.toString());
  }
}

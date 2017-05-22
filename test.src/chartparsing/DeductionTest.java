package chartparsing;

import static org.junit.Assert.assertTrue;

import java.text.ParseException;

import org.junit.Test;

import common.cfg.Cfg;
import common.lcfrs.Srcg;
import common.tag.Tag;

public class DeductionTest {

  static Cfg gen_cfgdedtest() {
    Cfg G = new Cfg();

    G.setTerminals(new String[] {"a", "b"});
    G.setVars(new String[] {"S"});
    G.setR(new String[][] {{"S", "a S b"}, {"S", "a b"}});
    G.setStart_var("S");

    return G;
  }

  static Tag gentag() throws ParseException {
    Tag g = new Tag();
    g.setNonterminals(new String[] {"S", "T"});
    g.setTerminals(new String[] {"a", "b", "c"});
    g.setStartsymbol("S");
    // Tree bracket format: (Elder Child1 Child2 Child3)
    // or (Elder (ElderOf1stSubtree Child1OfFirstSubtree) (ElderOf2ndSubtree
    // Child1Of2ndSubtree Child2Of2ndSubtree))
    // or with Gorn addresses: (0 (1 1.1) (2 2.1 2.2))
    g.addInitialTree("α1", "(S T b)");
    g.addInitialTree("α2", "(T c)");
    g.addAuxiliaryTree("β", "(T a T*)");
    return g;
  }

  static Srcg gensrcg() {
    Srcg srcg = new Srcg();
    srcg.setNonterminals(new String[] {"S", "A"});
    srcg.setTerminals(new String[] {"a", "b"});
    srcg.setVariables(new String[] {"X1", "X2"});
    srcg.setStartSymbol("S");
    srcg.addClause("S (X1 X2)", "A(X1, X2)");
    srcg.addClause("A (a X1, b X2)", "A(X1, X2)");
    srcg.addClause("A (a,b)", "ε");
    return srcg;
  }

  @Test public void testCfgTopdown() {
    String w = "a a b b";
    ParsingSchema schema =
      CfgToDeductionRulesConverter.CfgToTopDownRules(gen_cfgdedtest(), w);
    Deduction deduction = new Deduction();
    assertTrue(deduction.doParse(schema, false));
    deduction.printTrace();
  }

  @Test public void testCfgShiftreduce() {
    String w = "a a b b";
    ParsingSchema schema =
      CfgToDeductionRulesConverter.CfgToShiftReduceRules(gen_cfgdedtest(), w);
    Deduction deduction = new Deduction();
    assertTrue(deduction.doParse(schema, false));
    deduction.printTrace();
  }

  @Test public void testCfgEarley() {
    String w = "a a b b";
    ParsingSchema schema =
      CfgToDeductionRulesConverter.CfgToEarleyRules(gen_cfgdedtest(), w);
    Deduction deduction = new Deduction();
    assertTrue(deduction.doParse(schema, false));
    deduction.printTrace();
  }

  @Test public void testCfgLeftcorner() {
    String w = "a a b b";
    ParsingSchema schema =
      CfgToDeductionRulesConverter.CfgToLeftCornerRules(gen_cfgdedtest(), w);
    Deduction deduction = new Deduction();
    assertTrue(deduction.doParse(schema, false));
    deduction.printTrace();
  }

  @Test public void testTagCyk() throws ParseException {
    String w2 = "a c b";
    ParsingSchema schema =
      TagToDeductionRulesConverter.TagToParsingSchema(gentag(), w2, "cyk");
    Deduction deduction = new Deduction();
    assertTrue(deduction.doParse(schema, false));
    deduction.printTrace();
  }

  @Test public void testTagEarley() throws ParseException {
    String w2 = "a c b";
    ParsingSchema schema =
      TagToDeductionRulesConverter.TagToParsingSchema(gentag(), w2, "earley");
    Deduction deduction = new Deduction();
    assertTrue(deduction.doParse(schema, false));
    deduction.printTrace();
  }

  @Test public void testSrcgEarley() throws ParseException {
    String w3 = "a a b b";
    ParsingSchema schema = LcfrsToDeductionRulesConverter
      .SrcgToParsingSchema(gensrcg(), w3, "earley");
    Deduction deduction = new Deduction();
    assertTrue(deduction.doParse(schema, false));
    deduction.printTrace();
  }

}

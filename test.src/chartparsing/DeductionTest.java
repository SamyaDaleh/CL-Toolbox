package chartparsing;

import static org.junit.Assert.assertTrue;

import java.text.ParseException;

import org.junit.Test;

import chartparsing.converter.CfgToDeductionRulesConverter;
import chartparsing.converter.LcfrsToDeductionRulesConverter;
import chartparsing.converter.PcfgToDeductionRulesConverter;
import chartparsing.converter.TagToDeductionRulesConverter;
import common.cfg.Cfg;
import common.cfg.Pcfg;
import common.lcfrs.Srcg;
import common.tag.Tag;

public class DeductionTest {

  private static Cfg gen_cfgdedtest() {
    Cfg cfg = new Cfg();

    cfg.setTerminals(new String[] {"a", "b"});
    cfg.setNonterminals(new String[] {"S"});
    cfg.setProductionrules(new String[][] {{"S", "a S b"}, {"S", "a b"}});
    cfg.setStartSymbol("S");

    return cfg;
  }

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

  private static Srcg gensrcg() throws ParseException {
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
        CfgToDeductionRulesConverter.cfgToTopDownRules(gen_cfgdedtest(), w);
    Deduction deduction = new Deduction();
    assertTrue(deduction.doParse(schema, false));
    deduction.printTrace();
  }

  @Test public void testCfgShiftreduce() {
    String w = "a a b b";
    ParsingSchema schema =
        CfgToDeductionRulesConverter.cfgToShiftReduceRules(gen_cfgdedtest(), w);
    Deduction deduction = new Deduction();
    assertTrue(deduction.doParse(schema, false));
    deduction.printTrace();
  }

  @Test public void testCfgEarley() {
    String w = "a a b b";
    ParsingSchema schema =
        CfgToDeductionRulesConverter.cfgToEarleyRules(gen_cfgdedtest(), w);
    Deduction deduction = new Deduction();
    assertTrue(deduction.doParse(schema, false));
    deduction.printTrace();
  }

  @Test public void testCfgLeftcorner() {
    String w = "a a b b";
    ParsingSchema schema =
        CfgToDeductionRulesConverter.cfgToLeftCornerRules(gen_cfgdedtest(), w);
    Deduction deduction = new Deduction();
    assertTrue(deduction.doParse(schema, false));
    deduction.printTrace();
  }

  @Test public void testCfgCyk() {
    Cfg cfg = new Cfg();

    cfg.setTerminals(new String[] {"a", "b"});
    cfg.setNonterminals(new String[] {"S", "A", "B", "X1"});
    cfg.setProductionrules(
        new String[][] {{"S", "A X1"}, {"S", "A B"}, {"A", "a"}, {"B", "b"},
            {"X1", "S B"}});
    cfg.setStartSymbol("S");

    String w = "a a b b";
    ParsingSchema schema = CfgToDeductionRulesConverter.cfgToCykRules(cfg, w);
    Deduction deduction = new Deduction();
    assertTrue(deduction.doParse(schema, false));
    deduction.printTrace();
  }

  @Test public void testCfgCykExtended() {
    Cfg cfg = new Cfg();

    cfg.setTerminals(new String[] {"a", "b"});
    cfg.setNonterminals(new String[] {"S", "A", "B", "C", "X1"});
    cfg.setProductionrules(
        new String[][] {{"S", "A X1"}, {"S", "A B"}, {"C", "a"}, {"B", "b"},
            {"X1", "S B"}, {"A", "C"}});
    cfg.setStartSymbol("S");

    String w = "a a b b";
    ParsingSchema schema =
        CfgToDeductionRulesConverter.cfgToCykExtendedRules(cfg, w);
    Deduction deduction = new Deduction();
    assertTrue(deduction.doParse(schema, false));
    deduction.printTrace();
  }

  @Test public void testTagCyk() throws ParseException {
    String w2 = "a c b";
    ParsingSchema schema =
        TagToDeductionRulesConverter.tagToParsingSchema(gentag(), w2, "cyk");
    Deduction deduction = new Deduction();
    assertTrue(deduction.doParse(schema, false));
    deduction.printTrace();
  }

  @Test public void testTagEarley() throws ParseException {
    String w2 = "a c b";
    ParsingSchema schema =
        TagToDeductionRulesConverter.tagToParsingSchema(gentag(), w2, "earley");
    Deduction deduction = new Deduction();
    assertTrue(deduction.doParse(schema, false));
    deduction.printTrace();
  }

  @Test public void testSrcgCykUnary() throws ParseException {
    String w3 = "a a b b";
    ParsingSchema schema = LcfrsToDeductionRulesConverter
        .srcgToParsingSchema(gensrcg(), w3, "cyk-extended");
    Deduction deduction = new Deduction();
    assertTrue(deduction.doParse(schema, false));
    deduction.printTrace();
  }
  
  @Test public void testSrcgCykBinary() throws ParseException{
    Srcg srcg = new Srcg();
    srcg.setNonterminals(new String[]{"S", "A", "B", "C"});
    srcg.setTerminals(new String[]{"a", "b", "c"});
    srcg.setVariables(new String[]{"U", "V", "W", "X", "Y", "Z"});
    srcg.setStartSymbol("S");
    srcg.addClause("S(V Y W Z X )", "A(V,W,X) B(Y,Z)");
    srcg.addClause("A(a,a,a)", "ε");
    srcg.addClause("A(X U, Y V, Z W)", "A(X,Y,Z) C(U,V,W)");
    srcg.addClause("B(b,b)", "ε");
    srcg.addClause("B(X V,W Y)", "B(X,Y) B(V,W)");
    srcg.addClause("C(a,c,c)", "ε");
    String w = "a a b b a c b b a c";
    ParsingSchema schema = LcfrsToDeductionRulesConverter
        .srcgToParsingSchema(srcg, w, "cyk-extended");
    Deduction deduction = new Deduction();
    assertTrue(deduction.doParse(schema, false));
    deduction.printTrace();
  }

  @Test public void testSrcgEarley() throws ParseException {
    String w3 = "a a b b";
    ParsingSchema schema = LcfrsToDeductionRulesConverter
        .srcgToParsingSchema(gensrcg(), w3, "earley");
    Deduction deduction = new Deduction();
    assertTrue(deduction.doParse(schema, false));
    deduction.printTrace();
  }

  @Test public void testPcfgAstar() throws ParseException {
    Pcfg pcfg = new Pcfg();
    pcfg.setNonterminals(new String[] {"N", "A"});
    pcfg.setTerminals(
        new String[] {"camping", "car", "nice", "red", "ugly", "green", "house",
            "bike"});
    pcfg.setStartSymbol("N");
    pcfg.setProductionRules(new String[][] {{"N", "N N", "0.1"}, {"N", "red", "0.1"},
        {"N", "car", "0.1"}, {"N", "camping", "0.2"}, {"A", "nice", "0.3"},
        {"A", "red", "0.2"}, {"N", "A N", "0.2"}, {"N", "green", "0.1"},
        {"N", "bike", "0.1"}, {"N", "house", "0.1"}, {"A", "ugly", "0.25"},
        {"A", "green", "0.25"}});

    String w = "red nice ugly car";
    ParsingSchema schema =
        PcfgToDeductionRulesConverter.pcfgToParsingSchema(pcfg, w, "astar");
    Deduction deduction = new Deduction();
    assertTrue(deduction.doParse(schema, false));
    deduction.printTrace();
  }

}

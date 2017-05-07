import java.text.ParseException;

import chartparsing.CfgToDeductionRulesConverter;
import chartparsing.Deduction;
import chartparsing.ParsingSchema;
import chartparsing.TagToDeductionRulesConverter;
import common.cfg.Cfg;
import common.cfg.Pcfg;
import common.tag.Tag;

public class Main {

  static Pcfg gen_pcfg0() {
    Pcfg G = new Pcfg();

    G.setTerminals(new String[] {"0", "1"});
    G.setVars(new String[] {"S", "A", "B"});
    G.setR(new String[][] {{"S", "A B", "1"}, {"A", "1", "0.7"},
      {"A", "0", "0.3"}, {"B", "B B", "0.6"}, {"B", "0", "0.4"}});
    G.setStart_var("S");

    return G;
  }

  static Cfg gen_cfgdedtest() {
    Cfg G = new Cfg();

    G.setTerminals(new String[] {"a", "b"});
    G.setVars(new String[] {"S"});
    G.setR(new String[][] {{"S", "a S b"}, {"S", "a b"}});
    G.setStart_var("S");

    return G;
  }

  static Cfg gen_cfgbin() {
    Cfg G = new Cfg();

    G.setTerminals(new String[] {"a", "b"});
    G.setVars(new String[] {"S", "X"});
    G.setR(new String[][] {{"S", "a X"}, {"S", "a b"}, {"X", "S b"}});
    G.setStart_var("S");

    return G;
  }

  static Cfg gen_cfgleftrec() {
    Cfg G = new Cfg();

    G.setTerminals(new String[] {"a", "b"});
    G.setVars(new String[] {"S", "A"});
    G.setR(new String[][] {{"S", "a A"}, {"A", ""}});
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

  public static void main(String[] args) throws ParseException {
    // calculate sx estimates
    /* Map<String,Double> insides = SxCalc.getInsides(gen_pcfg0(), 4);
     * 
     * for (String in : insides.keySet()) { System.out.println(in + " = " +
     * insides.get(in)); } Map<String,Double> outsides =
     * SxCalc.getOutsides(insides, 4, gen_pcfg0()); for (String out :
     * outsides.keySet()) { System.out.println(out + " = " + outsides.get(out));
     * } // */

    // String w = "a a a b b b";
    // String w = "a a b b";
    // earley or shiftreduce or topdown
    /* ParsingSchema schema = CfgToDeductionRulesConverter
     * .CfgToParsingSchema(gen_cfgdedtest(), w, "earley"); // */
    /* String w = "a c b"; ParsingSchema schema = TagToDeductionRulesConverter
     * .TagToParsingSchema(gentag(), w, "cyk");
     * System.out.println(Deduction.doParse(schema)); // */

    /* String w = "a a b b"; Tag tagFromCfg = new Tag(gen_cfgbin());
     * ParsingSchema schema = TagToDeductionRulesConverter
     * .TagToParsingSchema(tagFromCfg, w, "cyk");
     * System.out.println(Deduction.doParse(schema)); // */

    String w = "a";
    ParsingSchema schema = CfgToDeductionRulesConverter
      .CfgToParsingSchema(gen_cfgleftrec(), w, "leftcorner");
    System.out.println(Deduction.doParse(schema));
  }

}

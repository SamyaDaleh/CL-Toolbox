package chartparsing;
import java.text.ParseException;

import chartparsing.CfgToDeductionRulesConverter;
import chartparsing.Deduction;
import chartparsing.ParsingSchema;
import common.cfg.Cfg;
import common.tag.Tag;

public class DeductionTest {
	
    static Cfg gen_cfgdedtest()
    { 
        Cfg G = new Cfg();

        G.setTerminals(new String[]{"a", "b"});
        G.setVars(new String[]{"S"});
        G.setR(new String[][]{
            { "S", "a S b" },
            { "S", "a b" }
        });
        G.setStart_var("S");

        return G;
    }
    
    static Tag gentag() throws ParseException {
      Tag g = new Tag();
      g.setNonterminals(new String[] {"S", "T"});
      g.setTerminals(new String[] {"a", "b", "c"});
      g.setStartsymbol("S");
      // Tree bracket format: (Elder Child1 Child2 Child3)
      // or (Elder (ElderOf1stSubtree Child1OfFirstSubtree) (ElderOf2ndSubtree Child1Of2ndSubtree Child2Of2ndSubtree))
      // or with Gorn addresses: (0 (1 1.1) (2 2.1 2.2))
      g.addInitialTree("α1","(S T b)");
      g.addInitialTree("α2","(T c)");
      g.addAuxiliaryTree("β","(T a T*)");
      return g;
    }
    
	public static void main(String[] args) throws ParseException {
		String w = "a a b b";
		ParsingSchema schema = CfgToDeductionRulesConverter.CfgToTopDownRules(gen_cfgdedtest(), w);
		Deduction deduction = new Deduction();
		if(deduction.doParse(schema, false)) {
			System.out.println("CFG Topdown Parsing successful");
		} else {
			System.out.println("CFG Topdown Parsing fail");
		} 
		deduction.printTrace();
		schema = CfgToDeductionRulesConverter.CfgToShiftReduceRules(gen_cfgdedtest(), w);
    if(deduction.doParse(schema, false)) {
      System.out.println("CFG Shiftreduce Parsing successful");
    } else {
      System.out.println("CFG Shiftreduce Parsing fail");
    } //*/
    deduction.printTrace();
     schema = CfgToDeductionRulesConverter.CfgToEarleyRules(gen_cfgdedtest(), w);
    if(deduction.doParse(schema, false)) {
      System.out.println("CFG Earley Parsing successful");
    } else {
      System.out.println("CFG Earley Parsing fail");
    } //*/
    deduction.printTrace();

    schema = CfgToDeductionRulesConverter.CfgToLeftCornerRules(gen_cfgdedtest(), w);
    if(deduction.doParse(schema, false)) {
      System.out.println("CFG Leftcorner Parsing successful");
    } else {
      System.out.println("CFG Leftcorner Parsing fail");
    }  //*/
    deduction.printTrace();
    
    String w2 = "a c b";
    schema = TagToDeductionRulesConverter
        .TagToParsingSchema(gentag(), w2, "cyk");
    if(deduction.doParse(schema, false)) {
      System.out.println("TAG CYK Parsing successful");
    } else {
      System.out.println("TAG CYK Parsing fail");
    }
    deduction.printTrace();
    schema = TagToDeductionRulesConverter
        .TagToParsingSchema(gentag(), w2, "earley");
    if(deduction.doParse(schema, false)) {
      System.out.println("TAG Earley Parsing successful");
    } else {
      System.out.println("TAG Earley Parsing fail");
    }
    deduction.printTrace();
	}

}

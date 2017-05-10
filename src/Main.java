import java.io.IOException;
import java.text.ParseException;

import chartparsing.CfgToDeductionRulesConverter;
import chartparsing.Deduction;
import chartparsing.ParsingSchema;
import chartparsing.TagToDeductionRulesConverter;
import common.GrammarParser;
import common.cfg.Cfg;
import common.tag.Tag;

/** Entry point into toolbox for the calls by command line */
public class Main {

  /** Command line arguments are passed here. Call without arguments displays
   * help about the what arguments to use. */
  public static void main(String[] args) throws ParseException, IOException {
    if (args.length < 3) {
      System.out.println(
        "Please pass at least 3 parameters: [grammar file] [input string] " 
      + "[parsing algorithm] [<optional parameters>]");
      System.out.println(
        "Parsing algorithm can be: cfg-topdown, cfg-shiftreduce, cfg-earley, tag-cyk");
      System.out.println(
          "Optional parameters can be: --sucess : prints a trace only of items " 
      + "that lead to a goal item.");
      System.out.println(
        "example: ..\\resources\\grammars\\anbncfg \"a a b b\" cfg-topdown");
      return;
    }
    String grammarfile = args[0];
    String w = args[1];
    String algorithm = args[2];
    boolean success = false;
    for (int i = 3; i < args.length; i ++) {
      if (args[i].equals("--success")) {
        success = true;
      }
    }
    if (grammarfile.endsWith(".cfg")) {
      Cfg cfg = GrammarParser.parseCfgFile(grammarfile);
      ParsingSchema schema = null;
      switch (algorithm) {
      case "cfg-topdown":
        schema =
          CfgToDeductionRulesConverter.CfgToParsingSchema(cfg, w, "topdown");
        break;
      case "cfg-shiftreduce":
        schema = CfgToDeductionRulesConverter.CfgToParsingSchema(cfg, w,
          "shiftreduce");
        break;
      case "cfg-earley":
        schema =
          CfgToDeductionRulesConverter.CfgToParsingSchema(cfg, w, "earley");
        break;
      case "tag-cyk":
        Tag tag = new Tag(cfg);
        schema = TagToDeductionRulesConverter.TagToParsingSchema(tag, w, "cyk");
        break;
      default:
        System.out.println(
          "I did not understand. Please check spelling of parsing algorithm.");
        return;
      }
      System.out.println(Deduction.doParse(schema, success));
    }
    if (grammarfile.endsWith(".tag")) {
      Tag tag = GrammarParser.parseTagFile(grammarfile);
      ParsingSchema schema = null;
      switch (algorithm) {
      case "tag-cyk":
        schema = TagToDeductionRulesConverter.TagToParsingSchema(tag, w, "cyk");
        break;
      case "tag-earley":
        schema =
          TagToDeductionRulesConverter.TagToParsingSchema(tag, w, "earley");
        break;
      default:
        System.out.println(
          "I did not understand. Please check spelling of parsing algorithm "
            + "and if algorithm is appropriate for grammar.");
        return;
      }
      System.out.println(Deduction.doParse(schema, success));
    }
    // calculate sx estimates
    /* Map<String,Double> insides = SxCalc.getInsides(gen_pcfg0(), 4);
     * 
     * for (String in : insides.keySet()) { System.out.println(in + " = " +
     * insides.get(in)); } Map<String,Double> outsides =
     * SxCalc.getOutsides(insides, 4, gen_pcfg0()); for (String out :
     * outsides.keySet()) { System.out.println(out + " = " + outsides.get(out));
     * } // */
  }

}

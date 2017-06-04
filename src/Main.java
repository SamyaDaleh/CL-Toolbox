import java.io.IOException;
import java.text.ParseException;

import chartparsing.ChartToTreeConverter;
import chartparsing.Deduction;
import chartparsing.GrammarToDeductionRulesConverter;
import chartparsing.ParsingSchema;
import common.GrammarParser;
import common.cfg.Cfg;
import common.cfg.Pcfg;
import common.lcfrs.Srcg;
import common.tag.Tag;
import common.tag.Tree;
import gui.DisplayTree;
import gui.ParsingTraceTable;

/** Entry point into toolbox for the calls by command line */
class Main {

  /** Command line arguments are passed here. Call without arguments displays
   * help about the what arguments to use. */
  public static void main(String[] args) throws ParseException, IOException {
    if (args.length < 3) {
      System.out.println(
        "Please pass at least 3 parameters: [grammar file] [input string] "
          + "[parsing algorithm] [<optional parameters>]");
      System.out.println(
        "Parsing algorithm can be: cfg-cyk, cfg-cyk-extended, cfg-earley, cfg-topdown, cfg-shiftreduce, "
          + "cfg-leftcorner, pcfg-astar, tag-cyk, tag-earley, srcg-cyk, srcg-cyk-extended, srcg-earley");
      System.out.println(
        "Optional parameters can be: --sucess : prints a trace only of items "
          + "that lead to a goal item.");
      System.out.println(
        "Optional parameters can be: --please : if a grammar doesn't fit an "
          + "algorithm, ask me to convert it. No promises.");
      System.out.println(
        "example: ..\\resources\\grammars\\anbn.cfg \"a a b b\" cfg-topdown");
      return;
    }
    String grammarfile = args[0];
    String w = args[1];
    String algorithm = args[2];
    boolean success = false;
    boolean please = false;
    for (int i = 3; i < args.length; i++) {
      if (args[i].equals("--success")) {
        success = true;
      }
      if (args[i].equals("--please")) {
        please = true;
      }
    }
    ParsingSchema schema = null;
    GrammarToDeductionRulesConverter gdrc =
      new GrammarToDeductionRulesConverter();
    if (please) {
      gdrc.setPlease(true);
    }
    Cfg cfg;
    Tag tag = null;
    Srcg srcg;
    Pcfg pcfg;
    if (grammarfile.endsWith(".cfg")) {
      cfg = GrammarParser.parseCfgFile(grammarfile);
      if (algorithm.startsWith("cfg")){
        cfg = gdrc.checkAndMayConvertToCfg(cfg, algorithm);
        if (cfg != null) {
          schema = gdrc.convertToSchema(cfg, w, algorithm);
        }
      } else  if (algorithm.startsWith("tag")){
        tag = gdrc.checkAndMayConvertToTag(cfg, algorithm);
        if (tag != null) {
          schema = gdrc.convertToSchema(tag, w, algorithm);
        }
      } else if (algorithm.startsWith("pcfg")) {
        pcfg = gdrc.checkAndMayConvertToPcfg(cfg, algorithm);
        if (pcfg != null) {
          schema = gdrc.convertToSchema(pcfg, w, algorithm);
        }
      } else if (algorithm.startsWith("srcg")) {
        srcg = gdrc.checkAndMayConvertToSrcg(cfg, algorithm);
        if (srcg != null) {
          schema = gdrc.convertToSchema(srcg, w, algorithm);
        }
      } else {
        System.out.println("I don't know that formalism, please check the spelling.");
        return;
      }
    } else 
      if (grammarfile.endsWith(".pcfg")) {
        pcfg = GrammarParser.parsePcfgFile(grammarfile);
        if (algorithm.startsWith("cfg")){
          cfg = gdrc.checkAndMayConvertToCfg(pcfg, algorithm);
          if (cfg != null) {
            schema = gdrc.convertToSchema(cfg, w, algorithm);
          }
        } else  if (algorithm.startsWith("tag")){
          tag = gdrc.checkAndMayConvertToTag(pcfg, algorithm);
          if (tag != null) {
            schema = gdrc.convertToSchema(tag, w, algorithm);
          }
        } else if (algorithm.startsWith("pcfg")) {
          pcfg = gdrc.checkAndMayConvertToPcfg(pcfg, algorithm);
          if (pcfg != null) {
            schema = gdrc.convertToSchema(pcfg, w, algorithm);
          }
        } else if (algorithm.startsWith("srcg")) {
          srcg = gdrc.checkAndMayConvertToSrcg(pcfg, algorithm);
          if (srcg != null) {
            schema = gdrc.convertToSchema(srcg, w, algorithm);
          }
        } else {
          System.out.println("I don't know that formalism, please check the spelling.");
          return;
        }
      }else if (grammarfile.endsWith(".tag")) {
        tag = GrammarParser.parseTagFile(grammarfile);
      if (algorithm.startsWith("cfg")){
        System.out.println("I can't parse with a less expressive formalism.");
        return;
      } else  if (algorithm.startsWith("tag")){
        tag = gdrc.checkAndMayConvertToTag(tag, algorithm);
        if (tag != null) {
          schema = gdrc.convertToSchema(tag, w, algorithm);
        }
      } else if (algorithm.startsWith("pcfg")) {
        System.out.println("I can't parse with a less expressive formalism.");
      } else if (algorithm.startsWith("srcg")) {
        System.out.println("I can't convert a tree language to a string language.");
        return;
      } else {
        System.out.println("I don't know that formalism, please check the spelling.");
        return;
      }
    } else if (grammarfile.endsWith(".srcg")) {
      srcg = GrammarParser.parseSrcgFile(grammarfile);
      if (algorithm.startsWith("cfg")){
        System.out.println("I can't parse with a less expressive formalism.");
      } else  if (algorithm.startsWith("tag")){
        System.out.println("I can't parse with a less expressive formalism.");
      } else if (algorithm.startsWith("pcfg")) {
        System.out.println("I can't parse with a less expressive formalism.");
      } else if (algorithm.startsWith("srcg")) {
        srcg = gdrc.checkAndMayConvertToSrcg(srcg, algorithm);
        if (srcg != null) {
          schema = gdrc.convertToSchema(srcg, w, algorithm);
        }
      } else {
        System.out.println("I don't know that formalism, please check the spelling.");
        return;
      }
    }
    Deduction deduction = new Deduction();
    System.out.println(deduction.doParse(schema, success));
    String[][] data = deduction.printTrace();
    ParsingTraceTable.displayTrace(data,
      new String[] {"Id", "Item", "Rules", "Backpointers"});
    if (schema != null) {
      if (algorithm.startsWith("tag")) {
        Tree derivedtree = ChartToTreeConverter.tagToDerivatedTree(
          deduction, schema.getGoals(), tag);
        if (derivedtree != null) {
          DisplayTree.main(new String[] {derivedtree.toString()});
        }
      }
      if (algorithm.startsWith("cfg")) {
        Tree derivedtree = ChartToTreeConverter.cfgToDerivatedTree(
          deduction, schema.getGoals(), algorithm.substring(4));
        if (derivedtree != null) {
          DisplayTree.main(new String[] {derivedtree.toString()});
        }
      }
    }
  }

}

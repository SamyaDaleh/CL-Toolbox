package cli;

import java.io.IOException;
import java.text.ParseException;

import chartparsing.Deduction;
import chartparsing.ParsingSchema;
import chartparsing.converter.ChartToTreeConverter;
import chartparsing.converter.GrammarToDeductionRulesConverter;
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
      printHelp();
      return;
    }
    String grammarFile = args[0];
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
    GrammarToGrammarConverter ggc = new GrammarToGrammarConverter(please);
    GrammarToDeductionRulesConverter gdrc =
      new GrammarToDeductionRulesConverter();
    Cfg cfg;
    Tag tag = null;
    Srcg srcg;
    Pcfg pcfg;
    String[] algorithmSplit = algorithm.split("-");
    String[] grammarFileSplit = grammarFile.split("[.]");
    switch (grammarFileSplit[grammarFileSplit.length - 1]) {
    case "cfg":
      cfg = GrammarParser.parseCfgFile(grammarFile);
      switch (algorithmSplit[0]) {
      case "cfg":
        cfg = ggc.checkAndMayConvertToCfg(cfg, algorithm);
        if (cfg != null) {
          schema = gdrc.convertToSchema(cfg, w, algorithm);
        }
        break;
      case "tag":
        tag = ggc.checkAndMayConvertToTag(cfg, algorithm);
        if (tag != null) {
          schema = gdrc.convertToSchema(tag, w, algorithm);
        }
        break;
      case "pcfg":
        pcfg = ggc.checkAndMayConvertToPcfg(cfg, algorithm);
        if (pcfg != null) {
          schema = gdrc.convertToSchema(pcfg, w, algorithm);
        }
        break;
      case "srcg":
        srcg = ggc.checkAndMayConvertToSrcg(cfg, algorithm);
        if (srcg != null) {
          schema = gdrc.convertToSchema(srcg, w, algorithm);
        }
        break;
      default:
        System.out
          .println("I don't know that formalism, please check the spelling.");
        return;
      }
      break;
    case "pcfg":
      pcfg = GrammarParser.parsePcfgFile(grammarFile);
      switch (algorithmSplit[0]) {
      case "cfg":
        cfg = ggc.checkAndMayConvertToCfg(pcfg, algorithm);
        if (cfg != null) {
          schema = gdrc.convertToSchema(cfg, w, algorithm);
        }
        break;
      case "pcfg":
        pcfg = ggc.checkAndMayConvertToPcfg(pcfg, algorithm);
        if (pcfg != null) {
          schema = gdrc.convertToSchema(pcfg, w, algorithm);
        }
        break;
      case "tag":
        tag = ggc.checkAndMayConvertToTag(pcfg, algorithm);
        if (tag != null) {
          schema = gdrc.convertToSchema(tag, w, algorithm);
        }
        break;
      case "srcg":
        srcg = ggc.checkAndMayConvertToSrcg(pcfg, algorithm);
        if (srcg != null) {
          schema = gdrc.convertToSchema(srcg, w, algorithm);
        }
        break;
      default:
        System.out
          .println("I don't know that formalism, please check the spelling.");
        return;
      }
      break;
    case "tag":
      tag = GrammarParser.parseTagFile(grammarFile);
      switch (algorithmSplit[0]) {
      case "cfg":
        System.out.println("I can't parse with a less expressive formalism.");
        return;
      case "pcfg":
        System.out.println("I can't parse with a less expressive formalism.");
        return;
      case "tag":
        tag = ggc.checkAndMayConvertToTag(tag, algorithm);
        if (tag != null) {
          schema = gdrc.convertToSchema(tag, w, algorithm);
        }
        break;
      case "srcg":
        System.out
          .println("I can't convert a tree language to a string language.");
        return;
      default:
        System.out
          .println("I don't know that formalism, please check the spelling.");
        return;
      }
      break;
    case "srcg":
      srcg = GrammarParser.parseSrcgFile(grammarFile);
      switch (algorithmSplit[0]) {
      case "cfg":
        System.out.println("I can't parse with a less expressive formalism.");
        return;
      case "pcfg":
        System.out.println("I can't parse with a less expressive formalism.");
        return;
      case "tag":
        System.out.println("I can't parse with a less expressive formalism.");
        return;
      case "srcg":
        srcg = ggc.checkAndMayConvertToSrcg(srcg, algorithm);
        if (srcg != null) {
          schema = gdrc.convertToSchema(srcg, w, algorithm);
        }
        break;
      default:
        System.out
          .println("I don't know that formalism, please check the spelling.");
        return;
      }
      break;
    default:
    }
    Deduction deduction = new Deduction();
    if (algorithm.equals("pcfg-astar")) {
      deduction.setReplace('l');
    }
    System.out.println(deduction.doParse(schema, success));
    String[][] data = deduction.printTrace();
    if (tag != null) {
      new ParsingTraceTable(data,
        new String[] {"Id", "Item", "Rules", "Backpointers"}, tag);
    } else {
      ParsingTraceTable.displayTrace(data,
        new String[] {"Id", "Item", "Rules", "Backpointers"});
    }
    if (schema != null) {
      switch (algorithmSplit[0]) {
      case "cfg":
        Tree derivedTree = ChartToTreeConverter.cfgToDerivatedTree(deduction,
          schema.getGoals(), algorithm.substring(4));
        if (derivedTree != null) {
          new DisplayTree(new String[] {derivedTree.toString()});
        }
        break;
      case "tag":
        derivedTree = ChartToTreeConverter.tagToDerivatedTree(deduction,
          schema.getGoals(), tag);
        if (derivedTree != null) {
          new DisplayTree(new String[] {derivedTree.toString()});
        }
        break;
      case "srcg":
        derivedTree = ChartToTreeConverter.srcgToDerivatedTree(deduction,
          schema.getGoals(), algorithm.substring(5));
        if (derivedTree != null) {
          new DisplayTree(new String[] {derivedTree.toString()});
        }
        break;
      default:
        break;
      }
    }
  }

  private static void printHelp() {
    System.out.println(
      "Please pass at least 3 parameters: [grammar file] [input string] "
        + "[parsing algorithm] [<optional parameters>]");
    System.out.println("Parsing algorithm can be one of: \n   cfg-cyk"
      + "\n   cfg-cyk-extended" + "\n   cfg-cyk-general" + "\n   cfg-earley"
      + "\n   cfg-leftcorner" + "\n   cfg-leftcorner-chart" + "\n   cfg-topdown"
      + "\n   cfg-shiftreduce" + "\n   cfg-unger" + "\n   pcfg-astar"
      + "\n   tag-cyk" + "\n   tag-earley" + "\n   tag-earley-prefixvalid"
      + "\n   srcg-cyk" + "\n   srcg-cyk-extended" + "\n   srcg-earley");
    System.out.println(
      "Optional parameters can be: \n   --success : prints a trace only of items "
        + "that lead to a goal item."
        + "\n   --please : if a grammar doesn't fit an "
        + "algorithm, ask me to convert it. No promises.");
    System.out.println(
      "example: java -jar CL-Toolbox.jar ..\\resources\\grammars\\anbn.cfg \"a a b b\" cfg-topdown --success");
  }

}

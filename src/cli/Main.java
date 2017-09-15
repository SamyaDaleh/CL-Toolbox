package cli;

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
import gui.JfxWindowHolder;
import gui.ParsingTraceTable;

/** Entry point into toolbox for the calls by command line */
class Main { // NO_UCD (test only)

  /** Command line arguments are passed here. Call without arguments displays
   * help about the what arguments to use.
   * @throws Exception */
  public static void main(String[] args) throws Exception {
    if (args.length < 3) {
      printHelp();
      return;
    }
    String grammarFile = args[0];
    String w = args[1];
    String algorithm = args[2];
    if (algorithm.equals("srcg-cyk")) {
      System.out.println("Using srcg-cyk-extended instead.");
      algorithm = "srcg-cyk-extended";
    } else if (algorithm.equals("tag-cyk")) {
      System.out.println("Using tag-cyk-extended instead.");
      algorithm = "tag-cyk-extended";
    }
    boolean success = false;
    boolean please = false;
    boolean javafx = false;
    for (int i = 3; i < args.length; i++) {
      if (args[i].equals("--success")) {
        success = true;
      } else if (args[i].equals("--please")) {
        please = true;
      }
      if (args[i].equals("--javafx")) {
        javafx = true;
      }
    }
    JfxWindowHolder jwh = new JfxWindowHolder();
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
    } else if (algorithm.equals("pcfg-cyk")) {
      deduction.setReplace('l');
    }
    System.out.println(deduction.doParse(schema, success));
    String[][] data = deduction.printTrace();
    if (tag != null) {
      if (javafx) {
        jwh.setRowData(data);
        jwh.setTag(tag);
        jwh.showParsingTraceTableFx();
      } else {
        new ParsingTraceTable(data,
          new String[] {"Id", "Item", "Rules", "Backpointers"}, tag);
      }
    } else {
      if (javafx) {
        jwh.setRowData(data);
        jwh.showParsingTraceTableFx();
      } else {
        new ParsingTraceTable(data,
          new String[] {"Id", "Item", "Rules", "Backpointers"}, null);
      }
    }
    if (schema != null) {
      drawDerivationTree(algorithm, schema, tag, deduction, javafx, jwh);
    }
  }

  private static void drawDerivationTree(String algorithm, ParsingSchema schema,
    Tag tag, Deduction deduction, boolean javafx, JfxWindowHolder jwc)
    throws Exception {
    String[] algorithmSplit = algorithm.split("-");
    Tree derivedTree = null;
    switch (algorithmSplit[0]) {
    case "cfg":
      derivedTree = ChartToTreeConverter.cfgToDerivatedTree(deduction,
        schema.getGoals(), algorithm.substring(4));
      break;
    case "tag":
      derivedTree = ChartToTreeConverter.tagToDerivatedTree(deduction,
        schema.getGoals(), tag);
      break;
    case "srcg":
      derivedTree = ChartToTreeConverter.srcgToDerivatedTree(deduction,
        schema.getGoals(), algorithm.substring(5));
      break;
    default:
      System.out.println("Unknown formalism " + algorithmSplit[0]
        + ", can not retrieve derivated tree.");
    }
    if (derivedTree != null) {
      if (javafx) {
        jwc.setArgs(new String[] {derivedTree.toString()});
        jwc.showDisplayTreeFx();
      } else {
        new DisplayTree(new String[] {derivedTree.toString()});
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
      + "\n   tag-cyk-extended" + "\n   tag-earley"
      + "\n   tag-earley-prefixvalid" + "\n   srcg-cyk-extended"
      + "\n   srcg-earley");
    System.out.println(
      "Optional parameters can be: \n   --success : prints a trace only of items "
        + "that lead to a goal item."
        + "\n   --please : if a grammar doesn't fit an "
        + "algorithm, ask me to convert it. No promises."
        + "\n   --javafx : display graphics with javafx instead of awt.");
    System.out.println(
      "example: java -jar CL-Toolbox.jar ..\\resources\\grammars\\anbn.cfg \"a a b b\" cfg-topdown --success");
  }
}

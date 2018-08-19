package com.github.samyadaleh.cltoolbox.cli;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

import com.github.samyadaleh.cltoolbox.chartparsing.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.github.samyadaleh.cltoolbox.chartparsing.converter.GrammarToDeductionRulesConverter;
import com.github.samyadaleh.cltoolbox.common.GrammarParser;
import com.github.samyadaleh.cltoolbox.common.cfg.Cfg;
import com.github.samyadaleh.cltoolbox.common.cfg.Pcfg;
import com.github.samyadaleh.cltoolbox.common.lcfrs.Srcg;
import com.github.samyadaleh.cltoolbox.common.tag.Tag;
import com.github.samyadaleh.cltoolbox.common.tag.Tree;
import com.github.samyadaleh.cltoolbox.gui.DisplayTree;
import com.github.samyadaleh.cltoolbox.gui.JfxWindowHolder;
import com.github.samyadaleh.cltoolbox.gui.ParsingTraceTable;

/**
 * Entry point into toolbox for the calls by command line
 */
class Main { // NO_UCD (test only)

  private static boolean success = false;
  private static boolean please = false;
  private static boolean javafx = false;
  private static ParsingSchema schema = null;
  private static Cfg cfg;
  private static Tag tag = null;
  private static Srcg srcg;
  private static Pcfg pcfg;
  private static final Logger log = LogManager.getLogger();

  /**
   * Command line arguments are passed here. Call without arguments displays
   * help about the what arguments to use.
   */
  public static void main(String[] args) {
    if (args.length < 3) {
      printHelp();
      return;
    }
    String grammarFile = args[0];
    String w = args[1];
    String algorithm = args[2];
    if (algorithm.equals("srcg-cyk")) {
      log.info("Using srcg-cyk-extended instead.");
      algorithm = "srcg-cyk-extended";
    } else if (algorithm.equals("tag-cyk")) {
      log.info("Using tag-cyk-extended instead.");
      algorithm = "tag-cyk-extended";
    }
    handleOptionalParameters(args);
    JfxWindowHolder jwh = new JfxWindowHolder();
    try {
      parseGrammarFileAndConvertToParsingSchema(grammarFile, w, algorithm);
    } catch (IOException | ParseException e) {
      log.error(e.getMessage(), e);
      return;
    } catch (IllegalArgumentException e) {
      log.warn(e.getMessage(), e);
    }
    logParsingSchema();
    Deduction deduction = new Deduction();
    if (algorithm.equals("pcfg-astar")) {
      deduction.setReplace('l');
    } else if (algorithm.equals("pcfg-cyk")) {
      deduction.setReplace('l');
    }
    try {
      log.info(deduction.doParse(schema, success));
    } catch (ParseException e) {
      log.error(e.getMessage(), e);
      return;
    }
    String[][] data = deduction.printTrace();
    if (tag != null) {
      if (javafx) {
        jwh.setRowData(data);
        jwh.setTag(tag);
        try {
          jwh.showParsingTraceTableFx();
        } catch (Exception e) {
          log.error(e.getMessage(), e);
          return;
        }
      } else {
        new ParsingTraceTable(data,
            new String[] {"Id", "Item", "Rules", "Backpointers"}, tag);
      }
    } else {
      if (javafx) {
        jwh.setRowData(data);
        try {
          jwh.showParsingTraceTableFx();
        } catch (Exception e) {
          log.error(e.getMessage(), e);
        }
      } else {
        new ParsingTraceTable(data,
            new String[] {"Id", "Item", "Rules", "Backpointers"}, null);
      }
    }
    if (schema != null) {
      try {
        drawDerivedTree(deduction, javafx, jwh);
      } catch (Exception e) {
        log.error(e.getMessage(), e);
      }
    }
  }

  private static void logParsingSchema() {
    if (log.isDebugEnabled() && schema != null) {
      log.debug("Parsing Schema with " + schema.getAxioms().size() + " axioms, "
          + schema.getRules().size() + " deduction rules and " + schema
          .getGoals().size() + " goal generated.");
      log.debug("Axioms:");
      for (StaticDeductionRule axiom : schema.getAxioms()) {
        log.debug(axiom.getName() + axiom.toString());
      }
      log.debug("Derivation Rules:");
      for (DynamicDeductionRuleInterface rule : schema.getRules()) {
        log.debug(rule.getName() + "\n" + rule.toString());
      }
      log.debug("Goal Items:");
      for (ChartItemInterface goal : schema.getGoals()) {
        log.debug(goal.toString());
      }
    }
  }

  private static void handleOptionalParameters(String[] args) {
    success = false;
    please = false;
    javafx = false;
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
  }

  private static void parseGrammarFileAndConvertToParsingSchema(
      String grammarFile, String w, String algorithm)
      throws IOException, ParseException {
    String[] grammarFileSplit = grammarFile.split("[.]");
    switch (grammarFileSplit[grammarFileSplit.length - 1]) {
    case "cfg":
      parseCfgFileAndConvertToSchema(grammarFile, w, algorithm);
      break;
    case "pcfg":
      parsePcfgFileAndConvertToSchema(grammarFile, w, algorithm);
      break;
    case "tag":
      parseTagFileAndConvertToSchema(grammarFile, w, algorithm);
      break;
    case "srcg":
      parseSrcgFileAndConvertToSchema(grammarFile, w, algorithm);
      break;
    default:
      log.warn("Unknown file format of file " + grammarFile);
    }
  }

  private static void parseSrcgFileAndConvertToSchema(String grammarFile,
      String w, String algorithm) throws IOException, ParseException {
    srcg = GrammarParser.parseSrcgFile(grammarFile);
    String[] algorithmSplit = algorithm.split("-");
    switch (algorithmSplit[0]) {
    case "cfg":
      throw new IllegalArgumentException(
          "I can't parse with a less expressive formalism.");
    case "pcfg":
      throw new IllegalArgumentException(
          "I can't parse with a less expressive formalism.");
    case "tag":
      throw new IllegalArgumentException(
          "I can't parse with a less expressive formalism.");
    case "srcg":
      srcg = GrammarToGrammarConverter
          .checkAndMayConvertToSrcg(srcg, algorithm, please);
      if (srcg != null) {
        schema = GrammarToDeductionRulesConverter
            .convertToSchema(srcg, w, algorithm);
      }
      break;
    default:
      throw new IllegalArgumentException("I don't know formalism \"" + algorithm
          + "\", please check the spelling.");
    }
  }

  private static void parseTagFileAndConvertToSchema(String grammarFile,
      String w, String algorithm) throws IOException, ParseException {
    tag = GrammarParser.parseTagFile(grammarFile);
    String[] algorithmSplit = algorithm.split("-");
    switch (algorithmSplit[0]) {
    case "cfg":
      throw new IllegalArgumentException(
          "I can't parse with a less expressive formalism.");
    case "pcfg":
      throw new IllegalArgumentException(
          "I can't parse with a less expressive formalism.");
    case "tag":
      tag = GrammarToGrammarConverter
          .checkAndMayConvertToTag(tag, algorithm, please);
      if (tag != null) {
        schema =
            GrammarToDeductionRulesConverter.convertToSchema(tag, w, algorithm);
      }
      break;
    case "srcg":
      throw new IllegalArgumentException(
          "I can't convert a tree language to a string language.");
    default:
      throw new IllegalArgumentException("I don't know formalism \"" + algorithm
          + "\", please check the spelling.");
    }
  }

  private static void parsePcfgFileAndConvertToSchema(String grammarFile,
      String w, String algorithm) throws IOException, ParseException {
    pcfg = GrammarParser.parsePcfgFile(grammarFile);
    String[] algorithmSplit = algorithm.split("-");
    switch (algorithmSplit[0]) {
    case "cfg":
      cfg = GrammarToGrammarConverter
          .checkAndMayConvertToCfg(pcfg, algorithm, please);
      if (cfg != null) {
        schema =
            GrammarToDeductionRulesConverter.convertToSchema(cfg, w, algorithm);
      }
      break;
    case "pcfg":
      pcfg = GrammarToGrammarConverter
          .checkAndMayConvertToPcfg(pcfg, algorithm, please);
      if (pcfg != null) {
        schema = GrammarToDeductionRulesConverter
            .convertToSchema(pcfg, w, algorithm);
      }
      break;
    case "tag":
      tag = GrammarToGrammarConverter
          .checkAndMayConvertToTag(pcfg, algorithm, please);
      if (tag != null) {
        schema =
            GrammarToDeductionRulesConverter.convertToSchema(tag, w, algorithm);
      }
      break;
    case "srcg":
      srcg = GrammarToGrammarConverter
          .checkAndMayConvertToSrcg(pcfg, algorithm, please);
      if (srcg != null) {
        schema = GrammarToDeductionRulesConverter
            .convertToSchema(srcg, w, algorithm);
      }
      break;
    default:
      throw new IllegalArgumentException("I don't know formalism \"" + algorithm
          + "\", please check the spelling.");
    }
  }

  private static void parseCfgFileAndConvertToSchema(String grammarFile,
      String w, String algorithm) throws IOException, ParseException {
    cfg = GrammarParser.parseCfgFile(grammarFile);
    String[] algorithmSplit = algorithm.split("-");
    switch (algorithmSplit[0]) {
    case "cfg":
      cfg = GrammarToGrammarConverter
          .checkAndMayConvertToCfg(cfg, algorithm, please);
      if (cfg != null) {
        schema =
            GrammarToDeductionRulesConverter.convertToSchema(cfg, w, algorithm);
      }
      break;
    case "tag":
      tag = GrammarToGrammarConverter
          .checkAndMayConvertToTag(cfg, algorithm, please);
      if (tag != null) {
        schema =
            GrammarToDeductionRulesConverter.convertToSchema(tag, w, algorithm);
      }
      break;
    case "pcfg":
      pcfg = GrammarToGrammarConverter
          .checkAndMayConvertToPcfg(cfg, algorithm, please);
      if (pcfg != null) {
        schema = GrammarToDeductionRulesConverter
            .convertToSchema(pcfg, w, algorithm);
      }
      break;
    case "srcg":
      srcg = GrammarToGrammarConverter
          .checkAndMayConvertToSrcg(cfg, algorithm, please);
      if (srcg != null) {
        schema = GrammarToDeductionRulesConverter
            .convertToSchema(srcg, w, algorithm);
      }
      break;
    default:
      throw new IllegalArgumentException("I don't know formalism \"" + algorithm
          + "\", please check the spelling.");
    }
  }

  private static void drawDerivedTree(Deduction deduction, boolean javafx,
      JfxWindowHolder jwc) throws Exception {
    List<Tree> derivedTrees = deduction.getDerivedTrees();
    for (Tree derivedTree : derivedTrees) {
      if (javafx) {
        jwc.setArgs(new String[] {derivedTree.toString()});
        jwc.showDisplayTreeFx();
      } else {
        new DisplayTree(new String[] {derivedTree.toString()});
      }
    }
  }

  private static void printHelp() {
    log.info("Please pass at least 3 parameters: [grammar file] [input string] "
        + "[parsing algorithm] [<optional parameters>]");
    log.info("Parsing algorithm can be one of: \n   cfg-cyk"
        + "\n   cfg-cyk-extended" + "\n   cfg-cyk-general" + "\n   cfg-earley"
        + "\n   cfg-earley-passive" + "\n   cfg-leftcorner"
        + "\n   cfg-leftcorner-chart" + "\n   cfg-topdown"
        + "\n   cfg-shiftreduce" + "\n   cfg-unger" + "\n   pcfg-astar"
        + "\n   tag-cyk-extended" + "\n   tag-cyk-general" + "\n   tag-earley"
        + "\n   tag-earley-prefixvalid" + "\n   srcg-cyk-extended"
        + "\n   srcg-cyk-general" + "\n   srcg-earley");
    log.info(
        "Optional parameters can be: \n   --success : prints a trace only of items "
            + "that lead to a goal item."
            + "\n   --please : if a grammar doesn't fit an "
            + "algorithm, ask me to convert it. No promises."
            + "\n   --javafx : display graphics with javafx instead of awt.");
    log.info(
        "example: java -jar CL-Toolbox.jar ..\\resources\\grammars\\anbn.cfg "
            + "\"a a b b\" cfg-topdown --success");
  }
}

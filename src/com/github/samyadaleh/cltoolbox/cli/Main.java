package com.github.samyadaleh.cltoolbox.cli;

import com.github.samyadaleh.cltoolbox.chartparsing.Deduction;
import com.github.samyadaleh.cltoolbox.chartparsing.ParsingSchema;
import com.github.samyadaleh.cltoolbox.chartparsing.StaticDeductionRule;
import com.github.samyadaleh.cltoolbox.chartparsing.converter.GrammarToDeductionRulesConverter;
import com.github.samyadaleh.cltoolbox.chartparsing.dynamicdeductionrule.DynamicDeductionRuleInterface;
import com.github.samyadaleh.cltoolbox.chartparsing.item.ChartItemInterface;
import com.github.samyadaleh.cltoolbox.common.ccg.Ccg;
import com.github.samyadaleh.cltoolbox.common.cfg.Cfg;
import com.github.samyadaleh.cltoolbox.common.cfg.Pcfg;
import com.github.samyadaleh.cltoolbox.common.lcfrs.Srcg;
import com.github.samyadaleh.cltoolbox.common.parser.*;
import com.github.samyadaleh.cltoolbox.common.tag.Tag;
import com.github.samyadaleh.cltoolbox.common.tag.Tree;
import com.github.samyadaleh.cltoolbox.gui.DisplayTree;
import com.github.samyadaleh.cltoolbox.gui.ParsingTraceTable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.text.ParseException;
import java.util.List;

/**
 * Entry point into toolbox for the calls by command line
 */
public class Main { // NO_UCD (test only)

  private static boolean success = false;
  private static boolean please = false;
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
    logCall(args);
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
    ParsingSchema schema;
    try {
      Reader reader = new FileReader(grammarFile);
      String[] grammarFileSplit = grammarFile.split("[.]");
      String formalism = grammarFileSplit[grammarFileSplit.length - 1];
      schema = parseGrammarReaderAndConvertToParsingSchema(reader, formalism, w,
          algorithm);

      logParsingSchema(schema);
      Deduction deduction = new Deduction();
      if (algorithm.equals("pcfg-astar")) {
        deduction.setReplace('l');
      } else if (algorithm.equals("pcfg-cyk")) {
        deduction.setReplace('l');
      }
      log.info(deduction.doParse(schema, success));
      if (displayParsingTraceTable(deduction))
        return;
      if (schema != null) {
        drawDerivedTree(deduction);
      }
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
  }

  private static boolean displayParsingTraceTable( Deduction deduction) {
    String[][] data = deduction.printTrace();
    if (tag != null) {
      new ParsingTraceTable(data,
          new String[] {"Id", "Item", "Rules", "Backpointers"}, tag);
    } else {
      new ParsingTraceTable(data,
          new String[] {"Id", "Item", "Rules", "Backpointers"}, null);
    }
    return false;
  }

  private static void logCall(String[] args) {
    StringBuilder call = new StringBuilder();
    for (String arg : args) {
      if (call.length() > 0) {
        call.append(" ");
      }
      if (arg.contains(" ")) {
        call.append('"').append(arg).append('"');
      } else {
        call.append(arg);
      }
    }
    log.info("Call: " + call.toString());
  }

  private static void logParsingSchema(ParsingSchema schema) {
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
    for (int i = 3; i < args.length; i++) {
      if (args[i].equals("--success")) {
        success = true;
      } else if (args[i].equals("--please")) {
        please = true;
      }
    }
  }

  public static ParsingSchema parseGrammarReaderAndConvertToParsingSchema(
      Reader reader, String formalism, String w, String algorithm)
      throws IOException, ParseException {
    BufferedReader grammarReader = new BufferedReader(reader);
    switch (formalism) {
    case "cfg":
      return parseCfgFileAndConvertToSchema(grammarReader, w, algorithm);
    case "pcfg":
      return parsePcfgFileAndConvertToSchema(grammarReader, w, algorithm);
    case "tag":
      return parseTagFileAndConvertToSchema(grammarReader, w, algorithm);
    case "srcg":
      return parseSrcgFileAndConvertToSchema(grammarReader, w, algorithm);
    case "ccg":
      return parseCcgFileAndConvertToSchema(grammarReader, w, algorithm);
    default:
      throw new IllegalArgumentException("Unknown formalism: " + formalism);
    }
  }

  private static ParsingSchema parseCcgFileAndConvertToSchema(
      BufferedReader grammarReader, String w, String algorithm)
      throws IOException, ParseException {
    Ccg ccg = CcgParser.parseCcgReader(grammarReader);
    return GrammarToDeductionRulesConverter.convertToSchema(ccg, w, algorithm);
  }

  private static ParsingSchema parseSrcgFileAndConvertToSchema(
      BufferedReader grammarFile, String w, String algorithm)
      throws ParseException {
    srcg = SrcgParser.parseSrcgReader(grammarFile);
    if (log.isDebugEnabled()) {
      log.debug("Grammar read from file: " + srcg.toString());
    }
    String[] algorithmSplit = algorithm.split("-");
    switch (algorithmSplit[0]) {
    case "cfg":
    case "pcfg":
    case "tag":
      throw new IllegalArgumentException(
          "I can't parse with a less expressive formalism.");
    case "srcg":
      srcg = GrammarToGrammarConverter
          .checkAndMayConvertToSrcg(srcg, algorithm, please);
      if (log.isDebugEnabled()) {
        log.debug("Grammar after conversion: " + srcg.toString());
      }
      return GrammarToDeductionRulesConverter
          .convertToSchema(srcg, w, algorithm);
    default:
      throw new IllegalArgumentException("I don't know formalism \"" + algorithm
          + "\", please check the spelling.");
    }
  }

  private static ParsingSchema parseTagFileAndConvertToSchema(
      BufferedReader grammarFile, String w, String algorithm)
      throws ParseException {
    tag = TagParser.parseTagReader(grammarFile);
    if (log.isDebugEnabled()) {
      log.debug("Grammar read from file: " + tag.toString());
    }
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
      if (log.isDebugEnabled()) {
        log.debug("Grammar after conversion: " + tag.toString());
      }
      return GrammarToDeductionRulesConverter
          .convertToSchema(tag, w, algorithm);
    case "srcg":
      srcg = GrammarToGrammarConverter
          .checkAndMayConvertToSrcg(tag, algorithm, please);
      if (log.isDebugEnabled()) {
        log.debug("Grammar after conversion: " + srcg.toString());
      }
      return GrammarToDeductionRulesConverter
          .convertToSchema(tag, w, algorithm);
    default:
      throw new IllegalArgumentException("I don't know formalism \"" + algorithm
          + "\", please check the spelling.");
    }
  }

  private static ParsingSchema parsePcfgFileAndConvertToSchema(
      BufferedReader grammarFile, String w, String algorithm)
      throws ParseException {
    pcfg = PcfgParser.parsePcfgReader(grammarFile);
    if (log.isDebugEnabled()) {
      log.debug("Grammar read from file: " + pcfg.toString());
    }
    String[] algorithmSplit = algorithm.split("-");
    switch (algorithmSplit[0]) {
    case "cfg":
      cfg = GrammarToGrammarConverter
          .checkAndMayConvertToCfg(pcfg, algorithm, please);
      if (log.isDebugEnabled()) {
        log.debug("Grammar after conversion: " + cfg.toString());
      }
      return GrammarToDeductionRulesConverter
          .convertToSchema(cfg, w, algorithm);
    case "pcfg":
      pcfg = GrammarToGrammarConverter
          .checkAndMayConvertToPcfg(pcfg, algorithm, please);
      if (log.isDebugEnabled()) {
        log.debug("Grammar after conversion: " + pcfg.toString());
      }
      return GrammarToDeductionRulesConverter
          .convertToSchema(pcfg, w, algorithm);
    case "tag":
      tag = GrammarToGrammarConverter
          .checkAndMayConvertToTag(pcfg, algorithm, please);
      if (log.isDebugEnabled()) {
        log.debug("Grammar after conversion: " + tag.toString());
      }
      return GrammarToDeductionRulesConverter
          .convertToSchema(tag, w, algorithm);
    case "srcg":
      srcg = GrammarToGrammarConverter
          .checkAndMayConvertToSrcg(pcfg, algorithm, please);
      if (log.isDebugEnabled()) {
        log.debug("Grammar after conversion: " + srcg.toString());
      }
      return GrammarToDeductionRulesConverter
          .convertToSchema(srcg, w, algorithm);
    default:
      throw new IllegalArgumentException("I don't know formalism \"" + algorithm
          + "\", please check the spelling.");
    }
  }

  private static ParsingSchema parseCfgFileAndConvertToSchema(
      BufferedReader grammarFile, String w, String algorithm)
      throws ParseException {
    cfg = CfgParser.parseCfgReader(grammarFile);
    if (log.isDebugEnabled()) {
      log.debug("Grammar read from file: " + cfg.toString());
    }
    String[] algorithmSplit = algorithm.split("-");
    switch (algorithmSplit[0]) {
    case "cfg":
      cfg = GrammarToGrammarConverter
          .checkAndMayConvertToCfg(cfg, algorithm, please);
      if (log.isDebugEnabled()) {
        log.debug("Grammar after conversion: " + cfg.toString());
      }
      return GrammarToDeductionRulesConverter
          .convertToSchema(cfg, w, algorithm);
    case "tag":
      tag = GrammarToGrammarConverter
          .checkAndMayConvertToTag(cfg, algorithm, please);
      if (log.isDebugEnabled()) {
        log.debug("Grammar after conversion: " + tag.toString());
      }
      return GrammarToDeductionRulesConverter
          .convertToSchema(tag, w, algorithm);
    case "pcfg":
      pcfg = GrammarToGrammarConverter
          .checkAndMayConvertToPcfg(cfg, algorithm, please);
      if (log.isDebugEnabled()) {
        log.debug("Grammar after conversion: " + pcfg.toString());
      }
      return GrammarToDeductionRulesConverter
          .convertToSchema(pcfg, w, algorithm);
    case "srcg":
      srcg = GrammarToGrammarConverter
          .checkAndMayConvertToSrcg(cfg, algorithm, please);
      if (log.isDebugEnabled()) {
        log.debug("Grammar after conversion: " + srcg.toString());
      }
      return GrammarToDeductionRulesConverter
          .convertToSchema(srcg, w, algorithm);
    default:
      throw new IllegalArgumentException("I don't know formalism \"" + algorithm
          + "\", please check the spelling.");
    }
  }

  private static void drawDerivedTree(Deduction deduction) throws Exception {
    List<Tree> derivedTrees = deduction.getDerivedTrees();
    for (Tree derivedTree : derivedTrees) {
      new DisplayTree(new String[] {derivedTree.toString()});
    }
  }

  private static void printHelp() {
    log.info("Please pass at least 3 parameters: [grammar file] [input string] "
        + "[parsing algorithm] [<optional parameters>]");
    log.info("Parsing algorithm can be one of: " + "\n   ccg-deduction"
        + "\n   cfg-cyk" + "\n   cfg-cyk-extended" + "\n   cfg-cyk-general"
        + "\n   cfg-earley" + "\n   cfg-earley-bottomup"
        + "\n   cfg-earley-passive" + "\n   cfg-leftcorner"
        + "\n   cfg-leftcorner-chart" + "\n   cfg-topdown"
        + "\n   cfg-shiftreduce" + "\n   cfg-lr-k   (with k >=0)"
        + "\n   cfg-unger" + "\n   pcfg-astar" + "\n   tag-cyk-extended"
        + "\n   tag-cyk-general" + "\n   tag-earley"
        + "\n   tag-earley-prefixvalid" + "\n   srcg-cyk-extended"
        + "\n   srcg-cyk-general" + "\n   srcg-earley");
    log.info(
        "Optional parameters can be: \n   --success : prints a trace only of items "
            + "that lead to a goal item."
            + "\n   --please : if a grammar doesn't fit an "
            + "algorithm, ask me to convert it. No promises.");
    log.info(
        "example: java -jar CL-Toolbox.jar ..\\resources\\grammars\\anbn.cfg "
            + "\"a a b b\" cfg-topdown --success");
  }
}

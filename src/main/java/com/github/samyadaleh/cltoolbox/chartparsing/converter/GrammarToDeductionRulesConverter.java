package com.github.samyadaleh.cltoolbox.chartparsing.converter;

import com.github.samyadaleh.cltoolbox.chartparsing.ParsingSchema;
import com.github.samyadaleh.cltoolbox.chartparsing.converter.ccg.CcgToDeductionRulesConverter;
import com.github.samyadaleh.cltoolbox.chartparsing.converter.cfg.*;
import com.github.samyadaleh.cltoolbox.chartparsing.converter.lag.LagToDeductionRulesConverter;
import com.github.samyadaleh.cltoolbox.chartparsing.converter.lcfrs.LcfrsToCykRulesConverter;
import com.github.samyadaleh.cltoolbox.chartparsing.converter.lcfrs.LcfrsToEarleyRulesConverter;
import com.github.samyadaleh.cltoolbox.chartparsing.converter.pcfg.PcfgToAstarRulesConverter;
import com.github.samyadaleh.cltoolbox.chartparsing.converter.pcfg.PcfgToCykRulesConverter;
import com.github.samyadaleh.cltoolbox.chartparsing.converter.tag.TagToCykRulesConverter;
import com.github.samyadaleh.cltoolbox.chartparsing.converter.tag.TagToEarleyPrefixValidRulesConverter;
import com.github.samyadaleh.cltoolbox.chartparsing.converter.tag.TagToEarleyRulesConverter;
import com.github.samyadaleh.cltoolbox.common.ccg.Ccg;
import com.github.samyadaleh.cltoolbox.common.cfg.Cfg;
import com.github.samyadaleh.cltoolbox.common.cfg.Pcfg;
import com.github.samyadaleh.cltoolbox.common.lag.Lag;
import com.github.samyadaleh.cltoolbox.common.lcfrs.Srcg;
import com.github.samyadaleh.cltoolbox.common.tag.Tag;

import java.text.ParseException;

public class GrammarToDeductionRulesConverter {

  /**
   * Call with appropriate grammar. Better call the convert-to function first.
   */
  public static ParsingSchema convertToSchema(Cfg cfg, String w,
      String algorithm) throws ParseException {
    validateInputSymbols(cfg, w);
    switch (algorithm) {
    case "cfg-topdown":
      return CfgToTopDownRulesConverter.cfgToTopDownRules(cfg, w);
    case "cfg-shiftreduce":
      return CfgToShiftReduceRulesConverter.cfgToShiftReduceRules(cfg, w);
    case "cfg-earley":
      return CfgToEarleyRulesConverter.cfgToEarleyRules(cfg, w);
    case "cfg-earley-bottomup":
      return CfgToEarleyRulesConverter.cfgToEarleyBottomupRules(cfg, w);
    case "cfg-earley-passive":
      return CfgToEarleyPassiveRulesConverter.cfgToEarleyPassiveRules(cfg, w);
    case "cfg-leftcorner":
      return CfgToLeftCornerRulesConverter.cfgToLeftCornerRules(cfg, w);
    case "cfg-leftcorner-chart":
      return CfgToLeftCornerChartRulesConverter
          .cfgToLeftCornerChartRules(cfg, w);
    case "cfg-cyk":
      return CfgToCykRulesConverter.cfgToCykRules(cfg, w);
    case "cfg-cyk-extended":
      return CfgToCykRulesConverter.cfgToCykExtendedRules(cfg, w);
    case "cfg-cyk-general":
      return CfgToCykRulesConverter.cfgToCykGeneralRules(cfg, w);
    case "cfg-unger":
      return CfgToUngerRulesConverter.cfgToUngerRules(cfg, w);
    default:
      if (algorithm.matches("cfg-lr-\\d+")) {
        String[] algorithmSplit = algorithm.split("-");
        return CfgToLrKRulesConverter
            .cfgToLrKRules(cfg, w, Integer.parseInt(algorithmSplit[2]));
      }
      throw new ParseException(
          "I did not understand. Please check the spelling of your parsing algorithm.",
          1);
    }
  }

  private static void validateInputSymbols(Cfg cfg, String w)
      throws ParseException {
    String[] wSplit = w.split(" ");
    if (wSplit.length > 1 || !"".equals(wSplit[0])) {
      for (String token : wSplit) {
        if (!cfg.terminalsContain(token)) {
          throw new ParseException("Token " + token
              + " from input is not a terminal in the grammar.", 0);
        }
      }
    }
  }

  /**
   * Call with appropriate grammar. Better call the convert-to function first.
   */
  public static ParsingSchema convertToSchema(Tag tag, String w,
      String algorithm) throws ParseException {
    validateInputSymbols(tag, w);
    switch (algorithm) {
    case "tag-cyk-extended":
      return TagToCykRulesConverter.tagToCykExtendedRules(tag, w);
    case "tag-cyk-general":
      return TagToCykRulesConverter.tagToCykGeneralRules(tag, w);
    case "tag-earley":
      return TagToEarleyRulesConverter.tagToEarleyRules(tag, w);
    case "tag-earley-prefixvalid":
      return TagToEarleyPrefixValidRulesConverter
          .tagToEarleyPrefixValidRules(tag, w);
    default:
      throw new ParseException(
          "I did not understand. Please check the spelling of your parsing algorithm.",
          1);
    }
  }

  private static void validateInputSymbols(Tag tag, String w)
      throws ParseException {
    String[] wSplit = w.split(" ");
    if (wSplit.length > 1 || !"".equals(wSplit[0])) {
      for (String token : wSplit) {
        if (!tag.terminalsContain(token)) {
          throw new ParseException("Token " + token
              + " from input is not a terminal in the grammar.", 0);
        }
      }
    }
  }

  /**
   * Call with appropriate grammar. Better call the convert-to function first.
   */
  public static ParsingSchema convertToSchema(Srcg srcg, String w,
      String algorithm) throws ParseException {
    validateInputSymbols(srcg, w);
    switch (algorithm) {
    case "srcg-earley":
      return LcfrsToEarleyRulesConverter.srcgToEarleyRules(srcg, w);
    case "srcg-cyk-extended":
      return LcfrsToCykRulesConverter.srcgToCykExtendedRules(srcg, w);
    case "srcg-cyk-general":
      return LcfrsToCykRulesConverter.srcgToCykGeneralRules(srcg, w);
    default:
      throw new ParseException(
          "I did not understand. Please check the spelling of your parsing algorithm.",
          1);
    }
  }

  private static void validateInputSymbols(Srcg srcg, String w)
      throws ParseException {
    String[] wSplit = w.split(" ");
    if (wSplit.length > 1 || !"".equals(wSplit[0])) {
      for (String token : wSplit) {
        if (!srcg.terminalsContain(token)) {
          throw new ParseException("Token " + token
              + " from input is not a terminal in the grammar.", 0);
        }
      }
    }
  }

  /**
   * Call with appropriate grammar. Better call the convert-to function first.
   */
  public static ParsingSchema convertToSchema(Pcfg pcfg, String w,
      String algorithm) throws ParseException {
    validateInputSymbols(pcfg, w);
    switch (algorithm) {
    case "pcfg-astar":
      return PcfgToAstarRulesConverter.pcfgToAstarRules(pcfg, w);
    case "pcfg-cyk":
      return PcfgToCykRulesConverter.pcfgToCykRules(pcfg, w);
    default:
      throw new ParseException(
          "I did not understand. Please check the spelling of your parsing algorithm.",
          1);
    }
  }

  private static void validateInputSymbols(Pcfg pcfg, String w)
      throws ParseException {
    String[] wSplit = w.split(" ");
    if (wSplit.length > 1 || !"".equals(wSplit[0])) {
      for (String token : wSplit) {
        if (!pcfg.terminalsContain(token)) {
          throw new ParseException("Token " + token
              + " from input is not a terminal in the grammar.", 0);
        }
      }
    }
  }

  public static ParsingSchema convertToSchema(Ccg ccg, String w,
      String algorithm) throws ParseException {
    validateInputSymbols(ccg, w);
    if ("ccg-deduction".equals(algorithm)) {
      return CcgToDeductionRulesConverter.ccgToDeductionRules(ccg, w);
    }
    throw new ParseException(
        "I did not understand. Please check the spelling of your parsing algorithm.",
        1);
  }

  public static ParsingSchema convertToSchema(Lag lag, String w,
      String algorithm) throws ParseException {
    if ("lag-deduction".equals(algorithm)) {
      return LagToDeductionRulesConverter.lagToDeductionRules(lag, w);
    }
    throw new ParseException(
        "I did not understand. Please check the spelling of your parsing algorithm.",
        1);
  }

  private static void validateInputSymbols(Ccg ccg, String w)
      throws ParseException {
    String[] wSplit = w.split(" ");
    if (wSplit.length > 1 || !"".equals(wSplit[0])) {
      for (String token : wSplit) {
        if (!ccg.getLexicon().containsKey(token)) {
          throw new ParseException("Token " + token
              + " from input is not a terminal in the grammar.", 0);
        }
      }
    }
  }
}

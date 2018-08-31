package com.github.samyadaleh.cltoolbox.chartparsing.converter;

import java.text.ParseException;

import com.github.samyadaleh.cltoolbox.chartparsing.ParsingSchema;
import com.github.samyadaleh.cltoolbox.chartparsing.converter.cfg.*;
import com.github.samyadaleh.cltoolbox.chartparsing.converter.lcfrs.LcfrsToCykRulesConverter;
import com.github.samyadaleh.cltoolbox.chartparsing.converter.lcfrs.LcfrsToEarleyRulesConverter;
import com.github.samyadaleh.cltoolbox.chartparsing.converter.pcfg.PcfgToAstarRulesConverter;
import com.github.samyadaleh.cltoolbox.chartparsing.converter.pcfg.PcfgToCykRulesConverter;
import com.github.samyadaleh.cltoolbox.chartparsing.converter.tag.TagToCykRulesConverter;
import com.github.samyadaleh.cltoolbox.chartparsing.converter.tag.TagToEarleyPrefixValidRulesConverter;
import com.github.samyadaleh.cltoolbox.chartparsing.converter.tag.TagToEarleyRulesConverter;
import com.github.samyadaleh.cltoolbox.common.cfg.Cfg;
import com.github.samyadaleh.cltoolbox.common.cfg.Pcfg;
import com.github.samyadaleh.cltoolbox.common.lcfrs.Srcg;
import com.github.samyadaleh.cltoolbox.common.tag.Tag;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GrammarToDeductionRulesConverter {
  private static final Logger log = LogManager.getLogger();

  /**
   * Call with appropriate grammar. Better call the convert-to function first.
   */
  public static ParsingSchema convertToSchema(Cfg cfg, String w,
      String algorithm) throws ParseException {
    switch (algorithm) {
    case "cfg-topdown":
      return CfgToTopDownRulesConverter.cfgToTopDownRules(cfg, w);
    case "cfg-shiftreduce":
      return CfgToShiftReduceRulesConverter.cfgToShiftReduceRules(cfg, w);
    case "cfg-earley":
      return CfgToEarleyRulesConverter.cfgToEarleyRules(cfg, w);
    case "cfg-earley-passive":
      return CfgToEarleyPassiveRulesConverter.cfgToEarleyPassiveRules(cfg, w);
    case "cfg-leftcorner":
      return CfgToLeftCornerRulesConverter.cfgToLeftCornerRules(cfg, w);
    case "cfg-leftcorner-chart":
      return CfgToLeftCornerChartRulesConverter.cfgToLeftCornerChartRules(cfg, w);
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
      log.info(
          "I did not understand. Please check the spelling of your parsing algorithm.");
      return null;
    }
  }

  /**
   * Call with appropriate grammar. Better call the convert-to function first.
   */
  public static ParsingSchema convertToSchema(Tag tag, String w,
      String algorithm) {
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
      log.info(
          "I did not understand. Please check the spelling of your parsing algorithm.");
      return null;
    }
  }

  /**
   * Call with appropriate grammar. Better call the convert-to function first.
   */
  public static ParsingSchema convertToSchema(Srcg srcg, String w,
      String algorithm) throws ParseException {
    switch (algorithm) {
    case "srcg-earley":
      return LcfrsToEarleyRulesConverter.srcgToEarleyRules(srcg, w);
    case "srcg-cyk-extended":
      return LcfrsToCykRulesConverter.srcgToCykExtendedRules(srcg, w);
    case "srcg-cyk-general":
      return LcfrsToCykRulesConverter.srcgToCykGeneralRules(srcg, w);
    default:
      log.info(
          "I did not understand. Please check the spelling of your parsing algorithm.");
      return null;
    }
  }

  /**
   * Call with appropriate grammar. Better call the convert-to function first.
   */
  public static ParsingSchema convertToSchema(Pcfg pcfg, String w,
      String algorithm) throws ParseException {
    switch (algorithm) {
    case "pcfg-astar":
      return PcfgToAstarRulesConverter.pcfgToAstarRules(pcfg, w);
    case "pcfg-cyk":
      return PcfgToCykRulesConverter.pcfgToCykRules(pcfg, w);
    default:
      log.info(
          "I did not understand. Please check the spelling of your parsing algorithm.");
      return null;
    }
  }

}

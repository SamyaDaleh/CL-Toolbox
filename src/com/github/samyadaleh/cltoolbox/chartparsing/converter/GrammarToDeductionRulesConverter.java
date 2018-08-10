package com.github.samyadaleh.cltoolbox.chartparsing.converter;

import java.text.ParseException;

import com.github.samyadaleh.cltoolbox.chartparsing.ParsingSchema;
import com.github.samyadaleh.cltoolbox.common.cfg.Cfg;
import com.github.samyadaleh.cltoolbox.common.cfg.Pcfg;
import com.github.samyadaleh.cltoolbox.common.lcfrs.Srcg;
import com.github.samyadaleh.cltoolbox.common.tag.Tag;

public class GrammarToDeductionRulesConverter {

  /**
   * Call with appropriate grammar. Better call the convert-to function first.
   * @throws ParseException
   */
  public ParsingSchema convertToSchema(Cfg cfg, String w, String algorithm)
    throws ParseException {
    switch (algorithm) {
    case "cfg-topdown":
      return CfgToDeductionRulesConverter.cfgToTopDownRules(cfg, w);
    case "cfg-shiftreduce":
      return CfgToDeductionRulesConverter.cfgToShiftReduceRules(cfg, w);
    case "cfg-earley":
      return CfgToDeductionRulesConverter.cfgToEarleyRules(cfg, w);
    case "cfg-earley-passive":
      return CfgToDeductionRulesConverter.cfgToEarleyPassiveRules(cfg, w);
    case "cfg-leftcorner":
      return CfgToDeductionRulesConverter.cfgToLeftCornerRules(cfg, w);
    case "cfg-leftcorner-chart":
      return CfgToDeductionRulesConverter.cfgToLeftCornerChartRules(cfg, w);
    case "cfg-cyk":
      return CfgToDeductionRulesConverter.cfgToCykRules(cfg, w);
    case "cfg-cyk-extended":
      return CfgToDeductionRulesConverter.cfgToCykExtendedRules(cfg, w);
    case "cfg-cyk-general":
      return CfgToDeductionRulesConverter.cfgToCykGeneralRules(cfg, w);
    case "cfg-unger":
      return CfgToDeductionRulesConverter.cfgToUngerRules(cfg, w);
    default:
      System.out.println(
        "I did not understand. Please check the spelling of your parsing algorithm.");
      return null;
    }
  }

  /**
   * Call with appropriate grammar. Better call the convert-to function first.
   */
  public ParsingSchema convertToSchema(Tag tag, String w, String algorithm) {
    switch (algorithm) {
    case "tag-cyk-extended":
      return TagToDeductionRulesConverter.tagToCykExtendedRules(tag, w);
    case "tag-earley":
      return TagToDeductionRulesConverter.tagToEarleyRules(tag, w);
    case "tag-earley-prefixvalid":
      return TagToDeductionRulesConverter.tagToEarleyPrefixValidRules(tag, w);
    default:
      System.out.println(
        "I did not understand. Please check the spelling of your parsing algorithm.");
      return null;
    }
  }

  /**
   * Call with appropriate grammar. Better call the convert-to function first.
   * @throws ParseException
   */
  public ParsingSchema convertToSchema(Srcg srcg, String w, String algorithm)
    throws ParseException {
    switch (algorithm) {
    case "srcg-earley":
      return LcfrsToDeductionRulesConverter.srcgToEarleyRules(srcg, w);
    case "srcg-cyk-extended":
      return LcfrsToDeductionRulesConverter.srcgToCykExtendedRules(srcg, w);
    case "srcg-cyk-general":
      return LcfrsToDeductionRulesConverter.srcgToCykGeneralRules(srcg, w);
    default:
      System.out.println(
        "I did not understand. Please check the spelling of your parsing algorithm.");
      return null;
    }
  }

  /**
   * Call with appropriate grammar. Better call the convert-to function first.
   * @throws ParseException
   */
  public ParsingSchema convertToSchema(Pcfg pcfg, String w, String algorithm)
    throws ParseException {
    switch (algorithm) {
    case "pcfg-astar":
      return PcfgToDeductionRulesConverter.pcfgToAstarRules(pcfg, w);
    case "pcfg-cyk":
      return PcfgToDeductionRulesConverter.pcfgToCykRules(pcfg, w);
    default:
      System.out.println(
        "I did not understand. Please check the spelling of your parsing algorithm.");
      return null;
    }
  }

}

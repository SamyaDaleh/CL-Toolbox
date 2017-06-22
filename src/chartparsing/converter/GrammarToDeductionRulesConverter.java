package chartparsing.converter;

import chartparsing.ParsingSchema;
import common.cfg.Cfg;
import common.cfg.Pcfg;
import common.lcfrs.Srcg;
import common.tag.Tag;

public class GrammarToDeductionRulesConverter {

  /** Call with appropriate grammar. Better call the convert-to function
   * first. */
  public ParsingSchema convertToSchema(Cfg cfg, String w, String algorithm) {
    switch (algorithm) {
    case "cfg-topdown":
      return CfgToDeductionRulesConverter
        .cfgToTopDownRules(cfg.getCfgWithoutEmptyProductions()
          .getCfgWithoutNonGeneratingSymbols().getCfgWithoutNonReachableSymbols(), w);
    case "cfg-shiftreduce":
      return CfgToDeductionRulesConverter
        .cfgToShiftReduceRules(cfg.getCfgWithoutEmptyProductions()
          .getCfgWithoutNonGeneratingSymbols().getCfgWithoutNonReachableSymbols(), w);
    case "cfg-earley":
      return CfgToDeductionRulesConverter.cfgToEarleyRules(cfg, w);
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

  /** Call with appropriate grammar. Better call the convert-to function
   * first. */
  public ParsingSchema convertToSchema(Tag tag, String w, String algorithm) {
    switch (algorithm) {
    case "tag-cyk":
      return TagToDeductionRulesConverter.tagToCykRules(tag, w);
    case "tag-earley":
      return TagToDeductionRulesConverter.tagToEarleyRules(tag, w);
    default:
      System.out.println(
        "I did not understand. Please check the spelling of your parsing algorithm.");
      return null;
    }
  }

  /** Call with appropriate grammar. Better call the convert-to function
   * first. */
  public ParsingSchema convertToSchema(Srcg srcg, String w, String algorithm) {
    switch (algorithm) {
    case "srcg-earley":
      return LcfrsToDeductionRulesConverter.srcgToEarleyRules(srcg, w);
    case "srcg-cyk":
      return LcfrsToDeductionRulesConverter.srcgToCykRules(srcg, w);
    case "srcg-cyk-extended":
      return LcfrsToDeductionRulesConverter.srcgToCykExtendedRules(srcg, w);
    default:
      System.out.println(
        "I did not understand. Please check the spelling of your parsing algorithm.");
      return null;
    }
  }

  /** Call with appropriate grammar. Better call the convert-to function
   * first. */
  public ParsingSchema convertToSchema(Pcfg pcfg, String w, String algorithm) {
    switch (algorithm) {
    case "pcfg-astar":
      return PcfgToDeductionRulesConverter.pcfgToAstarRules(pcfg, w);
    default:
      System.out.println(
        "I did not understand. Please check the spelling of your parsing algorithm.");
      return null;
    }
  }

}

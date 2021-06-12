package com.github.samyadaleh.cltoolbox.cli;

import com.github.samyadaleh.cltoolbox.common.cfg.Cfg;
import com.github.samyadaleh.cltoolbox.common.cfg.Pcfg;
import com.github.samyadaleh.cltoolbox.common.lcfrs.Srcg;
import com.github.samyadaleh.cltoolbox.common.tag.Tag;

import java.text.ParseException;

public class GrammarToGrammarConverter {

  public static Cfg checkAndMayConvertToCfg(Cfg cfg, String algorithm,
      boolean please) throws ParseException {
    switch (algorithm) {
    case "cfg-topdown":
      return getCfgForTopDown(cfg, please);
    case "cfg-shiftreduce":
      return getCfgForShiftReduce(cfg, please);
    case "cfg-earley":
    case "cfg-earley-bottomup":
    case "cfg-earley-passive":
    case "cfg-leftcorner-chart":
    case "cfg-leftcorner-bottomup":
    case "cfg-cyk-general":
      return cfg;
    case "cfg-leftcorner":
    case "cfg-unger":
      return getCfgForLeftCorner(cfg, please);
    case "cfg-cyk":
      return getCfgForCyk(cfg, please);
    case "cfg-cyk-extended":
      return getCfgForCykExtended(cfg, please);
    default:
      if (algorithm.matches("cfg-lr-\\d+")) {
        return cfg;
      }
      throw new IllegalArgumentException(
          "I did not understand. Please check the spelling of your parsing algorithm.");
    }
  }

  public static Cfg checkAndMayConvertToCfg(Pcfg pcfg, String algorithm,
      boolean please) throws ParseException {
    Cfg cfg = new Cfg(pcfg);
    return checkAndMayConvertToCfg(cfg, algorithm, please);
  }

  public static Pcfg checkAndMayConvertToPcfg(Cfg cfg, String algorithm,
      boolean please) throws ParseException {
    switch (algorithm) {
    case "pcfg-astar":
    case "pcfg-cyk":
      if (!cfg.isInChomskyNormalForm()) {
        if (please) {
          return new Pcfg(cfg.getCfgWithoutEmptyProductions()
              .getCfgWithoutNonGeneratingSymbols()
              .getCfgWithoutNonReachableSymbols().getBinarizedCfg()
              .getCfgWithEitherOneTerminalOrNonterminalsOnRhs()
              .getCfgWithoutChainRules());
        } else {
          throw new ParseException(
              "CFG must be in Chomsky Normal Form to convert it into a PCFG where "
                  + " is possible.", 1);
        }
      } else {
        return new Pcfg(cfg);
      }
    default:
      throw new IllegalArgumentException(
          "I did not understand. Please check the spelling of your parsing algorithm.");
    }
  }

  public static Pcfg checkAndMayConvertToPcfg(Pcfg pcfg, String algorithm,
      boolean please) throws ParseException {
    switch (algorithm) {
    case "pcfg-astar":
    case "pcfg-cyk":
      Cfg cfg = new Cfg(pcfg);
      if (!cfg.isInChomskyNormalForm()) {
        if (please) {
          throw new ParseException("PCFG can't be converted.", 1);
        } else {
          throw new ParseException(
              "PCFG must be in Chomsky Normal Form to apply A* parsing.", 1);
        }
      } else {
        return pcfg;
      }
    default:
      throw new IllegalArgumentException(
          "I did not understand. Please check the spelling of your parsing algorithm.");
    }
  }

  public static Srcg checkAndMayConvertToSrcg(Srcg srcg, String algorithm,
      boolean please) throws ParseException {
    switch (algorithm) {
    case "srcg-earley":
      return getSrcgForEarley(srcg, please);
    case "srcg-cyk-extended":
      return getSrcgForCykExtended(srcg, please);
    case "srcg-cyk-general":
      return getSrcgForCykGeneral(srcg, please);
    default:
      throw new IllegalArgumentException(
          "I did not understand. Please check the spelling of your parsing algorithm.");
    }
  }

  public static Srcg checkAndMayConvertToSrcg(Pcfg pcfg, String algorithm,
      boolean please) throws ParseException {
    Cfg cfg = new Cfg(pcfg);
    return checkAndMayConvertToSrcg(cfg, algorithm, please);
  }

  public static Srcg checkAndMayConvertToSrcg(Cfg cfg, String algorithm,
      boolean please) throws ParseException {
    Srcg srcg = new Srcg(cfg);
    return checkAndMayConvertToSrcg(srcg, algorithm, please);
  }

  public static Srcg checkAndMayConvertToSrcg(Tag tag, String algorithm,
      boolean please) throws ParseException {
    Srcg srcg = new Srcg(tag);
    return checkAndMayConvertToSrcg(srcg, algorithm, please);
  }

  public static Tag checkAndMayConvertToTag(Tag tag, String algorithm,
      boolean please) throws ParseException {
    switch (algorithm) {
    case "tag-cyk-extended":
      if (!tag.isBinarized()) {
        if (please) {
          return tag.getBinarizedTag();
        } else {
          throw new IllegalArgumentException(
              "TAG must be binarized to apply CYK parsing.");
        }
      } else {
        return tag;
      }
    case "tag-cyk-general":
    case "tag-earley":
    case "tag-earley-prefixvalid":
      return tag;
    default:
      throw new IllegalArgumentException(
          "I did not understand. Please check the spelling of your parsing algorithm.");
    }
  }

  public static Tag checkAndMayConvertToTag(Cfg cfg, String algorithm,
      boolean please) throws ParseException {
    Tag tag = new Tag(cfg);
    return checkAndMayConvertToTag(tag, algorithm, please);
  }

  public static Tag checkAndMayConvertToTag(Pcfg pcfg, String algorithm,
      boolean please) throws ParseException {
    Cfg cfg = new Cfg(pcfg);
    return checkAndMayConvertToTag(cfg, algorithm, please);
  }

  private static Cfg getCfgForCykExtended(Cfg cfg, boolean please)
      throws ParseException {
    if (!cfg.isInCanonicalTwoForm()) {
      if (please) {
        return cfg.getCfgWithoutEmptyProductions()
            .getCfgWithoutNonGeneratingSymbols()
            .getCfgWithoutNonReachableSymbols().getBinarizedCfg()
            .getCfgWithEitherOneTerminalOrNonterminalsOnRhs();
      } else {
        throw new ParseException(
            "CFG must be in Canonical 2 Form for extended CYK parsing.", 1);
      }
    } else {
      return cfg;
    }
  }

  private static Cfg getCfgForCyk(Cfg cfg, boolean please)
      throws ParseException {
    if (!cfg.isInChomskyNormalForm()) {
      if (please) {
        return cfg.getCfgWithoutEmptyProductions()
            .getCfgWithoutNonGeneratingSymbols()
            .getCfgWithoutNonReachableSymbols().getBinarizedCfg()
            .getCfgWithEitherOneTerminalOrNonterminalsOnRhs()
            .getCfgWithoutChainRules();
      } else {
        throw new ParseException(
            "CFG must be in Chomsky Normal Form for CYK parsing.", 1);
      }
    } else {
      return cfg;
    }
  }

  private static Cfg getCfgForLeftCorner(Cfg cfg, boolean please)
      throws ParseException {
    if (cfg.hasEpsilonProductions() || cfg.hasLeftRecursion()) {
      if (please) {
        return cfg.getCfgWithoutEmptyProductions().getCfgWithoutChainRules().getCfgWithoutLeftRecursion()
            .getCfgWithoutEmptyProductions().getCfgWithoutNonGeneratingSymbols()
            .getCfgWithoutNonReachableSymbols();
      } else {
        throw new ParseException(
            "CFG must not contain empty productions or left recursion for this parsing algorithm.",
            1);
      }
    } else {
      return cfg;
    }
  }

  private static Cfg getCfgForShiftReduce(Cfg cfg, boolean please)
      throws ParseException {
    if (cfg.hasEpsilonProductions()) {
      if (please) {
        return cfg.getCfgWithoutEmptyProductions()
            .getCfgWithoutNonGeneratingSymbols()
            .getCfgWithoutNonReachableSymbols();
      } else {
        throw new ParseException(
            "CFG must not contain empty productions for ShiftReduce parsing.",
            1);
      }
    } else {
      return cfg;
    }
  }

  private static Cfg getCfgForTopDown(Cfg cfg, boolean please)
      throws ParseException {
    if (cfg.hasLeftRecursion()) {
      if (please) {
        return cfg.getCfgWithoutEmptyProductions()
            .getCfgWithoutNonGeneratingSymbols()
            .getCfgWithoutNonReachableSymbols().getCfgWithoutChainRules()
            .getCfgWithoutLeftRecursion();
      } else {
        throw new ParseException(
            "CFG must not contain left recursion for TopDown parsing.", 1);
      }
    } else {
      return cfg;
    }
  }

  private static Srcg getSrcgForCykExtended(Srcg srcg, boolean please)
      throws ParseException {
    if (!srcg.isBinarized() || srcg.hasEpsilonProductions()) {
      if (please) {
        return srcg.getSrcgWithoutUselessRules().getBinarizedSrcg()
            .getSrcgWithoutEmptyProductions().getSrcgWithoutUselessRules();
      } else {
        throw new ParseException(
            "sRCG must be binarized and not contain empty productions to apply extended CYK parsing",
            1);
      }
    } else {
      return srcg;
    }
  }

  private static Srcg getSrcgForCykGeneral(Srcg srcg, boolean please)
      throws ParseException {
    if (srcg.hasEpsilonProductions()) {
      if (please) {
        return srcg.getSrcgWithoutUselessRules()
            .getSrcgWithoutEmptyProductions().getSrcgWithoutUselessRules();
      } else {
        throw new ParseException(
            "sRCG must not contain empty productions to apply general CYK parsing",
            1);
      }
    } else {
      return srcg;
    }
  }

  private static Srcg getSrcgForEarley(Srcg srcg, boolean please)
      throws ParseException {
    if (!srcg.isOrdered() || srcg.hasEpsilonProductions()) {
      if (please) {
        return srcg.getSrcgWithoutUselessRules().getOrderedSrcg()
            .getSrcgWithoutEmptyProductions().getSrcgWithoutUselessRules();
      } else {
        throw new ParseException(
            "sRCG must be ordered and not contain epsilon productions for this Earley algorithm",
            1);
      }
    } else {
      return srcg;
    }
  }
}

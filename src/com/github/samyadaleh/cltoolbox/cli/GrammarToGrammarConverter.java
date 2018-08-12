package com.github.samyadaleh.cltoolbox.cli;

import java.text.ParseException;

import com.github.samyadaleh.cltoolbox.common.cfg.Cfg;
import com.github.samyadaleh.cltoolbox.common.cfg.Pcfg;
import com.github.samyadaleh.cltoolbox.common.lcfrs.Srcg;
import com.github.samyadaleh.cltoolbox.common.tag.Tag;

class GrammarToGrammarConverter {

  static Cfg checkAndMayConvertToCfg(Cfg cfg, String algorithm, boolean please)
    throws ParseException {
    switch (algorithm) {
    case "cfg-topdown":
      return getCfgForTopDown(cfg, please);
    case "cfg-shiftreduce":
      return getCfgForShiftReduce(cfg, please);
    case "cfg-earley":
    case "cfg-earley-passive":
      return cfg;
    case "cfg-leftcorner":
      return getCfgForLeftCorner(cfg, please);
    case "cfg-leftcorner-chart":
      return cfg;
    case "cfg-cyk":
      return getCfgForCyk(cfg, please);
    case "cfg-cyk-extended":
      return getCfgForCykExtended(cfg, please);
    case "cfg-cyk-general":
      return cfg;
    case "cfg-unger":
      return getCfgForLeftCorner(cfg, please);
    default:
      System.out.println(
        "I did not understand. Please check the spelling of your parsing algorithm.");
      return null;
    }
  }

  static Cfg checkAndMayConvertToCfg(Pcfg pcfg, String algorithm,
    boolean please) throws ParseException {
    Cfg cfg = new Cfg(pcfg);
    return checkAndMayConvertToCfg(cfg, algorithm, please);
  }

  static Pcfg checkAndMayConvertToPcfg(Cfg cfg, String algorithm,
    boolean please) {
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
          System.out.println(
            "CFG must be in Chomsky Normal Form to convert it into a PCFG where "
              + " is possible.");
          return null;
        }
      } else {
        return new Pcfg(cfg);
      }
    default:
      System.out.println(
        "I did not understand. Please check the spelling of your parsing algorithm.");
      return null;
    }
  }

  static Pcfg checkAndMayConvertToPcfg(Pcfg pcfg, String algorithm,
    boolean please) {
    switch (algorithm) {
    case "pcfg-astar":
    case "pcfg-cyk":
      Cfg cfg = new Cfg(pcfg);
      if (!cfg.isInChomskyNormalForm()) {
        if (please) {
          System.out.println("PCFG can't be converted.");
          return null;
        } else {
          System.out.println(
            "PCFG must be in Chomsky Normal Form to apply A* parsing.");
          return null;
        }
      } else {
        return pcfg;
      }
    default:
      System.out.println(
        "I did not understand. Please check the spelling of your parsing algorithm.");
      return null;
    }
  }

  static Srcg checkAndMayConvertToSrcg(Cfg cfg, String algorithm,
    boolean please) throws ParseException {
    switch (algorithm) {
    case "srcg-earley":
      return getSrcgForEarley(cfg, please);
    case "srcg-cyk-extended":
      return getSrcgForCykExtended(cfg, please);
    case "srcg-cyk-general":
      return getSrcgForCykGeneral(cfg, please);
    default:
      System.out.println(
        "I did not understand. Please check the spelling of your parsing algorithm.");
      return null;
    }
  }

  static Srcg checkAndMayConvertToSrcg(Srcg srcg, String algorithm,
    boolean please) throws ParseException {
    switch (algorithm) {
    case "srcg-earley":
      return getSrcgForEarley(srcg, please);
    case "srcg-cyk-extended":
      return getSrcgForCykExtended(srcg, please);
    case "srcg-cyk-general":
      return getSrcgForCykGeneral(srcg, please);
    default:
      System.out.println(
        "I did not understand. Please check the spelling of your parsing algorithm.");
      return null;
    }
  }

  static Srcg checkAndMayConvertToSrcg(Pcfg pcfg, String algorithm,
    boolean please) throws ParseException {
    Cfg cfg = new Cfg(pcfg);
    return checkAndMayConvertToSrcg(cfg, algorithm, please);
  }

  static Tag checkAndMayConvertToTag(Tag tag, String algorithm, boolean please)
    throws ParseException {
    switch (algorithm) {
    case "tag-cyk-extended":
      if (!tag.isBinarized()) {
        if (please) {
          return tag.getBinarizedTag();
        } else {
          System.out.println("TAG must be binarized to apply CYK parsing.");
          return null;
        }
      } else {
        return tag;
      }
    case "tag-earley":
      return tag;
    case "tag-earley-prefixvalid":
      return tag;
    default:
      System.out.println(
        "I did not understand. Please check the spelling of your parsing algorithm.");
      return null;
    }
  }

  static Tag checkAndMayConvertToTag(Cfg cfg, String algorithm, boolean please)
    throws ParseException {
    switch (algorithm) {
    case "tag-cyk-extended":
      if (!cfg.isBinarized()) {
        if (please) {
          return new Tag(cfg.getBinarizedCfg());
        } else {
          System.out.println(
            "CFG must be binarized to convert it into a TAG where CYK parsing is possible.");
          return null;
        }
      } else {
        return new Tag(cfg);
      }
    case "tag-earley":
      return new Tag(cfg);
    case "tag-earley-prefixvalid":
      return new Tag(cfg);
    default:
      System.out.println(
        "I did not understand. Please check the spelling of your parsing algorithm.");
      return null;
    }
  }

  static Tag checkAndMayConvertToTag(Pcfg pcfg, String algorithm,
    boolean please) throws ParseException {
    Cfg cfg = new Cfg(pcfg);
    return checkAndMayConvertToTag(cfg, algorithm, please);
  }

  private static Cfg getCfgForCykExtended(Cfg cfg, boolean please) {
    if (!cfg.isInCanonicalTwoForm()) {
      if (please) {
        return cfg.getCfgWithoutEmptyProductions()
          .getCfgWithoutNonGeneratingSymbols()
          .getCfgWithoutNonReachableSymbols().getBinarizedCfg()
          .getCfgWithEitherOneTerminalOrNonterminalsOnRhs();
      } else {
        System.out
          .println("CFG must be in Canonical 2 Form for extended CYK parsing.");
        return null;
      }
    } else {
      return cfg;
    }
  }

  private static Cfg getCfgForCyk(Cfg cfg, boolean please) {
    if (!cfg.isInChomskyNormalForm()) {
      if (please) {
        return cfg.getCfgWithoutEmptyProductions()
          .getCfgWithoutNonGeneratingSymbols()
          .getCfgWithoutNonReachableSymbols().getBinarizedCfg()
          .getCfgWithEitherOneTerminalOrNonterminalsOnRhs()
          .getCfgWithoutChainRules();
      } else {
        System.out
          .println("CFG must be in Chomsky Normal Form for CYK parsing.");
        return null;
      }
    } else {
      return cfg;
    }
  }

  private static Cfg getCfgForLeftCorner(Cfg cfg, boolean please)
    throws ParseException {
    if (cfg.hasEpsilonProductions() || cfg.hasLeftRecursion()) {
      if (please) {
        return cfg.getCfgWithoutEmptyProductions().getCfgWithoutLeftRecursion()
          .getCfgWithoutEmptyProductions().getCfgWithoutNonGeneratingSymbols()
          .getCfgWithoutNonReachableSymbols();
      } else {
        System.out.println(
          "CFG must not contain empty productions or left recursion for this parsing algorithm.");
        return null;
      }
    } else {
      return cfg;
    }
  }

  private static Cfg getCfgForShiftReduce(Cfg cfg, boolean please) {
    if (cfg.hasEpsilonProductions()) {
      if (please) {
        return cfg.getCfgWithoutEmptyProductions()
          .getCfgWithoutNonGeneratingSymbols()
          .getCfgWithoutNonReachableSymbols();
      } else {
        System.out.println(
          "CFG must not contain empty productions for ShiftReduce parsing.");
        return null;
      }
    } else {
      return cfg;
    }
  }

  private static Cfg getCfgForTopDown(Cfg cfg, boolean please) {
    if (cfg.hasEpsilonProductions()) {
      if (please) {
        return cfg.getCfgWithoutEmptyProductions()
          .getCfgWithoutNonGeneratingSymbols()
          .getCfgWithoutNonReachableSymbols();
      } else {
        System.out.println(
          "CFG must not contain empty productions for TopDown parsing.");
        return null;
      }
    } else {
      return cfg;
    }
  }

  private static Srcg getSrcgForCykExtended(Srcg srcg, boolean please)
    throws ParseException {
    if (!srcg.isBinarized() || srcg.hasEpsilonProductions()) {
      if (please) {
        return srcg.getBinarizedSrcg().getSrcgWithoutEmptyProductions()
          .getSrcgWithoutUselessRules();
      } else {
        System.out.println(
          "sRCG must be binarized and not contain empty productions to apply extended CYK parsing");
        return null;
      }
    } else {
      return srcg;
    }
  }

  private static Srcg getSrcgForCykExtended(Cfg cfg, boolean please)
    throws ParseException {
    if (!cfg.isBinarized() || cfg.hasMixedRhs()) {
      if (please) {
        return new Srcg(cfg.getBinarizedCfg()
          .getCfgWithEitherOneTerminalOrNonterminalsOnRhs())
            .getSrcgWithoutUselessRules();
      } else {
        System.out.println(
          "CFG must be binarized and not contain mixed rhs sides to convert it into a sRCG where extended CYK parsing is possible.");
        return null;
      }
    } else {
      return new Srcg(cfg);
    }
  }

  private static Srcg getSrcgForCykGeneral(Srcg srcg, boolean please)
    throws ParseException {
    if (srcg.hasEpsilonProductions()) {
      if (please) {
        return srcg.getSrcgWithoutEmptyProductions()
          .getSrcgWithoutUselessRules();
      } else {
        System.out.println(
          "sRCG must not contain empty productions to apply general CYK parsing");
        return null;
      }
    } else {
      return srcg;
    }
  }

  private static Srcg getSrcgForCykGeneral(Cfg cfg, boolean please)
    throws ParseException {
    if (cfg.hasEpsilonProductions()) {
      if (please) {
        return new Srcg(cfg.getCfgWithoutEmptyProductions())
          .getSrcgWithoutUselessRules();
      } else {
        System.out.println(
          "CFG must not contain empty productions to be converted into a sRCG where general CYK parsing is possible.");
        return null;
      }
    } else {
      return new Srcg(cfg);
    }
  }

  private static Srcg getSrcgForEarley(Srcg srcg, boolean please)
    throws ParseException {
    if (!srcg.isOrdered() || srcg.hasEpsilonProductions()) {
      if (please) {
        return srcg.getOrderedSrcg().getSrcgWithoutEmptyProductions()
          .getSrcgWithoutUselessRules();
      } else {
        System.out.println(
          "sRCG must be ordered and not contain epsilon productions for this Earley algorithm");
        return null;
      }
    } else {
      return srcg;
    }
  }

  private static Srcg getSrcgForEarley(Cfg cfg, boolean please)
    throws ParseException {
    if (!cfg.isBinarized()) {
      if (please) {
        return new Srcg(cfg.getBinarizedCfg()).getSrcgWithoutUselessRules();
      } else {
        System.out.println(
          "CFG must be binarized to convert it into a sRCG where Earley parsing is possible.");
        return null;
      }
    } else {
      return new Srcg(cfg);
    }
  }
}

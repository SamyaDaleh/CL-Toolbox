package cli;

import java.text.ParseException;

import common.cfg.Cfg;
import common.cfg.Pcfg;
import common.lcfrs.Srcg;
import common.tag.Tag;

class GrammarToGrammarConverter {
  private final boolean please;

  GrammarToGrammarConverter(boolean please) {
    this.please = please;
  }

  Cfg checkAndMayConvertToCfg(Cfg cfg, String algorithm) {
    switch (algorithm) {
    case "cfg-topdown":
      return getCfgForTopDown(cfg);
    case "cfg-shiftreduce":
      return getCfgForShiftReduce(cfg);
    case "cfg-earley":
      return cfg;
    case "cfg-leftcorner":
      return getCfgForLeftCorner(cfg);
    case "cfg-leftcorner-chart":
      return cfg;
    case "cfg-cyk":
      return getCfgForCyk(cfg);
    case "cfg-cyk-extended":
      return getCfgForCykExtended(cfg);
    case "cfg-cyk-general":
      return cfg;
    case "cfg-unger":
      return getCfgForLeftCorner(cfg);
    default:
      System.out.println(
        "I did not understand. Please check the spelling of your parsing algorithm.");
      return null;
    }
  }

  private Cfg getCfgForCykExtended(Cfg cfg) {
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

  private Cfg getCfgForCyk(Cfg cfg) {
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

  private Cfg getCfgForLeftCorner(Cfg cfg) {
    if (cfg.hasEpsilonProductions() || cfg.hasDirectLeftRecursion()) {
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

  private Cfg getCfgForShiftReduce(Cfg cfg) {
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

  private Cfg getCfgForTopDown(Cfg cfg) {
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

  Tag checkAndMayConvertToTag(Cfg cfg, String algorithm) throws ParseException {
    switch (algorithm) {
    case "tag-cyk":
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

  Pcfg checkAndMayConvertToPcfg(Cfg cfg, String algorithm) {
    switch (algorithm) {
    case "pcfg-astar":
      if (!cfg.isInChomskyNormalForm()) {
        if (please) {
          return new Pcfg(cfg.getCfgWithoutEmptyProductions()
            .getCfgWithoutNonGeneratingSymbols()
            .getCfgWithoutNonReachableSymbols().getBinarizedCfg()
            .getCfgWithEitherOneTerminalOrNonterminalsOnRhs()
            .getCfgWithoutChainRules());
        } else {
          System.out.println(
            "CFG must be in Chomsky Normal Form to convert it into a PCFG where A* parsing is possible.");
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

  Srcg checkAndMayConvertToSrcg(Cfg cfg, String algorithm)
    throws ParseException {
    switch (algorithm) {
    case "srcg-earley":
      return getSrcgForEarley(cfg);
    case "srcg-cyk":
      return getSrcgForCyk(cfg);
    case "srcg-cyk-extended":
      return getSrcgForCykExtended(cfg);
    default:
      System.out.println(
        "I did not understand. Please check the spelling of your parsing algorithm.");
      return null;
    }
  }

  private Srcg getSrcgForCykExtended(Cfg cfg) throws ParseException {
    if (!cfg.isBinarized() || cfg.hasMixedRhs()) {
      if (please) {
        return new Srcg(cfg.getBinarizedCfg()
          .getCfgWithEitherOneTerminalOrNonterminalsOnRhs());
      } else {
        System.out.println(
          "CFG must be binarized and not contain mixed rhs sides to convert it into a sRCG where extended CYK parsing is possible.");
        return null;
      }
    } else {
      return new Srcg(cfg);
    }
  }

  private Srcg getSrcgForCyk(Cfg cfg) throws ParseException {
    if (!cfg.isBinarized() || cfg.hasChainRules() || cfg.hasMixedRhs()) {
      if (please) {
        return new Srcg(cfg.getBinarizedCfg().getCfgWithoutChainRules()
          .getCfgWithEitherOneTerminalOrNonterminalsOnRhs());
      } else {
        System.out.println(
          "CFG must be binarized, not contain chain rules and not contain rules with mixed rhs sides to convert it into a sRCG where CYK parsing is possible.");
        return null;
      }
    } else {
      return new Srcg(cfg);
    }
  }

  private Srcg getSrcgForEarley(Cfg cfg) throws ParseException {
    if (!cfg.isBinarized()) {
      if (please) {
        return new Srcg(cfg.getBinarizedCfg());
      } else {
        System.out.println(
          "CFG must be binarized to convert it into a sRCG where Earley parsing is possible.");
        return null;
      }
    } else {
      return new Srcg(cfg);
    }
  }

  Srcg checkAndMayConvertToSrcg(Srcg srcg, String algorithm)
    throws ParseException {
    switch (algorithm) {
    case "srcg-earley":
      return getSrcgForEarley(srcg);
    case "srcg-cyk":
      return getSrcgForCyk(srcg);
    case "srcg-cyk-extended":
      return getSrcgForCykExtended(srcg);
    default:
      System.out.println(
        "I did not understand. Please check the spelling of your parsing algorithm.");
      return null;
    }
  }

  private Srcg getSrcgForCykExtended(Srcg srcg) throws ParseException {
    if (!srcg.isBinarized() || srcg.hasEpsilonProductions()) {
      if (please) {
        return srcg.getBinarizedSrcg().getSrcgWithoutEmptyProductions();
      } else {
        System.out.println(
          "sRCG must be binarized and not contain empty productions to apply extended CYK parsing");
        return null;
      }
    } else {
      return srcg;
    }
  }

  private Srcg getSrcgForCyk(Srcg srcg) {
    if (!srcg.isBinarized() || srcg.hasChainRules()
      || srcg.hasEpsilonProductions()) {
      if (please) {
        System.out.println("Not implemented yet.");
        return null;
        // TODO
        // return srcg.getBinarizedSrcg().getSrcgWithoutChainRules();
      } else {
        System.out.println(
          "sRCG must be binarized not contain chain rules and not contain empty productions to apply CYK parsing");
        return null;
      }
    } else {
      return srcg;
    }
  }

  private Srcg getSrcgForEarley(Srcg srcg) throws ParseException {
    if (!srcg.isOrdered() || srcg.hasEpsilonProductions()) {
      if (please) {
        return srcg.getOrderedSrcg().getSrcgWithoutEmptyProductions();
      } else {
        System.out.println("sRCG must be ordered and not contain epsilon productions for this Earley algorithm");
        return null;
      }
    } else {
      return srcg;
    }
  }

  Tag checkAndMayConvertToTag(Tag tag, String algorithm) {
    switch (algorithm) {
    case "tag-cyk":
      if (!tag.isBinarized()) {
        if (please) {
          System.out.println("Not implemented yet.");
          return null;
          // TODO
          // return tag.getBinarizedTag();
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

  Pcfg checkAndMayConvertToPcfg(Pcfg pcfg, String algorithm) {
    switch (algorithm) {
    case "pcfg-astar":
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

  Tag checkAndMayConvertToTag(Pcfg pcfg, String algorithm)
    throws ParseException {
    Cfg cfg = new Cfg(pcfg);
    return checkAndMayConvertToTag(cfg, algorithm);
  }

  Srcg checkAndMayConvertToSrcg(Pcfg pcfg, String algorithm)
    throws ParseException {
    Cfg cfg = new Cfg(pcfg);
    return checkAndMayConvertToSrcg(cfg, algorithm);
  }

  Cfg checkAndMayConvertToCfg(Pcfg pcfg, String algorithm) {
    Cfg cfg = new Cfg(pcfg);
    return checkAndMayConvertToCfg(cfg, algorithm);
  }
}

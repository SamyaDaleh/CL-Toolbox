package chartparsing.converter;

import java.text.ParseException;

import common.cfg.Cfg;
import common.cfg.Pcfg;
import common.lcfrs.Srcg;
import common.tag.Tag;

public class GrammarToGrammarConverter {
  private boolean please;

  public GrammarToGrammarConverter(boolean please) {
    this.please = please;
  }

  public Cfg checkAndMayConvertToCfg(Cfg cfg, String algorithm) {
    switch (algorithm) {
    case "cfg-topdown":
      return getCfgForTopdown(cfg);
    case "cfg-shiftreduce":
      return getCfgForShiftreduce(cfg);
    case "cfg-earley":
      return cfg;
    case "cfg-leftcorner":
      return getCfgForLeftcorner(cfg);
    case "cfg-cyk":
      return getCfgForCyk(cfg);
    case "cfg-cyk-extended":
      return getCfgForCykExtended(cfg);
    default:
      System.out.println(
        "I did not understand. Please check the spelling of your parsing algorithm.");
      return null;
    }
  }

  private Cfg getCfgForCykExtended(Cfg cfg) {
    if (!cfg.isInCanonicalTwoForm()) {
      if (please) {
        return cfg.removeEmptyProductions().removeNonGeneratingSymbols()
          .removeNonReachableSymbols().binarize().replaceTerminals();
      } else {
        System.out.println(
          "CFG must be in Canonical 2 Form for extended CYK parsing.");
        return null;
      }
    } else {
      return cfg;
    }
  }

  private Cfg getCfgForCyk(Cfg cfg) {
    if (!cfg.isInChomskyNormalForm()) {
      if (please) {
        return cfg.removeEmptyProductions().removeNonGeneratingSymbols()
          .removeNonReachableSymbols().binarize().replaceTerminals()
          .removeChainRules();
      } else {
        System.out
          .println("CFG must be in Chomsky Normal Form for CYK parsing.");
        return null;
      }
    } else {
      return cfg;
    }
  }

  private Cfg getCfgForLeftcorner(Cfg cfg) {
    if (cfg.hasEpsilonProductions() || cfg.hasDirectLeftRecursion()) {
      if (please) {
        return cfg.removeEmptyProductions().removeLeftRecursion()
          .removeEmptyProductions().removeNonGeneratingSymbols()
          .removeNonReachableSymbols();
      } else {
        System.out.println(
          "CFG must not contain empty productions or left recursion for left corner parsing.");
        return null;
      }
    } else {
      return cfg;
    }
  }

  private Cfg getCfgForShiftreduce(Cfg cfg) {
    if (cfg.hasEpsilonProductions()) {
      if (please) {
        return cfg.removeEmptyProductions().removeNonGeneratingSymbols()
          .removeNonReachableSymbols();
      } else {
        System.out.println(
          "CFG must not contain empty productions for ShiftReduce parsing.");
        return null;
      }
    } else {
      return cfg;
    }
  }

  private Cfg getCfgForTopdown(Cfg cfg) {
    if (cfg.hasEpsilonProductions()) {
      if (please) {
        return cfg.removeEmptyProductions().removeNonGeneratingSymbols()
          .removeNonReachableSymbols();
      } else {
        System.out.println(
          "CFG must not contain empty productions for TopDown parsing.");
        return null;
      }
    } else {
      return cfg;
    }
  }

  public Tag checkAndMayConvertToTag(Cfg cfg, String algorithm)
    throws ParseException {
    switch (algorithm) {
    case "tag-cyk":
      if (!cfg.isBinarized()) {
        if (please) {
          return new Tag(cfg.binarize());
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
    default:
      System.out.println(
        "I did not understand. Please check the spelling of your parsing algorithm.");
      return null;
    }
  }

  public Pcfg checkAndMayConvertToPcfg(Cfg cfg, String algorithm) {
    switch (algorithm) {
    case "pcfg-astar":
      if (!cfg.isInChomskyNormalForm()) {
        if (please) {
          return new Pcfg(cfg.removeEmptyProductions()
            .removeNonGeneratingSymbols().removeNonReachableSymbols().binarize()
            .replaceTerminals().removeChainRules());
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

  public Srcg checkAndMayConvertToSrcg(Cfg cfg, String algorithm)
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
        return new Srcg(cfg.binarize().replaceTerminals());
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
        return new Srcg(cfg.binarize().removeChainRules().replaceTerminals());
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
        return new Srcg(cfg.binarize());
      } else {
        System.out.println(
          "CFG must be binarized to convert it into a sRCG where Earley parsing is possible.");
        return null;
      }
    } else {
      return new Srcg(cfg);
    }
  }

  public Srcg checkAndMayConvertToSrcg(Srcg srcg, String algorithm) {
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

  private Srcg getSrcgForCykExtended(Srcg srcg) {
    if (!srcg.isBinarized() || srcg.hasEpsilonProductions()) {
      if (please) {
        System.out.println("Not implemented yet.");
        return null;
        // TODO
        // Srcg srcg = new Srcg(cfg.binarize());
        // return LcfrsToDeductionRulesConverter.LcfrsToEarleyRules(srcg, w);
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
        // Srcg srcg = new Srcg(cfg.binarize());
        // return LcfrsToDeductionRulesConverter.LcfrsToEarleyRules(srcg, w);
      } else {
        System.out.println(
          "sRCG must be binarized not contain chain rules and not contain empty productions to apply CYK parsing");
        return null;
      }
    } else {
      return srcg;
    }
  }

  private Srcg getSrcgForEarley(Srcg srcg) {
    if (!srcg.isBinarized()) {
      if (please) {
        System.out.println("Not implemented yet.");
        return null;
        // TODO
        // Srcg srcg = new Srcg(cfg.binarize());
        // return LcfrsToDeductionRulesConverter.LcfrsToEarleyRules(srcg, w);
      } else {
        System.out.println("sRCG must be binarized to apply Earley parsing");
        return null;
      }
    } else {
      return srcg;
    }
  }

  public Tag checkAndMayConvertToTag(Tag tag, String algorithm) {
    switch (algorithm) {
    case "tag-cyk":
      if (!tag.isBinarized()) {
        if (please) {
          System.out.println("Not implemented yet.");
          return null;
          // TODO
          // Tag tag = new Tag(cfg.binarize());
          // return TagToDeductionRulesConverter.TagToCykRules(tag, w);
        } else {
          System.out.println("TAG must be binarized to apply CYK parsing.");
          return null;
        }
      } else {
        return tag;
      }
    case "tag-earley":
      return tag;
    default:
      System.out.println(
        "I did not understand. Please check the spelling of your parsing algorithm.");
      return null;
    }
  }

  public Pcfg checkAndMayConvertToPcfg(Pcfg pcfg, String algorithm) {
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

  public Tag checkAndMayConvertToTag(Pcfg pcfg, String algorithm)
    throws ParseException {
    Cfg cfg = new Cfg(pcfg);
    return checkAndMayConvertToTag(cfg, algorithm);

  }

  public Srcg checkAndMayConvertToSrcg(Pcfg pcfg, String algorithm)
    throws ParseException {
    Cfg cfg = new Cfg(pcfg);
    return checkAndMayConvertToSrcg(cfg, algorithm);
  }

  public Cfg checkAndMayConvertToCfg(Pcfg pcfg, String algorithm) {
    Cfg cfg = new Cfg(pcfg);
    return checkAndMayConvertToCfg(cfg, algorithm);
  }
}
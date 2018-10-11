package com.github.samyadaleh.cltoolbox.cli;

import com.github.samyadaleh.cltoolbox.common.AbstractNTSGrammar;
import com.github.samyadaleh.cltoolbox.common.cfg.Cfg;
import com.github.samyadaleh.cltoolbox.common.cfg.Pcfg;
import com.github.samyadaleh.cltoolbox.common.lcfrs.Srcg;
import com.github.samyadaleh.cltoolbox.common.tag.Tag;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.ParseException;

class GrammarToGrammarConverter {
  private static final Logger log = LogManager.getLogger();

  static Cfg checkAndMayConvertToCfg(Cfg cfg, String algorithm, boolean please)
      throws ParseException {
    switch (algorithm) {
    case "cfg-topdown":
      return getCfgForTopDown(cfg, please);
    case "cfg-shiftreduce":
      return getCfgForShiftReduce(cfg, please);
    case "cfg-earley":
    case "cfg-earley-passive":
    case "cfg-leftcorner-chart":
      return cfg;
    case "cfg-leftcorner":
      return getCfgForLeftCorner(cfg, please);
    case "cfg-cyk":
      return getCfgForCyk(cfg, please);
    case "cfg-cyk-extended":
      return getCfgForCykExtended(cfg, please);
    case "cfg-cyk-general":
      return cfg;
    case "cfg-unger":
      return getCfgForLeftCorner(cfg, please);
    default:
      if (algorithm.matches("cfg-lr-\\d+")) {
        return cfg;
      }
      log.info(
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
          log.info(
              "CFG must be in Chomsky Normal Form to convert it into a PCFG where "
                  + " is possible.");
          return null;
        }
      } else {
        return new Pcfg(cfg);
      }
    default:
      log.info(
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
          log.info("PCFG can't be converted.");
          return null;
        } else {
          log.info("PCFG must be in Chomsky Normal Form to apply A* parsing.");
          return null;
        }
      } else {
        return pcfg;
      }
    default:
      log.info(
          "I did not understand. Please check the spelling of your parsing algorithm.");
      return null;
    }
  }

  static Srcg checkAndMayConvertToSrcg(AbstractNTSGrammar gra, String algorithm,
      boolean please) {
    switch (algorithm) {
    case "srcg-earley":
      if (gra instanceof Cfg) {
        return getSrcgForEarley((Cfg) gra, please);
      }
      if (gra instanceof Srcg) {
        return getSrcgForEarley((Srcg) gra, please);
      }
    case "srcg-cyk-extended":
      if (gra instanceof Cfg) {
        return getSrcgForCykExtended((Cfg) gra, please);
      }
      if (gra instanceof Srcg) {
        return getSrcgForCykExtended((Srcg) gra, please);
      }
    case "srcg-cyk-general":
      if (gra instanceof Cfg) {
        return getSrcgForCykGeneral((Cfg) gra, please);
      }
      if (gra instanceof Srcg) {
        return getSrcgForCykGeneral((Srcg) gra, please);
      }
    default:
      throw new IllegalArgumentException(
          "I did not understand. Please check the spelling of your parsing algorithm.");
    }
  }

  static Srcg checkAndMayConvertToSrcg(Pcfg pcfg, String algorithm,
      boolean please) {
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
          throw new IllegalArgumentException(
              "TAG must be binarized to apply CYK parsing.");
        }
      } else {
        return tag;
      }
    case "tag-cyk-general":
      return tag;
    case "tag-earley":
      return tag;
    case "tag-earley-prefixvalid":
      return tag;
    default:
      throw new IllegalArgumentException(
          "I did not understand. Please check the spelling of your parsing algorithm.");
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
          log.info(
              "CFG must be binarized to convert it into a TAG where CYK parsing is possible.");
          return null;
        }
      } else {
        return new Tag(cfg);
      }
    case "tag-cyk-general":
      return new Tag(cfg);
    case "tag-earley":
      return new Tag(cfg);
    case "tag-earley-prefixvalid":
      return new Tag(cfg);
    default:
      log.info(
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
        System.out.println(
            "CFG must be in Canonical 2 Form for extended CYK parsing.");
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
        log.info(
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
        log.info(
            "CFG must not contain empty productions for ShiftReduce parsing.");
        return null;
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
            .getCfgWithoutNonReachableSymbols().getCfgWithoutLeftRecursion();
      } else {
        log.info("CFG must not contain left recursion for TopDown parsing.");
        return null;
      }
    } else {
      return cfg;
    }
  }

  private static Srcg getSrcgForCykExtended(Srcg srcg, boolean please) {
    try {
      if (!srcg.isBinarized() || srcg.hasEpsilonProductions()) {
        if (please) {
          return srcg.getBinarizedSrcg().getSrcgWithoutEmptyProductions()
              .getSrcgWithoutUselessRules();
        } else {
          log.info(
              "sRCG must be binarized and not contain empty productions to apply extended CYK parsing");
          return null;
        }
      } else {
        return srcg;
      }

    } catch (ParseException e) {
      throw new RuntimeException(e);
    }
  }

  private static Srcg getSrcgForCykExtended(Cfg cfg, boolean please) {
    try {
      if (!cfg.isBinarized() || cfg.hasMixedRhs()) {
        if (please) {
          return new Srcg(cfg.getBinarizedCfg()
              .getCfgWithEitherOneTerminalOrNonterminalsOnRhs())
              .getSrcgWithoutUselessRules();
        } else {
          log.info(
              "CFG must be binarized and not contain mixed rhs sides to convert it into a sRCG where extended CYK parsing is possible.");
          return null;
        }
      } else {
        return new Srcg(cfg);
      }
    } catch (ParseException e) {
      throw new RuntimeException(e);
    }
  }

  private static Srcg getSrcgForCykGeneral(Srcg srcg, boolean please) {
    try {
      if (srcg.hasEpsilonProductions()) {
        if (please) {
          return srcg.getSrcgWithoutEmptyProductions()
              .getSrcgWithoutUselessRules();
        } else {
          log.info(
              "sRCG must not contain empty productions to apply general CYK parsing");
          return null;
        }
      } else {
        return srcg;
      }
    } catch (ParseException e) {
      throw new RuntimeException(e);
    }
  }

  private static Srcg getSrcgForCykGeneral(Cfg cfg, boolean please) {
    try {
      if (cfg.hasEpsilonProductions()) {
        if (please) {
          return new Srcg(cfg.getCfgWithoutEmptyProductions())
              .getSrcgWithoutUselessRules();
        } else {
          log.info(
              "CFG must not contain empty productions to be converted into a sRCG where general CYK parsing is possible.");
          return null;
        }
      } else {
        return new Srcg(cfg);
      }

    } catch (ParseException e) {
      throw new RuntimeException(e);
    }
  }

  private static Srcg getSrcgForEarley(Srcg srcg, boolean please) {
    try {
      if (!srcg.isOrdered() || srcg.hasEpsilonProductions()) {
        if (please) {
          return srcg.getOrderedSrcg().getSrcgWithoutEmptyProductions()
              .getSrcgWithoutUselessRules();
        } else {
          log.info(
              "sRCG must be ordered and not contain epsilon productions for this Earley algorithm");
          return null;
        }
      } else {
        return srcg;
      }
    } catch (ParseException e) {
      throw new RuntimeException(e);
    }
  }

  private static Srcg getSrcgForEarley(Cfg cfg, boolean please) {
    try {
      if (!cfg.isBinarized()) {
        if (please) {
          return new Srcg(cfg.getBinarizedCfg()).getSrcgWithoutUselessRules();
        } else {
          log.info(
              "CFG must be binarized to convert it into a sRCG where Earley parsing is possible.");
          return null;
        }
      } else {
        return new Srcg(cfg);
      }
    } catch (ParseException e) {
      throw new RuntimeException(e);
    }
  }
}

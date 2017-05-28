package chartparsing;

import java.text.ParseException;

import common.cfg.Cfg;
import common.lcfrs.Srcg;
import common.tag.Tag;

public class GrammarToDeductionRulesConverter {
  private boolean please = false;

  public void setPlease(boolean please) {
    this.please = please;
  }

  public ParsingSchema Convert(Cfg cfg, String w, String algorithm)
    throws ParseException {
    switch (algorithm) {
    case "cfg-topdown":
      if (cfg.hasEpsilonProductions()) {
        if (please) {
          return CfgToDeductionRulesConverter
            .CfgToTopDownRules(cfg.removeEmptyProductions()
              .removeNonGeneratingSymbols().removeNonReachableSymbols(), w);
        } else {
          System.out.println(
            "CFG must not contain empty productions for TopDown parsing.");
          return null;
        }
      } else {
        return CfgToDeductionRulesConverter.CfgToTopDownRules(cfg, w);
      }
    case "cfg-shiftreduce":
      if (cfg.hasEpsilonProductions()) {
        if (please) {
          return CfgToDeductionRulesConverter
            .CfgToShiftReduceRules(cfg.removeEmptyProductions()
              .removeNonGeneratingSymbols().removeNonReachableSymbols(), w);
        } else {
          System.out.println(
            "CFG must not contain empty productions for ShiftReduce parsing.");
          return null;
        }
      } else {
        return CfgToDeductionRulesConverter.CfgToShiftReduceRules(cfg, w);
      }
    case "cfg-earley":
      return CfgToDeductionRulesConverter.CfgToEarleyRules(cfg, w);
    case "cfg-leftcorner":
      return CfgToDeductionRulesConverter.CfgToLeftCornerRules(cfg, w);
    case "cfg-cyk":
      if (!cfg.isInChomskyNormalForm()) {
        if (please) {
          return CfgToDeductionRulesConverter
            .CfgToCykRules(cfg.removeEmptyProductions()
              .removeNonGeneratingSymbols().removeNonReachableSymbols()
              .binarize().replaceTerminals().removeChainRules(), w);
        } else {
          System.out
            .println("CFG must be in Chomsky Normal Form for CYK parsing.");
          return null;
        }
      } else {
        return CfgToDeductionRulesConverter.CfgToCykRules(cfg, w);
      }
    case "cfg-cyk-extended":
      if (!cfg.isInCanonicalTwoForm()) {
        if (please) {
          return CfgToDeductionRulesConverter.CfgToCykRules(
            cfg.removeEmptyProductions().removeNonGeneratingSymbols()
              .removeNonReachableSymbols().binarize().replaceTerminals(),
            w);
        } else {
          System.out.println(
            "CFG must be in Canonical 2 Form for extended CYK parsing.");
          return null;
        }
      } else {
        return CfgToDeductionRulesConverter.CfgToCykExtendedRules(cfg, w);
      }
    case "tag-cyk":
      if (!cfg.isBinarized()) {
        if (please) {
          Tag tag = new Tag(cfg.binarize());
          return TagToDeductionRulesConverter.TagToCykRules(tag, w);
        } else {
          System.out.println(
            "CFG must be binarized to convert it into a TAG where CYK parsing is possible.");
          return null;
        }
      } else {
        Tag tag = new Tag(cfg);
        return TagToDeductionRulesConverter.TagToCykRules(tag, w);
      }
    case "tag-earley":
      Tag tag = new Tag(cfg);
      return TagToDeductionRulesConverter.TagToEarleyRules(tag, w);
    case "srcg-earley":
      if (!cfg.isBinarized()) {
        if (please) {
          Srcg srcg = new Srcg(cfg.binarize());
          return LcfrsToDeductionRulesConverter.LcfrsToEarleyRules(srcg, w);
        } else {
          System.out.println(
            "CFG must be binarized to convert it into a sRCG where Earley parsing is possible.");
          return null;
        }
      } else {
        Srcg srcg = new Srcg(cfg);
        return LcfrsToDeductionRulesConverter.LcfrsToEarleyRules(srcg, w);
      }
    case "srcg-cyk":
      if (!cfg.isBinarized() || cfg.hasChainRules() || cfg.hasMixedRhs()) {
        if (please) {
          Srcg srcg = new Srcg(cfg.binarize().removeChainRules().replaceTerminals());
          return LcfrsToDeductionRulesConverter.LcfrsToCykRules(srcg, w);
        } else {
          System.out.println(
            "CFG must be binarized, not contain chain rules and not contain rules with mixed rhs sides to convert it into a sRCG where CYK parsing is possible.");
          return null;
        }
      } else {
        Srcg srcg = new Srcg(cfg);
        return LcfrsToDeductionRulesConverter.LcfrsToCykRules(srcg, w);
      }
    case "srcg-cyk-extended":
      if (!cfg.isBinarized() || cfg.hasMixedRhs()) {
        if (please) {
          Srcg srcg = new Srcg(cfg.binarize().replaceTerminals());
          return LcfrsToDeductionRulesConverter.LcfrsToCykExtendedRules(srcg,
            w);
        } else {
          System.out.println(
            "CFG must be binarized and not contain mixed rhs sides to convert it into a sRCG where extended CYK parsing is possible.");
          return null;
        }
      } else {
        Srcg srcg = new Srcg(cfg);
        return LcfrsToDeductionRulesConverter.LcfrsToCykExtendedRules(srcg, w);
      }

    default:
      System.out.println(
        "I did not understand. Please check the spelling of your parsing algorithm.");
      return null;
    }
  }

  public ParsingSchema Convert(Tag tag, String w, String algorithm) {
    switch (algorithm) {
    case "cfg-topdown":
      System.out
        .println("I can only parse with equal or more expressive algorithms.");
      return null;
    case "cfg-shiftreduce":
      System.out
        .println("I can only parse with equal or more expressive algorithms.");
      return null;
    case "cfg-earley":
      System.out
        .println("I can only parse with equal or more expressive algorithms.");
      return null;
    case "cfg-leftcorner":
      System.out
        .println("I can only parse with equal or more expressive algorithms.");
      return null;
    case "cfg-cyk":
      System.out
        .println("I can only parse with equal or more expressive algorithms.");
      return null;
    case "cfg-cyk-extended":
      System.out
        .println("I can only parse with equal or more expressive algorithms.");
      return null;
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
        return TagToDeductionRulesConverter.TagToCykRules(tag, w);
      }
    case "tag-earley":
      return TagToDeductionRulesConverter.TagToEarleyRules(tag, w);
    case "srcg-earley":
      System.out
        .println("I can't convert a tree language into a string language.");
      return null;
    case "srcg-cyk":
      System.out
        .println("I can't convert a tree language into a string language.");
      return null;
    case "srcg-cyk-extended":
      System.out
        .println("I can't convert a tree language into a string language.");
      return null;

    default:
      System.out.println(
        "I did not understand. Please check the spelling of your parsing algorithm.");
      return null;
    }
  }

  public ParsingSchema Convert(Srcg srcg, String w, String algorithm) {
    switch (algorithm) {
    case "cfg-topdown":
      System.out
        .println("I can only parse with equal or more expressive algorithms.");
      return null;
    case "cfg-shiftreduce":
      System.out
        .println("I can only parse with equal or more expressive algorithms.");
      return null;
    case "cfg-earley":
      System.out
        .println("I can only parse with equal or more expressive algorithms.");
      return null;
    case "cfg-leftcorner":
      System.out
        .println("I can only parse with equal or more expressive algorithms.");
      return null;
    case "cfg-cyk":
      System.out
        .println("I can only parse with equal or more expressive algorithms.");
      return null;
    case "cfg-cyk-extended":
      System.out
        .println("I can only parse with equal or more expressive algorithms.");
      return null;
    case "tag-cyk":
      System.out
        .println("I can only parse with equal or more expressive algorithms.");
      return null;
    case "tag-earley":
      System.out
        .println("I can only parse with equal or more expressive algorithms.");
      return null;
    case "srcg-earley":
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
        return LcfrsToDeductionRulesConverter.LcfrsToEarleyRules(srcg, w);
      }
    case "srcg-cyk":
      if (!srcg.isBinarized() || srcg.hasChainRules() || srcg.hasEpsilonProductions()) {
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
        return LcfrsToDeductionRulesConverter.LcfrsToCykRules(srcg, w);
      }
    case "srcg-cyk-extended":
      if (!srcg.isBinarized() || srcg.hasEpsilonProductions()) {
        if (please) {
          System.out.println("Not implemented yet.");
          return null;
          // TODO
          // Srcg srcg = new Srcg(cfg.binarize());
          // return LcfrsToDeductionRulesConverter.LcfrsToEarleyRules(srcg, w);
        } else {
          System.out
            .println("sRCG must be binarized and not contain empty productions to apply extended CYK parsing");
          return null;
        }
      } else {
        return LcfrsToDeductionRulesConverter.LcfrsToCykExtendedRules(srcg, w);
      }

    default:
      System.out.println(
        "I did not understand. Please check the spelling of your parsing algorithm.");
      return null;
    }
  }
}

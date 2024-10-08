package com.github.samyadaleh.cltoolbox.common.parser;

import com.github.samyadaleh.cltoolbox.common.cfg.Cfg;
import com.github.samyadaleh.cltoolbox.common.cfg.CfgProductionRule;

import java.io.BufferedReader;
import java.io.Reader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class CfgParser {

  /**
   * Hand CFG reader to parse from, can come from string or multiline file.
   */
  public static Cfg parseCfgReader(Reader reader)
      throws ParseException {
    BufferedReader in = new BufferedReader(reader);
    Cfg cfg = new Cfg(in);
    List<Exception> errors = checkForGrammarProblems(cfg);
    if (!errors.isEmpty()) {
      GrammarParserUtils.printErrors(errors);
      throw (ParseException) errors.get(0);
    }
    return cfg;
  }

  private static List<Exception> checkForGrammarProblems(Cfg cfg) {
    List<Exception> errors = new ArrayList<>();
    if (cfg.getNonterminals() == null) {
      errors.add(new ParseException(
          "Nonterminals are null, check grammar format.", 0));
      return errors;
    }
    if (cfg.getTerminals() == null) {
      errors.add(new ParseException(
          "Terminals are null, check grammar format.", 0));
      return errors;
    }
    if (cfg.getProductionRules() == null) {
      errors.add(new ParseException(
          "Production rules are null, check grammar format.", 0));
      return errors;
    }
    for (String nt : cfg.getNonterminals()) {
      for (String t : cfg.getTerminals()) {
        if (t.equals(nt)) {
          errors.add(new ParseException(
              t + " declared in both terminals and nonterminals.", 0));
        }
      }
    }
    for (CfgProductionRule rule : cfg.getProductionRules()) {
      if (!cfg.getNonterminals().contains(rule.getLhs())) {
        errors.add(new ParseException(
            "LHS " + rule.getLhs() + " in rule " + rule
                + " not declared as nonterminal.", 0));
      }
      for (String rhsSym : rule.getRhs()) {
        if (!cfg.getTerminals().contains(rhsSym)
                && !cfg.getNonterminals().contains(rhsSym)
                && !rhsSym.isEmpty()) {
          errors.add(new ParseException(rhsSym + " in rule " + rule
              + " is neither declared as terminal nor as nonterminal.", 0));
        }
      }
    }
    if (cfg.getNonterminals() == null) {
      errors.add(new ParseException("No nonterminals declared in grammar.", 0));
    }
    if (cfg.getTerminals() == null) {
      errors.add(new ParseException("No terminals declared in grammar.", 0));
    }
    if (cfg.getStartSymbol() == null) {
      errors.add(new ParseException("No start symbol declared in grammar.", 0));
    }
    if (cfg.getProductionRules() == null) {
      errors.add(
          new ParseException("No production rules declared in grammar.", 0));
    }
    if (!cfg.nonterminalsContain(cfg.getStartSymbol())) {
      errors.add(new ParseException(
          "The start symbol is not one of the nonterminals.", 0));
    }
    for (String nt : cfg.getNonterminals()) {
      if (nt.contains("_")) {
        errors.add(new ParseException(
            "Grammar must not contain _ in any nonterminal.", 0));
      }
    }
    for (String t : cfg.getNonterminals()) {
      if (t.contains("_")) {
        errors.add(new ParseException(
            "Grammar must not contain _ in any terminal.", 0));
      }
    }
    return errors;
  }
}

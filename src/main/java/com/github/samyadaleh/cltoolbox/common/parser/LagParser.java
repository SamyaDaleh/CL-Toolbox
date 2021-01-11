package com.github.samyadaleh.cltoolbox.common.parser;

import com.github.samyadaleh.cltoolbox.common.cfg.Cfg;
import com.github.samyadaleh.cltoolbox.common.cfg.CfgProductionRule;
import com.github.samyadaleh.cltoolbox.common.lag.Lag;

import java.io.BufferedReader;
import java.io.Reader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import static com.github.samyadaleh.cltoolbox.common.ArrayUtils.contains;

public class LagParser {
  private static List<Exception> errors;

  /**
   * Hand CFG reader to parse from, can come from string or multiline file.
   */
  public static Lag parseCfgReader(Reader reader)
      throws ParseException {
    errors = new ArrayList<>();
    BufferedReader in = new BufferedReader(reader);
    Lag lag = new Lag(in);
    //checkForGrammarProblems(lag);
    if (GrammarParserUtils.printErrors(errors))
      throw (ParseException) errors.get(0);
    return lag;
  }

  private static void checkForGrammarProblems(Cfg cfg) {
    if (cfg.getNonterminals() == null) {
      errors.add(new ParseException(
          "Nonterminals are null, check grammar format.", 0));
      return;
    }
    if (cfg.getTerminals() == null) {
      errors.add(new ParseException(
          "Terminals are null, check grammar format.", 0));
      return;
    }
    if (cfg.getProductionRules() == null) {
      errors.add(new ParseException(
          "Production rules are null, check grammar format.", 0));
      return;
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
      if (!contains(cfg.getNonterminals(), rule.getLhs())) {
        errors.add(new ParseException(
            "LHS " + rule.getLhs() + " in rule " + rule.toString()
                + " not declared as nonterminal.", 0));
      }
      for (String rhsSym : rule.getRhs()) {
        if (!contains(cfg.getTerminals(), rhsSym) && !contains(
            cfg.getNonterminals(), rhsSym) && !rhsSym.equals("")) {
          errors.add(new ParseException(rhsSym + " in rule " + rule.toString()
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
  }
}

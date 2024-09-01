package com.github.samyadaleh.cltoolbox.common.parser;

import com.github.samyadaleh.cltoolbox.common.cfg.Pcfg;
import com.github.samyadaleh.cltoolbox.common.cfg.PcfgProductionRule;

import java.io.BufferedReader;
import java.io.Reader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class PcfgParser {

  /**
   * Parses a PCFG from a file and returns it as Pcfg.
   */
  public static Pcfg parsePcfgReader(Reader reader)
      throws ParseException {
    BufferedReader in = new BufferedReader(reader);
    Pcfg pcfg = new Pcfg(in);
    List<Exception> errors = checkForGrammarProblems(pcfg);
    if (!errors.isEmpty()) {
      GrammarParserUtils.printErrors(errors);
      throw (ParseException) errors.get(0);
    }
    return pcfg;
  }

  private static List<Exception> checkForGrammarProblems(Pcfg pcfg) {
    List<Exception> errors = new ArrayList<>();
    if (pcfg.getNonterminals() == null) {
      errors.add(new ParseException(
          "Nonterminals are null, check grammar format.", 0));
      return errors;
    }
    if (pcfg.getTerminals() == null) {
      errors.add(new ParseException(
          "Terminals are null, check grammar format.", 0));
      return errors;
    }
    if (pcfg.getProductionRules() == null) {
      errors.add(new ParseException(
          "Production rules are null, check grammar format.", 0));
      return errors;
    }
    for (String nt : pcfg.getNonterminals()) {
      for (String t : pcfg.getTerminals()) {
        if (t.equals(nt)) {
          errors.add(new ParseException(
              t + " declared in both terminals and nonterminals.", 0));
        }
      }
    }
    for (PcfgProductionRule rule : pcfg.getProductionRules()) {
      if (!pcfg.getNonterminals().contains(rule.getLhs())) {
        errors.add(new ParseException(
            "LHS " + rule.getLhs() + " in rule " + rule
                + " not declared as nonterminal.", 0));
      }
      for (String rhsSym : rule.getRhs()) {
        if (!pcfg.getTerminals().contains(rhsSym)
                && !pcfg.getNonterminals().contains(rhsSym)
                && !rhsSym.isEmpty()) {
          errors.add(new ParseException(rhsSym + " in rule " + rule
              + " is neither declared as terminal nor as nonterminal.", 0));
        }
      }
    }
    if (pcfg.getNonterminals() == null) {
      errors.add(new ParseException("No nonterminals declared in grammar.", 0));
    }
    if (pcfg.getTerminals() == null) {
      errors.add(new ParseException("No terminals declared in grammar.", 0));
    }
    if (pcfg.getStartSymbol() == null) {
      errors.add(new ParseException("No start symbol declared in grammar.", 0));
    }
    if (pcfg.getProductionRules() == null) {
      errors.add(
          new ParseException("No production rules declared in grammar.", 0));
    }
    if (!pcfg.nonterminalsContain(pcfg.getStartSymbol())) {
      errors.add(new ParseException(
          "The start symbol is not one of the nonterminals.", 0));
    }
    return errors;
  }
}

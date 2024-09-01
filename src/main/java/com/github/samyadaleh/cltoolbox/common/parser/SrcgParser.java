package com.github.samyadaleh.cltoolbox.common.parser;

import com.github.samyadaleh.cltoolbox.common.lcfrs.Clause;
import com.github.samyadaleh.cltoolbox.common.lcfrs.Predicate;
import com.github.samyadaleh.cltoolbox.common.lcfrs.Srcg;

import java.io.BufferedReader;
import java.io.Reader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class SrcgParser {

  /**
   * Parses a sRCG from a file and returns it as Srcg.
   */
  public static Srcg parseSrcgReader(Reader reader)
      throws ParseException {
    BufferedReader in = new BufferedReader(reader);
    Srcg srcg = new Srcg(in);
    List<Exception> errors = checkForGrammarProblems(srcg);
    if (!errors.isEmpty()) {
      GrammarParserUtils.printErrors(errors);
      throw (ParseException) errors.get(0);
    }
    return srcg;
  }

  private static List<Exception> checkForGrammarProblems(Srcg srcg) {
    List<Exception> errors = new ArrayList<>();
    if (srcg.getNonterminals() == null) {
      errors.add(new ParseException(
          "Nonterminals are null, check grammar format.", 0));
      return errors;
    }
    if (srcg.getTerminals() == null) {
      errors.add(new ParseException(
          "Terminals are null, check grammar format.", 0));
      return errors;
    }
    if (srcg.getVariables() == null) {
      errors.add(new ParseException(
          "Variables are null, check grammar format.", 0));
      return errors;
    }
    if (srcg.getClauses() == null) {
      errors.add(new ParseException(
          "Clauses are null, check grammar format.", 0));
      return errors;
    }
    for (String nt : srcg.getVariables()) {
      for (String t : srcg.getTerminals()) {
        if (t.equals(nt)) {
          errors.add(new ParseException(
              t + " declared in both terminals and variables.", 0));
        }
      }
    }
    for (Clause clause : srcg.getClauses()) {
      Predicate lhsPred = clause.getLhs();
      checkPredicateFormat(srcg, clause, lhsPred, errors);
      for (Predicate rhsPred : clause.getRhs()) {
        checkPredicateFormat(srcg, clause, rhsPred, errors);
      }
    }
    if (srcg.getNonterminals() == null) {
      errors.add(new ParseException("No nonterminals declared in grammar.", 0));
    }
    if (srcg.getStartSymbol() == null) {
      errors.add(new ParseException("No start symbol declared in grammar.", 0));
    }
    if (srcg.getClauses() == null) {
      errors.add(new ParseException("No clauses declared in grammar.", 0));
    }
    if (!srcg.nonterminalsContain(srcg.getStartSymbol())) {
      errors.add(new ParseException(
          "The start symbol is not one of the nonterminals.", 0));
    }
    return errors;
  }

  private static void checkPredicateFormat(Srcg srcg, Clause clause,
      Predicate pred, List<Exception> errors) {
    if (!srcg.getNonterminals().contains(pred.getNonterminal())) {
      errors.add(new ParseException(
          "Nonterminal " + pred.getNonterminal() + " in clause " + clause
              .toString() + " not declared as nonterminal.", 0));
    }
    for (String symbol : pred.getSymbolsAsPlainArray()) {
      if (!symbol.isEmpty() && !srcg.getTerminals().contains(symbol)
              && !srcg.getVariables().contains(symbol)) {
        errors.add(new ParseException(
            "Symbol " + symbol + " in clause " + clause.toString()
                + " is neither declared terminal nor variable.", 0));
      }
    }
  }
}

package com.github.samyadaleh.cltoolbox.common.parser;

import com.github.samyadaleh.cltoolbox.common.ArrayUtils;
import com.github.samyadaleh.cltoolbox.common.lcfrs.Clause;
import com.github.samyadaleh.cltoolbox.common.lcfrs.Predicate;
import com.github.samyadaleh.cltoolbox.common.lcfrs.Srcg;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SrcgGrammarParser {
  private static List<Exception> errors;
  private static final Logger log = LogManager.getLogger();

  /**
   * Parses a sRCG from a file and returns it as Srcg.
   */
  public static Srcg parseSrcgReader(Reader reader)
      throws IOException, ParseException {
    errors = new ArrayList<>();
    BufferedReader in = new BufferedReader(reader);
    Srcg srcg = new Srcg(in);
    checkForGrammarProblems(srcg);
    if (GrammarParserUtils.printErrors(errors))
      return null;
    return srcg;
  }

  private static void checkForGrammarProblems(Srcg srcg) {
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
      checkPredicateFormat(srcg, clause, lhsPred);
      for (Predicate rhsPred : clause.getRhs()) {
        checkPredicateFormat(srcg, clause, rhsPred);
      }
    }
    if (srcg.getNonterminals() == null) {
      errors.add(new ParseException("No nonterminals declared in grammar.", 0));
    }
    if (srcg.getTerminals() == null) {
      errors.add(new ParseException("No terminals declared in grammar.", 0));
    }
    if (srcg.getStartSymbol() == null) {
      errors.add(new ParseException("No start symbol declared in grammar.", 0));
    }
    if (srcg.getVariables() == null) {
      errors.add(new ParseException("No variables declared in grammar.", 0));
    }
    if (srcg.getClauses() == null) {
      errors.add(new ParseException("No clauses declared in grammar.", 0));
    }
  }

  private static void checkPredicateFormat(Srcg srcg, Clause clause,
      Predicate pred) {
    if (!ArrayUtils.contains(srcg.getNonterminals(), pred.getNonterminal())) {
      errors.add(new ParseException(
          "Nonterminal " + pred.getNonterminal() + " in clause " + clause
              .toString() + " not declared as nonterminal.", 0));
    }
    for (String symbol : pred.getSymbolsAsPlainArray()) {
      if (!symbol.equals("") && !ArrayUtils
          .contains(srcg.getTerminals(), symbol) && !ArrayUtils
          .contains(srcg.getVariables(), symbol)) {
        errors.add(new ParseException(
            "Symbol " + symbol + " in clause " + clause.toString()
                + " is neither declared terminal nor variable.", 0));
      }
    }
  }
}

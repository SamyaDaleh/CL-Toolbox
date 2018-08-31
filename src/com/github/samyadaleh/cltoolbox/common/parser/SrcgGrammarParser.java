package com.github.samyadaleh.cltoolbox.common.parser;

import com.github.samyadaleh.cltoolbox.common.ArrayUtils;
import com.github.samyadaleh.cltoolbox.common.lcfrs.Clause;
import com.github.samyadaleh.cltoolbox.common.lcfrs.Predicate;
import com.github.samyadaleh.cltoolbox.common.lcfrs.Srcg;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
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
  public static Srcg parseSrcgFile(String grammarFile) throws IOException {
    Srcg srcg = new Srcg();
    errors = new ArrayList<>();
    Map<String, List<String>> declarations =
        GrammarParserUtils.parseDeclarations(grammarFile, errors);
    for (Map.Entry<String, List<String>> entry : declarations.entrySet()) {
      switch (entry.getKey()) {
      case "N":
        String[] nts = entry.getValue().toArray(new String[0]);
        srcg.setNonterminals(nts);
        break;
      case "V":
        String[] vs = entry.getValue().toArray(new String[0]);
        srcg.setVariables(vs);
        break;
      case "T":
        String[] ts = entry.getValue().toArray(new String[0]);
        srcg.setTerminals(ts);
        break;
      case "S":
        if (entry.getValue().size() > 1) {
          errors.add(new ParseException("Too many start symbols declared", 0));
          continue;
        }
        srcg.setStartSymbol(entry.getValue().get(0));
        break;
      case "P":
        for (String clauseDec : entry.getValue()) {
          try {
            srcg.addClause(clauseDec);
          } catch (ParseException e) {
            errors.add(e);
          }
        }
        break;
      case "G":
        log.info("Grammar declaration detected. Nothing to do.");
        break;
      default:
        errors.add(
            new ParseException("Unknown declaration symbol: " + entry.getKey(),
                0));
      }
    }
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

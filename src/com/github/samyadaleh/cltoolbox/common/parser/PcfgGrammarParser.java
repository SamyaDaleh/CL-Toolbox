package com.github.samyadaleh.cltoolbox.common.parser;

import com.github.samyadaleh.cltoolbox.common.ArrayUtils;
import com.github.samyadaleh.cltoolbox.common.cfg.Pcfg;
import com.github.samyadaleh.cltoolbox.common.cfg.PcfgProductionRule;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PcfgGrammarParser {
  private static List<Exception> errors;
  private static final Logger log = LogManager.getLogger();

  /**
   * Parses a PCFG from a file and returns it as Pcfg.
   */
  public static Pcfg parsePcfgReader(BufferedReader grammarReader)
      throws IOException {
    Pcfg pcfg = new Pcfg();
    errors = new ArrayList<>();
    Map<String, List<String>> declarations =
        GrammarParserUtils.parseDeclarations(grammarReader, errors);
    for (Map.Entry<String, List<String>> entry : declarations.entrySet()) {
      switch (entry.getKey()) {
      case "N":
        String[] nts = entry.getValue().toArray(new String[0]);
        pcfg.setNonterminals(nts);
        break;
      case "T":
        String[] ts = entry.getValue().toArray(new String[0]);
        pcfg.setTerminals(ts);
        break;
      case "S":
        if (entry.getValue().size() > 1) {
          errors.add(new ParseException("Too many start symbols declared", 0));
          continue;
        }
        pcfg.setStartSymbol(entry.getValue().get(0));
        break;
      case "P":
        for (String rule : entry.getValue()) {
          try {
            pcfg.addProductionRule(rule);
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
    checkForGrammarProblems(pcfg);
    if (GrammarParserUtils.printErrors(errors))
      return null;
    return pcfg;
  }

  private static void checkForGrammarProblems(Pcfg pcfg) {
    for (String nt : pcfg.getNonterminals()) {
      for (String t : pcfg.getTerminals()) {
        if (t.equals(nt)) {
          errors.add(new ParseException(
              t + " declared in both terminals and nonterminals.", 0));
        }
      }
    }
    for (PcfgProductionRule rule : pcfg.getProductionRules()) {
      if (!ArrayUtils.contains(pcfg.getNonterminals(), rule.getLhs())) {
        errors.add(new ParseException(
            "LHS " + rule.getLhs() + " in rule " + rule.toString()
                + " not declared as nonterminal.", 0));
      }
      for (String rhsSym : rule.getRhs()) {
        if (!ArrayUtils.contains(pcfg.getTerminals(), rhsSym) && !ArrayUtils
            .contains(pcfg.getNonterminals(), rhsSym) && !rhsSym.equals("")) {
          errors.add(new ParseException(rhsSym + " in rule " + rule.toString()
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
  }
}

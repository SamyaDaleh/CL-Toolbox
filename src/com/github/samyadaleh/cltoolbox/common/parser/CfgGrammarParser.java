package com.github.samyadaleh.cltoolbox.common.parser;

import com.github.samyadaleh.cltoolbox.common.cfg.Cfg;
import com.github.samyadaleh.cltoolbox.common.cfg.CfgProductionRule;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.github.samyadaleh.cltoolbox.common.ArrayUtils.contains;

public class CfgGrammarParser {
  private static List<Exception> errors;
  private static final Logger log = LogManager.getLogger();

  /**
   * Parses a CFG from a file and returns it as Cfg.
   */
  public static Cfg parseCfgReader(BufferedReader grammarReader)
      throws IOException {
    Cfg cfg = new Cfg();
    errors = new ArrayList<>();
    Map<String, List<String>> declarations =
        GrammarParserUtils.parseDeclarations(grammarReader, errors);
    for (Map.Entry<String, List<String>> entry : declarations.entrySet()) {
      switch (entry.getKey()) {
      case "N":
        String[] nts = entry.getValue().toArray(new String[0]);
        cfg.setNonterminals(nts);
        break;
      case "T":
        String[] ts = entry.getValue().toArray(new String[0]);
        cfg.setTerminals(ts);
        break;
      case "S":
        if (entry.getValue().size() > 1) {
          errors.add(new ParseException("Too many start symbols declared", 0));
          continue;
        }
        cfg.setStartSymbol(entry.getValue().get(0));
        break;
      case "P":
        for (String rule : entry.getValue()) {
          try {
            cfg.addProductionRule(rule);
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
    checkForGrammarProblems(cfg);
    if (GrammarParserUtils.printErrors(errors))
      return null;
    return cfg;
  }

  private static void checkForGrammarProblems(Cfg cfg) {
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
  }
}

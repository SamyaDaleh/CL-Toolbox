package com.github.samyadaleh.cltoolbox.common.parser;

import com.github.samyadaleh.cltoolbox.common.cfg.AbstractCfg;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.ParseException;
import java.util.*;

/**
 * Parses different grammars from text files.
 */
public class GrammarParserUtils {
  private static final Logger log = LogManager.getLogger();

  static boolean printErrors(List<Exception> errors) {
    if (errors.size() > 0) {
      log.error(
          "The following errors occurred while reading the grammar file:");
      for (Exception e : errors) {
        log.error(e.getMessage(), e);
      }
      return true;
    }
    return false;
  }

  public static void addSymbolToCategory(List<String> category, int lineNumber,
      String token, String s) throws ParseException {
    if (token.equals(s)) {
      category.add(token);
    } else {
      throw new ParseException("Expected " + s + " but found " + token, lineNumber);
    }
  }

  public static void handleMainCategory(AbstractCfg acfg,
      Set<String> validCategories, List<String> category, int lineNumber,
      String token)
      throws ParseException {
    if (validCategories.contains(token)) {
      if ((token.equals("N") && acfg.getNonterminals() != null) || (
          token.equals("T") && acfg.getTerminals() != null) || (
          token.equals("S") && acfg.getStartSymbol() != null)) {
        throw new ParseException("Category " + token + " is already set.",
            lineNumber);
      }
      category.add(token);
    } else {
      throw new ParseException("Unknown declaration symbol " + token,
          lineNumber);
    }
  }

  public static List<String> addStartsymbolOrAddCategory(AbstractCfg acfg,
      List<String> category, int lineNumber, String token)
      throws ParseException {
    switch (category.get(0)) {
    case "P":
    case "N":
    case "T":
      addSymbolToCategory(category, lineNumber, token, "{");
      break;
    case "S":
      if (acfg.getStartSymbol() != null) {
        throw new ParseException("Startsymbol was declared twice: " + token,
            lineNumber);
      }
      acfg.setStartSymbol(token);
      category = new ArrayList<>();
      break;
    case "G":
      if (token.equals(">")) {
        category = new ArrayList<>();
      }
    default:
    }
    return category;
  }

  public static String findLhsOrAddCategory(List<String> category, int lineNumber,
      String lhs, String token) throws ParseException {
    if (lhs == null || !token.equals("-")) {
      lhs = token;
    } else if (token.equals("-")) {
      category.add(token);
    } else {
      throw new ParseException("Unexpected situation with token " + token,
          lineNumber);
    }
    return lhs;
  }
}

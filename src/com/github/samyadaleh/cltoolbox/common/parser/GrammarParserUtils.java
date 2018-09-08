package com.github.samyadaleh.cltoolbox.common.parser;

import com.github.samyadaleh.cltoolbox.common.AbstractNTSGrammar;
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

  public static void addSymbolToCategory(List<String> category, Token token,
      String s) throws ParseException {
    String tokenString = token.getString();
    if (tokenString.equals(s)) {
      category.add(tokenString);
    } else {
      throw new ParseException("Expected " + s + " but found " + tokenString,
          token.getLineNumber());
    }
  }

  public static void handleMainCategory(AbstractNTSGrammar acfg,
      Set<String> validCategories, List<String> category, Token token)
      throws ParseException {
    String tokenString = token.getString();
    if (validCategories.contains(tokenString)) {
      if ((tokenString.equals("N") && acfg.getNonterminals() != null) || (
          tokenString.equals("T") && acfg.getTerminals() != null) || (
          tokenString.equals("S") && acfg.getStartSymbol() != null)) {
        throw new ParseException("Category " + tokenString + " is already set.",
            token.getLineNumber());
      }
      category.add(tokenString);
    } else {
      throw new ParseException("Unknown declaration symbol " + token,
          token.getLineNumber());
    }
  }

  public static List<String> addStartsymbolOrAddCategory(
      AbstractNTSGrammar acfg,
      List<String> category, Token token) throws ParseException {
    String tokenString = token.getString();
    switch (category.get(0)) {
    case "P":
    case "N":
    case "T":
      addSymbolToCategory(category, token, "{");
      break;
    case "S":
      if (acfg.getStartSymbol() != null) {
        throw new ParseException(
            "Startsymbol was declared twice: " + tokenString,
            token.getLineNumber());
      }
      acfg.setStartSymbol(tokenString);
      category = new ArrayList<>();
      break;
    case "G":
      if (tokenString.equals(">")) {
        category = new ArrayList<>();
      }
    default:
    }
    return category;
  }

  public static String findLhsOrAddCategory(List<String> category, String lhs,
      Token token) throws ParseException {
    String tokenString = token.getString();
    if (lhs == null || !tokenString.equals("-")) {
      lhs = tokenString;
    } else if (tokenString.equals("-")) {
      category.add(tokenString);
    } else {
      throw new ParseException("Unexpected situation with token " + tokenString,
          token.getLineNumber());
    }
    return lhs;
  }
}

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
      log.warn(
          "The following errors occurred while reading the grammar file:");
      for (Exception e : errors) {
        log.warn(e.getMessage());
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
      throw new ParseException("Unknown declaration symbol " + tokenString,
          token.getLineNumber());
    }
  }

  public static List<String> addStartsymbolOrAddCategory(
      AbstractNTSGrammar antsgra, List<String> category, Token token,
      Set<String> validCategories) throws ParseException {
    String tokenString = token.getString();
    if ("S".equals(category.get(0))) {
      if (antsgra.getStartSymbol() != null) {
        throw new ParseException(
            "Startsymbol was declared twice: " + tokenString,
            token.getLineNumber());
      }
      antsgra.setStartSymbol(tokenString);
      category = new ArrayList<>();

    } else if ("G".equals(category.get(0))) {
      if (tokenString.equals(">")) {
        category = new ArrayList<>();
      }
    } else if (validCategories.contains(category.get(0))) {
      addSymbolToCategory(category, token, "{");
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

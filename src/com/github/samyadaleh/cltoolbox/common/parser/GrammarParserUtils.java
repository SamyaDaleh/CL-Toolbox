package com.github.samyadaleh.cltoolbox.common.parser;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parses different grammars from text files.
 */
public class GrammarParserUtils {
  private static final Pattern p = Pattern.compile("\"(.*?)\"");
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

  /**
   * For a line like "A", "S" it gets the content of each quote and makes each
   * an element of the returned array.
   */
  private static List<String> parseNT(String lineTrim) {
    Matcher m = p.matcher(lineTrim);
    List<String> nList = new ArrayList<>();
    while (m.find()) {
      String n = m.group();
      nList.add(n.substring(1, n.length() - 1));
    }
    return nList;
  }

  static Map<String, List<String>> parseDeclarations(
      BufferedReader grammarReader, List<Exception> errors) throws IOException {
    Map<String, List<String>> declarations = new LinkedHashMap<>();
    String line = grammarReader.readLine().trim();
    while (line != null) {
      String lineTrim = line.trim();
      int quotes = lineTrim.length() - lineTrim.replace("\"", "").length();
      if (quotes % 2 > 0) {
        errors.add(
            new ParseException("Uneven number of quotes in line " + lineTrim,
                0));
      }
      String[] splitEquals = lineTrim.split("=");
      if (splitEquals.length == 1) {
        errors.add(
            new ParseException("No equals symbol found in line " + lineTrim,
                0));
        continue;
      }
      String symbol = splitEquals[0].trim();
      if (declarations.containsKey(symbol)) {
        errors.add(new ParseException(symbol + " was declared twice.", 0));
      }
      declarations.put(symbol, parseNT(lineTrim));
      line = grammarReader.readLine();
    }
    grammarReader.close();
    return declarations;
  }

  public static void addSymbolToCategory(List<String> category, int lineNumber,
      String token, String s) throws ParseException {
    if (token.equals(s)) {
      category.add(token);
    } else {
      throw new ParseException("Expected " + s + " but found " + token, lineNumber);
    }
  }
}

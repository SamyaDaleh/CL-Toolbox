package com.github.samyadaleh.cltoolbox.common.parser.inner;

import com.github.samyadaleh.cltoolbox.common.lag.Lag;
import com.github.samyadaleh.cltoolbox.common.parser.GrammarParserUtils;
import com.github.samyadaleh.cltoolbox.common.parser.Token;
import com.github.samyadaleh.cltoolbox.common.parser.TokenReader;

import java.io.BufferedReader;
import java.text.ParseException;
import java.util.HashSet;
import java.util.Set;

public class InnerLagGrammarParser extends InnerGrammarParser{
  private Lag lag;
  private Set<String> validCategories;
  private Token token;

  public InnerLagGrammarParser(Lag lag, BufferedReader in) {
    super(in);
    this.lag = lag;
  }

  @Override protected void handleCategoryLengthGT3() throws ParseException {

  }

  @Override protected void handleCategoryLength3() throws ParseException {
  }

  @Override protected void handleCategoryLength2() throws ParseException {
    GrammarParserUtils.addSymbolToCategory(category, token, "{");

  }

  @Override protected void handleCategoryLength1() throws ParseException {
    GrammarParserUtils.addSymbolToCategory(category, token, "=");
  }

  @Override protected void handleCategoryLength0() throws ParseException {
    String tokenString = token.getString();
    if (validCategories.contains(tokenString)) {
      if ((tokenString.equals("LX") && lag.getLexicon() != null) || (
          tokenString.equals("ST_S") && lag.getInitialStates() != null) || (
          tokenString.equals("ST_F") && lag.getFinalStates() != null) || (
          tokenString.equals("RP") && lag.getLagRules() != null)) {
        throw new ParseException("Category " + tokenString + " is already set.",
            token.getLineNumber());
      }
      category.add(tokenString);
    } else {
      throw new ParseException("Unknown declaration symbol " + tokenString,
          token.getLineNumber());
    }
  }

  @Override
  public Set<String> getValidCategories() {
    Set<String> validCategories = new HashSet<>();
    validCategories.add("LX");
    validCategories.add("ST_S");
    validCategories.add("ST_F");
    validCategories.add("RP");
    return validCategories;
  }

  @Override public Character[] getSpecialChars() {
    return new Character[] {'-', '>', '{', '}', ',', '[', ']', '(', ')', ':'};
  }
}

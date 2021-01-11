package com.github.samyadaleh.cltoolbox.common.parser.inner;

import com.github.samyadaleh.cltoolbox.common.lag.Lag;
import com.github.samyadaleh.cltoolbox.common.lag.LagState;
import com.github.samyadaleh.cltoolbox.common.lag.LagWord;
import com.github.samyadaleh.cltoolbox.common.parser.GrammarParserUtils;

import java.io.BufferedReader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class InnerLagGrammarParser extends InnerGrammarParser{
  private Lag lag;
  private List<LagWord> lexicon = new ArrayList<>();
  private String wordSurface;
  private List<String> wordCategory = new ArrayList<>();
  private List<String> rulePackage = new ArrayList<>();
  private List<LagState> states = new ArrayList<>();

  public InnerLagGrammarParser(Lag lag, BufferedReader in) {
    super(in);
    this.lag = lag;
  }

  @Override protected void handleCategoryLengthGT3() throws ParseException {
    String tokenString = token.getString();
    if (",".equals(tokenString)) {
      return;
    }
    switch (category.get(0)) {
    case "LX":
      if ("[".equals(tokenString) || "(".equals(tokenString)) {
        category.add(tokenString);
      } else if (")".equals(tokenString)) {
        category.remove(category.size() - 1);
      } else if ("]".equals(tokenString)){
        String[] categoryArray = wordCategory.toArray(new String[0]);
        lexicon.add(new LagWord(wordSurface, categoryArray));
        wordSurface = null;
        wordCategory = new ArrayList<>();
      } else {
        if (category.size() == 4) {
          if (wordSurface != null) {
            throw new ParseException("Two word surfaces declared.",
                token.getLineNumber());
          }
          wordSurface = tokenString;
        } else if (category.size() == 5) {
          wordCategory.add(tokenString);
        } else {
          throw new ParseException(
              "Something seems to be wrong " + "with the brackets.",
              token.getLineNumber());
        }
      }
      break;
    case "ST_S":
    case "ST_F":
      if ("{".equals(tokenString) || "(".equals(tokenString)) {
        category.add(tokenString);
      } else if ("}".equals(tokenString) ) {
        category.remove(category.size() - 1);
      } else if (")".equals(tokenString)) {
        category.remove(category.size() - 1);
        if (category.size() == 3) {
          states.add(new LagState(rulePackage.toArray(new String[0]),
              wordCategory.toArray(new String[0])));
        }
      } else {
        if (category.size() == 5) {
          String lastCategory = category.get(category.size() - 1);
          if ("{".equals(lastCategory)) {
            rulePackage.add(tokenString);
          } else if ("(".equals(lastCategory)){
             wordCategory.add(tokenString);
          } else {
            throw new ParseException(
                "Something seems to be wrong with the brackets.",
                token.getLineNumber());
          }
        } else {
          throw new ParseException(
              "Something seems to be wrong with the brackets.",
              token.getLineNumber());
        }
      }
      break;
    case "RP":
      if (":".equals(tokenString) || "[".equals(tokenString)
          || "(".equals(tokenString) || "-".equals(tokenString)
          || ">".equals(tokenString) || "{".equals(tokenString)) {
        category.add(tokenString);
      } else if(")".equals(tokenString) || "}".equals(tokenString)
          || "]".equals(tokenString)) {
        category.remove(category.size()-1);
      } else {
        if (category.size() == 7) {
          // TODO we read the X or the b
        } else if (category.size() == 9) {
          // TODO
          // we read r1 or the second b
        } else {
          throw new ParseException(
              "Something seems to be wrong with the brackets.",
              token.getLineNumber());
        }
      }
      break;
    } /*
    RP = {
        r1 : [(X) (b c)] -> [{r1, r2} (b X c)],
    r2 : [(b X c) (b)] -> [{r2, r3} (X c)],
    r3 : [(c X) (c)] -> [{r3} (X)] }  //*/

  }

  @Override protected void handleCategoryLength3() throws ParseException {
    String tokenString = token.getString();
    if ("}".equals(tokenString)) {
      switch (category.get(0)){
      case "LX":
        lag.setLexicon(lexicon.toArray(new LagWord[0]));
        break;
      case "ST_S":
        lag.setInitialStates(states.toArray(new LagState[0]));
        break;
      case "ST_F":
        lag.setFinalStates(states.toArray(new LagState[0]));
        break;
      case "RP":
        // TODO implement analogous to LX
      }
    } else {
      category.add(tokenString);
    }
  }

  @Override protected void handleCategoryLength2() throws ParseException {
    if ("G".equals(category.get(0))) {
      if (token.getString().equals(">")) {
        category = new ArrayList<>();
      }
    } else {
      GrammarParserUtils.addSymbolToCategory(category, token, "{");
    }
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
    validCategories.add("G");
    return validCategories;
  }

  @Override public Character[] getSpecialChars() {
    return new Character[] {'-', '>', '{', '}', ',', '[', ']', '(', ')', ':'};
  }
}

package com.github.samyadaleh.cltoolbox.common.parser.inner;

import com.github.samyadaleh.cltoolbox.common.lcfrs.Srcg;
import com.github.samyadaleh.cltoolbox.common.parser.collectsymbols.CollectOuterRhsSymbolsSrcg;
import com.github.samyadaleh.cltoolbox.common.parser.collectsymbols.CollectSetContentsSrcg;
import com.github.samyadaleh.cltoolbox.common.parser.GrammarParserUtils;
import com.github.samyadaleh.cltoolbox.common.parser.Token;

import java.io.BufferedReader;
import java.text.ParseException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class InnerSrcgGrammarParser extends InnerGrammarParser {
  private String lhsNT;
  private StringBuilder lhs;
  private String currentRhsNt;
  private final Srcg srcg;

  public InnerSrcgGrammarParser(Srcg srcg, BufferedReader in) {
    super(in);
    this.srcg = srcg;
    lhsNT = null;
    lhs = new StringBuilder();
    currentRhsNt = null;
  }

  @Override protected void handleCategoryLengthGT3() throws ParseException {
    String tokenString = token.getString();
    switch (category.size()) {
    case 4:
      if (category.get(3).equals("-")) {
        GrammarParserUtils.addSymbolToCategory(category, token, ">");
      } else {
        lhs.append(" ").append(tokenString);
        if (tokenString.equals(")")) {
          category.remove(3);
        }
      }
      break;
    case 5:
      CollectOuterRhsSymbolsSrcg collectOuterRhsSymbols =
          (CollectOuterRhsSymbolsSrcg) new CollectOuterRhsSymbolsSrcg(srcg,
              category, lhsNT, lhs, currentRhsNt, rhs, token).invoke();
      category = collectOuterRhsSymbols.getCategory();
      lhsNT = collectOuterRhsSymbols.getLhsNT();
      lhs = collectOuterRhsSymbols.getLhsBuilder();
      currentRhsNt = collectOuterRhsSymbols.getCurrentRhsNt();
      rhs = collectOuterRhsSymbols.getRhs();
      break;
    default:
      switch (tokenString) {
      case ")":
        category.remove(5);
        currentRhsNt = null;
      default:
        rhs.append(" ").append(tokenString);
        break;
      }
      break;
    }
  }

  @Override protected void handleCategoryLength3() throws ParseException {
    CollectSetContentsSrcg collectSetContents =
        (CollectSetContentsSrcg) new CollectSetContentsSrcg(srcg, category,
            lhsNT, lhs, symbols, token).invoke();
    category = collectSetContents.getCategory();
    lhsNT = collectSetContents.getLhsNT();
    symbols = collectSetContents.getSymbols();
  }

  @Override protected void handleCategoryLength2() throws ParseException {
    category = GrammarParserUtils
        .addStartsymbolOrAddCategory(srcg, category, token, validCategories);
  }

  @Override protected void handleCategoryLength1() throws ParseException {
    GrammarParserUtils.addSymbolToCategory(category, token, "=");
  }

  @Override protected void handleCategoryLength0() throws ParseException {
    handleMainCategory(validCategories, category, token);
  }

  @Override protected Character[] getSpecialChars() {
    return new Character[] {'-', '>', '{', '}', ',', '|', '=', '<', '(', ')'};
  }

  @Override protected Set<String> getValidCategories() {
    Set<String> validCategories = new HashSet<>();
    validCategories.add("N");
    validCategories.add("T");
    validCategories.add("V");
    validCategories.add("S");
    validCategories.add("P");
    validCategories.add("G");
    return validCategories;
  }

  private void handleMainCategory(Set<String> validCategories,
      List<String> category, Token token) throws ParseException {
    String tokenString = token.getString();
    if (validCategories.contains(tokenString)) {
      if ((tokenString.equals("N") && srcg.getNonterminals() != null) || (
          tokenString.equals("T") && srcg.getTerminals() != null) || (
          tokenString.equals("S") && srcg.getStartSymbol() != null) || (
          tokenString.equals("V") && srcg.getVariables() != null)) {
        throw new ParseException("Category " + token + " is already set.",
            token.getLineNumber());
      }
      category.add(tokenString);
    } else {
      throw new ParseException("Unknown declaration symbol " + tokenString,
          token.getLineNumber());
    }
  }
}

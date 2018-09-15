package com.github.samyadaleh.cltoolbox.common.parser.inner;

import com.github.samyadaleh.cltoolbox.common.parser.collectsymbols.CollectSetContentsTag;
import com.github.samyadaleh.cltoolbox.common.parser.collectsymbols.CollectTreeSymbolsTag;
import com.github.samyadaleh.cltoolbox.common.parser.GrammarParserUtils;
import com.github.samyadaleh.cltoolbox.common.tag.Tag;

import java.io.BufferedReader;
import java.text.ParseException;
import java.util.HashSet;
import java.util.Set;

public class InnerTagGrammarParser extends InnerGrammarParser {
  private final Tag tag;
  private String lhs;

  public InnerTagGrammarParser(Tag tag, BufferedReader in) {
    super(in);
    this.tag = tag;
    this.lhs = null;
  }

  @Override protected void handleCategoryLengthGT3() throws ParseException {
    String tokenString = token.getString();
    CollectTreeSymbolsTag collectTreeTokens =
        (CollectTreeSymbolsTag) new CollectTreeSymbolsTag(tag, category, lhs,
            rhs, tokenString).invoke();
    category = collectTreeTokens.getCategory();
    lhs = collectTreeTokens.getLhs();
    rhs = collectTreeTokens.getRhs();
  }

  @Override protected void handleCategoryLength3() throws ParseException {
    CollectSetContentsTag collectSetContents =
        (CollectSetContentsTag) new CollectSetContentsTag(tag, category, lhs,
            rhs, symbols, token).invoke();
    category = collectSetContents.getCategory();
    lhs = collectSetContents.getLhs();
    rhs = collectSetContents.getRhs();
    symbols = collectSetContents.getSymbols();
  }

  @Override protected void handleCategoryLength2() throws ParseException {
    this.category = GrammarParserUtils
        .addStartsymbolOrAddCategory(tag, category, token, validCategories);
  }

  @Override protected void handleCategoryLength1() throws ParseException {
    GrammarParserUtils.addSymbolToCategory(category, token, "=");
  }

  @Override protected void handleCategoryLength0() throws ParseException {
    handleMainCategory();
  }

  @Override protected Character[] getSpecialChars() {
    return new Character[] {'>', '{', '}', ',', ':', '='};
  }

  @Override protected Set<String> getValidCategories() {
    Set<String> validCategories = new HashSet<>();
    validCategories.add("N");
    validCategories.add("T");
    validCategories.add("S");
    validCategories.add("I");
    validCategories.add("A");
    validCategories.add("G");
    return validCategories;
  }

  private void handleMainCategory() throws ParseException {
    String tokenString = token.getString();
    if (validCategories.contains(tokenString)) {
      if ((tokenString.equals("N") && tag.getNonterminals() != null) || (
          tokenString.equals("T") && tag.getTerminals() != null) || (
          tokenString.equals("S") && tag.getStartSymbol() != null) || (
          tokenString.equals("A") && tag.getAuxiliaryTreeNames().size() > 0)
          || (tokenString.equals("I")
          && tag.getInitialTreeNames().size() > 0)) {
        throw new ParseException("Category " + tokenString + " is already set.",
            token.getLineNumber());
      }
      category.add(tokenString);
    } else {
      throw new ParseException("Unknown declaration symbol " + tokenString,
          token.getLineNumber());
    }
  }
}

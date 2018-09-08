package com.github.samyadaleh.cltoolbox.common.parser;

import com.github.samyadaleh.cltoolbox.common.cfg.Cfg;

import java.io.BufferedReader;
import java.text.ParseException;
import java.util.HashSet;
import java.util.Set;

public class InnerCfgGrammarParser extends InnerGrammarParser {
  private Cfg cfg;
  private String lhs;

  public InnerCfgGrammarParser(Cfg cfg, BufferedReader in) {
    super(in);
    this.cfg = cfg;
    this.lhs = null;
  }

  @Override protected void handleCategoryLengthGT3() throws ParseException {
    String tokenString = token.getString();
    if (category.size() == 4) {

      if (tokenString.equals(">")) {
        category.add(tokenString);
      } else {
        throw new ParseException("Expected > but found " + token,
            token.getLineNumber());
      }
    } else {
      CollectSymbols collectRhsSymbols =
          new CollectRhsSymbolsCfg(cfg, category, lhs, rhs, tokenString)
              .invoke();
      category = collectRhsSymbols.getCategory();
      lhs = collectRhsSymbols.getLhs();
      rhs = collectRhsSymbols.getRhs();
    }
  }

  @Override protected void handleCategoryLength3() throws ParseException {
    CollectSetContentsCfg collectSetContentsCfg =
        (CollectSetContentsCfg) new CollectSetContentsCfg(cfg, category, lhs,
            symbols, token).invoke();
    category = collectSetContentsCfg.getCategory();
    lhs = collectSetContentsCfg.getLhs();
    symbols = collectSetContentsCfg.getSymbols();
  }

  @Override protected void handleCategoryLength2() throws ParseException {
    category = GrammarParserUtils
        .addStartsymbolOrAddCategory(cfg, category, token, validCategories);
  }

  @Override protected void handleCategoryLength1() throws ParseException {
    GrammarParserUtils.addSymbolToCategory(category, token, "=");
  }

  @Override protected void handleCategoryLength0() throws ParseException {
    GrammarParserUtils
        .handleMainCategory(cfg, validCategories, category, token);
  }

  @Override protected Character[] getSpecialChars() {
    return new Character[] {'-', '>', '{', '}', ',', '|', '='};
  }

  @Override protected Set<String> getValidCategories() {
    Set<String> validCategories = new HashSet<>();
    validCategories.add("N");
    validCategories.add("T");
    validCategories.add("S");
    validCategories.add("P");
    validCategories.add("G");
    return validCategories;
  }
}

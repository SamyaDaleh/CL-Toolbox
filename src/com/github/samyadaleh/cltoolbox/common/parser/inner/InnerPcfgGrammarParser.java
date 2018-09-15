package com.github.samyadaleh.cltoolbox.common.parser.inner;

import com.github.samyadaleh.cltoolbox.common.cfg.Pcfg;
import com.github.samyadaleh.cltoolbox.common.parser.collectsymbols.CollectRhsSymbolsPcfg;
import com.github.samyadaleh.cltoolbox.common.parser.collectsymbols.CollectSetContentsPcfg;
import com.github.samyadaleh.cltoolbox.common.parser.GrammarParserUtils;

import java.io.BufferedReader;
import java.text.ParseException;
import java.util.HashSet;
import java.util.Set;

public class InnerPcfgGrammarParser extends InnerGrammarParser {

  private String prob;
  private String lhs;
  private final Pcfg pcfg;

  public InnerPcfgGrammarParser(Pcfg pcfg, BufferedReader in) {
    super(in);
    this.pcfg = pcfg;
    prob = null;
    lhs = null;
  }

  @Override protected void handleCategoryLengthGT3() throws ParseException {
    String tokenString = token.getString();
    switch (category.size()) {
    case 4:
      lhs = GrammarParserUtils.findLhsOrAddCategory(category, lhs, token);
      break;
    case 5:
      if (tokenString.equals(">")) {
        category.add(tokenString);
        rhs = new StringBuilder();
      } else {
        throw new ParseException("Expected > but found " + token,
            token.getLineNumber());
      }
      break;
    default:
      CollectRhsSymbolsPcfg collectRhsSymbols =
          (CollectRhsSymbolsPcfg) new CollectRhsSymbolsPcfg(pcfg, category,
              prob, lhs, rhs, tokenString).invoke();
      category = collectRhsSymbols.getCategory();
      prob = collectRhsSymbols.getProb();
      lhs = collectRhsSymbols.getLhs();
      rhs = collectRhsSymbols.getRhs();
      break;

    }
  }

  @Override protected void handleCategoryLength3() throws ParseException {
    CollectSetContentsPcfg collectSetContents =
        (CollectSetContentsPcfg) new CollectSetContentsPcfg(pcfg, category,
            prob, lhs, symbols, token).invoke();
    category = collectSetContents.getCategory();
    prob = collectSetContents.getProb();
    lhs = collectSetContents.getLhs();
    symbols = collectSetContents.getSymbols();
  }

  @Override protected void handleCategoryLength2() throws ParseException {
    category = GrammarParserUtils
        .addStartsymbolOrAddCategory(pcfg, category, token, validCategories);
  }

  @Override protected void handleCategoryLength1() throws ParseException {
    GrammarParserUtils.addSymbolToCategory(category, token, "=");
  }

  @Override protected void handleCategoryLength0() throws ParseException {
    GrammarParserUtils
        .handleMainCategory(pcfg, validCategories, category, token);
  }

  @Override protected Character[] getSpecialChars() {
    return new Character[] {'-', '>', '{', '}', ',', '|', '=', ':'};
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

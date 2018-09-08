package com.github.samyadaleh.cltoolbox.common.parser;

import com.github.samyadaleh.cltoolbox.common.lcfrs.Srcg;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class CollectSetContentsSrcg extends CollectSetContentsNTSP {
  private final Srcg srcg;
  private String lhsNT;
  private final StringBuilder lhs;

  public CollectSetContentsSrcg(Srcg srcg, List<String> category, String lhsNT,
      StringBuilder lhs, List<String> symbols, Token token) {
    super(srcg, category, lhsNT, symbols, token);
    this.srcg = srcg;
    this.lhsNT = lhsNT;
    this.lhs = lhs;
  }

  public List<String> getCategory() {
    return category;
  }

  public String getLhsNT() {
    return lhsNT;
  }

  public List<String> getSymbols() {
    return symbols;
  }

  @Override void handleOtherCategories(List<String> category, String lhs,
      Token token) throws ParseException {
    String tokenString = token.getString();
    if (category.get(0).equals("V")) {
      switch (tokenString) {
      case "}":
        srcg.setVariables(symbols.toArray(new String[0]));
        this.category = new ArrayList<>();
        symbols = new ArrayList<>();
        break;
      case ",":
        break;
      default:
        symbols.add(tokenString);
      }
    } else if (category.get(0).equals("P")) {
      findXOrAddCategory();
    }
  }

  @Override protected void findXOrAddCategory() throws ParseException {
    if (lhs.toString().endsWith(")")) {
      GrammarParserUtils.addSymbolToCategory(category, token, "-");
    } else {
      lhsNT = findLhsNTOrAddCategory(category, lhsNT, token);
      if (token.getString().equals("(")) {
        lhs.append(lhsNT).append("(");
      }
    }
  }

  private String findLhsNTOrAddCategory(List<String> category, String lhs,
      Token token) throws ParseException {
    String tokenString = token.getString();
    if (lhs == null || !tokenString.equals("(")) {
      lhs = tokenString;
    } else if (tokenString.equals("(")) {
      category.add(tokenString);
    } else {
      throw new ParseException("Unexpected situation with token " + tokenString,
          token.getLineNumber());
    }
    return lhs;
  }
}

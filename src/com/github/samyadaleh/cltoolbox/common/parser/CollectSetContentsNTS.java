package com.github.samyadaleh.cltoolbox.common.parser;

import com.github.samyadaleh.cltoolbox.common.AbstractNTSGrammar;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public abstract class CollectSetContentsNTS {
  protected String lhs;
  protected List<String> symbols;
  protected Token token;
  protected AbstractNTSGrammar acfg;
  List<String> category;

  public CollectSetContentsNTS(AbstractNTSGrammar agra, List<String> category,
      String lhs, List<String> symbols, Token token) {
    this.category = category;
    this.lhs = lhs;
    this.symbols = symbols;
    this.token = token;
    this.acfg = agra;
  }

  public List<String> getCategory() {
    return category;
  }

  public String getLhs() {
    return lhs;
  }

  public List<String> getSymbols() {
    return symbols;
  }

  public CollectSetContentsNTS invoke() throws ParseException {
    String tokenString = token.getString();
    switch (category.get(0)) {
    case "N":
      switch (tokenString) {
      case "}":
        acfg.setNonterminals(symbols.toArray(new String[0]));
        category = new ArrayList<>();
        symbols = new ArrayList<>();
        break;
      case ",":
        break;
      default:
        symbols.add(tokenString);
      }
      break;
    case "T":
      switch (tokenString) {
      case "}":
        acfg.setTerminals(symbols.toArray(new String[0]));
        category = new ArrayList<>();
        symbols = new ArrayList<>();
        break;
      case ",":
        break;
      default:
        symbols.add(tokenString);
      }
      break;
    default:
      handleOtherCategories(category, lhs, token);
    }
    return this;
  }

  abstract void handleOtherCategories(List<String> category, String lhs,
      Token token) throws ParseException;
}

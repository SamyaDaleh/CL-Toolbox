package com.github.samyadaleh.cltoolbox.common.parser.collectsymbols;

import java.text.ParseException;
import java.util.List;

public abstract class CollectSymbols {
  List<String> category;
  String lhs;
  StringBuilder rhs;
  final String tokenString;

  CollectSymbols(List<String> category, String lhs, StringBuilder rhs,
      String tokenString) {
    this.category = category;
    this.lhs = lhs;
    this.rhs = rhs;
    this.tokenString = tokenString;
  }

  public List<String> getCategory() {
    return category;
  }

  public String getLhs() {
    return lhs;
  }

  public StringBuilder getRhs() {
    return rhs;
  }

  public CollectSymbols invoke() throws ParseException {
    switch (tokenString) {
    case "}":
      handleClosingBracket();
      break;
    case "|":
      handlePipe();
      break;
    case ",":
      handleComma();
      break;
    default:
      handleDefault();
    }
    return this;
  }

  protected abstract void handleDefault() throws ParseException;

  protected abstract void handleComma() throws ParseException;

  protected abstract void handlePipe() throws ParseException;

  protected abstract void handleClosingBracket() throws ParseException;
}

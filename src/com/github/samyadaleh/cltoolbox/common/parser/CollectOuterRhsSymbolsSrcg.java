package com.github.samyadaleh.cltoolbox.common.parser;

import com.github.samyadaleh.cltoolbox.common.lcfrs.Srcg;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class CollectOuterRhsSymbolsSrcg extends CollectSymbols {
  private Srcg srcg;
  private String lhsNT;
  private String currentRhsNt;
  private StringBuilder lhsBuilder;
  Token token;

  public String getCurrentRhsNt() {
    return currentRhsNt;
  }

  public StringBuilder getLhsBuilder() {
    return lhsBuilder;
  }

  public CollectOuterRhsSymbolsSrcg(Srcg srcg, List<String> category,
      String lhsNT, StringBuilder lhs, String currentRhsNt, StringBuilder rhs,
      Token token) {
    super(category, null, rhs, token.getString());
    this.srcg = srcg;
    this.lhsNT = lhsNT;
    this.currentRhsNt = currentRhsNt;
    this.lhsBuilder = lhs;
    this.token = token;
  }

  @Override protected void handleDefault() throws ParseException {
    currentRhsNt = findRhsNTOrAddCategory(category, currentRhsNt, token);
    if (tokenString.equals("(")) {
      rhs.append(currentRhsNt).append("(");
    }
  }

  @Override protected void handleComma() throws ParseException {
    srcg.addClause(lhsBuilder.toString(), rhs.toString());
    currentRhsNt = null;
    rhs = new StringBuilder();
    lhsNT = null;
    lhsBuilder = new StringBuilder();
    category.remove(4);
    category.remove(3);
  }

  @Override protected void handlePipe() throws ParseException {
    srcg.addClause(lhsBuilder.toString(), rhs.toString());
    currentRhsNt = null;
    rhs = new StringBuilder();
  }

  @Override protected void handleClosingBracket() throws ParseException {
    srcg.addClause(lhsBuilder.toString(), rhs.toString());
    category = new ArrayList<>();
    currentRhsNt = null;
    rhs = new StringBuilder();
    lhsNT = null;
    lhsBuilder = new StringBuilder();
  }

  private String findRhsNTOrAddCategory(List<String> category, String rhsnt,
      Token token) throws ParseException {
    String tokenString = token.getString();
    if (rhsnt == null || !tokenString.equals("(")) {
      rhsnt = tokenString;
    } else if (tokenString.equals("(")) {
      category.add(tokenString);
    } else {
      throw new ParseException("Unexpected situation with token " + tokenString,
          token.getLineNumber());
    }
    return rhsnt;
  }

  public String getLhsNT() {
    return lhsNT;
  }
}

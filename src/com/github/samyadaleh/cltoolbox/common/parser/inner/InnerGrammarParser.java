package com.github.samyadaleh.cltoolbox.common.parser.inner;

import com.github.samyadaleh.cltoolbox.common.parser.Token;
import com.github.samyadaleh.cltoolbox.common.parser.TokenReader;

import java.io.BufferedReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public abstract class InnerGrammarParser {
  protected BufferedReader in;
  protected List<String> category;
  protected StringBuilder rhs;
  protected List<String> symbols;
  protected Token token;
  Set<String> validCategories;

  InnerGrammarParser(BufferedReader in) {
    this.in = in;
  }

  public void invoke() throws IOException, ParseException {
    Character[] specialChars = getSpecialChars();
    TokenReader reader = new TokenReader(in, specialChars);
    validCategories = getValidCategories();
    category = new ArrayList<>();
    rhs = new StringBuilder();
    symbols = new ArrayList<>();
    while ((token = reader.getNextToken()) != null) {
      switch (category.size()) {
      case 0:
        handleCategoryLength0();
        break;
      case 1:
        handleCategoryLength1();
        break;
      case 2:
        handleCategoryLength2();
        break;
      case 3:
        handleCategoryLength3();
        break;
      default:
        handleCategoryLengthGT3();
      }
    }
  }

  protected abstract void handleCategoryLengthGT3()
      throws ParseException;

  protected abstract void handleCategoryLength3() throws ParseException;

  protected abstract void handleCategoryLength2() throws ParseException;

  protected abstract void handleCategoryLength1()
      throws ParseException;

  protected abstract void handleCategoryLength0() throws ParseException;

  protected abstract Character[] getSpecialChars();

  protected abstract Set<String> getValidCategories();
}

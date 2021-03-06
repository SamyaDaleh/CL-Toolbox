package com.github.samyadaleh.cltoolbox.common.parser.collectsymbols;

import com.github.samyadaleh.cltoolbox.common.tag.Tag;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class CollectTreeSymbolsTag extends CollectSymbols {
  private final Tag tag;

  public CollectTreeSymbolsTag(Tag tag, List<String> category, String lhs,
      StringBuilder rhs, String tokenString) {
    super(category, lhs, rhs, tokenString);
    this.tag = tag;
  }

  @Override protected void handleDefault() {
    if (rhs.length() > 0) {
      rhs.append(' ');
    }
    rhs.append(tokenString);
  }

  @Override protected void handleComma() throws ParseException {
    if (category.get(0).equals("I")) {
      tag.addInitialTree(lhs, rhs.toString());
    } else {
      tag.addAuxiliaryTree(lhs, rhs.toString());
    }
    rhs = new StringBuilder();
    lhs = null;
    category.remove(3);
  }

  @Override protected void handlePipe() {
    handleDefault();
  }

  @Override protected void handleClosingBracket() throws ParseException {
    if (category.get(0).equals("I")) {
      tag.addInitialTree(lhs, rhs.toString());
    } else {
      tag.addAuxiliaryTree(lhs, rhs.toString());
    }
    category = new ArrayList<>();
    lhs = null;
  }
}

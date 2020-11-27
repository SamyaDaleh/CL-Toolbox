package com.github.samyadaleh.cltoolbox.common.parser.collectsymbols;

import com.github.samyadaleh.cltoolbox.common.parser.Token;
import com.github.samyadaleh.cltoolbox.common.tag.Tag;

import java.text.ParseException;
import java.util.List;

public class CollectSetContentsTag extends CollectSetContentsNTS {
  private StringBuilder rhs;

  public CollectSetContentsTag(Tag tag, List<String> category, String lhs,
      StringBuilder rhs, List<String> symbols, Token token) {
    super(tag, category, lhs, symbols, token);
    this.rhs = rhs;
  }

  @Override void handleOtherCategories(List<String> category, String lhs,
      Token token) throws ParseException {
    if (category.get(0).equals("I") || category.get(0).equals("A")) {
      this.lhs = findLhsOrAddCategory(category, lhs, token);
      rhs = new StringBuilder();
    }
  }

  private String findLhsOrAddCategory(List<String> category, String lhs,
      Token token) throws ParseException {
    String tokenString = token.getString();
    if (lhs == null || !tokenString.equals(":")) {
      lhs = tokenString;
    } else if (tokenString.equals(":")) {
      category.add(tokenString);
    } else {
      throw new ParseException("Unexpected situation with token " + tokenString,
          token.getLineNumber());
    }
    return lhs;
  }

  public StringBuilder getRhs() {
    return rhs;
  }
}

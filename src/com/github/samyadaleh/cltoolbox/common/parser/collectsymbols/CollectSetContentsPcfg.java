package com.github.samyadaleh.cltoolbox.common.parser.collectsymbols;

import com.github.samyadaleh.cltoolbox.common.cfg.Pcfg;
import com.github.samyadaleh.cltoolbox.common.parser.Token;

import java.text.ParseException;
import java.util.List;

public class CollectSetContentsPcfg extends CollectSetContentsNTSP {
  private String prob;

  public CollectSetContentsPcfg(Pcfg pcfg, List<String> category, String prob,
      String lhs, List<String> symbols, Token token) {
    super(pcfg, category, lhs, symbols, token);
    this.prob = prob;
  }

  public String getProb() {
    return prob;
  }

  @Override protected void findXOrAddCategory() throws ParseException {
    prob = findProbabilityOrAddCategory(category, prob, token);
  }

  private String findProbabilityOrAddCategory(List<String> category,
      String prob, Token token) throws ParseException {
    String tokenString = token.getString();
    if (prob == null || !tokenString.equals(":")) {
      prob = tokenString;
    } else if (tokenString.equals(":")) {
      category.add(tokenString);
    } else {
      throw new ParseException("Unexpected situation with token " + tokenString,
          token.getLineNumber());
    }
    return prob;
  }
}

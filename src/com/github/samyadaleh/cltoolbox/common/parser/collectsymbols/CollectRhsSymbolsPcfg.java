package com.github.samyadaleh.cltoolbox.common.parser.collectsymbols;

import com.github.samyadaleh.cltoolbox.common.cfg.Pcfg;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class CollectRhsSymbolsPcfg extends CollectSymbols {
  private String prob;
  private Pcfg pcfg;

  public CollectRhsSymbolsPcfg(Pcfg pcfg, List<String> category, String prob,
      String lhs, StringBuilder rhs, String tokenString) {
    super(category, lhs, rhs, tokenString);
    this.prob = prob;
    this.pcfg = pcfg;
  }

  @Override protected void handleDefault() {
    if (rhs.length() > 0) {
      rhs.append(' ');
    }
    rhs.append(tokenString);
  }

  @Override protected void handleComma() throws ParseException {
    pcfg.addProductionRule(prob + " : " + lhs + " -> " + rhs.toString());
    rhs = new StringBuilder();
    lhs = null;
    prob = null;
    category.remove(5);
    category.remove(4);
    category.remove(3);
  }

  @Override protected void handlePipe() throws ParseException {
    pcfg.addProductionRule(prob + " : " + lhs + " -> " + rhs.toString());
    rhs = new StringBuilder();
  }

  @Override protected void handleClosingBracket() throws ParseException {
    category = new ArrayList<>();
    pcfg.addProductionRule(prob + " : " + lhs + " -> " + rhs.toString());
    prob = null;
    lhs = null;
  }

  public String getProb() {
    return prob;
  }
}

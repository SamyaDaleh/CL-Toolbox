package com.github.samyadaleh.cltoolbox.common.parser;

import com.github.samyadaleh.cltoolbox.common.cfg.Cfg;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class CollectRhsSymbolsCfg extends CollectSymbols {
  private Cfg cfg;

  public CollectRhsSymbolsCfg(Cfg cfg, List<String> category, String lhs,
      StringBuilder rhs, String tokenString) {
    super(category, lhs, rhs, tokenString);
    this.cfg = cfg;
  }

  @Override protected void handleDefault() {
    if (rhs.length() > 0) {
      rhs.append(' ');
    }
    rhs.append(tokenString);
  }

  @Override protected void handleComma() throws ParseException {
    cfg.addProductionRule(lhs + " -> " + rhs.toString());
    rhs = new StringBuilder();
    lhs = null;
    category.remove(4);
    category.remove(3);
  }

  @Override protected void handlePipe() throws ParseException {
    cfg.addProductionRule(lhs + " -> " + rhs.toString());
    rhs = new StringBuilder();
  }

  @Override protected void handleClosingBracket() throws ParseException {
    category = new ArrayList<>();
    cfg.addProductionRule(lhs + " -> " + rhs.toString());
    lhs = null;
  }
}

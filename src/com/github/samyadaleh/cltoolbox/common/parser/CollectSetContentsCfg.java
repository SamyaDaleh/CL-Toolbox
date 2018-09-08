package com.github.samyadaleh.cltoolbox.common.parser;

import com.github.samyadaleh.cltoolbox.common.cfg.Cfg;

import java.text.ParseException;
import java.util.List;

public class CollectSetContentsCfg extends CollectSetContentsNTSP {

  public CollectSetContentsCfg(Cfg cfg, List<String> category, String lhs,
      List<String> symbols, Token token) {
    super(cfg, category, lhs, symbols, token);
  }

   void handleOtherCategories(List<String> category, String lhs,
      Token token) throws ParseException {
    if (category.get(0).equals("P")){
      findXOrAddCategory();
    }
   }

  protected void findXOrAddCategory() throws ParseException {
    lhs = GrammarParserUtils.findLhsOrAddCategory(category, getLhs(), token);
  }
}

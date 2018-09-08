package com.github.samyadaleh.cltoolbox.common.parser;

import com.github.samyadaleh.cltoolbox.common.AbstractNTSGrammar;

import java.text.ParseException;
import java.util.List;

public abstract class CollectSetContentsNTSP extends CollectSetContentsNTS {

  CollectSetContentsNTSP(AbstractNTSGrammar agra, List<String> category, String lhs,
      List<String> symbols, Token token) {
    super(agra, category, lhs, symbols, token);
  }

  @Override void handleOtherCategories(List<String> category, String lhs,
      Token token) throws ParseException {
    if (category.get(0).equals("P") ) {
      findXOrAddCategory();
    }
  }

  protected abstract void findXOrAddCategory() throws ParseException;
}

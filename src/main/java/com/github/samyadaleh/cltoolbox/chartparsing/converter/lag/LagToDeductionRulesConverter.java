package com.github.samyadaleh.cltoolbox.chartparsing.converter.lag;

import com.github.samyadaleh.cltoolbox.chartparsing.ParsingSchema;
import com.github.samyadaleh.cltoolbox.chartparsing.StaticDeductionRule;
import com.github.samyadaleh.cltoolbox.chartparsing.lag.LagRuleApplication;
import com.github.samyadaleh.cltoolbox.common.ArrayUtils;
import com.github.samyadaleh.cltoolbox.common.lag.Lag;
import com.github.samyadaleh.cltoolbox.common.lag.LagRule;
import com.github.samyadaleh.cltoolbox.common.lag.LagState;
import com.github.samyadaleh.cltoolbox.common.lag.LagWord;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.github.samyadaleh.cltoolbox.common.Constants.DEDUCTION_RULE_LAG_DEDUCTION_AXIOM;

/**
 * Class that contains the methods to convert LAGs into Parsing Schemas.
 */
public class LagToDeductionRulesConverter {

  /**
   * Returns a Parsing Schema for a Left-Associatve Grammar.
   */
  public static ParsingSchema lagToDeductionRules(Lag lag, String w)
      throws ParseException {
    String[] wSplit = w.split(" ");
    ParsingSchema schema = new ParsingSchema();
    List<List<String[]>> initialCategories = new ArrayList<>();
    for (String letter : wSplit) {
      List<String[]> lxWordLookupResult = new ArrayList<>();
      for (LagWord lagWord : lag.getLexicon()) {
        if (letter.equals(lagWord.getWordSurface())) {
          lxWordLookupResult.add(lagWord.getCategory());
        }
      }
      if (lxWordLookupResult.size() == 0) {
        throw new ParseException("No lexicon entry for symbol " + letter, 0);
      }
      initialCategories.add(lxWordLookupResult);
    }
    List<String[][]> initialCategoriesSwapped =
        swapInitialCategories(initialCategories);

    for (String[][] initialCategorySwapped : initialCategoriesSwapped) {
      for (LagState initialState : lag.getInitialStates()) {
        if (ArrayUtils
            .match(initialCategorySwapped[0], initialState.getCategory())) {
          StaticDeductionRule axiom = new StaticDeductionRule();
          LagChartItem consequence = new LagChartItem(initialCategorySwapped,
              initialState.getRulePackage());
          axiom.addConsequence(consequence);
          axiom.setName(DEDUCTION_RULE_LAG_DEDUCTION_AXIOM);
          schema.addAxiom(axiom);
        }
      }
    }
    for (Map.Entry<String, LagRule> entry : lag.getLagRules().entrySet()) {
      LagRuleApplication rule =
          new LagRuleApplication(entry.getKey(), entry.getValue());
      schema.addRule(rule);
    }
    for (LagState finalState : lag.getFinalStates()) {
      LagChartItem goal =
          new LagChartItem(new String[][] {finalState.getCategory()},
              finalState.getRulePackage());
      schema.addGoal(goal);
    }
    return schema;
  }

  static List<String[][]> swapInitialCategories(
      List<List<String[]>> initialCategories) {
    List<List<String[]>> swappedCategoriesList = new ArrayList<>();
    for (String[] category : initialCategories.get(0)){
      List<String[]> categoryList = new ArrayList<>();
      categoryList.add(category);
      swappedCategoriesList.add(categoryList);
    }
    for (int i = 1; i < initialCategories.size(); i++) {
      if (initialCategories.get(i).size() == 1) {
        for(List<String[]> swappedCategories : swappedCategoriesList) {
          swappedCategories.add(initialCategories.get(i).get(0));
        }
      } else {
        List<List<String[]>> newSwappedCategoriesList = new ArrayList<>();
        for (List<String[]> swappedCategories : swappedCategoriesList) {
          for (String[] appendCategories : initialCategories.get(i)) {
            newSwappedCategoriesList.add(new ArrayList<>(swappedCategories));
            newSwappedCategoriesList.get(newSwappedCategoriesList.size() - 1)
                .add(appendCategories);
          }
        }
        swappedCategoriesList = newSwappedCategoriesList;
      }
    }
    List<String[][]> swappedCategories = new ArrayList<>();
    for (List<String[]> someList : swappedCategoriesList) {
      swappedCategories.add(someList.toArray(new String[0][]));
    }
    return swappedCategories;
  }
}

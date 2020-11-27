package com.github.samyadaleh.cltoolbox.chartparsing.converter.tag;

import com.github.samyadaleh.cltoolbox.chartparsing.*;
import com.github.samyadaleh.cltoolbox.chartparsing.dynamicdeductionrule.DynamicDeductionRuleInterface;
import com.github.samyadaleh.cltoolbox.chartparsing.item.ChartItemInterface;
import com.github.samyadaleh.cltoolbox.chartparsing.item.DeductionChartItem;
import com.github.samyadaleh.cltoolbox.chartparsing.tag.earleyprefixvalid.*;
import com.github.samyadaleh.cltoolbox.common.tag.Tag;
import com.github.samyadaleh.cltoolbox.common.tag.Tree;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.github.samyadaleh.cltoolbox.common.Constants.DEDUCTION_RULE_TAG_EARLEY_AXIOM;

public class TagToEarleyPrefixValidRulesConverter {
  public static ParsingSchema tagToEarleyPrefixValidRules(Tag tag, String w) {
    String[] wSplit = w.split(" ");
    ParsingSchema schema = new ParsingSchema();
    Set<String> iniTreesNameSet = tag.getInitialTreeNames();
    String[] iniTreeNames = iniTreesNameSet.toArray(new String[0]);
    Set<String> auxTreesNameSet = tag.getAuxiliaryTreeNames();
    String[] auxTreeNames = auxTreesNameSet.toArray(new String[0]);

    for (String iniTreeName : iniTreeNames) {
      if (tag.getInitialTree(iniTreeName).getRoot().getLabel()
          .equals(tag.getStartSymbol())) {
        StaticDeductionRule initialize = new StaticDeductionRule();
        ChartItemInterface consequence =
            new DeductionChartItem(iniTreeName, "", "la", "0", "0", "-", "-",
                "0", "0");
        List<Tree> derivedTrees = new ArrayList<>();
        derivedTrees.add(tag.getInitialTree(iniTreeName));
        consequence.setTrees(derivedTrees);
        initialize.addConsequence(consequence);
        initialize.setName(DEDUCTION_RULE_TAG_EARLEY_AXIOM);
        schema.addAxiom(initialize);
        if ("".equals(w)) {
          schema.addGoal(
              new DeductionChartItem(iniTreeName, "", "ra", "0", "0", "-", "-",
                  "0", "0"));
        } else {
          schema.addGoal(
              new DeductionChartItem(iniTreeName, "", "ra", "0", "0", "-", "-",
                  String.valueOf(wSplit.length), "0"));
        }
      }

      DynamicDeductionRuleInterface predictSubst =
          new TagEarleyPrefixValidPredictSubst(iniTreeName, tag);
      schema.addRule(predictSubst);
    }

    for (String auxTreeName : auxTreeNames) {
      DynamicDeductionRuleInterface predictAdjoinable =
          new TagEarleyPrefixValidPredictAdjoinable(auxTreeName, tag);
      schema.addRule(predictAdjoinable);
    }

    DynamicDeductionRuleInterface scanTerm =
        new TagEarleyPrefixValidScanTerm(wSplit, tag);
    schema.addRule(scanTerm);
    DynamicDeductionRuleInterface scanEps =
        new TagEarleyPrefixValidScanEps(tag);
    schema.addRule(scanEps);
    DynamicDeductionRuleInterface convertRb =
        new TagEarleyPrefixValidConvertRb();
    schema.addRule(convertRb);
    DynamicDeductionRuleInterface convertLa1 =
        new TagEarleyPrefixValidConvertLa1();
    schema.addRule(convertLa1);
    DynamicDeductionRuleInterface convertLa2 =
        new TagEarleyPrefixValidConvertLa2();
    schema.addRule(convertLa2);
    DynamicDeductionRuleInterface predictNoAdj =
        new TagEarleyPrefixValidPredictNoAdj(tag);
    schema.addRule(predictNoAdj);
    DynamicDeductionRuleInterface predictAdjoined =
        new TagEarleyPrefixValidPredictAdjoined(tag);
    schema.addRule(predictAdjoined);
    DynamicDeductionRuleInterface completeFoot =
        new TagEarleyPrefixValidCompleteFoot(tag);
    schema.addRule(completeFoot);
    DynamicDeductionRuleInterface adjoin = new TagEarleyPrefixValidAdjoin(tag);
    schema.addRule(adjoin);
    DynamicDeductionRuleInterface completeNode =
        new TagEarleyPrefixValidCompleteNode(tag);
    schema.addRule(completeNode);
    DynamicDeductionRuleInterface moveDown =
        new TagEarleyPrefixValidMoveDown(tag);
    schema.addRule(moveDown);
    DynamicDeductionRuleInterface moveRight =
        new TagEarleyPrefixValidMoveRight(tag);
    schema.addRule(moveRight);
    DynamicDeductionRuleInterface moveUp = new TagEarleyPrefixValidMoveUp(tag);
    schema.addRule(moveUp);
    DynamicDeductionRuleInterface substitute =
        new TagEarleyPrefixValidSubstitute(tag);
    schema.addRule(substitute);

    return schema;
  }
}

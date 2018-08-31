package com.github.samyadaleh.cltoolbox.chartparsing.converter.tag;

import com.github.samyadaleh.cltoolbox.chartparsing.*;
import com.github.samyadaleh.cltoolbox.chartparsing.dynamicdeductionrule.DynamicDeductionRuleInterface;
import com.github.samyadaleh.cltoolbox.chartparsing.item.ChartItemInterface;
import com.github.samyadaleh.cltoolbox.chartparsing.item.DeductionChartItem;
import com.github.samyadaleh.cltoolbox.chartparsing.tag.earley.*;
import com.github.samyadaleh.cltoolbox.common.tag.Tag;
import com.github.samyadaleh.cltoolbox.common.tag.Tree;
import com.github.samyadaleh.cltoolbox.common.tag.Vertex;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class TagToEarleyRulesConverter {
  /**
   * Returns a parsing schema for Earley parsing of the given input w with tag.
   */
  public static ParsingSchema tagToEarleyRules(Tag tag, String w) {
    String[] wSplit = w.split(" ");
    ParsingSchema schema = new ParsingSchema();
    Set<String> iniTreesNameSet = tag.getInitialTreeNames();
    Set<String> auxTreesNameSet = tag.getAuxiliaryTreeNames();
    Set<String> treesNameSet = tag.getTreeNames();

    DynamicDeductionRuleInterface scanTerm = new TagEarleyScanTerm(wSplit, tag);
    schema.addRule(scanTerm);
    DynamicDeductionRuleInterface scanEps = new TagEarleyScanEps(tag);
    schema.addRule(scanEps);
    DynamicDeductionRuleInterface predictNoAdj = new TagEarleyPredictNoAdj(tag);
    schema.addRule(predictNoAdj);
    DynamicDeductionRuleInterface completeFoot = new TagEarleyCompleteFoot(tag);
    schema.addRule(completeFoot);
    DynamicDeductionRuleInterface completeNode = new TagEarleyCompleteNode(tag);
    schema.addRule(completeNode);
    DynamicDeductionRuleInterface adjoin = new TagEarleyAdjoin(tag);
    schema.addRule(adjoin);
    DynamicDeductionRuleInterface moveDown = new TagEarleyMoveDown(tag);
    schema.addRule(moveDown);
    DynamicDeductionRuleInterface moveRight = new TagEarleyMoveRight(tag);
    schema.addRule(moveRight);
    DynamicDeductionRuleInterface moveUp = new TagEarleyMoveUp(tag);
    schema.addRule(moveUp);

    for (String auxTreeName : auxTreesNameSet) {
      DynamicDeductionRuleInterface predictAdjoinable =
          new TagEarleyPredictAdjoinable(auxTreeName, tag);
      schema.addRule(predictAdjoinable);
    }

    for (String treeName : treesNameSet) {
      for (Vertex p : tag.getTree(treeName).getVertexes()) {
        DynamicDeductionRuleInterface predictAdjoined =
            new TagEarleyPredictAdjoined(treeName, p.getGornAddress(), tag);
        schema.addRule(predictAdjoined);
        if (tag.isSubstitutionNode(p, treeName)) {
          DynamicDeductionRuleInterface substitute =
              new TagEarleySubstitute(treeName, p.getGornAddress(), tag);
          schema.addRule(substitute);
        }
      }
    }

    for (String iniTreeName : iniTreesNameSet) {
      if (tag.getInitialTree(iniTreeName).getRoot().getLabel()
          .equals(tag.getStartSymbol())) {
        StaticDeductionRule initialize = new StaticDeductionRule();
        ChartItemInterface consequence =
            new DeductionChartItem(iniTreeName, "", "la", "0", "-", "-", "0",
                "0");
        List<Tree> derivedTrees = new ArrayList<>();
        derivedTrees.add(tag.getInitialTree(iniTreeName));
        consequence.setTrees(derivedTrees);
        initialize.addConsequence(consequence);
        initialize.setName("initialize");
        schema.addAxiom(initialize);
        schema.addGoal(
            new DeductionChartItem(iniTreeName, "", "ra", "0", "-", "-",
                String.valueOf(wSplit.length), "0"));
      }

      DynamicDeductionRuleInterface predictSubst =
          new TagEarleyPredictSubst(iniTreeName, tag);
      schema.addRule(predictSubst);
    }
    return schema;
  }
}

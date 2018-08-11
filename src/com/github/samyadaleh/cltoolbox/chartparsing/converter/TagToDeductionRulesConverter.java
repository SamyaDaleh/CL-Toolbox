package com.github.samyadaleh.cltoolbox.chartparsing.converter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.github.samyadaleh.cltoolbox.chartparsing.DeductionChartItem;
import com.github.samyadaleh.cltoolbox.chartparsing.DynamicDeductionRuleInterface;
import com.github.samyadaleh.cltoolbox.chartparsing.ChartItemInterface;
import com.github.samyadaleh.cltoolbox.chartparsing.ParsingSchema;
import com.github.samyadaleh.cltoolbox.chartparsing.StaticDeductionRule;
import com.github.samyadaleh.cltoolbox.chartparsing.tag.cyk.TagCykAdjoin;
import com.github.samyadaleh.cltoolbox.chartparsing.tag.cyk.TagCykMoveBinary;
import com.github.samyadaleh.cltoolbox.chartparsing.tag.cyk.TagCykMoveUnary;
import com.github.samyadaleh.cltoolbox.chartparsing.tag.cyk.TagCykNullAdjoin;
import com.github.samyadaleh.cltoolbox.chartparsing.tag.cyk.TagCykSubstitute;
import com.github.samyadaleh.cltoolbox.chartparsing.tag.earley.TagEarleyAdjoin;
import com.github.samyadaleh.cltoolbox.chartparsing.tag.earley.TagEarleyCompleteFoot;
import com.github.samyadaleh.cltoolbox.chartparsing.tag.earley.TagEarleyCompleteNode;
import com.github.samyadaleh.cltoolbox.chartparsing.tag.earley.TagEarleyMoveDown;
import com.github.samyadaleh.cltoolbox.chartparsing.tag.earley.TagEarleyMoveRight;
import com.github.samyadaleh.cltoolbox.chartparsing.tag.earley.TagEarleyMoveUp;
import com.github.samyadaleh.cltoolbox.chartparsing.tag.earley.TagEarleyPredictAdjoinable;
import com.github.samyadaleh.cltoolbox.chartparsing.tag.earley.TagEarleyPredictAdjoined;
import com.github.samyadaleh.cltoolbox.chartparsing.tag.earley.TagEarleyPredictNoAdj;
import com.github.samyadaleh.cltoolbox.chartparsing.tag.earley.TagEarleyPredictSubst;
import com.github.samyadaleh.cltoolbox.chartparsing.tag.earley.TagEarleyScanEps;
import com.github.samyadaleh.cltoolbox.chartparsing.tag.earley.TagEarleyScanTerm;
import com.github.samyadaleh.cltoolbox.chartparsing.tag.earley.TagEarleySubstitute;
import com.github.samyadaleh.cltoolbox.chartparsing.tag.earleyprefixvalid.TagEarleyPrefixValidAdjoin;
import com.github.samyadaleh.cltoolbox.chartparsing.tag.earleyprefixvalid.TagEarleyPrefixValidCompleteFoot;
import com.github.samyadaleh.cltoolbox.chartparsing.tag.earleyprefixvalid.TagEarleyPrefixValidCompleteNode;
import com.github.samyadaleh.cltoolbox.chartparsing.tag.earleyprefixvalid.TagEarleyPrefixValidConvertLa1;
import com.github.samyadaleh.cltoolbox.chartparsing.tag.earleyprefixvalid.TagEarleyPrefixValidConvertLa2;
import com.github.samyadaleh.cltoolbox.chartparsing.tag.earleyprefixvalid.TagEarleyPrefixValidConvertRb;
import com.github.samyadaleh.cltoolbox.chartparsing.tag.earleyprefixvalid.TagEarleyPrefixValidMoveDown;
import com.github.samyadaleh.cltoolbox.chartparsing.tag.earleyprefixvalid.TagEarleyPrefixValidMoveRight;
import com.github.samyadaleh.cltoolbox.chartparsing.tag.earleyprefixvalid.TagEarleyPrefixValidMoveUp;
import com.github.samyadaleh.cltoolbox.chartparsing.tag.earleyprefixvalid.TagEarleyPrefixValidPredictAdjoinable;
import com.github.samyadaleh.cltoolbox.chartparsing.tag.earleyprefixvalid.TagEarleyPrefixValidPredictAdjoined;
import com.github.samyadaleh.cltoolbox.chartparsing.tag.earleyprefixvalid.TagEarleyPrefixValidPredictNoAdj;
import com.github.samyadaleh.cltoolbox.chartparsing.tag.earleyprefixvalid.TagEarleyPrefixValidPredictSubst;
import com.github.samyadaleh.cltoolbox.chartparsing.tag.earleyprefixvalid.TagEarleyPrefixValidScanEps;
import com.github.samyadaleh.cltoolbox.chartparsing.tag.earleyprefixvalid.TagEarleyPrefixValidScanTerm;
import com.github.samyadaleh.cltoolbox.chartparsing.tag.earleyprefixvalid.TagEarleyPrefixValidSubstitute;
import com.github.samyadaleh.cltoolbox.common.tag.Tag;
import com.github.samyadaleh.cltoolbox.common.tag.Tree;
import com.github.samyadaleh.cltoolbox.common.tag.Vertex;

/**
 * Convertes Tree adjoining grammars to parsing schemes for the respective
 * parsing algorithms. Based on the slides from Laura Kallmeyer about TAG
 * Parsing https://user.phil.hhu.de/~kallmeyer/ParsingBeyondCFG/4tag-parsing.pdf
 */
public class TagToDeductionRulesConverter {

  /** Returns a parsing schema for CYK parsing of the given input w with tag. */
  public static ParsingSchema tagToCykExtendedRules(Tag tag, String w) {
    if (!tag.isBinarized()) {
      System.out.println("TAG is not binarized, CYK-Parsing not applicable.");
      return null;
    }
    String[] wSplit = w.split(" ");
    ParsingSchema schema = new ParsingSchema();
    Set<String> iniTreesNameSet = tag.getInitialTreeNames();
    Set<String> auxTreesNameSet = tag.getAuxiliaryTreeNames();
    Set<String> treesNameSet = tag.getTreeNames();

    DynamicDeductionRuleInterface moveUnary = new TagCykMoveUnary(tag);
    schema.addRule(moveUnary);
    DynamicDeductionRuleInterface moveBinary = new TagCykMoveBinary();
    schema.addRule(moveBinary);
    DynamicDeductionRuleInterface nullAdjoin = new TagCykNullAdjoin(tag);
    schema.addRule(nullAdjoin);
    DynamicDeductionRuleInterface adjoin = new TagCykAdjoin(tag);
    schema.addRule(adjoin);

    for (int i = 0; i < wSplit.length; i++) {
      for (String treeName : treesNameSet) {
        for (Vertex p : tag.getTree(treeName).getVertexes()) {
          if (p.getLabel().equals(wSplit[i])) {
            StaticDeductionRule lexScan = new StaticDeductionRule();
            ChartItemInterface consequence =
              new DeductionChartItem(treeName, p.getGornAddress() + "⊤",
                String.valueOf(i), "-", "-", String.valueOf(i + 1));
            List<Tree> derivedTrees = new ArrayList<Tree>();
            derivedTrees.add(tag.getTree(treeName));
            consequence.setTrees(derivedTrees);
            lexScan.addConsequence(consequence);
            lexScan.setName("lex-scan " + wSplit[i]);
            schema.addAxiom(lexScan);
          } else if (p.getLabel().equals("")) {
            StaticDeductionRule epsScan = new StaticDeductionRule();
            ChartItemInterface consequence =
              new DeductionChartItem(treeName, p.getGornAddress() + "⊤",
                String.valueOf(i), "-", "-", String.valueOf(i));
            List<Tree> derivedTrees = new ArrayList<Tree>();
            derivedTrees.add(tag.getTree(treeName));
            consequence.setTrees(derivedTrees);
            epsScan.addConsequence(consequence);
            epsScan.setName("eps-scan");
            schema.addAxiom(epsScan);
          }
          if (tag.isSubstitutionNode(p, treeName)) {
            DynamicDeductionRuleInterface substitute =
              new TagCykSubstitute(treeName, p.getGornAddress(), tag);
            schema.addRule(substitute);
          }
        }
      }

      for (String iniTreeName : iniTreesNameSet) {
        Tree iniTree = tag.getInitialTree(iniTreeName);
        if (iniTree.getRoot().getLabel().equals(tag.getStartSymbol())) {
          schema.addGoal(new DeductionChartItem(iniTreeName, "⊤", "0", "-", "-",
            String.valueOf(wSplit.length)));
        }
      }

      for (int j = i; j <= wSplit.length; j++) {
        for (String auxTree : auxTreesNameSet) {
          StaticDeductionRule footPredict = new StaticDeductionRule();
          String footGorn =
            tag.getAuxiliaryTree(auxTree).getFoot().getGornAddress();
          ChartItemInterface consequence =
            new DeductionChartItem(auxTree, footGorn + "⊤", String.valueOf(i),
              String.valueOf(i), String.valueOf(j), String.valueOf(j));
          List<Tree> derivedTrees = new ArrayList<Tree>();
          derivedTrees.add(tag.getAuxiliaryTree(auxTree));
          consequence.setTrees(derivedTrees);
          footPredict.addConsequence(consequence);
          footPredict.setName("foot-predict");
          schema.addAxiom(footPredict);
        }
      }
    }

    return schema;
  }

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
          new DeductionChartItem(iniTreeName, "", "la", "0", "-", "-", "0", "0");
        List<Tree> derivedTrees = new ArrayList<Tree>();
        derivedTrees.add(tag.getInitialTree(iniTreeName));
        consequence.setTrees(derivedTrees);
        initialize.addConsequence(consequence);
        initialize.setName("initialize");
        schema.addAxiom(initialize);
        schema.addGoal(new DeductionChartItem(iniTreeName, "", "ra", "0", "-", "-",
          String.valueOf(wSplit.length), "0"));
      }

      DynamicDeductionRuleInterface predictSubst =
        new TagEarleyPredictSubst(iniTreeName, tag);
      schema.addRule(predictSubst);
    }
    return schema;
  }

  public static ParsingSchema tagToEarleyPrefixValidRules(Tag tag, String w) {
    String[] wSplit = w.split(" ");
    ParsingSchema schema = new ParsingSchema();
    Set<String> iniTreesNameSet = tag.getInitialTreeNames();
    String[] iniTreeNames =
      iniTreesNameSet.toArray(new String[iniTreesNameSet.size()]);
    Set<String> auxTreesNameSet = tag.getAuxiliaryTreeNames();
    String[] auxTreeNames =
      auxTreesNameSet.toArray(new String[auxTreesNameSet.size()]);

    for (String iniTreeName : iniTreeNames) {
      if (tag.getInitialTree(iniTreeName).getRoot().getLabel()
        .equals(tag.getStartSymbol())) {
        StaticDeductionRule initialize = new StaticDeductionRule();
        ChartItemInterface consequence = new DeductionChartItem(iniTreeName, "", "la", "0", "0",
          "-", "-", "0", "0");
        List<Tree> derivedTrees = new ArrayList<Tree>();
        derivedTrees.add(tag.getInitialTree(iniTreeName));
        consequence.setTrees(derivedTrees);
        initialize.addConsequence(consequence);
        initialize.setName("initialize");
        schema.addAxiom(initialize);
        schema.addGoal(new DeductionChartItem(iniTreeName, "", "ra", "0", "0", "-",
          "-", String.valueOf(wSplit.length), "0"));
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
    DynamicDeductionRuleInterface scanEps = new TagEarleyPrefixValidScanEps(tag);
    schema.addRule(scanEps);
    DynamicDeductionRuleInterface convertRb = new TagEarleyPrefixValidConvertRb();
    schema.addRule(convertRb);
    DynamicDeductionRuleInterface convertLa1 = new TagEarleyPrefixValidConvertLa1();
    schema.addRule(convertLa1);
    DynamicDeductionRuleInterface convertLa2 = new TagEarleyPrefixValidConvertLa2();
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
    DynamicDeductionRuleInterface moveDown = new TagEarleyPrefixValidMoveDown(tag);
    schema.addRule(moveDown);
    DynamicDeductionRuleInterface moveRight = new TagEarleyPrefixValidMoveRight(tag);
    schema.addRule(moveRight);
    DynamicDeductionRuleInterface moveUp = new TagEarleyPrefixValidMoveUp(tag);
    schema.addRule(moveUp);
    DynamicDeductionRuleInterface substitute = new TagEarleyPrefixValidSubstitute(tag);
    schema.addRule(substitute);

    return schema;
  }
}

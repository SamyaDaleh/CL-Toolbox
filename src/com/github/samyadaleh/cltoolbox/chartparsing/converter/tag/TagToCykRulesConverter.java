package com.github.samyadaleh.cltoolbox.chartparsing.converter.tag;

import com.github.samyadaleh.cltoolbox.chartparsing.*;
import com.github.samyadaleh.cltoolbox.chartparsing.dynamicdeductionrule.DynamicDeductionRuleInterface;
import com.github.samyadaleh.cltoolbox.chartparsing.item.ChartItemInterface;
import com.github.samyadaleh.cltoolbox.chartparsing.item.DeductionChartItem;
import com.github.samyadaleh.cltoolbox.chartparsing.tag.cyk.*;
import com.github.samyadaleh.cltoolbox.common.tag.Tag;
import com.github.samyadaleh.cltoolbox.common.tag.Tree;
import com.github.samyadaleh.cltoolbox.common.tag.Vertex;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TagToCykRulesConverter {
  private static final Logger log = LogManager.getLogger();

  /**
   * Returns a parsing schema for CYK parsing of the given input w with tag.
   */
  public static ParsingSchema tagToCykExtendedRules(Tag tag, String w) {
    if (!tag.isBinarized()) {
      log.info("TAG is not binarized, CYK-Parsing not applicable.");
      return null;
    }
    String[] wSplit = w.split(" ");
    ParsingSchema schema = new ParsingSchema();
    DynamicDeductionRuleInterface moveUnary = new TagCykMoveUnary(tag);
    schema.addRule(moveUnary);
    DynamicDeductionRuleInterface moveBinary = new TagCykMoveBinary();
    schema.addRule(moveBinary);
    addAllCykRulesExceptMove(tag, wSplit, schema);
    return schema;
  }

  /**
   * Returns a parsing schema for CYK parsing of the given input w with tag.
   */
  public static ParsingSchema tagToCykGeneralRules(Tag tag, String w) {
    String[] wSplit = w.split(" ");
    ParsingSchema schema = new ParsingSchema();
    Set<Integer> childCounts = new HashSet<>();
    for (String treeName : tag.getTreeNames()) {
      Tree tree = tag.getTree(treeName);
      for (Vertex p : tree.getVertexes()) {
        List<Vertex> children = tree.getChildren(p);
        if (children.size() > 0) {
          childCounts.add(children.size());
        }
      }
    }
    for (Integer count : childCounts) {
      DynamicDeductionRuleInterface moveGeneral =
          new TagCykMoveGeneral(tag, count);
      schema.addRule(moveGeneral);
    }
    addAllCykRulesExceptMove(tag, wSplit, schema);
    return schema;
  }

  private static void addAllCykRulesExceptMove(Tag tag, String[] wSplit,
      ParsingSchema schema) {
    Set<String> treesNameSet = tag.getTreeNames();

    DynamicDeductionRuleInterface nullAdjoin = new TagCykNullAdjoin(tag);
    schema.addRule(nullAdjoin);
    DynamicDeductionRuleInterface adjoin = new TagCykAdjoin(tag);
    schema.addRule(adjoin);

    for (int i = 0; i < wSplit.length; i++) {
      for (String treeName : treesNameSet) {
        for (Vertex p : tag.getTree(treeName).getVertexes()) {
          addCykScanRules(tag, wSplit, schema, i, treeName, p);
          addCykSubstitutionRule(tag, schema, treeName, p);
        }
      }
      addCykFootPredictRules(tag, wSplit, schema, i);
    }
    addCykGoalItems(tag, wSplit, schema);
  }

  private static void addCykFootPredictRules(Tag tag, String[] wSplit,
      ParsingSchema schema, int i) {
    Set<String> auxTreesNameSet = tag.getAuxiliaryTreeNames();
    for (int j = i; j <= wSplit.length; j++) {
      for (String auxTree : auxTreesNameSet) {
        StaticDeductionRule footPredict = new StaticDeductionRule();
        String footGorn =
            tag.getAuxiliaryTree(auxTree).getFoot().getGornAddress();
        ChartItemInterface consequence =
            new DeductionChartItem(auxTree, footGorn + "⊤", String.valueOf(i),
                String.valueOf(i), String.valueOf(j), String.valueOf(j));
        List<Tree> derivedTrees = new ArrayList<>();
        derivedTrees.add(tag.getAuxiliaryTree(auxTree));
        consequence.setTrees(derivedTrees);
        footPredict.addConsequence(consequence);
        footPredict.setName("foot-predict");
        schema.addAxiom(footPredict);
      }
    }
  }

  private static void addCykGoalItems(Tag tag, String[] wSplit,
      ParsingSchema schema) {
    Set<String> iniTreesNameSet = tag.getInitialTreeNames();
    for (String iniTreeName : iniTreesNameSet) {
      Tree iniTree = tag.getInitialTree(iniTreeName);
      if (iniTree.getRoot().getLabel().equals(tag.getStartSymbol())) {
        schema.addGoal(new DeductionChartItem(iniTreeName, "⊤", "0", "-", "-",
            String.valueOf(wSplit.length)));
      }
    }
  }

  private static void addCykSubstitutionRule(Tag tag, ParsingSchema schema,
      String treeName, Vertex p) {
    if (tag.isSubstitutionNode(p, treeName)) {
      DynamicDeductionRuleInterface substitute =
          new TagCykSubstitute(treeName, p.getGornAddress(), tag);
      schema.addRule(substitute);
    }
  }

  private static void addCykScanRules(Tag tag, String[] wSplit,
      ParsingSchema schema, int i, String treeName, Vertex p) {
    if (p.getLabel().equals(wSplit[i])) {
      StaticDeductionRule lexScan = new StaticDeductionRule();
      ChartItemInterface consequence =
          new DeductionChartItem(treeName, p.getGornAddress() + "⊤",
              String.valueOf(i), "-", "-", String.valueOf(i + 1));
      List<Tree> derivedTrees = new ArrayList<>();
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
      List<Tree> derivedTrees = new ArrayList<>();
      derivedTrees.add(tag.getTree(treeName));
      consequence.setTrees(derivedTrees);
      epsScan.addConsequence(consequence);
      epsScan.setName("eps-scan");
      schema.addAxiom(epsScan);
    }
  }
}

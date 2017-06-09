package chartparsing.converter;

import java.util.Set;

import chartparsing.DynamicDeductionRule;
import chartparsing.ParsingSchema;
import chartparsing.StaticDeductionRule;
import chartparsing.tagrules.TagCykAdjoin;
import chartparsing.tagrules.TagCykMoveBinary;
import chartparsing.tagrules.TagCykMoveUnary;
import chartparsing.tagrules.TagCykNullAdjoin;
import chartparsing.tagrules.TagCykSubstitute;
import chartparsing.tagrules.TagEarleyAdjoin;
import chartparsing.tagrules.TagEarleyCompleteFoot;
import chartparsing.tagrules.TagEarleyCompleteNode;
import chartparsing.tagrules.TagEarleyMoveDown;
import chartparsing.tagrules.TagEarleyMoveRight;
import chartparsing.tagrules.TagEarleyMoveUp;
import chartparsing.tagrules.TagEarleyPredictAdjoinable;
import chartparsing.tagrules.TagEarleyPredictAdjoined;
import chartparsing.tagrules.TagEarleyPredictNoAdj;
import chartparsing.tagrules.TagEarleyPredictSubst;
import chartparsing.tagrules.TagEarleyScanEps;
import chartparsing.tagrules.TagEarleyScanTerm;
import chartparsing.tagrules.TagEarleySubstitute;
import common.tag.Tag;
import common.tag.TagCykItem;
import common.tag.TagEarleyItem;
import common.tag.Tree;
import common.tag.Vertex;

/** Convertes Tree adjoining grammars to parsing schemes for the respective
 * parsing algorithms. Based on the slides from Laura Kallmeyer about TAG
 * Parsing
 * https://user.phil.hhu.de/~kallmeyer/ParsingBeyondCFG/4tag-parsing.pdf */
public class TagToDeductionRulesConverter {

  /** Returns a parsing schema for CYK parsing of the given input w with tag. */
  public static ParsingSchema tagToCykRules(Tag tag, String w) {
    if (!tag.isBinarized()) {
      System.out.println("TAG is not binarized, CYK-Parsing not applicable.");
      return null;
    }
    String[] wSplit = w.split(" ");
    ParsingSchema schema = new ParsingSchema();
    Set<String> iniTreesNameSet = tag.getInitialTreeNames();
    String[] iniTreeNames =
      iniTreesNameSet.toArray(new String[iniTreesNameSet.size()]);
    Set<String> auxTreesNameSet = tag.getAuxiliaryTreeNames();
    String[] auxTreeNames =
      auxTreesNameSet.toArray(new String[auxTreesNameSet.size()]);
    Set<String> treesNameSet = tag.getTreeNames();
    String[] treeNames = treesNameSet.toArray(new String[treesNameSet.size()]);

    DynamicDeductionRule moveUnary = new TagCykMoveUnary(tag);
    schema.addRule(moveUnary);
    DynamicDeductionRule moveBinary = new TagCykMoveBinary();
    schema.addRule(moveBinary);
    DynamicDeductionRule nullAdjoin = new TagCykNullAdjoin(tag);
    schema.addRule(nullAdjoin);
    DynamicDeductionRule adjoin = new TagCykAdjoin(tag);
    schema.addRule(adjoin);

    for (int i = 0; i < wSplit.length; i++) {
      for (String treeName : treeNames) {
        for (Vertex p : tag.getTree(treeName).getVertexes()) {
          if (p.getLabel().equals(wSplit[i])) {
            StaticDeductionRule lexScan = new StaticDeductionRule();
            lexScan.addConsequence(new TagCykItem(treeName,
              p.getGornAddress() + "⊤", i, null, null, i + 1));
            lexScan.setName("lex-scan " + wSplit[i]);
            schema.addAxiom(lexScan);
          }
          if (p.getLabel().equals("")) {
            StaticDeductionRule epsScan = new StaticDeductionRule();
            epsScan.addConsequence(new TagCykItem(treeName,
              p.getGornAddress() + "⊤", i, null, null, i));
            epsScan.setName("eps-scan");
            schema.addAxiom(epsScan);
          }
          if (tag.isSubstitutionNode(p, treeName)) {
            DynamicDeductionRule substitute =
              new TagCykSubstitute(treeName, p.getGornAddress(), tag);
            schema.addRule(substitute);
          }
        }
      }

      for (String iniTreeName : iniTreeNames) {
        Tree iniTree = tag.getInitialTree(iniTreeName);
        if (iniTree.getRoot().getLabel().equals(tag.getStartSymbol())) {
          schema.addGoal(
            new TagCykItem(iniTreeName, "⊤", 0, null, null, wSplit.length));
        }
      }

      for (int j = i; j <= wSplit.length; j++) {
        for (String auxTree : auxTreeNames) {
          StaticDeductionRule footPredict = new StaticDeductionRule();
          String footGorn =
            tag.getAuxiliaryTree(auxTree).getFoot().getGornAddress();
          footPredict.addConsequence(
            new TagCykItem(auxTree, footGorn + "⊤", i, i, j, j));
          footPredict.setName("foot-predict");
          schema.addAxiom(footPredict);
        }
      }
    }

    return schema;
  }

  /** Returns a parsing schema for Earley parsing of the given input w with
   * tag. */
  public static ParsingSchema tagToEarleyRules(Tag tag, String w) {
    String[] wSplit = w.split(" ");
    ParsingSchema schema = new ParsingSchema();
    Set<String> iniTreesNameSet = tag.getInitialTreeNames();
    String[] iniTreeNames =
      iniTreesNameSet.toArray(new String[iniTreesNameSet.size()]);
    Set<String> auxTreesNameSet = tag.getAuxiliaryTreeNames();
    String[] auxTreeNames =
      auxTreesNameSet.toArray(new String[auxTreesNameSet.size()]);
    Set<String> treesNameSet = tag.getTreeNames();
    String[] treeNames = treesNameSet.toArray(new String[treesNameSet.size()]);

    DynamicDeductionRule scanTerm = new TagEarleyScanTerm(wSplit, tag);
    schema.addRule(scanTerm);
    DynamicDeductionRule scanEps = new TagEarleyScanEps(tag);
    schema.addRule(scanEps);
    DynamicDeductionRule predictNoAdj = new TagEarleyPredictNoAdj(tag);
    schema.addRule(predictNoAdj);
    DynamicDeductionRule completeFoot = new TagEarleyCompleteFoot(tag);
    schema.addRule(completeFoot);
    DynamicDeductionRule completeNode = new TagEarleyCompleteNode(tag);
    schema.addRule(completeNode);
    DynamicDeductionRule adjoin = new TagEarleyAdjoin(tag);
    schema.addRule(adjoin);
    DynamicDeductionRule moveDown = new TagEarleyMoveDown(tag);
    schema.addRule(moveDown);
    DynamicDeductionRule moveRight = new TagEarleyMoveRight(tag);
    schema.addRule(moveRight);
    DynamicDeductionRule moveUp = new TagEarleyMoveUp(tag);
    schema.addRule(moveUp);

    for (String auxTreeName : auxTreeNames) {
      DynamicDeductionRule predictAdjoinable =
        new TagEarleyPredictAdjoinable(auxTreeName, tag);
      schema.addRule(predictAdjoinable);
    }

    for (String treeName : treeNames) {
      for (Vertex p : tag.getTree(treeName).getVertexes()) {
        DynamicDeductionRule predictAdjoined =
          new TagEarleyPredictAdjoined(treeName, p.getGornAddress(), tag);
        schema.addRule(predictAdjoined);
        if (tag.isSubstitutionNode(p, treeName)) {
          DynamicDeductionRule substitute =
            new TagEarleySubstitute(treeName, p.getGornAddress(), tag);
          schema.addRule(substitute);
        }
      }
    }

    for (String iniTreeName : iniTreeNames) {
      if (tag.getInitialTree(iniTreeName).getRoot().getLabel()
        .equals(tag.getStartSymbol())) {
        StaticDeductionRule initialize = new StaticDeductionRule();
        initialize.addConsequence(new TagEarleyItem(iniTreeName, "", "la", 0,
          (Integer) null, null, 0, false));
        initialize.setName("initialize");
        schema.addAxiom(initialize);
        schema.addGoal(new TagEarleyItem(iniTreeName, "", "ra", 0,
          (Integer) null, null, wSplit.length, false));
      }

      DynamicDeductionRule predictSubst =
        new TagEarleyPredictSubst(iniTreeName, tag);
      schema.addRule(predictSubst);
    }
    return schema;
  }
}

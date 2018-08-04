package chartparsing.converter;

import java.util.Set;

import chartparsing.DeductionItem;
import chartparsing.DynamicDeductionRule;
import chartparsing.Item;
import chartparsing.ParsingSchema;
import chartparsing.StaticDeductionRule;
import chartparsing.tag.cyk.TagCykAdjoin;
import chartparsing.tag.cyk.TagCykMoveBinary;
import chartparsing.tag.cyk.TagCykMoveUnary;
import chartparsing.tag.cyk.TagCykNullAdjoin;
import chartparsing.tag.cyk.TagCykSubstitute;
import chartparsing.tag.earley.TagEarleyAdjoin;
import chartparsing.tag.earley.TagEarleyCompleteFoot;
import chartparsing.tag.earley.TagEarleyCompleteNode;
import chartparsing.tag.earley.TagEarleyMoveDown;
import chartparsing.tag.earley.TagEarleyMoveRight;
import chartparsing.tag.earley.TagEarleyMoveUp;
import chartparsing.tag.earley.TagEarleyPredictAdjoinable;
import chartparsing.tag.earley.TagEarleyPredictAdjoined;
import chartparsing.tag.earley.TagEarleyPredictNoAdj;
import chartparsing.tag.earley.TagEarleyPredictSubst;
import chartparsing.tag.earley.TagEarleyScanEps;
import chartparsing.tag.earley.TagEarleyScanTerm;
import chartparsing.tag.earley.TagEarleySubstitute;
import chartparsing.tag.earleyprefixvalid.TagEarleyPrefixValidAdjoin;
import chartparsing.tag.earleyprefixvalid.TagEarleyPrefixValidCompleteFoot;
import chartparsing.tag.earleyprefixvalid.TagEarleyPrefixValidCompleteNode;
import chartparsing.tag.earleyprefixvalid.TagEarleyPrefixValidConvertLa1;
import chartparsing.tag.earleyprefixvalid.TagEarleyPrefixValidConvertLa2;
import chartparsing.tag.earleyprefixvalid.TagEarleyPrefixValidConvertRb;
import chartparsing.tag.earleyprefixvalid.TagEarleyPrefixValidMoveDown;
import chartparsing.tag.earleyprefixvalid.TagEarleyPrefixValidMoveRight;
import chartparsing.tag.earleyprefixvalid.TagEarleyPrefixValidMoveUp;
import chartparsing.tag.earleyprefixvalid.TagEarleyPrefixValidPredictAdjoinable;
import chartparsing.tag.earleyprefixvalid.TagEarleyPrefixValidPredictAdjoined;
import chartparsing.tag.earleyprefixvalid.TagEarleyPrefixValidPredictNoAdj;
import chartparsing.tag.earleyprefixvalid.TagEarleyPrefixValidPredictSubst;
import chartparsing.tag.earleyprefixvalid.TagEarleyPrefixValidScanEps;
import chartparsing.tag.earleyprefixvalid.TagEarleyPrefixValidScanTerm;
import chartparsing.tag.earleyprefixvalid.TagEarleyPrefixValidSubstitute;
import common.tag.Tag;
import common.tag.Tree;
import common.tag.Vertex;

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

    DynamicDeductionRule moveUnary = new TagCykMoveUnary(tag);
    schema.addRule(moveUnary);
    DynamicDeductionRule moveBinary = new TagCykMoveBinary();
    schema.addRule(moveBinary);
    DynamicDeductionRule nullAdjoin = new TagCykNullAdjoin(tag);
    schema.addRule(nullAdjoin);
    DynamicDeductionRule adjoin = new TagCykAdjoin(tag);
    schema.addRule(adjoin);

    for (int i = 0; i < wSplit.length; i++) {
      for (String treeName : treesNameSet) {
        for (Vertex p : tag.getTree(treeName).getVertexes()) {
          if (p.getLabel().equals(wSplit[i])) {
            StaticDeductionRule lexScan = new StaticDeductionRule();
            Item consequence =
              new DeductionItem(treeName, p.getGornAddress() + "⊤",
                String.valueOf(i), "-", "-", String.valueOf(i + 1));
            consequence.setTree(tag.getTree(treeName));
            lexScan.addConsequence(consequence);
            lexScan.setName("lex-scan " + wSplit[i]);
            schema.addAxiom(lexScan);
          } else if (p.getLabel().equals("")) {
            StaticDeductionRule epsScan = new StaticDeductionRule();
            Item consequence =
              new DeductionItem(treeName, p.getGornAddress() + "⊤",
                String.valueOf(i), "-", "-", String.valueOf(i));
            consequence.setTree(tag.getTree(treeName));
            epsScan.addConsequence(consequence);
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

      for (String iniTreeName : iniTreesNameSet) {
        Tree iniTree = tag.getInitialTree(iniTreeName);
        if (iniTree.getRoot().getLabel().equals(tag.getStartSymbol())) {
          schema.addGoal(new DeductionItem(iniTreeName, "⊤", "0", "-", "-",
            String.valueOf(wSplit.length)));
        }
      }

      for (int j = i; j <= wSplit.length; j++) {
        for (String auxTree : auxTreesNameSet) {
          StaticDeductionRule footPredict = new StaticDeductionRule();
          String footGorn =
            tag.getAuxiliaryTree(auxTree).getFoot().getGornAddress();
          Item consequence =
            new DeductionItem(auxTree, footGorn + "⊤", String.valueOf(i),
              String.valueOf(i), String.valueOf(j), String.valueOf(j));
          consequence.setTree(tag.getAuxiliaryTree(auxTree));
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

    for (String auxTreeName : auxTreesNameSet) {
      DynamicDeductionRule predictAdjoinable =
        new TagEarleyPredictAdjoinable(auxTreeName, tag);
      schema.addRule(predictAdjoinable);
    }

    for (String treeName : treesNameSet) {
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

    for (String iniTreeName : iniTreesNameSet) {
      if (tag.getInitialTree(iniTreeName).getRoot().getLabel()
        .equals(tag.getStartSymbol())) {
        StaticDeductionRule initialize = new StaticDeductionRule();
        Item consequence =
          new DeductionItem(iniTreeName, "", "la", "0", "-", "-", "0", "0");
        consequence.setTree(tag.getInitialTree(iniTreeName));
        initialize.addConsequence(consequence);
        initialize.setName("initialize");
        schema.addAxiom(initialize);
        schema.addGoal(new DeductionItem(iniTreeName, "", "ra", "0", "-", "-",
          String.valueOf(wSplit.length), "0"));
      }

      DynamicDeductionRule predictSubst =
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
        Item consequence = new DeductionItem(iniTreeName, "", "la", "0", "0",
          "-", "-", "0", "0");
        consequence.setTree(tag.getInitialTree(iniTreeName));
        initialize.addConsequence(consequence);
        initialize.setName("initialize");
        schema.addAxiom(initialize);
        schema.addGoal(new DeductionItem(iniTreeName, "", "ra", "0", "0", "-",
          "-", String.valueOf(wSplit.length), "0"));
      }

      DynamicDeductionRule predictSubst =
        new TagEarleyPrefixValidPredictSubst(iniTreeName, tag);
      schema.addRule(predictSubst);
    }

    for (String auxTreeName : auxTreeNames) {
      DynamicDeductionRule predictAdjoinable =
        new TagEarleyPrefixValidPredictAdjoinable(auxTreeName, tag);
      schema.addRule(predictAdjoinable);
    }

    DynamicDeductionRule scanTerm =
      new TagEarleyPrefixValidScanTerm(wSplit, tag);
    schema.addRule(scanTerm);
    DynamicDeductionRule scanEps = new TagEarleyPrefixValidScanEps(tag);
    schema.addRule(scanEps);
    DynamicDeductionRule convertRb = new TagEarleyPrefixValidConvertRb();
    schema.addRule(convertRb);
    DynamicDeductionRule convertLa1 = new TagEarleyPrefixValidConvertLa1();
    schema.addRule(convertLa1);
    DynamicDeductionRule convertLa2 = new TagEarleyPrefixValidConvertLa2();
    schema.addRule(convertLa2);
    DynamicDeductionRule predictNoAdj =
      new TagEarleyPrefixValidPredictNoAdj(tag);
    schema.addRule(predictNoAdj);
    DynamicDeductionRule predictAdjoined =
      new TagEarleyPrefixValidPredictAdjoined(tag);
    schema.addRule(predictAdjoined);
    DynamicDeductionRule completeFoot =
      new TagEarleyPrefixValidCompleteFoot(tag);
    schema.addRule(completeFoot);
    DynamicDeductionRule adjoin = new TagEarleyPrefixValidAdjoin(tag);
    schema.addRule(adjoin);
    DynamicDeductionRule completeNode =
      new TagEarleyPrefixValidCompleteNode(tag);
    schema.addRule(completeNode);
    DynamicDeductionRule moveDown = new TagEarleyPrefixValidMoveDown(tag);
    schema.addRule(moveDown);
    DynamicDeductionRule moveRight = new TagEarleyPrefixValidMoveRight(tag);
    schema.addRule(moveRight);
    DynamicDeductionRule moveUp = new TagEarleyPrefixValidMoveUp(tag);
    schema.addRule(moveUp);
    DynamicDeductionRule substitute = new TagEarleyPrefixValidSubstitute(tag);
    schema.addRule(substitute);

    return schema;
  }
}

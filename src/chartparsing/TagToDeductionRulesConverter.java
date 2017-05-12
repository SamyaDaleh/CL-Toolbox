package chartparsing;

import java.util.Set;

import chartparsing.tagrules.TagCykAdjoin;
import chartparsing.tagrules.TagCykMovebinary;
import chartparsing.tagrules.TagCykMoveunary;
import chartparsing.tagrules.TagCykNulladjoin;
import chartparsing.tagrules.TagCykSubstitute;
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

  /** Common entry point to comfortably call the different functions. */
  public static ParsingSchema TagToParsingSchema(Tag tag, String w,
    String schema) {
    switch (schema) {
    case "cyk":
      return TagToCykRules(tag, w);
    case "earley":
      return TagToEarleyRules(tag, w);
    default:
      return null;
    }
  }

  /** Returns a parsing schema for CYK parsing of the given input w with tag. */
  public static ParsingSchema TagToCykRules(Tag tag, String w) {
    if (!tag.isBinarized()) {
      System.out.println("TAG is not binarized, CYK-Parsing not applicable.");
      return null;
    }
    String[] wsplit = w.split(" ");
    ParsingSchema schema = new ParsingSchema();
    Set<String> initreesnameset = tag.getInitialTreeNames();
    String[] initreenames =
      initreesnameset.toArray(new String[initreesnameset.size()]);
    Set<String> auxtreesnameset = tag.getAuxiliaryTreeNames();
    String[] auxtreenames =
      auxtreesnameset.toArray(new String[auxtreesnameset.size()]);
    Set<String> treesnameset = tag.getTreeNames();
    String[] treenames = treesnameset.toArray(new String[treesnameset.size()]);

    DynamicDeductionRule moveunary = new TagCykMoveunary(tag);
    schema.addRule(moveunary);
    DynamicDeductionRule movebinary = new TagCykMovebinary();
    schema.addRule(movebinary);
    DynamicDeductionRule nulladjoin = new TagCykNulladjoin(tag);
    schema.addRule(nulladjoin);
    DynamicDeductionRule adjoin = new TagCykAdjoin(tag);
    schema.addRule(adjoin);

    for (int i = 0; i < wsplit.length; i++) {
      for (String treename : treenames) {
        for (Vertex p : tag.getTree(treename).getVertexes()) {
          if (p.getLabel().equals(wsplit[i])) {
            StaticDeductionRule lexscan = new StaticDeductionRule();
            lexscan.addConsequence(new TagCykItem(treename,
              p.getGornaddress() + "⊤", i, null, null, i + 1));
            lexscan.setName("lex-scan " + wsplit[i]);
            schema.addAxiom(lexscan);
            // System.out.println(lexscan.toString()); // DEBUG
          }
          if (p.getLabel().equals("")) {
            StaticDeductionRule epsscan = new StaticDeductionRule();
            epsscan.addConsequence(new TagCykItem(treename,
              p.getGornaddress() + "⊤", i, null, null, i));
            epsscan.setName("eps-scan");
            schema.addAxiom(epsscan);
            // System.out.println(epsscan.toString()); // DEBUG
          }
          if (tag.isSubstitutionNode(p, treename)) {
            DynamicDeductionRule substitute =
              new TagCykSubstitute(treename, p.getGornaddress(), tag);
            schema.addRule(substitute);
          }
        }
      }

      for (String initreename : initreenames) {
        Tree initree = tag.getInitialTree(initreename);
        if (initree.getRoot().getLabel().equals(tag.getStartSymbol())) {
          schema.addGoal(
            new TagCykItem(initreename, "⊤", 0, null, null, wsplit.length));
        }
      }

      for (int j = i; j <= wsplit.length; j++) {
        for (String auxtree : auxtreenames) {
          StaticDeductionRule footpredict = new StaticDeductionRule();
          String footgorn =
            tag.getAuxiliaryTree(auxtree).getFoot().getGornaddress();
          footpredict.addConsequence(
            new TagCykItem(auxtree, footgorn + "⊤", i, i, j, j));
          footpredict.setName("foot-predict");
          schema.addAxiom(footpredict);
          // System.out.println(footpredict.toString()); // DEBUG
        }
      }
    }

    return schema;
  }

  /** Returns a parsing schema for Earley parsing of the given input w with
   * tag. */
  public static ParsingSchema TagToEarleyRules(Tag tag, String w) {
    String[] wsplit = w.split(" ");
    ParsingSchema schema = new ParsingSchema();
    Set<String> initreesnameset = tag.getInitialTreeNames();
    String[] initreenames =
      initreesnameset.toArray(new String[initreesnameset.size()]);
    Set<String> auxtreesnameset = tag.getAuxiliaryTreeNames();
    String[] auxtreenames =
      auxtreesnameset.toArray(new String[auxtreesnameset.size()]);
    Set<String> treesnameset = tag.getTreeNames();
    String[] treenames = treesnameset.toArray(new String[treesnameset.size()]);

    DynamicDeductionRule scanterm = new TagEarleyScanterm(tag);
    schema.addRule(scanterm);
    DynamicDeductionRule scaneps = new TagEarleyScaneps(tag);
    schema.addRule(scaneps);
    DynamicDeductionRule predictnoadj = new TagEarleyPredictnoadjoin(tag);
    schema.addRule(predictnoadj);
    DynamicDeductionRule completefoot = new TagEarleyCompletefoot(tag);
    schema.addRule(completefoot);
    DynamicDeductionRule completenode = new TagEarleyCompletenode(tag);
    schema.addRule(completenode);
    DynamicDeductionRule adjoin = new TagEarleyAdjoin(tag);
    schema.addRule(adjoin);
    DynamicDeductionRule movedown = new TagEarleyMovedown(tag);
    schema.addRule(movedown);
    DynamicDeductionRule moveright = new TagEarleyMoveright(tag);
    schema.addRule(moveright);
    DynamicDeductionRule moveup = new TagEarleyMoveup(tag);
    schema.addRule(moveup);

    for (String auxtreename : auxtreenames) {
      DynamicDeductionRule predictadjoinable =
        new TagEarleyPredictadjoinable(auxtreename, tag);
      schema.addRule(predictadjoinable);
    }

    for (String treename : treenames) {
      DynamicDeductionRule predictadjoined =
        new TagEarleyPredictadjoined(treename, tag);
      schema.addRule(predictadjoined);
      DynamicDeductionRule substitute = new TagEarleySubstitute(treename, tag);
      schema.addRule(substitute);
    }

    for (String initreename : initreenames) {
      if (tag.getInitialTree(initreename).getRoot().getLabel()
        .equals(tag.getStartSymbol())) {
        StaticDeductionRule initialize = new StaticDeductionRule();
        initialize.addConsequence(new TagEarleyItem(initreename, "", "la", 0,
          (Integer) null, null, 0, false));
        initialize.setName("initialize");
        schema.addAxiom(initialize);
        // System.out.println(initialize.toString()); // DEBUG
        schema.addGoal(new TagEarleyItem(initreename, "", "ra", 0,
          (Integer) null, null, wsplit.length, false));
      }

      DynamicDeductionRule predictsubst =
        new TagEarleyPredictsubst(initreename, tag);
      schema.addRule(predictsubst);
    }
    return schema;
  }
}

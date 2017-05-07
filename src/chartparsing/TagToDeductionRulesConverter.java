/*
 * Based on the slides from Laura Kallmeyer about TAG Parsing
 * https://user.phil.hhu.de/~kallmeyer/ParsingBeyondCFG/4tag-parsing.pdf
 */

package chartparsing;

import java.util.Set;

import common.tag.Tag;
import common.tag.TagItem;
import common.tag.Tree;
import common.tag.Vertex;

public class TagToDeductionRulesConverter {

  public static ParsingSchema TagToParsingSchema(Tag tag, String w,
    String schema) {
    switch (schema) {
    case "cyk":
      return TagToCykRules(tag, w);
    default:
      return null;
    }
  }

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
    for (int i = 0; i < wsplit.length; i++) {
      for (String tree : treenames) {
        for (Vertex p : tag.getTree(tree).getVertexes()) {
          if (p.getLabel().equals(wsplit[i])) {
            DeductionRule lexscan = new DeductionRule();
            lexscan.addConsequence(new TagItem(tree, p.getGornaddress() + "⊤",
              i, null, null, i + 1));
            lexscan.setName("lex-scan " + wsplit[i]);
            schema.addRule(lexscan);
            // System.out.println(lexscan.toString()); // DEBUG
          }
          if (p.getLabel().equals("")) {
            DeductionRule epsscan = new DeductionRule();
            epsscan.addConsequence(
              new TagItem(tree, p.getGornaddress() + "⊤", i, null, null, i));
            epsscan.setName("eps-scan");
            schema.addRule(epsscan);
            // System.out.println(epsscan.toString()); // DEBUG
          }
        }
      }

      for (String initreename : initreenames) {
        Tree initree = tag.getInitialTree(initreename);
        if (initree.getRoot().getLabel().equals(tag.getStartSymbol())) {
          schema.addGoal(
            new TagItem(initreename, "⊤", 0, null, null, wsplit.length));
        }
      }

      for (int j = i; j <= wsplit.length; j++) {
        for (String auxtree : auxtreenames) {
          DeductionRule footpredict = new DeductionRule();
          String footgorn =
            tag.getAuxiliaryTree(auxtree).getFoot().getGornaddress();
          footpredict
            .addConsequence(new TagItem(auxtree, footgorn + "⊤", i, i, j, j));
          footpredict.setName("foot-predict");
          schema.addRule(footpredict);
          // System.out.println(footpredict.toString()); // DEBUG
        }
        for (String initree : initreenames) {
          for (String tree2 : treenames) {
            for (Vertex p : tag.getTree(tree2).getVertexes()) {
              if (p.getLabel().equals(tag.getTree(initree).getRoot().getLabel())
                && tag.isSubstitutionNode(p, tree2)) {
                DeductionRule substitute = new DeductionRule();
                substitute
                  .addAntecedence(new TagItem(initree, "⊤", i, null, null, j));
                substitute.addConsequence(new TagItem(tree2,
                  p.getGornaddress() + "⊤", i, null, null, j));
                substitute.setName("substitute in " + p.getGornaddress());
                schema.addRule(substitute);
                // System.out.println(substitute.toString()); // DEBUG
              }
            }
          }
        }

        for (String treename : treenames) {
          Tree tree = tag.getTree(treename);
          for (Vertex p : tree.getVertexes()) {
            if (tree.getNodeByGornAdress(p.getGornaddress() + ".1") != null
              && tree.getNodeByGornAdress(p.getGornaddress() + ".2") == null) {
              DeductionRule moveunary = new DeductionRule();
              moveunary.addAntecedence(new TagItem(treename,
                p.getGornaddress() + ".1" + "⊤", i, null, null, j));
              moveunary.addConsequence(new TagItem(treename,
                p.getGornaddress() + "⊥", i, null, null, j));
              moveunary.setName("move-unary");
              schema.addRule(moveunary);
              // System.out.println(moveunary.toString()); // DEBUG
            }
            for (int k = i; k <=j; k++) {
              if (tree.getNodeByGornAdress(p.getGornaddress() + ".1") != null
                  && tree.getNodeByGornAdress(p.getGornaddress() + ".2") != null) {
                DeductionRule movebinary = new DeductionRule();
                movebinary.addAntecedence(new TagItem(treename,
                  p.getGornaddress() + ".1" + "⊤", i, null, null, k));
                movebinary.addAntecedence(new TagItem(treename,
                  p.getGornaddress() + ".2" + "⊤", k, null, null, j));
                movebinary.addConsequence(new TagItem(treename,
                  p.getGornaddress() + "⊥", i, null, null, j));
                movebinary.setName("move-binary");
                schema.addRule(movebinary);
                // System.out.println(movebinary.toString()); // DEBUG
              }
            }
            
            // TODO if f_OA(tree,node) = 0
            DeductionRule nulladjoin = new DeductionRule();
            nulladjoin.addAntecedence(new TagItem(treename,
              p.getGornaddress() + "⊥", i, null, null, j));
            nulladjoin.addConsequence(new TagItem(treename,
              p.getGornaddress() + "⊤", i, null, null, j));
            nulladjoin.setName("null-adjoin");
            schema.addRule(nulladjoin);
            // System.out.println(nulladjoin.toString()); // DEBUG
          }
        }

        for (int f1 = i; f1 <= j; f1++) {
          for (int f2 = f1; f2 <= j; f2++) {
            for (String treename : treenames) {
              Tree tree = tag.getTree(treename);
              for (Vertex p : tree.getVertexes()) {
                if (tree.getNodeByGornAdress(p.getGornaddress() + ".1") != null
                  && tree
                    .getNodeByGornAdress(p.getGornaddress() + ".2") == null) {
                  DeductionRule moveunary = new DeductionRule();
                  moveunary.addAntecedence(new TagItem(treename,
                    p.getGornaddress() + ".1" + "⊤", i, f1, f2, j));
                  moveunary.addConsequence(new TagItem(treename,
                    p.getGornaddress() + "⊥", i, f1, f2, j));
                  moveunary.setName("move-unary");
                  schema.addRule(moveunary);
                  // System.out.println(moveunary.toString()); // DEBUG
                }
                // TODO if f_OA(tree,node) = 0
                DeductionRule nulladjoin = new DeductionRule();
                nulladjoin.addAntecedence(new TagItem(treename,
                  p.getGornaddress() + "⊥", i, f1, f2, j));
                nulladjoin.addConsequence(new TagItem(treename,
                  p.getGornaddress() + "⊤", i, f1, f2, j));
                nulladjoin.setName("null-adjoin");
                schema.addRule(nulladjoin);
                // System.out.println(nulladjoin.toString()); // DEBUG

                for (int k = i; k <= j; k++) {
                  // foot left
                  if (k >= f2) {
                    DeductionRule movebinary = new DeductionRule();
                    movebinary.addAntecedence(new TagItem(treename,
                      p.getGornaddress() + ".1" + "⊤", i, f1, f2, k));
                    movebinary.addAntecedence(new TagItem(treename,
                      p.getGornaddress() + ".2" + "⊤", k, null, null, j));
                    movebinary.addConsequence(new TagItem(treename,
                      p.getGornaddress() + "⊥", i, f1, f2, j));
                    movebinary.setName("move-binary");
                    schema.addRule(movebinary);
                  }
                  // foot right
                  if (k <= f1) {
                    DeductionRule movebinary = new DeductionRule();
                    movebinary.addAntecedence(new TagItem(treename,
                      p.getGornaddress() + ".1" + "⊤", i, null, null, k));
                    movebinary.addAntecedence(new TagItem(treename,
                      p.getGornaddress() + ".2" + "⊤", k, f1, f2, j));
                    movebinary.addConsequence(new TagItem(treename,
                      p.getGornaddress() + "⊥", i, f1, f2, j));
                    movebinary.setName("move-binary");
                    schema.addRule(movebinary);
                  }

                }
                for (String auxtree : auxtreenames) {
                  for (int f1b = f1; f1b <= f2; f1b++) {
                    for (int f2b = f1b; f2b <= f2; f2b++) {
                      DeductionRule adjoin = new DeductionRule();
                      adjoin.addAntecedence(
                        new TagItem(auxtree, "⊤", i, f1, f2, j));
                      adjoin.addAntecedence(new TagItem(treename,
                        p.getGornaddress() + "⊥", f1, f1b, f2b, f2));
                      adjoin.addConsequence(new TagItem(treename,
                        p.getGornaddress() + "⊤", i, f1b, f2b, j));
                      adjoin.setName("adjoin " + auxtree + " in " + treename
                        + " at " + p.getGornaddress());
                      schema.addRule(adjoin);
                    }
                  }

                  DeductionRule adjoin = new DeductionRule();
                  adjoin
                    .addAntecedence(new TagItem(auxtree, "⊤", i, f1, f2, j));
                  adjoin.addAntecedence(new TagItem(treename,
                    p.getGornaddress() + "⊥", f1, null, null, f2));
                  adjoin.addConsequence(new TagItem(treename,
                    p.getGornaddress() + "⊤", i, null, null, j));
                  adjoin.setName("adjoin " + auxtree + " in " + treename
                    + " at " + p.getLabel());
                  schema.addRule(adjoin);
                }
              }
            }
          }
        }
      }
    }

    return schema;
  }
}

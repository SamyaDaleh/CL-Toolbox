package chartparsing;

import java.util.Set;

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
    for (int i = 0; i < wsplit.length; i++) {
      for (String tree : treenames) {
        for (Vertex p : tag.getTree(tree).getVertexes()) {
          if (p.getLabel().equals(wsplit[i])) {
            StaticDeductionRule lexscan = new StaticDeductionRule();
            lexscan.addConsequence(new TagCykItem(tree,
              p.getGornaddress() + "⊤", i, null, null, i + 1));
            lexscan.setName("lex-scan " + wsplit[i]);
            schema.addAxiom(lexscan);
            // System.out.println(lexscan.toString()); // DEBUG
          }
          if (p.getLabel().equals("")) {
            StaticDeductionRule epsscan = new StaticDeductionRule();
            epsscan.addConsequence(
              new TagCykItem(tree, p.getGornaddress() + "⊤", i, null, null, i));
            epsscan.setName("eps-scan");
            schema.addAxiom(epsscan);
            // System.out.println(epsscan.toString()); // DEBUG
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
        for (String initree : initreenames) {
          for (String tree2 : treenames) {
            for (Vertex p : tag.getTree(tree2).getVertexes()) {
              if (p.getLabel().equals(tag.getTree(initree).getRoot().getLabel())
                && tag.isSubstitutionNode(p, tree2)) {
                StaticDeductionRule substitute = new StaticDeductionRule();
                substitute.addAntecedence(
                  new TagCykItem(initree, "⊤", i, null, null, j));
                substitute.addConsequence(new TagCykItem(tree2,
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
              StaticDeductionRule moveunary = new StaticDeductionRule();
              moveunary.addAntecedence(new TagCykItem(treename,
                p.getGornaddress() + ".1" + "⊤", i, null, null, j));
              moveunary.addConsequence(new TagCykItem(treename,
                p.getGornaddress() + "⊥", i, null, null, j));
              moveunary.setName("move-unary");
              schema.addRule(moveunary);
              // System.out.println(moveunary.toString()); // DEBUG
            }
            for (int k = i; k <= j; k++) {
              if (tree.getNodeByGornAdress(p.getGornaddress() + ".1") != null
                && tree
                  .getNodeByGornAdress(p.getGornaddress() + ".2") != null) {
                StaticDeductionRule movebinary = new StaticDeductionRule();
                movebinary.addAntecedence(new TagCykItem(treename,
                  p.getGornaddress() + ".1" + "⊤", i, null, null, k));
                movebinary.addAntecedence(new TagCykItem(treename,
                  p.getGornaddress() + ".2" + "⊤", k, null, null, j));
                movebinary.addConsequence(new TagCykItem(treename,
                  p.getGornaddress() + "⊥", i, null, null, j));
                movebinary.setName("move-binary");
                schema.addRule(movebinary);
                // System.out.println(movebinary.toString()); // DEBUG
              }
            }

            // TODO if f_OA(tree,node) = 0
            StaticDeductionRule nulladjoin = new StaticDeductionRule();
            nulladjoin.addAntecedence(new TagCykItem(treename,
              p.getGornaddress() + "⊥", i, null, null, j));
            nulladjoin.addConsequence(new TagCykItem(treename,
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
                  StaticDeductionRule moveunary = new StaticDeductionRule();
                  moveunary.addAntecedence(new TagCykItem(treename,
                    p.getGornaddress() + ".1" + "⊤", i, f1, f2, j));
                  moveunary.addConsequence(new TagCykItem(treename,
                    p.getGornaddress() + "⊥", i, f1, f2, j));
                  moveunary.setName("move-unary");
                  schema.addRule(moveunary);
                  // System.out.println(moveunary.toString()); // DEBUG
                }
                // TODO if f_OA(tree,node) = 0
                StaticDeductionRule nulladjoin = new StaticDeductionRule();
                nulladjoin.addAntecedence(new TagCykItem(treename,
                  p.getGornaddress() + "⊥", i, f1, f2, j));
                nulladjoin.addConsequence(new TagCykItem(treename,
                  p.getGornaddress() + "⊤", i, f1, f2, j));
                nulladjoin.setName("null-adjoin");
                schema.addRule(nulladjoin);
                // System.out.println(nulladjoin.toString()); // DEBUG

                for (int k = i; k <= j; k++) {
                  // foot left
                  if (k >= f2) {
                    StaticDeductionRule movebinary = new StaticDeductionRule();
                    movebinary.addAntecedence(new TagCykItem(treename,
                      p.getGornaddress() + ".1" + "⊤", i, f1, f2, k));
                    movebinary.addAntecedence(new TagCykItem(treename,
                      p.getGornaddress() + ".2" + "⊤", k, null, null, j));
                    movebinary.addConsequence(new TagCykItem(treename,
                      p.getGornaddress() + "⊥", i, f1, f2, j));
                    movebinary.setName("move-binary");
                    schema.addRule(movebinary);
                  }
                  // foot right
                  if (k <= f1) {
                    StaticDeductionRule movebinary = new StaticDeductionRule();
                    movebinary.addAntecedence(new TagCykItem(treename,
                      p.getGornaddress() + ".1" + "⊤", i, null, null, k));
                    movebinary.addAntecedence(new TagCykItem(treename,
                      p.getGornaddress() + ".2" + "⊤", k, f1, f2, j));
                    movebinary.addConsequence(new TagCykItem(treename,
                      p.getGornaddress() + "⊥", i, f1, f2, j));
                    movebinary.setName("move-binary");
                    schema.addRule(movebinary);
                  }

                }
                for (String auxtree : auxtreenames) {
                  for (int f1b = f1; f1b <= f2; f1b++) {
                    for (int f2b = f1b; f2b <= f2; f2b++) {
                      StaticDeductionRule adjoin = new StaticDeductionRule();
                      adjoin.addAntecedence(
                        new TagCykItem(auxtree, "⊤", i, f1, f2, j));
                      adjoin.addAntecedence(new TagCykItem(treename,
                        p.getGornaddress() + "⊥", f1, f1b, f2b, f2));
                      adjoin.addConsequence(new TagCykItem(treename,
                        p.getGornaddress() + "⊤", i, f1b, f2b, j));
                      adjoin.setName("adjoin " + auxtree + " in " + treename
                        + " at " + p.getGornaddress());
                      schema.addRule(adjoin);
                    }
                  }

                  StaticDeductionRule adjoin = new StaticDeductionRule();
                  adjoin
                    .addAntecedence(new TagCykItem(auxtree, "⊤", i, f1, f2, j));
                  adjoin.addAntecedence(new TagCykItem(treename,
                    p.getGornaddress() + "⊥", f1, null, null, f2));
                  adjoin.addConsequence(new TagCykItem(treename,
                    p.getGornaddress() + "⊤", i, null, null, j));
                  adjoin.setName("adjoin " + auxtree + " in " + treename
                    + " at " + p.getGornaddress());
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

    for (String initreename : initreenames) {
      if (tag.getInitialTree(initreename).getRoot().getLabel()
        .equals(tag.getStartSymbol())) {
        StaticDeductionRule initialize = new StaticDeductionRule();
        initialize.addConsequence(
          new TagEarleyItem(initreename, "", "la", 0, (Integer) null, null, 0, false));
        initialize.setName("initialize");
        schema.addAxiom(initialize);
        // System.out.println(initialize.toString()); // DEBUG
        schema.addGoal(new TagEarleyItem(initreename, "", "ra", 0, (Integer) null, null,
          wsplit.length, false));
      }
    }
    for (int i = 0; i <= wsplit.length; i++) {
      for (String treename : treenames) {
        for (Vertex p : tag.getTree(treename).getVertexes()) {
          for (String auxtreename : auxtreenames) {
            Vertex footnode = tag.getAuxiliaryTree(auxtreename).getFoot();
            if (footnode.getLabel().equals(p.getLabel())) {
              StaticDeductionRule predictadjoined = new StaticDeductionRule();
              predictadjoined.addAntecedence(new TagEarleyItem(auxtreename,
                footnode.getGornaddress(), "lb", i, (Integer) null, null, i, false));
              predictadjoined.addConsequence(new TagEarleyItem(treename,
                p.getGornaddress(), "lb", i, (Integer) null, null, i, false));
              predictadjoined.setName("PredictAdjoined");
              schema.addRule(predictadjoined);
              // System.out.println(predictadjoined.toString()); // DEBUG
            }
          }
          if (tag.isSubstitutionNode(p, treename)) {
            for (String initreename : initreenames) {
              if (p.getLabel()
                .equals(tag.getInitialTree(initreename).getRoot().getLabel())) {
                StaticDeductionRule predictsubst = new StaticDeductionRule();
                predictsubst.addAntecedence(new TagEarleyItem(treename,
                  p.getGornaddress(), "lb", i, (Integer) null, null, i, false));
                predictsubst.addConsequence(new TagEarleyItem(initreename, "",
                  "la", i, (Integer) null, null, i, false));
                predictsubst.setName("PredictSubst");
                schema.addRule(predictsubst);
                // System.out.println(predictsubst.toString()); // DEBUG
              }
            }
          }
        }
      }

      for (int l = i; l <= wsplit.length; l++) {
        for (String treename : treenames) {
          for (Vertex p : tag.getTree(treename).getVertexes()) {
            if (l < wsplit.length && p.getLabel().equals(wsplit[l])) {
              StaticDeductionRule scanterm = new StaticDeductionRule();
              scanterm.addAntecedence(new TagEarleyItem(treename,
                p.getGornaddress(), "la", i, (Integer) null, null, l, false));
              scanterm.addConsequence(new TagEarleyItem(treename,
                p.getGornaddress(), "ra", i, (Integer) null, null, l + 1, false));
              scanterm.setName("ScanTerm " + wsplit[l]);
              schema.addRule(scanterm);
              // System.out.println(scanterm.toString()); // DEBUG
            } else if (p.getLabel().equals("")) {
              StaticDeductionRule scaneps = new StaticDeductionRule();
              scaneps.addAntecedence(new TagEarleyItem(treename,
                p.getGornaddress(), "la", i, (Integer) null, null, l, false));
              scaneps.addConsequence(new TagEarleyItem(treename,
                p.getGornaddress(), "ra", i, (Integer) null, null, l, false));
              scaneps.setName("Scan-ε");
              schema.addRule(scaneps);
              // System.out.println(scaneps.toString()); // DEBUG
            }

            // TODO f_OA(gamma,p) = 0
            if (!tag.isInTerminals(p.getLabel())) {
              StaticDeductionRule predictnoadj = new StaticDeductionRule();
              predictnoadj.addAntecedence(new TagEarleyItem(treename,
                p.getGornaddress(), "la", i, "?", "?", l, false));
              predictnoadj.addConsequence(new TagEarleyItem(treename,
                p.getGornaddress(), "lb", l, (Integer) null, null, l, false));
              predictnoadj.setName("PredictNoAdj");
              schema.addRule(predictnoadj);
              // System.out.println(predictnoadj.toString()); // DEBUG
            }
            if (tag.getTree(treename)
              .getNodeByGornAdress(p.getGornaddress() + ".1") != null) {
              StaticDeductionRule movedown = new StaticDeductionRule();
              movedown.addAntecedence(new TagEarleyItem(treename,
                p.getGornaddress(), "lb", i, (Integer) null, null, l, false));
              movedown.addConsequence(new TagEarleyItem(treename,
                p.getGornaddress() + ".1", "la", i, (Integer) null, null, l, false));
              movedown.setName("MoveDown");
              schema.addRule(movedown);
              // System.out.println(movedown.toString()); // DEBUG
            }
            if (tag.getTree(treename).getNodeByGornAdress(
              p.getGornAddressOfPotentialRightSibling()) != null) {
              StaticDeductionRule moveright = new StaticDeductionRule();
              moveright.addAntecedence(new TagEarleyItem(treename,
                p.getGornaddress(), "ra", i, (Integer) null, null, l, false));
              moveright.addConsequence(new TagEarleyItem(treename,
                p.getGornAddressOfPotentialRightSibling(), "la", i, (Integer) null, null,
                l, false));
              moveright.setName("MoveRight");
              schema.addRule(moveright);
              // System.out.println(moveright.toString()); // DEBUG
            } else {
              if (!p.getGornaddress().equals("")) {
                StaticDeductionRule moveup = new StaticDeductionRule();
                moveup.addAntecedence(new TagEarleyItem(treename,
                  p.getGornaddress(), "ra", i, (Integer) null, null, l, false));
                moveup.addConsequence(new TagEarleyItem(treename,
                  p.getGornAddressOfParent(), "rb", i, (Integer) null, null, l, false));
                moveup.setName("MoveUp");
                schema.addRule(moveup);
                // System.out.println(moveup.toString()); // DEBUG
              }
            }
            for (String auxtree : auxtreenames) {
              Vertex footnode = tag.getAuxiliaryTree(auxtree).getFoot();
              if (p.getLabel().equals(footnode.getLabel())) {
                StaticDeductionRule predictadjoinable = new StaticDeductionRule();
                predictadjoinable.addAntecedence(new TagEarleyItem(treename,
                  p.getGornaddress(), "la", i, "?", "?", l, false));
                predictadjoinable.addConsequence(new TagEarleyItem(auxtree, "",
                  "la", l, (Integer) null, null, l, false));
                predictadjoinable.setName("PredictAdjoinable");
                schema.addRule(predictadjoinable);
                // System.out.println(predictadjoinable.toString()); // DEBUG
              }
              StaticDeductionRule completefoot = new StaticDeductionRule();
              completefoot.addAntecedence(new TagEarleyItem(treename,
                p.getGornaddress(), "rb", i, "?", "?", l, false));
              completefoot.addAntecedence(new TagEarleyItem(auxtree,
                footnode.getGornaddress(), "lb", i, (Integer) null, null, i, false));
              completefoot.addConsequence(new TagEarleyItem(auxtree,
                footnode.getGornaddress(), "rb", i, i, l, l, false));
              completefoot.setName("CompleteFoot");
              schema.addRule(completefoot);
              // System.out.println(completefoot.toString()); // DEBUG
            }
            if (tag.isSubstitutionNode(p, treename)) {
              for (String initreename : initreenames) {
                if (p.getLabel().equals(
                  tag.getInitialTree(initreename).getRoot().getLabel())) {
                  StaticDeductionRule substitute = new StaticDeductionRule();
                  substitute.addAntecedence(new TagEarleyItem(initreename, "",
                    "ra", i, (Integer) null, null, l, false));
                  substitute.addConsequence(new TagEarleyItem(treename,
                    p.getGornaddress(), "rb", i, (Integer) null, null, l, false));
                  substitute.setName("Substitute");
                  schema.addRule(substitute);
                  // System.out.println(substitute.toString()); // DEBUG
                }
              }
            }
            if (tag.isInNonterminals(p.getLabel())) {
              for (int f = 0; f <= i; f++) {
                StaticDeductionRule completenode = new StaticDeductionRule();
                completenode.addAntecedence(new TagEarleyItem(treename,
                  p.getGornaddress(), "la", f, (Integer) null, null, i, false));
                completenode.addAntecedence(new TagEarleyItem(treename,
                  p.getGornaddress(), "rb", i, (Integer) null, null, l, false));
                completenode.addConsequence(new TagEarleyItem(treename,
                  p.getGornaddress(), "ra", f, (Integer) null, null, l, false));
                completenode.setName("CompleteNode");
                schema.addRule(completenode);
                // System.out.println(completenode.toString()); // DEBUG
                completenode = new StaticDeductionRule();
                completenode.addAntecedence(new TagEarleyItem(treename,
                  p.getGornaddress(), "la", f, (Integer) null, null, i, false));
                completenode.addAntecedence(new TagEarleyItem(treename,
                  p.getGornaddress(), "rb", i, (Integer) null, null, l, true));
                completenode.addConsequence(new TagEarleyItem(treename,
                  p.getGornaddress(), "ra", f, (Integer) null, null, l, false));
                completenode.setName("CompleteNode");
                schema.addRule(completenode);
                // System.out.println(completenode.toString()); // DEBUG
              }
            }
          }
        }

        for (int j = i; j <= l; j++) {
          for (int k = j; k <= l; k++) {
            for (String treename : treenames) {
              for (Vertex p : tag.getTree(treename).getVertexes()) {
                if (l < wsplit.length && p.getLabel().equals(wsplit[l])) {
                  StaticDeductionRule scanterm = new StaticDeductionRule();
                  scanterm.addAntecedence(new TagEarleyItem(treename,
                    p.getGornaddress(), "la", i, j, k, l, false));
                  scanterm.addConsequence(new TagEarleyItem(treename,
                    p.getGornaddress(), "ra", i, j, k, l + 1, false));
                  scanterm.setName("ScanTerm " + wsplit[l]);
                  schema.addRule(scanterm);
                  // System.out.println(scanterm.toString()); // DEBUG
                } else if (p.getLabel().equals("")) {
                  StaticDeductionRule scaneps = new StaticDeductionRule();
                  scaneps.addAntecedence(new TagEarleyItem(treename,
                    p.getGornaddress(), "la", i, j, k, l, false));
                  scaneps.addConsequence(new TagEarleyItem(treename,
                    p.getGornaddress(), "ra", i, j, k, l, false));
                  scaneps.setName("Scan-ε");
                  schema.addRule(scaneps);
                  // System.out.println(scaneps.toString()); // DEBUG
                }
                if (tag.getTree(treename)
                  .getNodeByGornAdress(p.getGornaddress() + ".1") != null) {
                  StaticDeductionRule movedown = new StaticDeductionRule();
                  movedown.addAntecedence(new TagEarleyItem(treename,
                    p.getGornaddress(), "lb", i, j, k, l, false));
                  movedown.addConsequence(new TagEarleyItem(treename,
                    p.getGornaddress() + ".1", "la", i, j, k, l, false));
                  movedown.setName("MoveDown");
                  schema.addRule(movedown);
                  // System.out.println(movedown.toString()); // DEBUG
                }
                if (tag.getTree(treename).getNodeByGornAdress(
                  p.getGornAddressOfPotentialRightSibling()) != null) {
                  StaticDeductionRule moveright = new StaticDeductionRule();
                  moveright.addAntecedence(new TagEarleyItem(treename,
                    p.getGornaddress(), "ra", i, j, k, l, false));
                  moveright.addConsequence(new TagEarleyItem(treename,
                    p.getGornAddressOfPotentialRightSibling(), "la", i, j, k, l,
                    false));
                  moveright.setName("MoveRight");
                  schema.addRule(moveright);
                  // System.out.println(moveright.toString()); // DEBUG
                } else {
                  if (!p.getGornaddress().equals("")) {
                    StaticDeductionRule moveup = new StaticDeductionRule();
                    moveup.addAntecedence(new TagEarleyItem(treename,
                      p.getGornaddress(), "ra", i, j, k, l, false));
                    moveup.addConsequence(new TagEarleyItem(treename,
                      p.getGornAddressOfParent(), "rb", i, j, k, l, false));
                    moveup.setName("MoveUp");
                    schema.addRule(moveup);
                    // System.out.println(moveup.toString()); // DEBUG
                  }
                }
                for (String auxtree : auxtreenames) {
                  Vertex footnode = tag.getAuxiliaryTree(auxtree).getFoot();
                  if (p.getLabel().equals(footnode.getLabel())) {

                    StaticDeductionRule adjoin = new StaticDeductionRule();
                    adjoin.addAntecedence(
                      new TagEarleyItem(auxtree, "", "ra", i, j, k, l, false));
                    adjoin.addAntecedence(new TagEarleyItem(treename,
                      p.getGornaddress(), "rb", j, (Integer) null, null, k, false));
                    adjoin.addConsequence(new TagEarleyItem(treename,
                      p.getGornaddress(), "rb", i, (Integer) null, null, l, true));
                    adjoin.setName("Adjoin");
                    schema.addRule(adjoin);
                    // System.out.println(adjoin.toString()); // DEBUG
                    for (int g = j; g < wsplit.length; g++) {
                      for (int h = g; h < wsplit.length; h++) {
                        adjoin = new StaticDeductionRule();
                        adjoin.addAntecedence(new TagEarleyItem(auxtree, "",
                          "ra", i, j, k, l, false));
                        adjoin.addAntecedence(new TagEarleyItem(treename,
                          p.getGornaddress(), "rb", j, g, h, k, false));
                        adjoin.addConsequence(new TagEarleyItem(treename,
                          p.getGornaddress(), "rb", i, g, h, l, true));
                        adjoin.setName("Adjoin");
                        schema.addRule(adjoin);
                        // System.out.println(adjoin.toString()); // DEBUG
                      }
                    }
                  }
                }

                if (tag.isInNonterminals(p.getLabel())) {
                  for (int f = 0; f <= i; f++) {
                    // left null
                    StaticDeductionRule completenode = new StaticDeductionRule();
                    completenode.addAntecedence(new TagEarleyItem(treename,
                      p.getGornaddress(), "la", f, (Integer) null, null, i, false));
                    completenode.addAntecedence(new TagEarleyItem(treename,
                      p.getGornaddress(), "rb", i, j, k, l, false));
                    completenode.addConsequence(new TagEarleyItem(treename,
                      p.getGornaddress(), "ra", f, j, k, l, false));
                    completenode.setName("CompleteNode");
                    schema.addRule(completenode);
                    // System.out.println(completenode.toString()); // DEBUG
                    completenode = new StaticDeductionRule();
                    completenode.addAntecedence(new TagEarleyItem(treename,
                      p.getGornaddress(), "la", f, (Integer) null, null, i, false));
                    completenode.addAntecedence(new TagEarleyItem(treename,
                      p.getGornaddress(), "rb", i, j, k, l, true));
                    completenode.addConsequence(new TagEarleyItem(treename,
                      p.getGornaddress(), "ra", f, j, k, l, false));
                    completenode.setName("CompleteNode");
                    schema.addRule(completenode);
                    // System.out.println(completenode.toString()); // DEBUG

                    // right null
                    completenode = new StaticDeductionRule();
                    completenode.addAntecedence(new TagEarleyItem(treename,
                      p.getGornaddress(), "la", f, i, j, k, false));
                    completenode.addAntecedence(new TagEarleyItem(treename,
                      p.getGornaddress(), "rb", k, (Integer) null, null, l, false));
                    completenode.addConsequence(new TagEarleyItem(treename,
                      p.getGornaddress(), "ra", f, i, j, l, false));
                    completenode.setName("CompleteNode");
                    schema.addRule(completenode);
                    // System.out.println(completenode.toString()); // DEBUG
                    completenode = new StaticDeductionRule();
                    completenode.addAntecedence(new TagEarleyItem(treename,
                      p.getGornaddress(), "la", f, i, j, k, false));
                    completenode.addAntecedence(new TagEarleyItem(treename,
                      p.getGornaddress(), "rb", k, (Integer) null, null, l, true));
                    completenode.addConsequence(new TagEarleyItem(treename,
                      p.getGornaddress(), "ra", f, i, j, l, false));
                    completenode.setName("CompleteNode");
                    schema.addRule(completenode);
                    // System.out.println(completenode.toString()); // DEBUG
                  }
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

package com.github.samyadaleh.cltoolbox.common;

import com.github.samyadaleh.cltoolbox.common.cfg.CfgProductionRule;
import com.github.samyadaleh.cltoolbox.common.lcfrs.Clause;
import com.github.samyadaleh.cltoolbox.common.lcfrs.Predicate;
import com.github.samyadaleh.cltoolbox.common.tag.Tree;
import com.github.samyadaleh.cltoolbox.common.tag.Vertex;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class TreeUtils {

  public static Tree getTreeOfSrcgClause(Clause clause, List<Integer> vector) {
    StringBuilder extractedRule = new StringBuilder();
    extractedRule.append(clause.getLhs().getNonterminal()).append(" ->");
    int terminalsInLhs = 0;
    for (String symbol : clause.getLhs().getSymbolsAsPlainArray()) {
      if (!TreeUtils.symbolIsVariable(clause, symbol)) {
        terminalsInLhs++;
      }
    }
    String[] lhsSymbols = clause.getLhs().getSymbolsAsPlainArray();
    int i = 0;
    for (int terminalsProcessed = 0;
         terminalsProcessed < terminalsInLhs / 2; i++) {
      String symbol = lhsSymbols[i];
      boolean found = TreeUtils.symbolIsVariable(clause, symbol);
      if (!found) {
        terminalsProcessed++;
        extractedRule.append(" ").append(symbol).append('<')
            .append(vector.get(i * 2)).append('>');
      }
    }
    for (Predicate rhs : clause.getRhs()) {
      extractedRule.append(" ").append(rhs.getNonterminal());
    }
    for (; i < lhsSymbols.length; i++) {
      String symbol = lhsSymbols[i];
      boolean found = TreeUtils.symbolIsVariable(clause, symbol);
      if (!found) {
        extractedRule.append(" ").append(symbol).append('<')
            .append(vector.get(i * 2)).append('>');
      }
    }
    try {
      return new Tree(new CfgProductionRule(extractedRule.toString()));
    } catch (ParseException e) {
      throw new RuntimeException(e);
    }
  }

  public static Tree getTreeOfSrcgClause(Clause clause) {
    if (clause.getRhs().size() == 0) {
      try {
        return new Tree(new CfgProductionRule(
            clause.getLhs().getNonterminal() + " -> " + ArrayUtils
                .getSubSequenceAsString(
                    clause.getLhs().getSymbolsAsPlainArray(), 0,
                    clause.getLhs().getSymbolsAsPlainArray().length)));
      } catch (ParseException e) {
        throw new RuntimeException(e);
      }
    }
    StringBuilder cfgRuleString =
        new StringBuilder(clause.getLhs().getNonterminal());
    cfgRuleString.append(" ->");
    for (Predicate rhsPred : clause.getRhs()) {
      cfgRuleString.append(" ").append(rhsPred.getNonterminal());
    }
    for (String sym : clause.getLhs().getSymbolsAsPlainArray()) {
      boolean found = false;
      for (Predicate rhsPred : clause.getRhs()) {
        int[] indices = rhsPred.find(sym);
        if (indices[0] != -1) {
          found = true;
          break;
        }
      }
      if (!found) {
        cfgRuleString.append(" ").append(sym);
      }
    }
    try {
      return new Tree(new CfgProductionRule(cfgRuleString.toString()));
    } catch (ParseException e) {
      throw new RuntimeException(e);
    }
  }

  private static boolean symbolIsVariable(Clause clause, String symbol) {
    boolean found = false;
    for (Predicate rhsPred : clause.getRhs()) {
      int[] indices = rhsPred.find(symbol);
      if (indices[0] >= 0) {
        found = true;
        break;
      }
    }
    return found;
  }

  /**
   * Performs leftmost substitution of tree into derivatedTree.
   */
  public static Tree performLeftmostSubstitution(Tree derivedTree, Tree tree) {
    String derivedTreeString = derivedTree.toString();
    try {
      return new Tree(derivedTreeString.substring(0,
          derivedTreeString.indexOf("(" + tree.getRoot().getLabel() + " )"))
          + tree.toString() + derivedTreeString.substring(
          derivedTreeString.indexOf("(" + tree.getRoot().getLabel() + " )")
              + tree.getRoot().getLabel().length() + 3));
    } catch (ParseException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Returns a new tree where the leftmost terminal without position index has
   * got index pos.
   */
  public static Tree performPositionSubstitution(Tree tree, String terminal,
      String pos) {
    String derivedTreeString = tree.toString();
    String searchFor = terminal + " ";
    int index = derivedTreeString.indexOf(searchFor);
    try {
      return new Tree(
          derivedTreeString.substring(0, index) + terminal + "<" + pos + ">"
              + derivedTreeString.substring(index + searchFor.length()));
    } catch (ParseException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Returns true if
   * - tree2 contains tree1 <- is implicated by rule 3.
   * - tree1 contains one substitution node that does not matter
   * <- is implicated by rule 3, too
   * - the subtrees in tree2 das correspond to the substitution nodes from
   * tree1 starting from the second one contain only epsilon leaves.
   */
  public static boolean addsNothing(Tree tree1, Tree tree2,
      String[] nonterminals) {
    List<Tree> substitutionTrees =
        getSubstitutionSubtrees(tree1, tree2, nonterminals);
    if (substitutionTrees.size() < 2) {
      return false;
    }
    for (int i = 1; i < substitutionTrees.size(); i++) {
      if (!substitutionTrees.get(i).allLeavesAreEpsilon()) {
        return false;
      }
    }
    return true;
  }

  private static List<Tree> getSubstitutionSubtrees(Tree tree1, Tree tree2,
      String[] nonterminals) {
    return getSubstitutionSubtrees(tree1, tree2, tree2.getRoot(), nonterminals);
  }

  private static List<Tree> getSubstitutionSubtrees(Tree tree1, Tree tree2,
      Vertex node2, String[] nonterminals) {
    // TODO return a list of subtrees from tree1 that correspond to the substitution nodes in tree2.
    //  How does it work? I can locally tell if this part of the sub-structure is the same, but I won't know about the others
    //  easy. If one of them returns false, dismiss the subtrees you collected
    //  not easy. What if it returns subtrees, because it found them fitting in its branch, that doesn't mean my ones are correct.
    //  Here is how it works. First I need to call the same with nodes instead of trees
    // TODO if node 2 and root of tree1 have the same label, start from here
    //  else call this method for every child node of node2.
    //  no, you have to call it anyway cause you might be wrong
    List<Tree> subTrees =
        collectSubTrees(tree1, tree1.getRoot(), tree2, node2, nonterminals);
    if (subTrees.size() > 0) {
      return subTrees;
    }
    for (Vertex child2 : tree2.getChildren(node2)) {
      subTrees =
          collectSubTrees(tree1, tree1.getRoot(), tree2, child2, nonterminals);
      if (subTrees.size() > 0) {
        return subTrees;
      }
    }
    return new ArrayList<>();
  }

  private static List<Tree> collectSubTrees(Tree tree1, Vertex node1,
      Tree tree2, Vertex node2, String[] nonterminals) {
    List<Tree> subtrees = new ArrayList<>();
    // if they have different labels, return nothing.
    if (!node1.getLabel().equals(node2.getLabel())) {
      return subtrees;
    }
    // if they have different amounts of children but node1 has more than 0
    // child (no substitution node) return nothing.
    if (tree1.getChildren(node1).size() > 0
        && tree1.getChildren(node1).size() != tree2.getChildren(node2).size()) {
      return subtrees;
    }
    // if vortex1 is a substitution node with the same label of node2, collext
    // subtree in tree2 starting from here and return it.
    if (tree1.getChildren(node1).size() == 0 && ArrayUtils
        .contains(nonterminals, node1.getLabel())) {
      try {
        subtrees.add(new Tree(collectSubtreeAsString(tree2, node2)));
      } catch (ParseException e) {
        // should never happen
        throw new RuntimeException(e);
      }
    }
    // if they have the same amount of childen more than 0, call this
    // recursively for every chiid and collect all subtrees.
    if (tree1.getChildren(node1).size() == tree2.getChildren(node2).size()
        && tree1.getChildren(node1).size() > 0) {

      for (int i = 0; i < tree1.getChildren(node1).size(); i++) {
        Vertex child1 = tree1.getChildren(node1).get(i);
        Vertex child2 = tree2.getChildren(node2).get(i);
        subtrees.addAll(
            collectSubTrees(tree1, child1, tree2, child2, nonterminals));
      }
    }
    return subtrees;
  }

  private static String collectSubtreeAsString(Tree tree2, Vertex node2) {
    StringBuilder newTree = new StringBuilder("(");
    if (node2.getLabel().equals("")) {
      newTree.append("ε");
    } else {
      newTree.append(node2.getLabel());
    }
    newTree.append(" ");
    for (Vertex child : tree2.getChildren(node2)) {
      newTree.append(collectSubtreeAsString(tree2, child)).append(" ");
    }
    newTree.append(")");
    return newTree.toString();
  }
}

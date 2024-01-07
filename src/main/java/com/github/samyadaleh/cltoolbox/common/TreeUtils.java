package com.github.samyadaleh.cltoolbox.common;

import com.github.samyadaleh.cltoolbox.common.cfg.CfgProductionRule;
import com.github.samyadaleh.cltoolbox.common.lcfrs.Clause;
import com.github.samyadaleh.cltoolbox.common.lcfrs.Predicate;
import com.github.samyadaleh.cltoolbox.common.tag.Tree;
import com.github.samyadaleh.cltoolbox.common.tag.Vertex;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import static com.github.samyadaleh.cltoolbox.common.Constants.ARROW_RIGHT;
import static com.github.samyadaleh.cltoolbox.common.Constants.EPSILON;

public class TreeUtils {

  public static Tree getTreeOfSrcgClause(Clause clause, List<Integer> vector) {
    StringBuilder extractedRule = new StringBuilder();
    extractedRule.append(clause.getLhs().getNonterminal()).append(" ").append(ARROW_RIGHT);
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
            clause.getLhs().getNonterminal() + " " + ARROW_RIGHT + " " + ArrayUtils
                .getSubSequenceAsString(
                    clause.getLhs().getSymbolsAsPlainArray(), 0,
                    clause.getLhs().getSymbolsAsPlainArray().length)));
      } catch (ParseException e) {
        throw new RuntimeException(e);
      }
    }
    StringBuilder cfgRuleString =
        new StringBuilder(clause.getLhs().getNonterminal());
    cfgRuleString.append(" ").append(ARROW_RIGHT);
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
          + tree + derivedTreeString.substring(
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
      newTree.append(EPSILON);
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

  public static Tree mergeTrees(Tree... trees) {
    String[] treeStrings = new String[trees.length];
    for (int i = 0; i < trees.length; i++) {
      treeStrings[i] = trees[i].toString();
    }
    try {
      return new Tree(mergeTrees(treeStrings));
    } catch (ParseException e) {
      // should never happen
      throw new RuntimeException(e);
    }
  }

  public static String mergeTrees(String... trees) {
    StringBuilder merged = new StringBuilder();
    int[] positions = new int[trees.length]; // All initially 0

    while (true) {
      // Check if we've reached the end of all trees, if so break
      boolean allEndReached = true;
      for (int i = 0; i < positions.length; i++) {
        if (positions[i] < trees[i].length()) {
          allEndReached = false;
          break;
        }
      }

      if (allEndReached) {
        return merged.toString();
      }

      // Check for the start of a subtree in a tree where others don't have
      int subtreeStartIndex = -1;
      for (int i = 0; i < trees.length; i++) {
        char ch = trees[i].charAt(positions[i]);
        if (ch == '(') {
          boolean othersHaveClosingBracket = false;
          for (int j = 0; j < trees.length; j++) {
            if (j != i && positions[j] < trees[j].length() && trees[j].charAt(positions[j]) == ')') {
              othersHaveClosingBracket = true;
              break;
            }
          }
          if (othersHaveClosingBracket) {
            subtreeStartIndex = i;
            break;
          }
        }
      }

      if (subtreeStartIndex == -1) { // All chars are the same
        merged.append(trees[0].charAt(positions[0]));
        for (int i = 0; i < positions.length; i++) {
          positions[i]++;
        }
      } else { // We have a unique char, so copy the subtree
        positions[subtreeStartIndex] = copySubtree(trees[subtreeStartIndex],
            merged, positions[subtreeStartIndex]);
      }
    }
  }

  private static int copySubtree(String tree1, StringBuilder merged, int i) {
    int bracketCounter = 1;
    merged.append(tree1.charAt(i));
    i++;
    while (bracketCounter != 0) {
      if (tree1.charAt(i) == '(') {
        bracketCounter++;
      } else if (tree1.charAt(i) == ')') {
        bracketCounter--;
      }
      merged.append(tree1.charAt(i));
      i++;
    }
    return i;
  }

}

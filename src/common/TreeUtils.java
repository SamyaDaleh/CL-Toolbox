package common;

import java.text.ParseException;

import common.cfg.CfgProductionRule;
import common.lcfrs.Clause;
import common.tag.Tree;

public class TreeUtils {

  /**
   * performs rightmost substitution of derivatedTree into rule for bottomup
   * approaches. 
   */
  public static Tree performRightmostSubstitution(Tree derivatedTree,
    CfgProductionRule rule) throws ParseException {
    Tree newRuleTree = new Tree(rule);
    String ruleTreeString = newRuleTree.toString();
    Tree newDerivatedTree = new Tree(ruleTreeString.substring(0,
      ruleTreeString
        .lastIndexOf("(" + derivatedTree.getRoot().getLabel() + " )"))
      + derivatedTree.toString()
      + ruleTreeString.substring(ruleTreeString
        .lastIndexOf("(" + derivatedTree.getRoot().getLabel() + " )")
        + derivatedTree.getRoot().getLabel().length() + 3));
    return newDerivatedTree;
  }

  /**
   * Performs leftmost substitution of tree into derivatedTree.
   */
  public static Tree performLeftmostSubstitution(Tree derivedTree, Tree tree)
    throws ParseException {
    String derivedTreeString = derivedTree.toString();
    Tree newDerivatedTree = new Tree(derivedTreeString.substring(0,
      derivedTreeString.lastIndexOf("(" + tree.getRoot().getLabel() + " )"))
      + tree.toString()
      + derivedTreeString.substring(
        derivedTreeString.lastIndexOf("(" + tree.getRoot().getLabel() + " )")
          + tree.getRoot().getLabel().length() + 3));
    return newDerivatedTree;
  }

  /**
   * Returns equivalent trees for sRCG clauses only using nonterminals.
   * Not for terminating rules which would need a range as well.
   */
  public static Tree getTreeforSrcgClause(Clause clause) {
    // TODO Auto-generated method stub
    return null;
  }
}

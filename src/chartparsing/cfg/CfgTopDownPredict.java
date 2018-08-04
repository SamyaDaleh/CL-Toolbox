package chartparsing.cfg;

import java.text.ParseException;
import java.util.List;

import chartparsing.AbstractDynamicDeductionRule;
import chartparsing.DeductionItem;
import chartparsing.Item;
import common.ArrayUtils;
import common.TreeUtils;
import common.cfg.CfgProductionRule;
import common.tag.Tree;

/**
 * If a nonterminal is on top of a stack it can be replaced by any rhs where it
 * is the lhs.
 */
public class CfgTopDownPredict extends AbstractDynamicDeductionRule {

  private final CfgProductionRule rule;

  public CfgTopDownPredict(CfgProductionRule rule) {
    this.rule = rule;
    this.name = "predict " + rule.toString();
    this.antNeeded = 1;
  }

  @Override public List<Item> getConsequences() throws ParseException {
    if (antecedences.size() == antNeeded) {
      String[] itemForm = antecedences.get(0).getItemform();
      String stack = itemForm[0];
      String[] stackSplit = stack.split(" ");
      String i = itemForm[1];
      Tree prevTree = antecedences.get(0).getTree();
      if (stackSplit[0].equals(rule.getLhs())) {
        if (stackSplit.length == 1) {
          Item consequence =
            new DeductionItem(String.join(" ", rule.getRhs()), i);
          if (prevTree == null) {
            consequence.setTree(new Tree(rule));
          } else {
            consequence.setTree(
              TreeUtils.performLeftmostSubstitution(prevTree, new Tree(rule)));
          }
          consequences.add(consequence);
        } else {
          Item consequence =
            new DeductionItem(String.join(" ", rule.getRhs()) + " " + ArrayUtils
              .getSubSequenceAsString(stackSplit, 1, stackSplit.length), i);
          if (prevTree == null) {
            consequence.setTree(new Tree(rule));
          } else {
            consequence.setTree(
              TreeUtils.performLeftmostSubstitution(prevTree, new Tree(rule)));
          }
          consequences.add(consequence);
        }
      }
    }
    return consequences;
  }

  @Override public String toString() {
    return "[" + rule.getLhs() + "α,i]" + "\n______ " + rule.toString() + ", |"
      + ArrayUtils.toString(rule.getRhs()) + " α| ≤ n - i\n" + "["
      + ArrayUtils.toString(rule.getRhs()) + " α,i]";
  }

}

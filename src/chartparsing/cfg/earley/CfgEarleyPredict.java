package chartparsing.cfg.earley;

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
 * If the next symbol after the dot is a nonterminal, for a rule with that
 * symbol as lhs predict a new item.
 */
public class CfgEarleyPredict extends AbstractDynamicDeductionRule {

  private final CfgProductionRule rule;

  public CfgEarleyPredict(CfgProductionRule rule) {
    this.rule = rule;
    this.name = "predict " + rule.toString();
    this.antNeeded = 1;
  }

  @Override public List<Item> getConsequences() throws ParseException {
    if (antecedences.size() == antNeeded) {
      String[] itemForm = antecedences.get(0).getItemform();
      String stack = itemForm[0];
      String[] stackSplit = stack.split(" ");
      int j = Integer.parseInt(itemForm[2]);

      for (String stackSymbol : stackSplit) {
        if (stackSymbol.startsWith("•") && stackSymbol
          .substring(1, stackSymbol.length()).equals(rule.getLhs())) {
          String newStack;
          if (rule.getRhs()[0].equals("")) {
            newStack = rule.getLhs() + " -> •";
          } else {
            newStack =
              rule.getLhs() + " -> " + "•" + String.join(" ", rule.getRhs());
          }
          Item consequence =
            new DeductionItem(newStack, String.valueOf(j), String.valueOf(j));
          Tree derivedTree = antecedences.get(0).getTree();
          if (derivedTree == null) {
            derivedTree = new Tree(rule);
          } else {
            derivedTree = TreeUtils.performLeftmostSubstitution(derivedTree,
              new Tree(rule));
          }
          consequence.setTree(derivedTree);
          consequences.add(consequence);
          break;
        }
      }
    }
    return consequences;
  }

  @Override public String toString() {
    return "[A -> α •" + rule.getLhs() + "β,i,j]" + "\n______ "
      + rule.toString() + "\n" + "[" + rule.getLhs() + " -> •"
      + ArrayUtils.toString(rule.getRhs()) + ",j,j]";
  }

}

package chartparsing.cfgrules;

import java.util.List;

import chartparsing.AbstractDynamicDeductionRule;
import common.ArrayUtils;
import common.Item;
import common.cfg.CfgDottedItem;
import common.cfg.CfgProductionRule;

/** If the next symbol after the dot is a nonterminal, for a rule with that
 * symbol as lhs predict a new item. */
public class CfgEarleyPredict extends AbstractDynamicDeductionRule {

  private final CfgProductionRule rule;

  public CfgEarleyPredict(CfgProductionRule rule) {
    this.rule = rule;
    this.name = "predict " + rule.toString();
    this.antNeeded = 1;
  }

  @Override public List<Item> getConsequences() {
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
          consequences.add(new CfgDottedItem(newStack, j, j));
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

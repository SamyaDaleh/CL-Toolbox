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
    this.antneeded = 1;
  }

  @Override public List<Item> getConsequences() {
    if (antecedences.size() == antneeded) {
      String[] itemform = antecedences.get(0).getItemform();
      String stack = itemform[0];
      String[] stacksplit = stack.split(" ");
      int j = Integer.parseInt(itemform[2]);

      for (String stacksymbol : stacksplit) {
        if (stacksymbol.startsWith("•") && stacksymbol
          .substring(1, stacksymbol.length()).equals(rule.getLhs())) {
          String newstack;
          if (rule.getRhs()[0].equals("")) {
            newstack = rule.getLhs() + " -> •";
          } else {
            newstack =
              rule.getLhs() + " -> " + "•" + String.join(" ", rule.getRhs());
          }
          consequences.add(new CfgDottedItem(newstack, j, j));
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

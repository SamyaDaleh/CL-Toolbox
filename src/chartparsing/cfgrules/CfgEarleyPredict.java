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

  private CfgProductionRule rule;

  public CfgEarleyPredict(CfgProductionRule rule) {
    this.rule = rule;
    this.name ="predict " + rule.toString();
    this.antneeded = 1;
  }

  @Override public List<Item> getConsequences() {
    if (antecedences.size() == antneeded) {
      String[] itemform = antecedences.get(0).getItemform();
      String stack = itemform[0];
      String[] stacksplit = stack.split(" ");
      int j = Integer.parseInt(itemform[2]);

      for (int k = 0; k < stacksplit.length; k++) {
        if (stacksplit[k].startsWith("•") && stacksplit[k]
          .substring(1, stacksplit[k].length()).equals(rule.getLhs())) {
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
    StringBuilder representation = new StringBuilder();
    representation.append("[A -> α •" + rule.getLhs() + "β,i,j]");
    representation.append("\n______ " + rule.toString() + "\n");
    representation.append("[" + rule.getLhs() + " -> •"
      + ArrayUtils.toString(rule.getRhs()) + ",j,j]");
    return representation.toString();
  }

}

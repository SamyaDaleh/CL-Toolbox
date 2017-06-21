package chartparsing.cfg;

import java.util.List;

import chartparsing.AbstractDynamicDeductionRule;
import chartparsing.Item;
import common.ArrayUtils;
import common.cfg.CfgProductionRule;

/** If a nonterminal is on top of a stack it can be replaced by any rhs where it
 * is the lhs. */
public class CfgTopDownPredict extends AbstractDynamicDeductionRule {

  private final CfgProductionRule rule;

  public CfgTopDownPredict(CfgProductionRule rule) {
    this.rule = rule;
    this.name = "predict " + rule.toString();
    this.antNeeded = 1;
  }

  @Override public List<Item> getConsequences() {
    if (antecedences.size() == antNeeded) {
      String[] itemForm = antecedences.get(0).getItemform();
      String stack = itemForm[0];
      String[] stackSplit = stack.split(" ");
      int i = Integer.parseInt(itemForm[1]);
      if (stackSplit[0].equals(rule.getLhs())) {
        if (stackSplit.length == 1) {
          consequences.add(new CfgItem(String.join(" ", rule.getRhs()), i));
        } else {
          consequences
            .add(
              new CfgItem(
                String.join(" ", rule.getRhs()) + " " + ArrayUtils
                  .getSubSequenceAsString(stackSplit, 1, stackSplit.length),
                i));
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

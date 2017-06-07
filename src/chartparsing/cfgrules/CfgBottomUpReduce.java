package chartparsing.cfgrules;

import java.util.List;

import chartparsing.AbstractDynamicDeductionRule;
import common.ArrayUtils;
import common.Item;
import common.cfg.CfgItem;
import common.cfg.CfgProductionRule;

/** If the top o the stack matches the rhs of a rule, replace it with the
 * lhs. */
public class CfgBottomUpReduce extends AbstractDynamicDeductionRule {

  private final CfgProductionRule rule;

  public CfgBottomUpReduce(CfgProductionRule rule) {
    this.rule = rule;
    this.antNeeded = 1;
    this.name = "reduce " + rule.toString();
  }

  @Override public List<Item> getConsequences() {
    if (antecedences.size() == this.antNeeded) {
      String[] itemForm = antecedences.get(0).getItemform();
      String stack = itemForm[0];
      String[] stackSplit = stack.split(" ");
      int i = Integer.parseInt(itemForm[1]);
      String gamma =
        ArrayUtils.getStringHeadIfEndsWith(stackSplit, rule.getRhs());
      if (gamma != null) {
        if (gamma.length() == 0) {
          consequences.add(new CfgItem(rule.getLhs(), i));
        } else {
          consequences.add(new CfgItem(gamma + " " + rule.getLhs(), i));
        }
      }
    }
    return consequences;
  }

  @Override public String toString() {
    return "[Γ " + ArrayUtils.toString(rule.getRhs()) + ",i]" + "\n______"
      + rule.toString() + "\n" + "[Γ " + rule.getLhs() + ",i]";
  }

}

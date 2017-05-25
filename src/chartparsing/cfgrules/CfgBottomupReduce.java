package chartparsing.cfgrules;

import java.util.List;

import chartparsing.AbstractDynamicDeductionRule;
import common.ArrayUtils;
import common.Item;
import common.cfg.CfgItem;
import common.cfg.CfgProductionRule;

/** If the top o the stack matches the rhs of a rule, replace it with the
 * lhs. */
public class CfgBottomupReduce extends AbstractDynamicDeductionRule{

  private CfgProductionRule rule;

  public CfgBottomupReduce(CfgProductionRule rule) {
    this.rule = rule;
    this.antneeded = 1;
    this.name = "reduce " + rule.toString();
  }

  @Override public List<Item> getConsequences() {
    if (antecedences.size() == this.antneeded) {
      String[] itemform = antecedences.get(0).getItemform();
      String stack = itemform[0];
      String[] stacksplit = stack.split(" ");
      int i = Integer.parseInt(itemform[1]);
      String gamma =
        ArrayUtils.getStringHeadIfEndsWith(stacksplit, rule.getRhs());
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
    StringBuilder representation = new StringBuilder();
    representation.append("[Γ " + ArrayUtils.toString(rule.getRhs()) + ",i]");
    representation.append("\n______" + rule.toString() + "\n");
    representation.append("[Γ " + rule.getLhs() + ",i]");
    return representation.toString();
  }

}

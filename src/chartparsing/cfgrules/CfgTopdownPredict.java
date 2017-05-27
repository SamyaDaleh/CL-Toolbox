package chartparsing.cfgrules;

import java.util.List;

import chartparsing.AbstractDynamicDeductionRule;
import common.ArrayUtils;
import common.Item;
import common.cfg.CfgItem;
import common.cfg.CfgProductionRule;

/** If a nonterminal is on top of a stack it can be replaced by any rhs where it
 * is the lhs. */
public class CfgTopdownPredict extends AbstractDynamicDeductionRule {

  private final CfgProductionRule rule;

  public CfgTopdownPredict(CfgProductionRule rule) {
    this.rule = rule;
    this.name = "predict " + rule.toString();
    this.antneeded = 1;
  }

  @Override public List<Item> getConsequences() {
    if (antecedences.size() == antneeded) {
      String[] itemform = antecedences.get(0).getItemform();
      String stack = itemform[0];
      String[] stacksplit = stack.split(" ");
      int i = Integer.parseInt(itemform[1]);
      if (stacksplit[0].equals(rule.getLhs())) {
        // TODO why is there a handle for epsilon productions if TopDown can't be called if CFG has one? Recheck
        if (rule.getRhs().length == 1 && rule.getRhs()[0].equals("")) {
          consequences.add(new CfgItem(
            ArrayUtils.getSubSequenceAsString(stacksplit, 1, stacksplit.length),
            i));
        } else {
          consequences
            .add(
              new CfgItem(
                String.join(" ", rule.getRhs()) + " " + ArrayUtils
                  .getSubSequenceAsString(stacksplit, 1, stacksplit.length),
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

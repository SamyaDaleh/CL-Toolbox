package chartparsing.cfg.unger;

import java.util.List;

import chartparsing.AbstractDynamicDeductionRule;
import chartparsing.DeductionItem;
import chartparsing.Item;
import common.cfg.CfgProductionRule;

/** If the top o the stack matches the rhs of a rule, replace it with the
 * lhs. */
public class CfgUngerComplete extends AbstractDynamicDeductionRule {

  private final CfgProductionRule rule;

  public CfgUngerComplete(CfgProductionRule rule) {
    this.rule = rule;
    this.antNeeded = rule.getRhs().length + 1;
    this.name = "complete " + rule.toString();
  }

  @Override public List<Item> getConsequences() {
    if (antecedences.size() == this.antNeeded) {
      for (Item mayLhsItem : antecedences) {
        if (!mayLhsItem.getItemform()[0].startsWith("•")) {
          continue;
        }
        String prevIjPlusOne = mayLhsItem.getItemform()[1];
        for (int i = 0; i < antNeeded - 1; i++) {
          boolean found = false;
          for (Item mayRhsItem : antecedences) {
            if (mayRhsItem.getItemform()[0].endsWith("•")
              && mayRhsItem.getItemform()[0]
                .substring(0, mayRhsItem.getItemform()[0].length() - 1)
                .equals(rule.getRhs()[i])
              && mayRhsItem.getItemform()[1].equals(prevIjPlusOne)) {
              found = true;
              prevIjPlusOne = mayRhsItem.getItemform()[2];
              break;
            }
          }
          if (!found) {
            return this.consequences;
          }
        }
        if (prevIjPlusOne.equals(mayLhsItem.getItemform()[2])) {
          consequences.add(new DeductionItem(rule.getLhs() + "•",
            mayLhsItem.getItemform()[1], mayLhsItem.getItemform()[2]));
        }
      }
    }
    return consequences;
  }

  @Override public String toString() {
    return "[•A, i_0, i_k], [A_1•, i_0, i_1], ... , [A_k•,i_(k-1), i_k]"
      + "\n______" + rule.toString() + "\n" + "[A•, i_0, i_k]";
  }

}

package chartparsing.cfg;

import java.util.List;

import chartparsing.AbstractDynamicDeductionRule;
import chartparsing.DeductionItem;
import chartparsing.Item;
import common.cfg.CfgProductionRule;

/** If the item matches the rhs of a chain rule, get a new item that represents
 * the lhs. */
public class CfgCykCompleteUnary extends AbstractDynamicDeductionRule {

  private final CfgProductionRule rule;

  public CfgCykCompleteUnary(CfgProductionRule rule) {
    this.rule = rule;
    this.name = "complete " + rule.toString();
    this.antNeeded = 1;
  }

  @Override public List<Item> getConsequences() {
    if (antecedences.size() == antNeeded) {
      String[] itemForm1 = antecedences.get(0).getItemform();
      String nt1 = itemForm1[0];
      String i1 = itemForm1[1];
      int i1int = Integer.parseInt(i1);
      String j1 = itemForm1[2];
      int j1int = Integer.parseInt(j1);

      if (nt1.equals(rule.getRhs()[0])) {
        this.consequences.add(new DeductionItem(rule.getLhs(),
          String.valueOf(i1int), String.valueOf(j1int)));
      }
    }
    return this.consequences;
  }

  @Override public String toString() {
    return "[" + rule.getRhs()[0] + ",i,j]]" + "\n______ \n" + "["
      + rule.getLhs() + ",i,j]";
  }
}

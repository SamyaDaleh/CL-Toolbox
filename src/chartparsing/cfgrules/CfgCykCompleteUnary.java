package chartparsing.cfgrules;

import java.util.List;

import chartparsing.AbstractDynamicDeductionRule;
import common.Item;
import common.cfg.CfgItem;
import common.cfg.CfgProductionRule;

/** If the item matches the rhs of a chain rule, get a new item that represents
 * the lhs. */
public class CfgCykCompleteUnary extends AbstractDynamicDeductionRule {

  private final CfgProductionRule rule;

  public CfgCykCompleteUnary(CfgProductionRule rule) {
    this.rule = rule;
    this.name = "complete " + rule.toString();
    this.antneeded = 1;
  }

  @Override public List<Item> getConsequences() {
    if (antecedences.size() == antneeded) {
      String[] itemform1 = antecedences.get(0).getItemform();
      String nt1 = itemform1[0];
      String i1 = itemform1[1];
      int i1int = Integer.parseInt(i1);
      String j1 = itemform1[2];
      int j1int = Integer.parseInt(j1);

      if (nt1.equals(rule.getRhs()[0])) {
        this.consequences.add(new CfgItem(rule.getLhs(), i1int, j1int));
      }
    }
    return this.consequences;
  }

  @Override public String toString() {
    return "[" + rule.getRhs()[0] + ",i,j]]" + "\n______ \n" + "["
      + rule.getLhs() + ",i,j]";
  }
}

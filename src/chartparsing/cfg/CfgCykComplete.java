package chartparsing.cfg;

import java.util.List;

import chartparsing.AbstractDynamicDeductionRule;
import chartparsing.DeductionItem;
import chartparsing.Item;
import common.cfg.CfgProductionRule;

/** If two items match the rhs of a rule, get a new item that represents the
 * lhs. */
public class CfgCykComplete extends AbstractDynamicDeductionRule {

  private final CfgProductionRule rule;

  public CfgCykComplete(CfgProductionRule rule) {
    this.rule = rule;
    this.name = "complete " + rule.toString();
    this.antNeeded = 2;
  }

  @Override public List<Item> getConsequences() {
    if (antecedences.size() == antNeeded) {
      String[] itemForm1 = antecedences.get(0).getItemform();
      String nt1 = itemForm1[0];
      String i1 = itemForm1[1];
      int i1int = Integer.parseInt(i1);
      String j1 = itemForm1[2];
      int j1int = Integer.parseInt(j1);

      String[] itemForm2 = antecedences.get(1).getItemform();
      String nt2 = itemForm2[0];
      String i2 = itemForm2[1];
      int i2int = Integer.parseInt(i2);
      String j2 = itemForm2[2];
      int j2int = Integer.parseInt(j2);

      if (nt1.equals(rule.getRhs()[0]) && nt2.equals(rule.getRhs()[1])
        && i1int + j1int == i2int) {
        this.consequences.add(new DeductionItem(rule.getLhs(),
          String.valueOf(i1int), String.valueOf(j1int + j2int)));
      } else if (nt2.equals(rule.getRhs()[0]) && nt1.equals(rule.getRhs()[1])
        && i2int + j2int == i1int) {
        // the other way round
        this.consequences.add(new DeductionItem(rule.getLhs(),
          String.valueOf(i2int), String.valueOf(j2int + j1int)));
      }
    }
    return this.consequences;
  }

  @Override public String toString() {
    return "[" + rule.getRhs()[0] + ",1,l1], [" + rule.getRhs()[1] + ",i+l1,l2]"
      + "\n______ \n" + "[" + rule.getLhs() + ",i,l1+l2]";
  }
}

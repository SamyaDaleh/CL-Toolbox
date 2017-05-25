package chartparsing.cfgrules;

import java.util.LinkedList;
import java.util.List;

import chartparsing.DynamicDeductionRule;
import common.Item;
import common.cfg.CfgItem;
import common.cfg.CfgProductionRule;

/** If two items match the rhs of a rule, get a new item that represents the
 * lhs. */
public class CfgCykComplete implements DynamicDeductionRule {

  private List<Item> antecedences = new LinkedList<Item>();
  private List<Item> consequences = new LinkedList<Item>();
  private String name = null;

  private CfgProductionRule rule;
  private int antneeded = 2;

  public CfgCykComplete(CfgProductionRule rule) {
    this.rule = rule;
    this.name = "complete " + rule.toString();
  }

  @Override public void addAntecedence(Item item) {
    antecedences.add(item);
  }

  @Override public List<Item> getAntecedences() {
    return antecedences;
  }

  @Override public void setAntecedences(List<Item> antecedences) {
    this.antecedences = antecedences;
  }

  @Override public List<Item> getConsequences() {
    if (antecedences.size() == antneeded) {
      String[] itemform1 = antecedences.get(0).getItemform();
      String nt1 = itemform1[0];
      String i1 = itemform1[1];
      int i1int = Integer.parseInt(i1);
      String j1 = itemform1[2];
      int j1int = Integer.parseInt(j1);

      String[] itemform2 = antecedences.get(1).getItemform();
      String nt2 = itemform2[0];
      String i2 = itemform2[1];
      int i2int = Integer.parseInt(i2);
      String j2 = itemform2[2];
      int j2int = Integer.parseInt(j2);

      if (nt1.equals(rule.getRhs()[0]) && nt2.equals(rule.getRhs()[1])
        && i1int + j1int == i2int) {
        this.consequences.add(new CfgItem(rule.getLhs(), i1int, j1int + j2int));
      } else if (nt2.equals(rule.getRhs()[0]) && nt1.equals(rule.getRhs()[1])
        && i2int + j2int == i1int) {
        // the other way round
        this.consequences.add(new CfgItem(rule.getLhs(), i2int, j2int + j1int));
      }
    }
    return this.consequences;
  }

  @Override public String getName() {
    return this.name;
  }

  @Override public int getAntecedencesNeeded() {
    return this.antneeded;
  }

  @Override public String toString() {
    StringBuilder representation = new StringBuilder();
    representation.append(
      "[" + rule.getRhs()[0] + ",1,l1], [" + rule.getRhs()[1] + ",i+l1,l2]");
    representation.append("\n______ \n");
    representation.append("[" + rule.getLhs() + ",i,l1+l2]");
    return representation.toString();
  }

  @Override public void clearItems() {
    antecedences = new LinkedList<Item>();
    consequences = new LinkedList<Item>();
  }
}

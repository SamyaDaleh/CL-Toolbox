package chartparsing.cfgrules;

import java.util.LinkedList;
import java.util.List;

import chartparsing.DynamicDeductionRule;
import common.Item;
import common.cfg.CfgItem;
import common.cfg.CfgProductionRule;

/** If the item matches the rhs of a chain rule, get a new item that represents
 * the lhs. */
public class CfgCykCompleteUnary implements DynamicDeductionRule {

  private List<Item> antecedences = new LinkedList<Item>();
  private List<Item> consequences = new LinkedList<Item>();
  private String name = null;

  private CfgProductionRule rule;
  private int antneeded = 1;

  public CfgCykCompleteUnary(CfgProductionRule rule) {
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

      if (nt1.equals(rule.getRhs()[0])) {
        this.consequences.add(new CfgItem(rule.getLhs(), i1int, j1int));
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
      "[" + rule.getRhs()[0] + ",i,j]]");
    representation.append("\n______ \n");
    representation.append("[" + rule.getLhs() + ",i,j]");
    return representation.toString();
  }

  @Override public void clearItems() {
    antecedences = new LinkedList<Item>();
    consequences = new LinkedList<Item>();
  }
}

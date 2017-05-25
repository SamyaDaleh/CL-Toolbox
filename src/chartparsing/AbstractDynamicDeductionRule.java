package chartparsing;

import java.util.LinkedList;
import java.util.List;

import common.Item;

/** Class to hold the methods commonly used by all DeductionRules. */
public abstract class AbstractDynamicDeductionRule
  implements DynamicDeductionRule {

  protected List<Item> antecedences;
  protected List<Item> consequences;
  protected String name;

  protected int antneeded;

  @Override public void addAntecedence(Item item) {
    this.antecedences.add(item);
  }

  @Override public List<Item> getAntecedences() {
    return this.antecedences;
  }

  @Override public void setAntecedences(List<Item> antecedences) {
    this.antecedences = antecedences;
  }

  @Override public String getName() {
    return this.name;
  }

  @Override public int getAntecedencesNeeded() {
    return this.antneeded;
  }

  public void setAntecedednesNeeded(int antneeded) {
    this.antneeded = antneeded;
  }

  @Override public void clearItems() {
    antecedences = new LinkedList<Item>();
    consequences = new LinkedList<Item>();
  }

}

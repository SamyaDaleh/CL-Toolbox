package chartparsing;

import java.util.ArrayList;
import java.util.List;

/** Static deduction rule that stores a set of antecedences and consequences.
 * Used as axiom. */
public class StaticDeductionRule implements DeductionRule{
  private List<Item> antecedences = new ArrayList<Item>();
  final List<Item> consequences = new ArrayList<Item>();
  private String name = null;

  public void addAntecedence(Item item) {
    antecedences.add(item);
  }

  public void addConsequence(Item item) {
    consequences.add(item);
  }

  public List<Item> getAntecedences() {
    return antecedences;
  }

  public void setAntecedences(List<Item> antecedences) {
    this.antecedences = antecedences;
  }

  public List<Item> getConsequences() {
    return consequences;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getName() {
    return this.name;
  }

  @Override public String toString() {
    StringBuilder representation = new StringBuilder();
    for (Item rule : antecedences) {
      representation.append(rule.toString());
    }
    representation.append("\n______\n");
    for (Item rule : consequences) {
      representation.append(rule.toString());
    }
    return representation.toString();
  }
}

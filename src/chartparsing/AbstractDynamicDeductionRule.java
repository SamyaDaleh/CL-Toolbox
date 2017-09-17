package chartparsing;

import java.util.ArrayList;
import java.util.List;

/** Class to hold the methods commonly used by all DeductionRules. */
public abstract class AbstractDynamicDeductionRule
  implements DynamicDeductionRule {

  protected List<Item> antecedences;
  protected List<Item> consequences;
  protected String name;

  protected int antNeeded;

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
    return this.antNeeded;
  }

  @Override public void clearItems() {
    antecedences = new ArrayList<Item>();
    consequences = new ArrayList<Item>();
  }

}

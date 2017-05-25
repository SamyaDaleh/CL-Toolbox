package chartparsing.cfgrules;

import java.util.LinkedList;
import java.util.List;

import chartparsing.DynamicDeductionRule;
import common.Item;
import common.cfg.CfgItem;

/** Moves the next input symbol onto the stack */
public class CfgBottomupShift implements DynamicDeductionRule {

  private List<Item> antecedences = new LinkedList<Item>();
  private List<Item> consequences = new LinkedList<Item>();
  private String name = "shift";

  private String[] wsplit;

  private int antneeded = 1;

  public CfgBottomupShift(String[] wsplit) {
    this.wsplit = wsplit;
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
      String[] itemform = antecedences.get(0).getItemform();
      String stack = itemform[0];
      int i = Integer.parseInt(itemform[1]);
      if (i < wsplit.length) {
        if (stack.length() == 0) {
          consequences.add(new CfgItem(wsplit[i], i + 1));
        } else {
          consequences.add(new CfgItem(stack + " " + wsplit[i], i + 1));
        }
      }
    }
    return consequences;
  }

  @Override public String getName() {
    return this.name;
  }

  @Override public int getAntecedencesNeeded() {
    return this.antneeded;
  }

  @Override public String toString() {
    StringBuilder representation = new StringBuilder();
    representation.append("[Γ,i]");
    representation.append("\n______ w_i = a\n");
    representation.append("[Γa,i+1]");
    return representation.toString();
  }

  @Override public void clearItems() {
    antecedences = new LinkedList<Item>();
    consequences = new LinkedList<Item>();
  }

}

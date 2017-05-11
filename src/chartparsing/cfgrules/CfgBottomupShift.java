package chartparsing.cfgrules;

import java.util.LinkedList;
import java.util.List;

import chartparsing.DynamicDeductionRule;
import common.Item;
import common.cfg.CfgItem;

/** Moves the next input symbol onto the stack */
public class CfgBottomupShift implements DynamicDeductionRule {

  List<Item> antecedences = new LinkedList<Item>();
  List<Item> consequences = new LinkedList<Item>();
  String name = null;

  String[] wsplit;

  int antneeded = 1;

  public CfgBottomupShift(String[] wsplit) {
    this.wsplit = wsplit;
    this.setName("shift");
  }

  @Override public void addAntecedence(Item item) {
    antecedences.add(item);
  }

  @Override public void addConsequence(Item item) {
    consequences.add(item);
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
        this.setName("shift " + wsplit[i]);
      }
    }
    return consequences;
  }

  @Override public void setConsequences(List<Item> consequences) {
    // ignore
  }

  @Override public void setName(String name) {
    this.name = name;
  }

  @Override public String getName() {
    return this.name;
  }

  @Override public int getAntecedencesNeeded() {
    return this.antneeded;
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

  @Override public void clearItems() {
    antecedences = new LinkedList<Item>();
    consequences = new LinkedList<Item>();
  }

}

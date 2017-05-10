package chartparsing.cfgrules;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import chartparsing.DynamicDeductionRule;
import common.Item;
import common.cfg.CfgItem;

public class CfgTopdownScan implements DynamicDeductionRule {
  
  List<Item> antecedences = new LinkedList<Item>();
  List<Item> consequences = new LinkedList<Item>();
  String name = null;
  
  String[] wsplit;

  int antneeded = 1;
  
  public void setwsplit(String[] wsplit) {
    this.wsplit = wsplit;
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
    consequences = new LinkedList<Item>();
    if (antecedences.size() == antneeded) {
      String[] itemform = antecedences.get(0).getItemform();
      String stack = itemform[0];
      int i = Integer.parseInt(itemform[1]);
      // if stack[i] equals wsplit[i]
      // consequences.add(new CfgItem("",0));
    }
    antecedences = new LinkedList<Item>();
    return consequences;
  }

  @Override public void setConsequences(List<Item> consequences) {
    // ignore
  }

  @Override public void setName(String name) {
    this.name=name;
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

}

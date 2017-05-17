package chartparsing.cfgrules;

import java.util.LinkedList;
import java.util.List;

import chartparsing.DynamicDeductionRule;
import common.ArrayUtils;
import common.Item;
import common.cfg.CfgDottedItem;
import common.cfg.CfgProductionRule;

/** If the next symbol after the dot is a nonterminal, for a rule with that
 * symbol as lhs predict a new item. */
public class CfgEarleyPredict implements DynamicDeductionRule {

  List<Item> antecedences = new LinkedList<Item>();
  List<Item> consequences = new LinkedList<Item>();
  String name = null;

  CfgProductionRule rule;

  int antneeded = 1;

  public CfgEarleyPredict(CfgProductionRule rule) {
    this.rule = rule;
    this.setName("predict " + rule.toString());
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
      String[] stacksplit = stack.split(" ");
      int j = Integer.parseInt(itemform[2]);

      for (int k = 0; k < stacksplit.length; k++) {
        if (stacksplit[k].startsWith("•") && stacksplit[k]
          .substring(1, stacksplit[k].length()).equals(rule.getLhs())) {
          String newstack;
          if (rule.getRhs()[0].equals("")) {
            newstack = rule.getLhs() + " -> •";
          } else {
            newstack =
              rule.getLhs() + " -> " + "•" + String.join(" ", rule.getRhs());
          }
          consequences.add(new CfgDottedItem(newstack, j, j));
          break;
        }
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
    representation.append("[A -> α •" + rule.getLhs() + "β,i,j]");
    representation.append("\n______ " + rule.toString() + "\n");
    representation.append("[" + rule.getLhs() + " -> •"
      + ArrayUtils.toString(rule.getRhs()) + ",j,j]");
    return representation.toString();
  }

  @Override public void clearItems() {
    antecedences = new LinkedList<Item>();
    consequences = new LinkedList<Item>();
  }

}

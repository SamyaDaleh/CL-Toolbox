package chartparsing.cfgrules;

import java.util.LinkedList;
import java.util.List;

import chartparsing.DynamicDeductionRule;
import common.ArrayUtils;
import common.Item;
import common.cfg.CfgItem;
import common.cfg.CfgProductionRule;

/** If the top o the stack matches the rhs of a rule, replace it with the
 * lhs. */
public class CfgBottomupReduce implements DynamicDeductionRule {

  List<Item> antecedences = new LinkedList<Item>();
  List<Item> consequences = new LinkedList<Item>();
  String name = null;

  CfgProductionRule rule;

  int antneeded = 1;

  public CfgBottomupReduce(CfgProductionRule rule) {
    this.rule = rule;
    this.setName("reduce " + rule.toString());
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
      int i = Integer.parseInt(itemform[1]);
      String gamma =
        ArrayUtils.getStringHeadIfEndsWith(stacksplit, rule.getRhs());
      if (gamma != null) {
        if (gamma.length() == 0) {
          consequences.add(new CfgItem(rule.getLhs(), i));
        } else {
          consequences.add(new CfgItem(gamma + " " + rule.getLhs(), i));
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
    representation.append("[Γ " + ArrayUtils.toString(rule.getRhs()) + ",i]");
    representation.append("\n______" + rule.toString() + "\n");
    representation.append("[Γ " + rule.getLhs() + ",i]");
    return representation.toString();
  }

  @Override public void clearItems() {
    antecedences = new LinkedList<Item>();
    consequences = new LinkedList<Item>();
  }

}

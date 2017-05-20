package chartparsing.cfgrules;

import java.util.LinkedList;
import java.util.List;

import chartparsing.DynamicDeductionRule;
import common.ArrayUtils;
import common.Item;
import common.cfg.CfgItem;
import common.cfg.CfgProductionRule;

/** If a nonterminal is on top of a stack it can be replaced by any rhs where it
 * is the lhs. */
public class CfgTopdownPredict implements DynamicDeductionRule {

  private List<Item> antecedences = new LinkedList<Item>();
  private List<Item> consequences = new LinkedList<Item>();
  private String name = null;

  private CfgProductionRule rule;

  private int antneeded = 1;

  public CfgTopdownPredict(CfgProductionRule rule) {
    this.rule = rule;
    this.name = "predict " + rule.toString();
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
      if (stacksplit[0].equals(rule.getLhs())) {
        // TODO check if epsilon is like rhs length 0 or 1 with entry ""
        if (rule.getRhs().length == 0) {
          consequences.add(new CfgItem(
            ArrayUtils.getSubSequenceAsString(stacksplit, 1, stacksplit.length),
            i));
        } else {
          consequences
            .add(
              new CfgItem(
                String.join(" ", rule.getRhs()) + " " + ArrayUtils
                  .getSubSequenceAsString(stacksplit, 1, stacksplit.length),
                i));
        }
      }
    }
    return consequences;
  }

  @Override public void setConsequences(List<Item> consequences) {
    // ignore
  }

  @Override public String getName() {
    return this.name;
  }

  @Override public int getAntecedencesNeeded() {
    return this.antneeded;
  }

  @Override public String toString() {
    StringBuilder representation = new StringBuilder();
    representation.append("[" + rule.getLhs() + "α,i]");
    representation.append("\n______ " + rule.toString() + ", |"
      + ArrayUtils.toString(rule.getRhs()) + " α| ≤ n - i\n");
    representation.append("[" + ArrayUtils.toString(rule.getRhs()) + " α,i]");
    return representation.toString();
  }

  @Override public void clearItems() {
    antecedences = new LinkedList<Item>();
    consequences = new LinkedList<Item>();
  }

}

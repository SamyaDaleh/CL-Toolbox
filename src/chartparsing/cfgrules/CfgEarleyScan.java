package chartparsing.cfgrules;

import java.util.LinkedList;
import java.util.List;

import chartparsing.DynamicDeductionRule;
import common.ArrayUtils;
import common.Item;
import common.cfg.CfgDottedItem;

/** The scan rule for topdown removes a terminal if it is the next input
 * symbol. */
public class CfgEarleyScan implements DynamicDeductionRule {

  List<Item> antecedences = new LinkedList<Item>();
  List<Item> consequences = new LinkedList<Item>();
  String name = null;

  String[] wsplit;

  int antneeded = 1;

  public CfgEarleyScan(String[] wsplit) {
    this.wsplit = wsplit;
    this.setName("scan");
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
      int j = Integer.parseInt(itemform[2]);

      for (int k = 0; k < stacksplit.length; k++) {
        if (stacksplit[k].startsWith("•") && j < wsplit.length && wsplit[j]
          .equals(stacksplit[k].substring(1, stacksplit[k].length()))) {
          StringBuilder newstack = new StringBuilder();
          newstack
            .append(ArrayUtils.getSubSequenceAsString(stacksplit, 0, k));
          if (k == stacksplit.length - 1) {
            newstack.append(" " + wsplit[j] + " •");
          } else {
            newstack.append(" " + wsplit[j] + " •" + ArrayUtils
              .getSubSequenceAsString(stacksplit, k + 1, stacksplit.length));
          }
          consequences.add(new CfgDottedItem(newstack.toString(), i, j + 1));
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

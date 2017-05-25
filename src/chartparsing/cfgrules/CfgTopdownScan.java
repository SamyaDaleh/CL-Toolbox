package chartparsing.cfgrules;

import java.util.LinkedList;
import java.util.List;

import chartparsing.DynamicDeductionRule;
import common.ArrayUtils;
import common.Item;
import common.cfg.CfgItem;

/** The scan rule for topdown removes a terminal if it is the next input
 * symbol. */
public class CfgTopdownScan implements DynamicDeductionRule {

  private List<Item> antecedences = new LinkedList<Item>();
  private List<Item> consequences = new LinkedList<Item>();
  private String name = "scan";

  private String[] wsplit;

  private int antneeded = 1;

  public CfgTopdownScan(String[] wsplit) {
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
      String[] stacksplit = stack.split(" ");
      int i = Integer.parseInt(itemform[1]);
      if (i < wsplit.length && stacksplit[0].equals(wsplit[i])) {
        consequences.add(new CfgItem(
          ArrayUtils.getSubSequenceAsString(stacksplit, 1, stacksplit.length),
          i + 1));
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
    representation.append("[aα,i]");
    representation.append("\n______ w_i = a\n");
    representation.append("[α,i+1]");
    return representation.toString();
  }

  @Override public void clearItems() {
    antecedences = new LinkedList<Item>();
    consequences = new LinkedList<Item>();
  }

}

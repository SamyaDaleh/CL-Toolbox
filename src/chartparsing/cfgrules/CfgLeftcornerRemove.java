package chartparsing.cfgrules;

import java.util.LinkedList;
import java.util.List;

import chartparsing.DynamicDeductionRule;
import common.ArrayUtils;
import common.Item;
import common.cfg.CfgDollarItem;

/** If topmost symbol on stacks completed and predicted are the same, remove
 * both. */
public class CfgLeftcornerRemove implements DynamicDeductionRule {

  private List<Item> antecedences = new LinkedList<Item>();
  private List<Item> consequences = new LinkedList<Item>();
  private String name = "remove";

  private int antneeded = 1;

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
      String stackcompl = itemform[0];
      String[] stackcomplsplit = stackcompl.split(" ");
      String stackpred = itemform[1];
      String[] stackpredsplit = stackpred.split(" ");
      String stacklhs = itemform[2];
      if (stackcomplsplit[0].equals(stackpredsplit[0])) {
        String newcompl = ArrayUtils.getSubSequenceAsString(stackcomplsplit, 1,
          stackcomplsplit.length);
        String newpred = ArrayUtils.getSubSequenceAsString(stackpredsplit, 1,
          stackpredsplit.length);
        consequences.add(new CfgDollarItem(newcompl, newpred, stacklhs));
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
    representation.append("[Xα,Xβ,ɣ]");
    representation.append("\n______\n");
    representation.append("[α,β,ɣ]");
    return representation.toString();
  }

  @Override public void clearItems() {
    antecedences = new LinkedList<Item>();
    consequences = new LinkedList<Item>();
  }

}

package chartparsing.cfgrules;

import java.util.LinkedList;
import java.util.List;

import chartparsing.DynamicDeductionRule;
import common.ArrayUtils;
import common.Item;
import common.cfg.CfgDollarItem;
import common.cfg.CfgProductionRule;

/** If the top of the completed stack is the left corner of a production rule,
 * pop that symbol, push the rest of the rhs to the stack to be predicted and
 * add the lhs to the stack of lhs */
public class CfgLeftcornerReduce implements DynamicDeductionRule {

  List<Item> antecedences = new LinkedList<Item>();
  List<Item> consequences = new LinkedList<Item>();
  String name = null;

  CfgProductionRule rule;

  int antneeded = 1;

  public CfgLeftcornerReduce(CfgProductionRule rule) {
    this.setName("reduce " + rule.toString());
    this.rule = rule;
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
      String stackcompl = itemform[0];
      String[] stackcomplsplit = stackcompl.split(" ");
      String stackpred = itemform[1];
      String[] stackpredsplit = stackpred.split(" ");
      String stacklhs = itemform[2];

      if (!stackpredsplit[0].equals("$")
        && stackcomplsplit[0].equals(rule.getRhs()[0])) {
        String newcompl = ArrayUtils.getSubSequenceAsString(stackcomplsplit, 1,
          stackcompl.length());
        String newpred;
        if (rule.getRhs().length == 1) {
          newpred = "$ " + stackpred;
        } else {
          newpred = ArrayUtils.getSubSequenceAsString(rule.getRhs(), 1,
            rule.getRhs().length) + " $ " + stackpred;
        }
          
        String newlhs;
        if (stacklhs.length() == 0) {
          newlhs = rule.getLhs();
        } else {
          newlhs = rule.getLhs() + " " + stacklhs;
        }
        consequences.add(new CfgDollarItem(newcompl, newpred, newlhs));
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

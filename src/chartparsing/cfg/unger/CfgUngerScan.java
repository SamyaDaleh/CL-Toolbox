package chartparsing.cfg.unger;

import java.util.List;

import chartparsing.AbstractDynamicDeductionRule;
import chartparsing.DeductionItem;
import chartparsing.Item;

/** Makes a predicted item to a recognized item. */
public class CfgUngerScan extends AbstractDynamicDeductionRule {

  private final String[] wSplit;

  public CfgUngerScan(String[] wSplit) {
    this.wSplit = wSplit;
    this.name = "scan";
    this.antNeeded = 1;
  }

  @Override public List<Item> getConsequences() {
    if (antecedences.size() == antNeeded) {
      String[] itemForm = antecedences.get(0).getItemform();
      int pos = Integer.parseInt(itemForm[1]);
      if (itemForm[0].charAt(0) == '•'
        && itemForm[0].substring(1).equals(wSplit[pos])) {
        Item consequence =
          new DeductionItem(wSplit[pos] + "•", itemForm[1], itemForm[2]);
        consequence.setTree(antecedences.get(0).getTree());
        consequences.add(consequence);
      }
    }
    return consequences;
  }

  @Override public String toString() {
    return "[•a,i,i+1]" + "\n______ w_i = a\n" + "[a•,i,i+1]";
  }

}

package chartparsing.cfg;

import java.util.List;

import chartparsing.AbstractDynamicDeductionRule;
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
        consequences
          .add(new CfgItem(wSplit[pos] + "•", itemForm[1], itemForm[2]));
      }
    }
    return consequences;
  }

  @Override public String toString() {
    return "[•a,i,i+1]" + "\n______ w_i = a\n" + "[a•,i,i+1]";
  }

}

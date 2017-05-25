package chartparsing.cfgrules;

import java.util.List;

import chartparsing.AbstractDynamicDeductionRule;
import common.Item;
import common.cfg.CfgItem;

/** Moves the next input symbol onto the stack */
public class CfgBottomupShift extends AbstractDynamicDeductionRule {

  private String[] wsplit;

  public CfgBottomupShift(String[] wsplit) {
    this.wsplit = wsplit;
    this.name = "shift";
    this.antneeded = 1;
  }

  @Override public List<Item> getConsequences() {
    if (antecedences.size() == antneeded) {
      String[] itemform = antecedences.get(0).getItemform();
      String stack = itemform[0];
      int i = Integer.parseInt(itemform[1]);
      if (i < wsplit.length) {
        if (stack.length() == 0) {
          consequences.add(new CfgItem(wsplit[i], i + 1));
        } else {
          consequences.add(new CfgItem(stack + " " + wsplit[i], i + 1));
        }
      }
    }
    return consequences;
  }

  @Override public String toString() {
    StringBuilder representation = new StringBuilder();
    representation.append("[Γ,i]");
    representation.append("\n______ w_i = a\n");
    representation.append("[Γa,i+1]");
    return representation.toString();
  }
  
}

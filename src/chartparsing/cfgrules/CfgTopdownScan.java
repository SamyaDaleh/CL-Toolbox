package chartparsing.cfgrules;

import java.util.List;

import chartparsing.AbstractDynamicDeductionRule;
import common.ArrayUtils;
import common.Item;
import common.cfg.CfgItem;

/** The scan rule for topdown removes a terminal if it is the next input
 * symbol. */
public class CfgTopdownScan extends AbstractDynamicDeductionRule {

  private final String[] wsplit;

  public CfgTopdownScan(String[] wsplit) {
    this.wsplit = wsplit;
    this.name = "scan";
    this.antneeded = 1;
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

  @Override public String toString() {
    return "[aα,i]" + "\n______ w_i = a\n" + "[α,i+1]";
  }

}

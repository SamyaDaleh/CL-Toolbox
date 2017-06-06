package chartparsing.cfgrules;

import java.util.List;

import chartparsing.AbstractDynamicDeductionRule;
import common.ArrayUtils;
import common.Item;
import common.cfg.CfgDottedItem;

/** The scan rule for topdown removes a terminal if it is the next input
 * symbol. */
public class CfgEarleyScan extends AbstractDynamicDeductionRule {

  private final String[] wsplit;

  public CfgEarleyScan(String[] wsplit) {
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
      int j = Integer.parseInt(itemform[2]);

      for (int k = 0; k < stacksplit.length; k++) {
        if (stacksplit[k].startsWith("•") && j < wsplit.length && wsplit[j]
          .equals(stacksplit[k].substring(1, stacksplit[k].length()))) {
          StringBuilder newstack = new StringBuilder();
          newstack.append(ArrayUtils.getSubSequenceAsString(stacksplit, 0, k));
          if (k == stacksplit.length - 1) {
            newstack.append(" ").append(wsplit[j]).append(" •");
          } else {
            newstack.append(" ").append(wsplit[j]).append(" •")
              .append(ArrayUtils.getSubSequenceAsString(stacksplit, k + 1,
                stacksplit.length));
          }
          consequences.add(new CfgDottedItem(newstack.toString(), i, j + 1));
        }
      }
    }
    return consequences;
  }

  @Override public String toString() {
    return "[A -> α •a β,i,j]" + "\n______ w_j = a\n" + "[A -> α a • β,i,j+1]";
  }

}

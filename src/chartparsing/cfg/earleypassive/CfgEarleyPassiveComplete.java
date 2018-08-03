package chartparsing.cfg.earleypassive;

import chartparsing.AbstractDynamicDecutionRuleTwoAntecedences;
import chartparsing.DeductionItem;
import common.ArrayUtils;

/**
 * Moves the dot over a nonterminal by using a passive item.
 */
public class CfgEarleyPassiveComplete
  extends AbstractDynamicDecutionRuleTwoAntecedences {

  public CfgEarleyPassiveComplete() {
    this.name = "complete";
    this.antNeeded = 2;
  }

  @Override protected void calculateConsequences(String[] itemForm1,
    String[] itemForm2) {
    if (itemForm1[0].contains("•") && !itemForm2[0].contains("•")
      && itemForm1[2].equals(itemForm2[1])) {
      String stack1 = itemForm1[0];
      String[] stackSplit1 = stack1.split(" ");
      for (int l = 0; l < stackSplit1.length; l++) {
        if (stackSplit1[l].startsWith("•")
          && stackSplit1[l].substring(1).equals(itemForm2[0])) {
          String newStack;
          if (l == stackSplit1.length - 1) {
            newStack = ArrayUtils.getSubSequenceAsString(stackSplit1, 0, l)
              + " " + itemForm2[0] + " •";
          } else {
            newStack = ArrayUtils.getSubSequenceAsString(stackSplit1, 0, l)
              + " " + itemForm2[0] + " •" + ArrayUtils
                .getSubSequenceAsString(stackSplit1, l + 1, stackSplit1.length);
          }
          consequences
            .add(new DeductionItem(newStack, itemForm1[1], itemForm2[2]));
          break;
        }
      }
    }
  }

  @Override public String toString() {
    return "[A -> α •B β,i,j] [B,j,k]" + "\n______\n" + "[A -> α B •β,i,k]";
  }

}

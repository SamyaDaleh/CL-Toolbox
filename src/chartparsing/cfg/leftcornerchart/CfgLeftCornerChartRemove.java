package chartparsing.cfg.leftcornerchart;

import chartparsing.AbstractDynamicDecutionRuleTwoAntecedences;
import chartparsing.DeductionItem;
import common.ArrayUtils;

/**
 * If topmost symbol on stacks completed and predicted are the same, remove
 * both.
 */
public class CfgLeftCornerChartRemove
  extends AbstractDynamicDecutionRuleTwoAntecedences {

  public CfgLeftCornerChartRemove() {
    this.name = "remove";
    this.antNeeded = 2;
  }

  protected void calculateConsequences(String[] itemForm1, String[] itemForm2) {
    String[] mayDottedRuleSplit = itemForm1[0].split(" ");
    for (int k = 0; k < mayDottedRuleSplit.length; k++) {
      if (mayDottedRuleSplit[k].startsWith("•")) {
        int i = Integer.parseInt(itemForm1[1]);
        int l1 = Integer.parseInt(itemForm1[2]);
        int j = Integer.parseInt(itemForm2[1]);
        int l2 = Integer.parseInt(itemForm2[2]);
        if (mayDottedRuleSplit[k].substring(1).equals(itemForm2[0])
          && i + l1 == j && mayDottedRuleSplit[k].length() > 1) {
          if (k == mayDottedRuleSplit.length - 1) {
            consequences.add(new DeductionItem(
              ArrayUtils.getSubSequenceAsString(mayDottedRuleSplit, 0, k) + " "
                + mayDottedRuleSplit[k].substring(1) + " •",
              String.valueOf(i), String.valueOf(l1 + l2)));
          } else {
            consequences.add(new DeductionItem(
              ArrayUtils.getSubSequenceAsString(mayDottedRuleSplit, 0, k) + " "
                + mayDottedRuleSplit[k].substring(1) + " •"
                + ArrayUtils.getSubSequenceAsString(mayDottedRuleSplit, k + 1,
                  mayDottedRuleSplit.length),
              String.valueOf(i), String.valueOf(l1 + l2)));
          }
        } else {
          return;
        }
      }
    }
  }

  @Override public String toString() {
    return "[A -> α •X β,i,l_1], [X,j,l_2]" + "\n______ j = i+l_1\n"
      + "[A -> α X •β,i,l_1+l_2]";
  }

}

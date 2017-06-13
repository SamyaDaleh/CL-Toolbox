package chartparsing.cfgrules;

import java.util.List;

import chartparsing.AbstractDynamicDeductionRule;
import common.ArrayUtils;
import common.Item;
import common.cfg.CfgItem;

/** If topmost symbol on stacks completed and predicted are the same, remove
 * both. */
public class CfgLeftCornerChartRemove extends AbstractDynamicDeductionRule {

  public CfgLeftCornerChartRemove() {
    this.name = "remove";
    this.antNeeded = 2;
  }

  @Override public List<Item> getConsequences() {
    if (antecedences.size() == antNeeded) {
      String[] itemForm1 = antecedences.get(0).getItemform();
      String[] itemForm2 = antecedences.get(1).getItemform();
      calculateConsequences(itemForm1, itemForm2);
      calculateConsequences(itemForm2, itemForm1);
    }
    return consequences;
  }

  private void calculateConsequences(String[] itemForm1, String[] itemForm2) {
    String[] mayDottedRuleSplit = itemForm1[0].split(" ");
    for (int k = 0; k < mayDottedRuleSplit.length; k++) {
      if (mayDottedRuleSplit[k].startsWith("•")) {
        int i = Integer.parseInt(itemForm1[1]);
        int l1 = Integer.parseInt(itemForm1[2]);
        int j = Integer.parseInt(itemForm2[1]);
        int l2 = Integer.parseInt(itemForm2[2]);
        if (mayDottedRuleSplit[k].substring(1).equals(itemForm2[0]) && i + l1 == j) {
          if (k == mayDottedRuleSplit.length - 1) {
            consequences
              .add(new CfgItem(
                ArrayUtils.getSubSequenceAsString(mayDottedRuleSplit, 0, k)
                  + " " + mayDottedRuleSplit[k].substring(1) + " •",
                i, l1 + l2));
          } else {
            consequences
              .add(
                new CfgItem(
                  ArrayUtils.getSubSequenceAsString(mayDottedRuleSplit, 0, k)
                    + " " + mayDottedRuleSplit[k].substring(1)
                    + " •" + ArrayUtils.getSubSequenceAsString(
                      mayDottedRuleSplit, k + 1, mayDottedRuleSplit.length),
                  i, l1 + l2));
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

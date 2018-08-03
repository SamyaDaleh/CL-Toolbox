package chartparsing.cfg.earleypassive;

import java.util.List;

import chartparsing.AbstractDynamicDeductionRule;
import chartparsing.DeductionItem;
import chartparsing.Item;

/**
 * Converts an active item (with a dot) into a passive item that does not care
 * which rule led to its creation.
 *
 */
public class CfgEarleyPassiveConvert extends AbstractDynamicDeductionRule {

  public CfgEarleyPassiveConvert() {
    this.name = "convert";
    this.antNeeded = 1;
  }

  @Override public List<Item> getConsequences() {
    if (antecedences.size() == antNeeded) {
      String[] itemForm = antecedences.get(0).getItemform();
      if (itemForm[0].contains("•")) {
        String lhsSym = itemForm[0].split(" ")[0];
        consequences.add(new DeductionItem(lhsSym, itemForm[1], itemForm[2]));
      }
    }
    return consequences;
  }

  @Override public String toString() {
    return "[B → γ•, j, k]\n" + "_________\n"
      + "[B, j, k]";
  }

}

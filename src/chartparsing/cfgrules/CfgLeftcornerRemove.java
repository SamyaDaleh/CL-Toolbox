package chartparsing.cfgrules;

import java.util.List;

import chartparsing.AbstractDynamicDeductionRule;
import common.ArrayUtils;
import common.Item;
import common.cfg.CfgDollarItem;

/** If topmost symbol on stacks completed and predicted are the same, remove
 * both. */
public class CfgLeftcornerRemove extends AbstractDynamicDeductionRule {

  public CfgLeftcornerRemove() {
    this.name = "remove";
    this.antneeded = 1;
  }

  @Override public List<Item> getConsequences() {
    if (antecedences.size() == antneeded) {
      String[] itemform = antecedences.get(0).getItemform();
      String stackcompl = itemform[0];
      String[] stackcomplsplit = stackcompl.split(" ");
      String stackpred = itemform[1];
      String[] stackpredsplit = stackpred.split(" ");
      String stacklhs = itemform[2];
      if (stackcompl.length() > 0 && stackpred.length() > 0
        && stackcomplsplit[0].equals(stackpredsplit[0])) {
        String newcompl = ArrayUtils.getSubSequenceAsString(stackcomplsplit, 1,
          stackcomplsplit.length);
        String newpred = ArrayUtils.getSubSequenceAsString(stackpredsplit, 1,
          stackpredsplit.length);
        consequences.add(new CfgDollarItem(newcompl, newpred, stacklhs));
      }
    }
    return consequences;
  }

  @Override public String toString() {
    return "[Xα,Xβ,ɣ]" + "\n______\n" + "[α,β,ɣ]";
  }

}

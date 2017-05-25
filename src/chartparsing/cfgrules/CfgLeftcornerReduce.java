package chartparsing.cfgrules;

import java.util.List;

import chartparsing.AbstractDynamicDeductionRule;
import common.ArrayUtils;
import common.Item;
import common.cfg.CfgDollarItem;
import common.cfg.CfgProductionRule;

/** If the top of the completed stack is the left corner of a production rule,
 * pop that symbol, push the rest of the rhs to the stack to be predicted and
 * add the lhs to the stack of lhs */
public class CfgLeftcornerReduce extends AbstractDynamicDeductionRule {

  private CfgProductionRule rule;

  public CfgLeftcornerReduce(CfgProductionRule rule) {
    this.name = "reduce " + rule.toString();
    this.rule = rule;
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

      if (!stackpredsplit[0].equals("$")
        && stackcomplsplit[0].equals(rule.getRhs()[0])) {
        String newcompl = ArrayUtils.getSubSequenceAsString(stackcomplsplit, 1,
          stackcompl.length());
        String newpred;
        if (rule.getRhs().length == 1) {
          newpred = "$ " + stackpred;
        } else {
          newpred = ArrayUtils.getSubSequenceAsString(rule.getRhs(), 1,
            rule.getRhs().length) + " $ " + stackpred;
        }

        String newlhs;
        if (stacklhs.length() == 0) {
          newlhs = rule.getLhs();
        } else {
          newlhs = rule.getLhs() + " " + stacklhs;
        }
        consequences.add(new CfgDollarItem(newcompl, newpred, newlhs));
      }
    }
    return consequences;
  }

  @Override public String toString() {
    StringBuilder representation = new StringBuilder();
    representation.append("[" + rule.getRhs()[0] + "α,Bβ,ɣ]");
    representation
      .append("\n______ " + ArrayUtils.toString(rule.getRhs()) + ", B ≠ $\n");
    representation
      .append("[α," + ArrayUtils.getSubSequenceAsString(rule.getRhs(), 1,
        rule.getRhs().length) + "$Bβ," + rule.getLhs() + "ɣ]");
    return representation.toString();
  }

}

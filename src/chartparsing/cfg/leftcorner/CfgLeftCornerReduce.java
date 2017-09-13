package chartparsing.cfg.leftcorner;

import java.util.List;

import chartparsing.AbstractDynamicDeductionRule;
import chartparsing.DeductionItem;
import chartparsing.Item;
import common.ArrayUtils;
import common.cfg.CfgProductionRule;

/** If the top of the completed stack is the left corner of a production rule,
 * pop that symbol, push the rest of the rhs to the stack to be predicted and
 * add the lhs to the stack of lhs */
public class CfgLeftCornerReduce extends AbstractDynamicDeductionRule {

  private final CfgProductionRule rule;

  public CfgLeftCornerReduce(CfgProductionRule rule) {
    this.name = "reduce " + rule.toString();
    this.rule = rule;
    this.antNeeded = 1;
  }

  @Override public List<Item> getConsequences() {
    if (antecedences.size() == antNeeded) {
      String[] itemForm = antecedences.get(0).getItemform();
      String stackCompl = itemForm[0];
      String[] stackComplSplit = stackCompl.split(" ");
      String stackPred = itemForm[1];
      String[] stackPredSplit = stackPred.split(" ");
      String stackLhs = itemForm[2];

      if (!stackPredSplit[0].equals("$")
        && stackComplSplit[0].equals(rule.getRhs()[0])) {
        String newCompl = ArrayUtils.getSubSequenceAsString(stackComplSplit, 1,
          stackCompl.length());
        String newPred;
        if (rule.getRhs().length == 1) {
          newPred = "$ " + stackPred;
        } else {
          newPred = ArrayUtils.getSubSequenceAsString(rule.getRhs(), 1,
            rule.getRhs().length) + " $ " + stackPred;
        }

        String newLhs;
        if (stackLhs.length() == 0) {
          newLhs = rule.getLhs();
        } else {
          newLhs = rule.getLhs() + " " + stackLhs;
        }
        consequences.add(new DeductionItem(newCompl, newPred, newLhs));
      }
    }
    return consequences;
  }

  @Override public String toString() {
    return "[" + rule.getRhs()[0] + "α,Bβ,ɣ]" + "\n______ "
      + ArrayUtils.toString(rule.getRhs()) + ", B ≠ $\n" + "[α," + ArrayUtils
        .getSubSequenceAsString(rule.getRhs(), 1, rule.getRhs().length)
      + "$Bβ," + rule.getLhs() + "ɣ]";
  }

}

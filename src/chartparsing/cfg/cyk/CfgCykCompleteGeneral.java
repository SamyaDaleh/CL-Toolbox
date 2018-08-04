package chartparsing.cfg.cyk;

import java.text.ParseException;
import java.util.List;

import chartparsing.AbstractDynamicDeductionRule;
import chartparsing.DeductionItem;
import chartparsing.Item;
import common.TreeUtils;
import common.cfg.CfgProductionRule;
import common.tag.Tree;

/**
 * If two items match the rhs of a rule, get a new item that represents the lhs.
 */
public class CfgCykCompleteGeneral extends AbstractDynamicDeductionRule {

  private final CfgProductionRule rule;

  public CfgCykCompleteGeneral(CfgProductionRule rule) {
    this.rule = rule;
    this.name = "complete " + rule.toString();
    this.antNeeded = rule.getRhs().length;
  }

  @Override public List<Item> getConsequences() throws ParseException {
    if (antecedences.size() == antNeeded) {
      int minI = Integer.MAX_VALUE;
      int prevItemStart = 0;
      for (Item mayFirstRhsItem : antecedences) {
        int i = Integer.parseInt(mayFirstRhsItem.getItemform()[1]);
        if (i >= minI) {
          continue;
        }
        minI = i;
        prevItemStart = i;
      }
      int lSum = 0;
      Tree derivedTree = new Tree(rule);
      for (int j = 0; j < rule.getRhs().length; j++) {
        boolean found = false;
        for (Item mayRhsItem : antecedences) {
          int i = Integer.parseInt(mayRhsItem.getItemform()[1]);
          if (i == prevItemStart
            && mayRhsItem.getItemform()[0].equals(rule.getRhs()[j])) {
            found = true;
            if (mayRhsItem.getTree() != null) {
              derivedTree = TreeUtils.performLeftmostSubstitution(derivedTree,
                mayRhsItem.getTree());
            }
            int l = Integer.parseInt(mayRhsItem.getItemform()[2]);
            prevItemStart = i + l;
            lSum += l;
            break;
          }
        }
        if (!found) {
          return this.consequences;
        }
      }
      Item consequence = new DeductionItem(rule.getLhs(), String.valueOf(minI),
        String.valueOf(lSum));
      consequence.setTree(derivedTree);
      consequences.add(consequence);
    }
    return this.consequences;
  }

  @Override public String toString() {
    return "[" + rule.getRhs()[0] + ",1,l1], [" + rule.getRhs()[1] + ",i+l1,l2]"
      + "\n______ \n" + "[" + rule.getLhs() + ",i,l1+l2]";
  }
}

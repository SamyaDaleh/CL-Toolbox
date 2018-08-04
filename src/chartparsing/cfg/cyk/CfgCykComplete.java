package chartparsing.cfg.cyk;

import java.text.ParseException;

import chartparsing.AbstractDynamicDecutionRuleTwoAntecedences;
import chartparsing.DeductionItem;
import chartparsing.Item;
import common.TreeUtils;
import common.cfg.CfgProductionRule;
import common.tag.Tree;

/**
 * If two items match the rhs of a rule, get a new item that represents the lhs.
 */
public class CfgCykComplete extends AbstractDynamicDecutionRuleTwoAntecedences {

  private final CfgProductionRule rule;

  public CfgCykComplete(CfgProductionRule rule) {
    this.rule = rule;
    this.name = "complete " + rule.toString();
    this.antNeeded = 2;
  }

  protected void calculateConsequences(String[] itemForm1, String[] itemForm2)
    throws ParseException {
    String nt1 = itemForm1[0];
    String i1 = itemForm1[1];
    int i1int = Integer.parseInt(i1);
    String j1 = itemForm1[2];
    int j1int = Integer.parseInt(j1);
    String nt2 = itemForm2[0];
    String i2 = itemForm2[1];
    int i2int = Integer.parseInt(i2);
    String j2 = itemForm2[2];
    int j2int = Integer.parseInt(j2);
    if (nt1.equals(rule.getRhs()[0]) && nt2.equals(rule.getRhs()[1])
      && i1int + j1int == i2int) {
      Item consequence = new DeductionItem(rule.getLhs(), String.valueOf(i1int),
        String.valueOf(j1int + j2int));
      Tree derivedTree = new Tree(rule);
      if (i1.equals(antecedences.get(0).getItemform()[1])) {
        derivedTree = TreeUtils.performLeftmostSubstitution(derivedTree,
          antecedences.get(0).getTree());
        derivedTree = TreeUtils.performLeftmostSubstitution(derivedTree,
          antecedences.get(1).getTree());
      } else {
        derivedTree = TreeUtils.performLeftmostSubstitution(derivedTree,
          antecedences.get(1).getTree());
        derivedTree = TreeUtils.performLeftmostSubstitution(derivedTree,
          antecedences.get(0).getTree());
      }
      consequence.setTree(derivedTree);
      this.consequences.add(consequence);
    }
  }

  @Override public String toString() {
    return "[" + rule.getRhs()[0] + ",1,l1], [" + rule.getRhs()[1] + ",i+l1,l2]"
      + "\n______ \n" + "[" + rule.getLhs() + ",i,l1+l2]";
  }
}

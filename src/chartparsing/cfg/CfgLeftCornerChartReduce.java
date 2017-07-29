package chartparsing.cfg;

import java.util.List;

import chartparsing.AbstractDynamicDeductionRule;
import chartparsing.DeductionItem;
import chartparsing.Item;
import common.ArrayUtils;
import common.cfg.CfgProductionRule;

public class CfgLeftCornerChartReduce extends AbstractDynamicDeductionRule {

  private final CfgProductionRule rule;

  public CfgLeftCornerChartReduce(CfgProductionRule rule) {
    this.name = "reduce " + rule.toString();
    this.rule = rule;
    this.antNeeded = 1;
  }

  @Override public List<Item> getConsequences() {
    if (antecedences.size() == antNeeded) {
      String[] itemForm = antecedences.get(0).getItemform();
      String i = itemForm[1];
      String l = itemForm[2];
      if (itemForm[0].equals(rule.getRhs()[0])) {
        consequences.add(new DeductionItem(
          rule.getLhs() + " -> " + rule.getRhs()[0] + " •" + ArrayUtils
            .getSubSequenceAsString(rule.getRhs(), 1, rule.getRhs().length),
          i, l));
      }
    }
    return this.consequences;
  }

  @Override public String toString() {
    return "[" + rule.getRhs()[0] + ",i,l]" + "\n______ \n" + "["
      + rule.getLhs() + " - > " + rule.getRhs()[0] + " •" + ArrayUtils
        .getSubSequenceAsString(rule.getRhs(), 1, rule.getRhs().length)
      + ",i,l]";
  }
}

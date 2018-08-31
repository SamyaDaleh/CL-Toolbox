package com.github.samyadaleh.cltoolbox.chartparsing.cfg.cyk;

import java.text.ParseException;
import java.util.List;

import com.github.samyadaleh.cltoolbox.chartparsing.dynamicdeductionrule.AbstractDynamicDecutionRuleTwoAntecedences;
import com.github.samyadaleh.cltoolbox.chartparsing.item.DeductionChartItem;
import com.github.samyadaleh.cltoolbox.chartparsing.item.ChartItemInterface;
import com.github.samyadaleh.cltoolbox.common.cfg.CfgProductionRule;
import com.github.samyadaleh.cltoolbox.common.tag.Tree;

/**
 * If two items match the rhs of a rule, get a new item that represents the lhs.
 */
public class CfgCykComplete extends AbstractDynamicDecutionRuleTwoAntecedences {

  private final CfgProductionRule rule;
  private final CfgCykUtils cfgCykUtils = new CfgCykUtils();

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
      ChartItemInterface consequence =
          new DeductionChartItem(rule.getLhs(), String.valueOf(i1int),
              String.valueOf(j1int + j2int));
      List<Tree> derivedTrees =
          cfgCykUtils.generateDerivedTrees(i1, antecedences, rule);
      consequence.setTrees(derivedTrees);
      logItemGeneration(consequence);
      consequences.add(consequence);
    }
  }

  @Override public String toString() {
    return "[" + rule.getRhs()[0] + ",i,l1], [" + rule.getRhs()[1] + ",i+l1,l2]"
        + "\n______ \n" + "[" + rule.getLhs() + ",i,l1+l2]";
  }
}

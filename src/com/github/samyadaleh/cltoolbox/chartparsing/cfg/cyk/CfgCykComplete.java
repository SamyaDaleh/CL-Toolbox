package com.github.samyadaleh.cltoolbox.chartparsing.cfg.cyk;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import com.github.samyadaleh.cltoolbox.chartparsing.AbstractDynamicDecutionRuleTwoAntecedences;
import com.github.samyadaleh.cltoolbox.chartparsing.DeductionChartItem;
import com.github.samyadaleh.cltoolbox.chartparsing.ChartItemInterface;
import com.github.samyadaleh.cltoolbox.common.TreeUtils;
import com.github.samyadaleh.cltoolbox.common.cfg.CfgProductionRule;
import com.github.samyadaleh.cltoolbox.common.tag.Tree;

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
      ChartItemInterface consequence = new DeductionChartItem(rule.getLhs(), String.valueOf(i1int),
        String.valueOf(j1int + j2int));
      Tree derivedTreeBase = new Tree(rule);
      List<Tree> derivedTrees = new ArrayList<Tree>();
      if (i1.equals(antecedences.get(0).getItemform()[1])) {
        for (Tree tree1 : antecedences.get(0).getTrees()) {
          for (Tree tree2 : antecedences.get(1).getTrees()) {
            Tree derivedTree =
              TreeUtils.performLeftmostSubstitution(derivedTreeBase, tree1);
            derivedTree =
              TreeUtils.performLeftmostSubstitution(derivedTree, tree2);
            derivedTrees.add(derivedTree);
          }
        }
      } else {
        for (Tree tree1 : antecedences.get(0).getTrees()) {
          for (Tree tree2 : antecedences.get(1).getTrees()) {
            Tree derivedTree =
              TreeUtils.performLeftmostSubstitution(derivedTreeBase, tree2);
            derivedTree =
              TreeUtils.performLeftmostSubstitution(derivedTree, tree1);
            derivedTrees.add(derivedTree);
          }
        }
      }
      consequence.setTrees(derivedTrees);
      this.consequences.add(consequence);
    }
  }

  @Override public String toString() {
    return "[" + rule.getRhs()[0] + ",1,l1], [" + rule.getRhs()[1] + ",i+l1,l2]"
      + "\n______ \n" + "[" + rule.getLhs() + ",i,l1+l2]";
  }
}

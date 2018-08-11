package com.github.samyadaleh.cltoolbox.chartparsing.cfg.cyk;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import com.github.samyadaleh.cltoolbox.chartparsing.AbstractDynamicDeductionRule;
import com.github.samyadaleh.cltoolbox.chartparsing.DeductionChartItem;
import com.github.samyadaleh.cltoolbox.chartparsing.ChartItemInterface;
import com.github.samyadaleh.cltoolbox.common.TreeUtils;
import com.github.samyadaleh.cltoolbox.common.cfg.CfgProductionRule;
import com.github.samyadaleh.cltoolbox.common.tag.Tree;

/**
 * If the item matches the rhs of a chain rule, get a new item that represents
 * the lhs.
 */
public class CfgCykCompleteUnary extends AbstractDynamicDeductionRule {

  private final CfgProductionRule rule;

  public CfgCykCompleteUnary(CfgProductionRule rule) {
    this.rule = rule;
    this.name = "complete " + rule.toString();
    this.antNeeded = 1;
  }

  @Override public List<ChartItemInterface> getConsequences() throws ParseException {
    if (antecedences.size() == antNeeded) {
      String[] itemForm1 = antecedences.get(0).getItemform();
      String nt1 = itemForm1[0];
      String i1 = itemForm1[1];
      int i1int = Integer.parseInt(i1);
      String j1 = itemForm1[2];
      int j1int = Integer.parseInt(j1);

      if (nt1.equals(rule.getRhs()[0])) {
        ChartItemInterface consequence = new DeductionChartItem(rule.getLhs(),
          String.valueOf(i1int), String.valueOf(j1int));
        Tree derivedTreeBase = new Tree(rule);
        List<Tree> derivedTrees = new ArrayList<Tree>();
        for (Tree tree : antecedences.get(0).getTrees()) {
          derivedTrees
            .add(TreeUtils.performLeftmostSubstitution(derivedTreeBase, tree));
        }
        consequence.setTrees(derivedTrees);
        this.consequences.add(consequence);
      }
    }
    return this.consequences;
  }

  @Override public String toString() {
    return "[" + rule.getRhs()[0] + ",i,j]]" + "\n______ \n" + "["
      + rule.getLhs() + ",i,j]";
  }
}

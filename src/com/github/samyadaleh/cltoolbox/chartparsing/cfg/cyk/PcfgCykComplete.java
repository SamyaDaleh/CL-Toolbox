package com.github.samyadaleh.cltoolbox.chartparsing.cfg.cyk;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import com.github.samyadaleh.cltoolbox.chartparsing.DynamicDeductionRuleInterface;
import com.github.samyadaleh.cltoolbox.chartparsing.ChartItemInterface;
import com.github.samyadaleh.cltoolbox.chartparsing.ProbabilisticChartItemInterface;
import com.github.samyadaleh.cltoolbox.common.TreeUtils;
import com.github.samyadaleh.cltoolbox.common.cfg.CfgProductionRule;
import com.github.samyadaleh.cltoolbox.common.cfg.PcfgProductionRule;
import com.github.samyadaleh.cltoolbox.common.tag.Tree;

/**
 * Similar to the complete rule for CYK, but used for a PCFG and with weights
 * for probabilistic CYK parsing.
 */
public class PcfgCykComplete implements DynamicDeductionRuleInterface {

  protected List<ProbabilisticChartItemInterface> antecedences =
      new ArrayList<>();
  protected List<ProbabilisticChartItemInterface> consequences =
      new ArrayList<>();
  protected String name = null;

  protected final PcfgProductionRule pRule;

  private final int antneeded = 2;

  public PcfgCykComplete(PcfgProductionRule pRule) {
    this.pRule = pRule;
    this.name = "complete " + pRule.toString();
  }

  @Override public List<ChartItemInterface> getAntecedences() {
    List<ChartItemInterface> outantecedences = new ArrayList<>();
    outantecedences.addAll(this.antecedences);
    return outantecedences;
  }

  @Override public void setAntecedences(List<ChartItemInterface> antecedences) {
    List<ProbabilisticChartItemInterface> inAntecedences = new ArrayList<>();
    for (ChartItemInterface item : antecedences) {
      inAntecedences.add((ProbabilisticChartItemInterface) item);
    }
    this.antecedences = inAntecedences;
  }

  @Override public List<ChartItemInterface> getConsequences()
    throws ParseException {
    if (antecedences.size() == antneeded) {
      String[] itemForm1 = antecedences.get(0).getItemForm();
      String[] itemForm2 = antecedences.get(1).getItemForm();
      calculateConsequences(itemForm1, itemForm2);
      calculateConsequences(itemForm2, itemForm1);
    }
    List<ChartItemInterface> outcon = new ArrayList<>();
    outcon.addAll(this.consequences);
    return outcon;
  }

  protected void calculateConsequences(String[] itemForm1, String[] itemForm2)
    throws ParseException {
    String nt1 = itemForm1[0];
    String i1 = itemForm1[1];
    int i1Int = Integer.parseInt(i1);
    String j1 = itemForm1[2];
    int j1Int = Integer.parseInt(j1);
    Double x1 = antecedences.get(0).getProbability();

    String nt2 = itemForm2[0];
    String i2 = itemForm2[1];
    int i2Int = Integer.parseInt(i2);
    String j2 = itemForm2[2];
    int j2Int = Integer.parseInt(j2);
    Double x2 = antecedences.get(1).getProbability();

    if (nt1.equals(pRule.getRhs()[0]) && nt2.equals(pRule.getRhs()[1])
      && j1Int == i2Int) {
      ProbabilisticChartItemInterface consequence = new PcfgCykItem(
        x1 + x2 + -Math.log(pRule.getP()), pRule.getLhs(), i1Int, j2Int);
      addTreesToConsequence(i1, consequence);
      this.consequences.add(consequence);
    }
  }

  protected void addTreesToConsequence(String i1,
    ProbabilisticChartItemInterface consequence) throws ParseException {
    List<Tree> derivedTrees = new ArrayList<>();
    Tree derivedTreeBase =
      new Tree(new CfgProductionRule(pRule.getLhs(), pRule.getRhs()));
    if (i1.equals(antecedences.get(0).getItemForm()[1])) {
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
  }

  @Override public String getName() {
    return this.name;
  }

  @Override public int getAntecedencesNeeded() {
    return this.antneeded;
  }

  @Override public String toString() {
    return "x1 : [" + pRule.getRhs()[0] + ", i, j], x2 : [" + pRule.getRhs()[1]
      + ", j, k]" + "\n______ \n" + "x1 + x2 + |log("
      + String.valueOf(pRule.getP()) + ")| : [" + pRule.getLhs() + ", i, k]";
  }

  @Override public void clearItems() {
    antecedences = new ArrayList<>();
    consequences = new ArrayList<>();
  }

}

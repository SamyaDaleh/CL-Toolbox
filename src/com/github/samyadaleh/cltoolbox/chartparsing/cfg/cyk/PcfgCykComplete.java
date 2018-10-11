package com.github.samyadaleh.cltoolbox.chartparsing.cfg.cyk;

import com.github.samyadaleh.cltoolbox.chartparsing.dynamicdeductionrule.AbstractDynamicDecutionRuleTwoAntecedences;
import com.github.samyadaleh.cltoolbox.chartparsing.item.ChartItemInterface;
import com.github.samyadaleh.cltoolbox.chartparsing.item.ProbabilisticChartItemInterface;
import com.github.samyadaleh.cltoolbox.common.cfg.CfgProductionRule;
import com.github.samyadaleh.cltoolbox.common.cfg.PcfgProductionRule;
import com.github.samyadaleh.cltoolbox.common.tag.Tree;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Similar to the complete rule for CYK, but used for a PCFG and with weights
 * for probabilistic CYK parsing.
 */
public class PcfgCykComplete
    extends AbstractDynamicDecutionRuleTwoAntecedences {

  protected final PcfgProductionRule pRule;

  private static final Logger log = LogManager.getLogger();

  public PcfgCykComplete(PcfgProductionRule pRule) {
    this.pRule = pRule;
    this.name = "complete " + pRule.toString();
    this.antNeeded = 2;
  }

  @Override public List<ChartItemInterface> getAntecedences() {
    return new ArrayList<>(this.antecedences);
  }

  @Override public void setAntecedences(List<ChartItemInterface> antecedences) {
    List<ChartItemInterface> inAntecedences = new ArrayList<>(antecedences);
    this.antecedences = inAntecedences;
  }

  protected void calculateConsequences(String[] itemForm1, String[] itemForm2) {
    String nt1 = itemForm1[0];
    String i1 = itemForm1[1];
    int i1Int = Integer.parseInt(i1);
    String j1 = itemForm1[2];
    int j1Int = Integer.parseInt(j1);
    Double x1 = ((ProbabilisticChartItemInterface) antecedences.get(0))
        .getProbability();

    String nt2 = itemForm2[0];
    String i2 = itemForm2[1];
    int i2Int = Integer.parseInt(i2);
    String j2 = itemForm2[2];
    int j2Int = Integer.parseInt(j2);
    Double x2 = ((ProbabilisticChartItemInterface) antecedences.get(1))
        .getProbability();

    if (nt1.equals(pRule.getRhs()[0]) && nt2.equals(pRule.getRhs()[1])
        && j1Int == i2Int) {
      ProbabilisticChartItemInterface consequence =
          new PcfgCykItem(x1 + x2 + -Math.log(pRule.getP()), pRule.getLhs(),
              i1Int, j2Int);
      addTreesToConsequence(i1, consequence);
      logItemGeneration(consequence);
      consequences.add(consequence);
    }
  }

  protected void addTreesToConsequence(String i1,
      ProbabilisticChartItemInterface consequence) {
    CfgProductionRule rule =
        new CfgProductionRule(pRule.getLhs(), pRule.getRhs());
    List<Tree> derivedTrees =
        CfgCykUtils.generateDerivedTrees(i1, this.getAntecedences(), rule);
    consequence.setTrees(derivedTrees);
  }

  @Override public String getName() {
    return this.name;
  }

  @Override public int getAntecedencesNeeded() {
    return this.antNeeded;
  }

  @Override public String toString() {
    return "x1 : [" + pRule.getRhs()[0] + ", i, j], x2 : [" + pRule.getRhs()[1]
        + ", j, k]" + "\n______ \n" + "x1 + x2 + |log(" + String
        .valueOf(pRule.getP()) + ")| : [" + pRule.getLhs() + ", i, k]";
  }

  @Override public void clearItems() {
    antecedences = new ArrayList<>();
    consequences = new ArrayList<>();
  }

  protected void logItemGeneration(ChartItemInterface item) {
    if (log.isDebugEnabled()) {
      StringBuilder out = new StringBuilder("generated: ");
      out.append(item).append(" with trees:");
      for (Tree tree : item.getTrees()) {
        out.append(' ').append(tree);
      }
      out.append(" from:");
      for (ChartItemInterface antecedence : antecedences) {
        out.append(' ').append(antecedence);
      }
      out.append(" with rule ").append(name);
      log.debug(out.toString());
    }
  }

}

package com.github.samyadaleh.cltoolbox.chartparsing.cfg.cyk.astar;

import java.util.Map;

import com.github.samyadaleh.cltoolbox.chartparsing.item.ProbabilisticChartItemInterface;
import com.github.samyadaleh.cltoolbox.chartparsing.cfg.cyk.PcfgCykComplete;
import com.github.samyadaleh.cltoolbox.common.cfg.PcfgProductionRule;

import static com.github.samyadaleh.cltoolbox.common.Constants.DEDUCTION_RULE_PCFG_CYK_COMPLETE;

/**
 * Similar to the complete rule for CYK, but used for a PCFG and with weights
 * for astar parsing.
 */
public class PcfgAstarComplete extends PcfgCykComplete {

  private final Map<String, Double> outsides;
  private final int n;

  public PcfgAstarComplete(PcfgProductionRule pRule,
      Map<String, Double> outsides, int n) {
    super(pRule);
    this.n = n;
    this.outsides = outsides;
    this.name = DEDUCTION_RULE_PCFG_CYK_COMPLETE + " " + pRule;
  }

  @Override
  protected void calculateConsequences(String[] itemForm1, String[] itemForm2) {
    String nt1 = itemForm1[0];
    String i1 = itemForm1[1];
    int i1Int = Integer.parseInt(i1);
    String j1 = itemForm1[2];
    int j1Int = Integer.parseInt(j1);
    Double w1 = ((ProbabilisticChartItemInterface) antecedences.get(0))
        .getProbability();

    String outKey1 = SxCalc.getOutsideKey(nt1, i1Int, j1Int - i1Int, n - j1Int);
    if (!outsides.containsKey(outKey1)) {
      return;
    }
    double x1 = w1 - outsides
        .get(SxCalc.getOutsideKey(nt1, i1Int, j1Int - i1Int, n - j1Int));

    String nt2 = itemForm2[0];
    String i2 = itemForm2[1];
    int i2Int = Integer.parseInt(i2);
    String j2 = itemForm2[2];
    int j2Int = Integer.parseInt(j2);
    Double w2 = ((ProbabilisticChartItemInterface) antecedences.get(1))
        .getProbability();

    String outkey2 = SxCalc.getOutsideKey(nt2, i2Int, j2Int - i2Int, n - j2Int);
    if (!outsides.containsKey(outkey2)) {
      return;
    }
    double x2 = w2 - outsides
        .get(SxCalc.getOutsideKey(nt2, i2Int, j2Int - i2Int, n - j2Int));

    if (nt1.equals(pRule.getRhs()[0]) && nt2.equals(pRule.getRhs()[1])
        && j1Int == i2Int) {
      String outKey3 =
          SxCalc.getOutsideKey(pRule.getLhs(), i1Int, j2Int - i1Int, n - j2Int);
      if (!outsides.containsKey(outKey3)) {
        return;
      }
      Double newOutP = outsides.get(outKey3);
      ProbabilisticChartItemInterface consequence =
          new PcfgAstarItem(x1 + x2
              + -Math.log(((PcfgProductionRule) pRule).getP()), newOutP,
              pRule.getLhs(), i1Int, j2Int);
      addTreesToConsequence(i1, consequence);
      logItemGeneration(consequence);
      consequences.add(consequence);
    }
  }

  @Override public String toString() {
    return "x1 + out(" + pRule.getRhs()[0] + ", i, j - i, n - j) : [" + pRule
        .getRhs()[0] + ",i,j], x2 + out(" + pRule.getRhs()[1]
        + ", j, k - j, n - k) : [" + pRule.getRhs()[1] + ", j, k]"
        + "\n______ \n" + "x1 + x2 + |log(" + ((PcfgProductionRule)pRule).getP()
        + ")| + out(" + pRule.getLhs() + ", i, k - i, n - k) : [" + pRule
        .getLhs() + ", i, k]";
  }

}

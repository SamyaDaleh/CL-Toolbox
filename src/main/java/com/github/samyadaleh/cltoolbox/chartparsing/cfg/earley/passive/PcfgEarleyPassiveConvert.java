package com.github.samyadaleh.cltoolbox.chartparsing.cfg.earley.passive;

import java.util.ArrayList;
import java.util.List;

import com.github.samyadaleh.cltoolbox.chartparsing.cfg.cyk.PcfgCykItem;
import com.github.samyadaleh.cltoolbox.chartparsing.dynamicdeductionrule.AbstractDynamicDeductionRule;
import com.github.samyadaleh.cltoolbox.chartparsing.item.DeductionChartItem;
import com.github.samyadaleh.cltoolbox.chartparsing.item.ChartItemInterface;
import com.github.samyadaleh.cltoolbox.chartparsing.item.ProbabilisticChartItemInterface;
import com.github.samyadaleh.cltoolbox.common.cfg.PcfgProductionRule;

import static com.github.samyadaleh.cltoolbox.common.Constants.DEDUCTION_RULE_CFG_EARLEY_CONVERT;
import static com.github.samyadaleh.cltoolbox.common.Constants.DEDUCTION_RULE_PCFG_LEFTCORNER_CONVERT;

/**
 * Converts an active item (with a dot) into a passive item that does not care
 * which rule led to its creation.
 * Reused for weighted Left-Corner.
 */
public class PcfgEarleyPassiveConvert extends AbstractDynamicDeductionRule {

  private final List<PcfgProductionRule> pRules;

  public PcfgEarleyPassiveConvert(List<PcfgProductionRule> pRules) {
    this.name = DEDUCTION_RULE_PCFG_LEFTCORNER_CONVERT;
    this.antNeeded = 1;
    this.pRules = pRules;
  }

  public PcfgEarleyPassiveConvert() {
    this.name = DEDUCTION_RULE_CFG_EARLEY_CONVERT;
    this.antNeeded = 1;
    pRules = new ArrayList<>();
  }

  @Override public List<ChartItemInterface> getConsequences() {
    if (antecedences.size() == antNeeded) {
      String[] itemForm = antecedences.get(0).getItemForm();
      if (itemForm[0].endsWith("•")) {
        String lhsSym = itemForm[0].split(" ")[0];
        ChartItemInterface consequence;
        if (antecedences.get(0) instanceof ProbabilisticChartItemInterface) {
          Double wPrev = ((PcfgCykItem) antecedences.get(0)).getProbability();
          String rule = itemForm[0].substring(0, itemForm[0].length() - 2);
          Double pRule = getProbablityOfRule(rule);
          double wNew = wPrev + Math.abs(Math.log(pRule));
          int j = Integer.parseInt(itemForm[1]);
          int k = Integer.parseInt(itemForm[2]);
          consequence = new PcfgCykItem(wNew, lhsSym, j, k);
        } else {
          consequence = new DeductionChartItem(lhsSym, itemForm[1], itemForm[2]);
        }
        consequence.setTrees(antecedences.get(0).getTrees());
        logItemGeneration(consequence);
        consequences.add(consequence);
      }
    }
    return consequences;
  }

  private Double getProbablityOfRule(String rule) {
    String searchPattern = ": " + rule;
    for (PcfgProductionRule pRule : pRules) {
      if (pRule.toString().endsWith(searchPattern)) {
        return pRule.getP();
      }
    }
    return 0.0;
  }

  @Override public String toString() {
    return "[B → γ•, j, k]\n" + "_________\n" + "[B, j, k]";
  }

}

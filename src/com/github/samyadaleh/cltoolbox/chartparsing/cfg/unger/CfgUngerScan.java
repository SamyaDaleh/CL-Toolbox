package com.github.samyadaleh.cltoolbox.chartparsing.cfg.unger;

import java.util.List;

import com.github.samyadaleh.cltoolbox.chartparsing.AbstractDynamicDeductionRule;
import com.github.samyadaleh.cltoolbox.chartparsing.DeductionChartItem;
import com.github.samyadaleh.cltoolbox.chartparsing.ChartItemInterface;

/** Makes a predicted item to a recognized item. */
public class CfgUngerScan extends AbstractDynamicDeductionRule {

  private final String[] wSplit;

  public CfgUngerScan(String[] wSplit) {
    this.wSplit = wSplit;
    this.name = "scan";
    this.antNeeded = 1;
  }

  @Override public List<ChartItemInterface> getConsequences() {
    if (antecedences.size() == antNeeded) {
      String[] itemForm = antecedences.get(0).getItemForm();
      int pos = Integer.parseInt(itemForm[1]);
      int length =
        Integer.parseInt(itemForm[2]) - Integer.parseInt(itemForm[1]);
      if (itemForm[0].charAt(0) == '•'
        && itemForm[0].substring(1).equals(wSplit[pos]) && length == 1) {
        this.name = "scan " + wSplit[pos];
        ChartItemInterface consequence =
          new DeductionChartItem(wSplit[pos] + "•", itemForm[1], itemForm[2]);
        consequence.setTrees(antecedences.get(0).getTrees());
        logItemGeneration(consequence);
        consequences.add(consequence);
      }
    }
    return consequences;
  }

  @Override public String toString() {
    return "[•a,i,i+1]" + "\n______ w_i = a\n" + "[a•,i,i+1]";
  }

}

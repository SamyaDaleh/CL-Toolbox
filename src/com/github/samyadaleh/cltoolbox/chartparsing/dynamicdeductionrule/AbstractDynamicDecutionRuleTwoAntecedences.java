package com.github.samyadaleh.cltoolbox.chartparsing.dynamicdeductionrule;

import com.github.samyadaleh.cltoolbox.chartparsing.item.ChartItemInterface;

import java.util.List;

public abstract class AbstractDynamicDecutionRuleTwoAntecedences
  extends AbstractDynamicDeductionRule {

  @Override public List<ChartItemInterface> getConsequences() {
    if (antecedences.size() == antNeeded) {
      String[] itemForm1 = antecedences.get(0).getItemForm();
      String[] itemForm2 = antecedences.get(1).getItemForm();
      calculateConsequences(itemForm1, itemForm2);
      calculateConsequences(itemForm2, itemForm1);
    }
    return consequences;
  }

  protected abstract void calculateConsequences(String[] itemForm1,
    String[] itemForm2);

}

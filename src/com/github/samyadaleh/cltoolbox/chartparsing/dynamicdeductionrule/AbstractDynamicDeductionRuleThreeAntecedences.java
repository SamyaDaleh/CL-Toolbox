package com.github.samyadaleh.cltoolbox.chartparsing.dynamicdeductionrule;

import com.github.samyadaleh.cltoolbox.chartparsing.item.ChartItemInterface;

import java.util.List;

public abstract class AbstractDynamicDeductionRuleThreeAntecedences
    extends AbstractDynamicDeductionRule {


  @Override public List<ChartItemInterface> getConsequences() {
    if (antecedences.size() == antNeeded) {
      String[] itemForm1 = antecedences.get(0).getItemForm();
      String[] itemForm2 = antecedences.get(1).getItemForm();
      String[] itemForm3 = antecedences.get(2).getItemForm();
      calculateConsequences(itemForm1, itemForm2, itemForm3);
      calculateConsequences(itemForm1, itemForm3, itemForm2);
      calculateConsequences(itemForm2, itemForm1, itemForm3);
      calculateConsequences(itemForm2, itemForm3, itemForm1);
      calculateConsequences(itemForm3, itemForm1, itemForm2);
      calculateConsequences(itemForm3, itemForm2, itemForm1);
    }
    return consequences;
  }

  protected abstract void calculateConsequences(String[] itemForm1,
      String[] itemForm2, String[] itemForm3);

}

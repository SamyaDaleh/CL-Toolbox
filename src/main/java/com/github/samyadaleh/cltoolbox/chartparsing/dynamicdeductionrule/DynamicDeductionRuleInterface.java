package com.github.samyadaleh.cltoolbox.chartparsing.dynamicdeductionrule;

import com.github.samyadaleh.cltoolbox.chartparsing.DeductionRuleInterface;

/**
 * Specification for dynamic rules where antecedences have to be set and they
 * generate the consequences on runtime. The deduction system needs to know
 * how many antecedences to set.
 */
public interface DynamicDeductionRuleInterface extends DeductionRuleInterface {
  int getAntecedencesNeeded();
  void clearItems();
}

package com.github.samyadaleh.cltoolbox.chartparsing;

/**
 * Specification for dynamic rules where antecedences have to be set and they
 * generate the consequences on runtime. The deduction system needs to know
 * how many antecedences to set.
 */
public interface DynamicDeductionRule extends DeductionRule{
  int getAntecedencesNeeded();
  void clearItems();
}

package com.github.samyadaleh.cltoolbox.chartparsing;

import com.github.samyadaleh.cltoolbox.chartparsing.item.ChartItemInterface;

import java.text.ParseException;
import java.util.List;

/** Interface that defines any kind of rule used for deduction. */
public interface DeductionRuleInterface {

  List<ChartItemInterface> getAntecedences();

  void setAntecedences(List<ChartItemInterface> antecedences);

  List<ChartItemInterface> getConsequences() throws ParseException;

  String getName();

  @Override String toString();
}

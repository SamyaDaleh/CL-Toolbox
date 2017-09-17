package chartparsing;

import java.util.List;

/** Interface that defines any kind of rule used for deduction. */
interface DeductionRule {

  List<Item> getAntecedences();

  void setAntecedences(List<Item> antecedences);

  List<Item> getConsequences();

  String getName();

  @Override String toString();
}

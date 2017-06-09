package chartparsing;

import java.util.List;

import common.Item;

/** Interface that defines any kind of rule used for deduction. */
interface DeductionRule {

  void addAntecedence(Item item);

  List<Item> getAntecedences();

  void setAntecedences(List<Item> antecedences);

  List<Item> getConsequences();

  String getName();

  @Override String toString();
}

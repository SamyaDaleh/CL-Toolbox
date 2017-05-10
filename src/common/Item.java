package common;

/** Interface for all items that are used by the deduction system that can be
 * used as antecedences or consequences of rules and can be derived. */
public interface Item {
  void setItemform(String[] itemform);

  String[] getItemform();
}

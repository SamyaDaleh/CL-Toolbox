package chartparsing.tagrules;

import java.util.LinkedList;
import java.util.List;

import chartparsing.DynamicDeductionRule;
import common.Item;
import common.tag.TagCykItem;

/** From a two sibling nodes move up to the parent node. */
public class TagCykMovebinary implements DynamicDeductionRule {

  List<Item> antecedences = new LinkedList<Item>();
  List<Item> consequences = new LinkedList<Item>();
  String name = null;

  int antneeded = 2;

  /** Rule doesn't need anything to do its job. */
  public TagCykMovebinary() {
    this.name = "move-binary";
  }

  @Override public void addAntecedence(Item item) {
    this.antecedences.add(item);
  }

  @Override public void addConsequence(Item item) {
    this.consequences.add(item);
  }

  @Override public List<Item> getAntecedences() {
    return this.antecedences;
  }

  @Override public void setAntecedences(List<Item> antecedences) {
    this.antecedences = antecedences;
  }

  @Override public List<Item> getConsequences() {
    if (antecedences.size() == antneeded) {
      String[] itemform1 = antecedences.get(0).getItemform();
      String[] itemform2 = antecedences.get(1).getItemform();
      String treename1 = itemform1[0];
      String treename2 = itemform2[0];
      String node1 = itemform1[1];
      String node2 = itemform1[2];
      int i = Integer.parseInt(itemform1[2]);
      int k2 = Integer.parseInt(itemform2[2]);
      Integer f1;
      Integer f2;
      try {
        f1 = Integer.parseInt(itemform1[3]);
        f2 = Integer.parseInt(itemform1[4]);
      } catch (NumberFormatException e) {
        f1 = null;
        f2 = null;
      }
      Integer f1b;
      Integer f2b;
      try {
        f1b = Integer.parseInt(itemform2[3]);
        f2b = Integer.parseInt(itemform2[4]);
      } catch (NumberFormatException e) {
        f1b = null;
        f2b = null;
      }
      int k1 = Integer.parseInt(itemform1[5]);
      int j = Integer.parseInt(itemform2[5]);
      if (treename1.equals(treename2)
        && node1.substring(0, -3).equals(node2.substring(0, -3))) {
        if (node1.endsWith(".1⊤") && node2.endsWith(".2⊤") && k1 == k2) {
          String parentnode = node1.substring(0, node1.length() - 3) + "⊥";
          Integer f1new = (f1 == null) ? f1b : f1;
          Integer f2new = (f2 == null) ? f2b : f2;
          consequences
            .add(new TagCykItem(treename1, parentnode, i, f1new, f2new, j));
        } else if (node2.endsWith(".1⊤") && node1.endsWith(".2⊤") && j == i) {
          // the other way around
          String parentnode = node1.substring(0, node1.length() - 3) + "⊥";
          Integer f1new = (f1 == null) ? f1b : f1;
          Integer f2new = (f2 == null) ? f2b : f2;
          consequences
            .add(new TagCykItem(treename1, parentnode, k2, f1new, f2new, k1));
        }
      }
    }
    return consequences;
  }

  @Override public void setConsequences(List<Item> consequences) {
    // ignore
  }

  @Override public void setName(String name) {
    this.name = name;
  }

  @Override public String getName() {
    return name;
  }

  @Override public int getAntecedencesNeeded() {
    return antneeded;
  }

  @Override public void clearItems() {
    antecedences = new LinkedList<Item>();
    consequences = new LinkedList<Item>();
  }

}

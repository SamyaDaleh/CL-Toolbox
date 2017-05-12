package chartparsing.tagrules;

import java.util.LinkedList;
import java.util.List;

import chartparsing.DynamicDeductionRule;
import common.Item;
import common.tag.Tag;
import common.tag.TagEarleyItem;

/** If a node has a child, move to the fist child. */
public class TagEarleyMovedown implements DynamicDeductionRule {

  List<Item> antecedences = new LinkedList<Item>();
  List<Item> consequences = new LinkedList<Item>();
  String name = null;
  Tag tag = null;

  int antneeded = 1;

  /** Constructor needs the grammar to retrieve information about the
   * antecedence. */
  public TagEarleyMovedown(Tag tag) {
    this.tag = tag;
    this.name = "move down";
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
      String[] itemform = antecedences.get(0).getItemform();
      String treename = itemform[0];
      String node = itemform[1];
      String pos = itemform[2];
      int i = Integer.parseInt(itemform[3]);
      Integer j;
      Integer k;
      try {
        j = Integer.parseInt(itemform[4]);
        k = Integer.parseInt(itemform[5]);
      } catch (NumberFormatException e) {
        j = null;
        k = null;
      }
      int l = Integer.parseInt(itemform[6]);
      String adj = itemform[7];
      if (pos.equals("lb") && adj.equals("0")
        && tag.getTree(treename).getNodeByGornAdress(node + ".1") != null) {
        consequences.add(new TagEarleyItem(treename, node + ".1", "la", i,
          (Integer) j, k, l, false));
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

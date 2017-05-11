package chartparsing.tagrules;

import java.util.LinkedList;
import java.util.List;

import chartparsing.DynamicDeductionRule;
import common.Item;
import common.tag.Tag;
import common.tag.TagEarleyItem;

/** If the dot is at a node where adjunction is possible, predict the auxiliary
 * tree that can be adjoined into that node. */
public class TagCykMoveunary implements DynamicDeductionRule {

  List<Item> antecedences = new LinkedList<Item>();
  List<Item> consequences = new LinkedList<Item>();
  String name = null;

  String auxtreename = null;
  Tag tag = null;

  int antneeded = 1;

  /** Constructor takes an auxiliary tree for the items the rule shall derive,
   * also needs the grammar to retrieve information about the antecedence. */
  TagCykMoveunary(String auxtreename, Tag tag) {
    this.auxtreename = auxtreename;
    this.tag = tag;
    this.name = "move-unary";
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
      int l = Integer.parseInt(itemform[6]);
      boolean adjoinable = tag.isAdjoinable(auxtreename, treename, node);
      if (adjoinable && itemform[2].equals("la") && itemform[0].equals("0")) {
        consequences.add(new TagEarleyItem(auxtreename, "", "la", l,
          (Integer) null, null, l, false));
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

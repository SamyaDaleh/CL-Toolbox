package chartparsing.tagrules;

import java.util.LinkedList;
import java.util.List;

import chartparsing.DynamicDeductionRule;
import common.Item;
import common.tag.Tag;
import common.tag.TagEarleyItem;

/** If a dot is at the foot node of an auxiliary tree, predict that it was
 * adjoined into another tree and move into that tree at the affected node. */
public class TagEarleyPredictadjoined implements DynamicDeductionRule {

  List<Item> antecedences = new LinkedList<Item>();
  List<Item> consequences = new LinkedList<Item>();
  String name = null;

  String outtreename = null;
  String outnode = null;
  Tag tag = null;

  int antneeded = 1;

  /** Constructor takes a tree and a node where maybe is, was, will be adjoined,
   * also needs the grammar to retrieve information about the antecedence. */
  public TagEarleyPredictadjoined(String outtreename, String outnode, Tag tag) {
    this.outtreename = outtreename;
    this.outnode = outnode;
    this.tag = tag;
    this.name = "predict adjoined";
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
      boolean adjoinable = tag.isAdjoinable(treename, outtreename, outnode);
      boolean isFootNode = tag.getAuxiliaryTree(treename) != null && tag
        .getAuxiliaryTree(treename).getFoot().getGornaddress().equals(node);
      if (adjoinable && isFootNode && itemform[2].equals("lb")
        && itemform[7].equals("0") && itemform[3].equals(itemform[6])
        && itemform[4].equals("-") && itemform[5].equals("-")) {
        consequences.add(new TagEarleyItem(outtreename, outnode, "lb", l,
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

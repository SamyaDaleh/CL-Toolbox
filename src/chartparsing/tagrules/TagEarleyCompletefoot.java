package chartparsing.tagrules;

import java.util.LinkedList;
import java.util.List;

import chartparsing.DynamicDeductionRule;
import common.Item;
import common.tag.Tag;
import common.tag.TagEarleyItem;

/** If there is an auxiliary tree and another tree where the aux tree can adjoin
 * into, fill the foot of the aux tree with the span of the other tree. */
public class TagEarleyCompletefoot implements DynamicDeductionRule {

  List<Item> antecedences = new LinkedList<Item>();
  List<Item> consequences = new LinkedList<Item>();
  String name = null;

  Tag tag = null;

  int antneeded = 2;

  /** Constructor needs the grammar to retrieve information about the
   * antecedences. */
  public TagEarleyCompletefoot(Tag tag) {
    this.tag = tag;
    this.name = "complete foot";
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
      String treename1 = itemform1[0];
      String node1 = itemform1[1];
      String pos1 = itemform1[2];
      int i1 = Integer.parseInt(itemform1[3]);
      String j = itemform1[4];
      String k = itemform1[5];
      int l = Integer.parseInt(itemform1[6]);
      String adj1 = itemform1[7];
      String[] itemform2 = antecedences.get(1).getItemform();
      String treename2 = itemform2[0];
      String node2 = itemform2[1];
      String pos2 = itemform2[2];
      int i21 = Integer.parseInt(itemform2[3]);
      String f12 = itemform2[4];
      String f22 = itemform2[5];
      int i22 = Integer.parseInt(itemform2[6]);
      String adj2 = itemform2[7];
      boolean adjoinable1 = tag.isAdjoinable(treename2, treename1, node1);
      boolean adjoinable2 = tag.isAdjoinable(treename1, treename2, node2);
      if (i1 == i21 && adj1.equals("0") && adj2.equals("0")) {
        if (adjoinable1 && pos1.equals("rb") && pos2.equals("lb") && i21 == i22
          && f12.equals("-") && f22.equals("-")) {
          consequences.add(
            new TagEarleyItem(treename2, node2, "rb", i1, i1, l, l, false));
        } else if (adjoinable2 && pos2.equals("rb") && pos1.equals("lb")
          && i1 == l && j.equals("-") && k.equals("-")) {
          // the other way around
          consequences.add(new TagEarleyItem(treename1, node1, "rb", i21, i21,
            i22, i22, false));
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

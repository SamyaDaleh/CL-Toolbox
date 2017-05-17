package chartparsing.tagrules;

import java.util.LinkedList;
import java.util.List;

import chartparsing.DynamicDeductionRule;
import common.Item;
import common.tag.Tag;
import common.tag.TagEarleyItem;

/** If the dot is at a node where adjunction is not obligatory, just skip it. */
public class TagEarleyPredictnoadj implements DynamicDeductionRule {

  List<Item> antecedences = new LinkedList<Item>();
  List<Item> consequences = new LinkedList<Item>();
  String name = null;

  Tag tag = null;

  int antneeded = 1;

  /** Constructor needs the grammar to retrieve information about the
   * antecedence. */
  public TagEarleyPredictnoadj(Tag tag) {
    this.tag = tag;
    this.name = "predict no adjoin";
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
      boolean obligatoryadjoin = tag.getTree(treename).isInOA(node);
      if (!obligatoryadjoin && itemform[2].equals("la")
        && itemform[7].equals("0")) {
        consequences.add(new TagEarleyItem(treename, node, "lb", l,
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

  @Override public String toString() {
    StringBuilder representation = new StringBuilder();
    representation.append("[ɣ,p,la,i,j,k,l,0]");
    representation.append("\n______ f_OA(ɣ,p) = 0\n");
    representation.append("[ɣ,p,lb,l,-,-,l,0]");
    return representation.toString();
  }

}

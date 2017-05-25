package chartparsing.tagrules;

import java.util.LinkedList;
import java.util.List;

import chartparsing.DynamicDeductionRule;
import common.Item;
import common.tag.Tag;
import common.tag.TagEarleyItem;

/** If the node's label is epsilon, just move on. */
public class TagEarleyScaneps implements DynamicDeductionRule {

  private List<Item> antecedences = new LinkedList<Item>();
  private List<Item> consequences = new LinkedList<Item>();
  private String name = "scan epsilon";
  private Tag tag = null;

  private int antneeded = 1;

  /** Constructor needs the grammar to retrieve information about the antecedence. */
  public TagEarleyScaneps(Tag tag) {
    this.tag = tag;
  }

  @Override public void addAntecedence(Item item) {
    this.antecedences.add(item);
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
      if (pos.equals("la") && adj.equals("0") && tag.getTree(treename)
        .getNodeByGornAdress(node).getLabel().equals("")) {
        consequences.add(
          new TagEarleyItem(treename, node, "ra", i, (Integer) j, k, l, false));
      }
    }
    return consequences;
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
    representation.append("\n______ l(ɣ,p) = ε\n");
    representation.append("[ɣ,p,ra,i,j,k,l,0]");
    return representation.toString();
  }

}

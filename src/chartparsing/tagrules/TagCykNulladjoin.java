package chartparsing.tagrules;

import java.util.LinkedList;
import java.util.List;

import chartparsing.DynamicDeductionRule;
import common.Item;
import common.tag.Tag;
import common.tag.TagCykItem;

/** Goes from bottom into top position without adjoining. */
public class TagCykNulladjoin implements DynamicDeductionRule {

  private List<Item> antecedences = new LinkedList<Item>();
  private List<Item> consequences = new LinkedList<Item>();
  private String name = "null-adjoin";

  private Tag tag = null;

  private int antneeded = 1;

  /** Constructor needs the grammar to check if adjoins is obligatory. */
  public TagCykNulladjoin(Tag tag) {
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
      String nodegorn;
      if (node.length() == 1) {
        nodegorn = "";
      } else {
        nodegorn = node.substring(0, node.length() - 1);
      }
      int i = Integer.parseInt(itemform[2]);
      Integer f1;
      Integer f2;
      try {
        f1 = Integer.parseInt(itemform[3]);
        f2 = Integer.parseInt(itemform[4]);
      } catch (NumberFormatException e) {
        f1 = null;
        f2 = null;
      }
      boolean obligatoryadjoin = tag.getTree(treename).isInOA(nodegorn);
      int j = Integer.parseInt(itemform[5]);
      if (node.endsWith("⊥") && !obligatoryadjoin) {
        String newnode = node.substring(0, node.length() - 1) + "⊤";
        consequences.add(new TagCykItem(treename, newnode, i, f1, f2, j));
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
    representation.append("[ɣ,p⊥,i,f1,f2,j]");
    representation.append("\n______ f_OA(ɣ,p) = 0\n");
    representation.append("[ɣ,p⊤,i,f1,f2,j]");
    return representation.toString();
  }

}

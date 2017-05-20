package chartparsing.tagrules;

import java.util.LinkedList;
import java.util.List;

import chartparsing.DynamicDeductionRule;
import common.Item;
import common.tag.Tag;
import common.tag.TagCykItem;

/** Tries to substitute a given initial tree into the node of the tree it
 * remembers. */
public class TagCykSubstitute implements DynamicDeductionRule {

  private List<Item> antecedences = new LinkedList<Item>();
  private List<Item> consequences = new LinkedList<Item>();
  private String name = null;

  private Tag tag = null;
  private String nodegorn = null;
  private String treename = null;

  private int antneeded = 1;

  /** Remembers tree and node it can substitute in. */
  public TagCykSubstitute(String treename, String nodegorn, Tag tag) {
    this.tag = tag;
    this.treename = treename;
    this.nodegorn = nodegorn;
    this.name = "substitute in " + treename + "(" + nodegorn + ")";
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
      int i = Integer.parseInt(itemform[2]);
      int j = Integer.parseInt(itemform[5]);
      if (tag.getInitialTree(treename) != null && node.equals("⊤")) {
        consequences.add(
          new TagCykItem(this.treename, this.nodegorn + "⊤", i, null, null, j));
      }
    }
    return consequences;
  }

  @Override public void setConsequences(List<Item> consequences) {
    // ignore
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
    representation.append("[α,ε⊤,i,-,-,j]");
    representation.append("\n______ l(α,ε) = l(" + treename + "," + nodegorn
      + "), " + treename + "(" + nodegorn + ") a substitution node\n");
    representation.append("[" + treename + "," + nodegorn + "⊤,i,-,-,j]");
    return representation.toString();
  }

}

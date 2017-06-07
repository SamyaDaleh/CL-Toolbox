package chartparsing.tagrules;

import java.util.List;

import chartparsing.AbstractDynamicDeductionRule;
import common.Item;
import common.tag.Tag;
import common.tag.TagCykItem;

/** Tries to substitute a given initial tree into the node of the tree it
 * remembers. */
public class TagCykSubstitute extends AbstractDynamicDeductionRule {
  
  private final Tag tag;
  private final String nodeGorn;
  private final String treeName;

  /** Remembers tree and node it can substitute in. */
  public TagCykSubstitute(String treeName, String nodeGorn, Tag tag) {
    this.tag = tag;
    this.treeName = treeName;
    this.nodeGorn = nodeGorn;
    this.name = "substitute in " + treeName + "(" + nodeGorn + ")";
    this.antNeeded = 1;
  }

  @Override public List<Item> getConsequences() {
    if (antecedences.size() == antNeeded) {
      String[] itemForm = antecedences.get(0).getItemform();
      String treeName = itemForm[0];
      String node = itemForm[1];
      int i = Integer.parseInt(itemForm[2]);
      int j = Integer.parseInt(itemForm[5]);
      if (tag.getInitialTree(treeName) != null && node.equals("⊤")) {
        consequences.add(
          new TagCykItem(this.treeName, this.nodeGorn + "⊤", i, null, null, j));
        this.name = "substitute " + this.treeName + "[" + this.nodeGorn + ","+ treeName + "]";
      }
    }
    return consequences;
  }

  @Override public String toString() {
    return "[α,ε⊤,i,-,-,j]" + "\n______ l(α,ε) = l(" + treeName + "," + nodeGorn
        + "), " + treeName + "(" + nodeGorn + ") a substitution node\n"
        + "[" + treeName + "," + nodeGorn + "⊤,i,-,-,j]";
  }

}

package chartparsing.tag.cyk;

import java.util.List;

import chartparsing.AbstractDynamicDeductionRule;
import chartparsing.DeductionItem;
import chartparsing.Item;
import common.tag.Tag;

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
      String i = itemForm[2];
      String j = itemForm[5];
      if (tag.getInitialTree(treeName) != null && node.equals("⊤")) {
        consequences.add(new DeductionItem(this.treeName, this.nodeGorn + "⊤",
          i, "-", "-", j));
        this.name = "substitute " + this.treeName + "[" + this.nodeGorn + ","
          + treeName + "]";
      }
    }
    return consequences;
  }

  @Override public String toString() {
    return "[α,ε⊤,i,-,-,j]" + "\n______ l(α,ε) = l(" + treeName + "," + nodeGorn
      + "), " + treeName + "(" + nodeGorn + ") a substitution node\n" + "["
      + treeName + "," + nodeGorn + "⊤,i,-,-,j]";
  }

}

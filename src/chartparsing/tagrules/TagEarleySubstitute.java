package chartparsing.tagrules;

import java.util.List;

import chartparsing.AbstractDynamicDeductionRule;
import common.Item;
import common.tag.Tag;
import common.tag.TagEarleyItem;

/** If a potential initial tree is complete, substitute it if possible. */
public class TagEarleySubstitute extends AbstractDynamicDeductionRule {

  private final String outtreename;
  private final String outnode;
  private final Tag tag;

  /** Constructor takes a tree and a node where maybe is, was, will be adjoined,
   * also needs the grammar to retrieve information about the antecedence. */
  public TagEarleySubstitute(String outtreename, String outnode, Tag tag) {
    this.outtreename = outtreename;
    this.outnode = outnode;
    this.tag = tag;
    this.name = "substitute in " + outtreename + "(" + outnode + ")";
    this.antneeded = 1;
  }

  @Override public List<Item> getConsequences() {
    if (antecedences.size() == antneeded) {
      String[] itemform = antecedences.get(0).getItemform();
      String treename = itemform[0];
      String node = itemform[1];
      String pos = itemform[2];
      int i = Integer.parseInt(itemform[3]);
      String f1 = itemform[4];
      String f2 = itemform[5];
      int j = Integer.parseInt(itemform[6]);
      String adj = itemform[7];
      if (tag.getInitialTree(treename) != null && node.equals("")
        && f1.equals("-") && f2.equals("-") && adj.equals("0")
        && pos.equals("ra")) {
        consequences.add(new TagEarleyItem(outtreename, outnode, "rb", i,
          (Integer) null, null, j, false));
        // imagine a tree with 1 node where you would substitute into the root
        // ...
        String outnodename = outnode.length() == 0 ? "ε" : outnode;
        this.name = "substitute " + outtreename + "[" + outnodename + ","
          + treename + "]";
      }
    }
    return consequences;
  }

  @Override public String toString() {
    return "[α,ε,ra,i,-,-,j,0]" + "\n______ " + outtreename + "(" + outnode
      + ") a substitution node, α ∈ I, l(" + outtreename + "," + outnode
      + ") = l(α,ε)\n" + "[" + outtreename + "," + outnode + ",rb,i,-,-,j,0]";
  }

}

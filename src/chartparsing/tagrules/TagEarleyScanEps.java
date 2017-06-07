package chartparsing.tagrules;

import java.util.List;

import chartparsing.AbstractDynamicDeductionRule;
import common.Item;
import common.tag.Tag;
import common.tag.TagEarleyItem;

/** If the node's label is epsilon, just move on. */
public class TagEarleyScanEps extends AbstractDynamicDeductionRule {

  private final Tag tag;

  /** Constructor needs the grammar to retrieve information about the
   * antecedence. */
  public TagEarleyScanEps(Tag tag) {
    this.tag = tag;
    this.name = "scan epsilon";
    this.antNeeded = 1;
  }

  @Override public List<Item> getConsequences() {
    if (antecedences.size() == antNeeded) {
      String[] itemForm = antecedences.get(0).getItemform();
      String treeName = itemForm[0];
      String node = itemForm[1];
      String pos = itemForm[2];
      int i = Integer.parseInt(itemForm[3]);
      Integer j;
      Integer k;
      try {
        j = Integer.parseInt(itemForm[4]);
        k = Integer.parseInt(itemForm[5]);
      } catch (NumberFormatException e) {
        j = null;
        k = null;
      }
      int l = Integer.parseInt(itemForm[6]);
      String adj = itemForm[7];
      if (pos.equals("la") && adj.equals("0") && tag.getTree(treeName)
        .getNodeByGornAdress(node).getLabel().equals("")) {
        consequences.add(
          new TagEarleyItem(treeName, node, "ra", i, j, k, l, false));
      }
    }
    return consequences;
  }

  @Override public String toString() {
    return
        "[ɣ,p,la,i,j,k,l,0]" + "\n______ l(ɣ,p) = ε\n" + "[ɣ,p,ra,i,j,k,l,0]";
  }

}

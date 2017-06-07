package chartparsing.tagrules;

import java.util.List;

import chartparsing.AbstractDynamicDeductionRule;
import common.Item;
import common.tag.Tag;
import common.tag.TagEarleyItem;

/** If the node's label is the next input symbol, consume it. */
public class TagEarleyScanTerm extends AbstractDynamicDeductionRule {

  private final String[] wSplit;
  private final Tag tag;

  /** Constructor takes the input string to compare with the tree labels, also
   * needs the grammar to retrieve information about the antecedence. */
  public TagEarleyScanTerm(String[] wSplit, Tag tag) {
    this.wSplit = wSplit;
    this.tag = tag;
    this.name = "scan term";
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
      if (l < wSplit.length && pos.equals("la") && adj.equals("0")
        && tag.getTree(treeName).getNodeByGornAdress(node).getLabel()
          .equals(wSplit[l])) {
        consequences.add(new TagEarleyItem(treeName, node, "ra", i, j,
          k, l + 1, false));
      }
    }
    return consequences;
  }

  @Override public String toString() {
    return "[ɣ,p,la,i,j,k,l,0]" + "\n______ l(ɣ,p) = w_l\n"
        + "[ɣ,p,ra,i,j,k,l+1,0]";
  }

}

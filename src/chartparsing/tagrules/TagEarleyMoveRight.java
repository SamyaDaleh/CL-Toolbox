package chartparsing.tagrules;

import java.util.List;

import chartparsing.AbstractDynamicDeductionRule;
import common.Item;
import common.tag.Tag;
import common.tag.TagEarleyItem;

/** If a node has a right sibling, move to that sibling. */
public class TagEarleyMoveRight extends AbstractDynamicDeductionRule {

  private final Tag tag;

  /** Constructor needs the grammar to retrieve information about the
   * antecedence. */
  public TagEarleyMoveRight(Tag tag) {
    this.tag = tag;
    this.name = "move right";
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
      String siblingGorn = tag.getTree(treeName).getNodeByGornAdress(node)
        .getGornAddressOfPotentialRightSibling();
      if (pos.equals("ra") && adj.equals("0")
        && tag.getTree(treeName).getNodeByGornAdress(siblingGorn) != null) {
        consequences.add(new TagEarleyItem(treeName, siblingGorn, "la", i, j, k, l, false));
      }
    }
    return consequences;
  }

  @Override public String toString() {
    return "[ɣ,p,ra,i,j,k,l,0]" + "\n______ ɣ(p+1) is defined\n"
        + "[ɣ,p+1,la,i,j,k,l,0]";
  }
  
}

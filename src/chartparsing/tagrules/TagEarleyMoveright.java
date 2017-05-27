package chartparsing.tagrules;

import java.util.List;

import chartparsing.AbstractDynamicDeductionRule;
import common.Item;
import common.tag.Tag;
import common.tag.TagEarleyItem;

/** If a node has a right sibling, move to that sibling. */
public class TagEarleyMoveright extends AbstractDynamicDeductionRule {

  private final Tag tag;

  /** Constructor needs the grammar to retrieve information about the
   * antecedence. */
  public TagEarleyMoveright(Tag tag) {
    this.tag = tag;
    this.name = "move right";
    this.antneeded = 1;
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
      String siblinggorn = tag.getTree(treename).getNodeByGornAdress(node)
        .getGornAddressOfPotentialRightSibling();
      if (pos.equals("ra") && adj.equals("0")
        && tag.getTree(treename).getNodeByGornAdress(siblinggorn) != null) {
        consequences.add(new TagEarleyItem(treename, siblinggorn, "la", i, j, k, l, false));
      }
    }
    return consequences;
  }

  @Override public String toString() {
    return "[ɣ,p,ra,i,j,k,l,0]" + "\n______ ɣ(p+1) is defined\n"
        + "[ɣ,p+1,la,i,j,k,l,0]";
  }
  
}

package chartparsing.tagrules;

import java.util.List;

import chartparsing.AbstractDynamicDeductionRule;
import common.Item;
import common.tag.Tag;
import common.tag.TagEarleyItem;

/** If a node has a child, move to the fist child. */
public class TagEarleyMovedown extends AbstractDynamicDeductionRule {
  
  private Tag tag;

  /** Constructor needs the grammar to retrieve information about the
   * antecedence. */
  public TagEarleyMovedown(Tag tag) {
    this.tag = tag;
    this.name = "move down";
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
      if (pos.equals("lb") && adj.equals("0")
        && tag.getTree(treename).getNodeByGornAdress(node + ".1") != null) {
        consequences.add(new TagEarleyItem(treename, node + ".1", "la", i,
          (Integer) j, k, l, false));
      }
    }
    return consequences;
  }

  @Override public String toString() {
    StringBuilder representation = new StringBuilder();
    representation.append("[ɣ,p,lb,i,j,k,l,0]");
    representation.append("\n______ ɣ(p.1) is defined\n");
    representation.append("[ɣ,p.1,la,i,j,k,l,0]");
    return representation.toString();
  }

}

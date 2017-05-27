package chartparsing.tagrules;

import java.util.List;

import chartparsing.AbstractDynamicDeductionRule;
import common.Item;
import common.tag.Tag;
import common.tag.TagEarleyItem;

/** If the dot is at a node where adjunction is not obligatory, just skip it. */
public class TagEarleyPredictnoadj extends AbstractDynamicDeductionRule {

  private final Tag tag;

  /** Constructor needs the grammar to retrieve information about the
   * antecedence. */
  public TagEarleyPredictnoadj(Tag tag) {
    this.tag = tag;
    this.name = "predict no adjoin";
    this.antneeded = 1;
  }

  @Override public List<Item> getConsequences() {
    if (antecedences.size() == antneeded) {
      String[] itemform = antecedences.get(0).getItemform();
      String treename = itemform[0];
      String node = itemform[1];
      int l = Integer.parseInt(itemform[6]);
      boolean obligatoryadjoin = tag.getTree(treename).isInOA(node);
      if (!obligatoryadjoin && itemform[2].equals("la")
        && itemform[7].equals("0")) {
        consequences.add(new TagEarleyItem(treename, node, "lb", l,
          (Integer) null, null, l, false));
      }
    }
    return consequences;
  }

  @Override public String toString() {
    return "[ɣ,p,la,i,j,k,l,0]" + "\n______ f_OA(ɣ,p) = 0\n"
        + "[ɣ,p,lb,l,-,-,l,0]";
  }

}

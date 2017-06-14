package chartparsing.tag;

import java.util.List;

import chartparsing.AbstractDynamicDeductionRule;
import chartparsing.Item;
import common.tag.Tag;

/** If the dot is at a node where adjunction is not obligatory, just skip it. */
public class TagEarleyPredictNoAdj extends AbstractDynamicDeductionRule {

  private final Tag tag;

  /** Constructor needs the grammar to retrieve information about the
   * antecedence. */
  public TagEarleyPredictNoAdj(Tag tag) {
    this.tag = tag;
    this.name = "predict no adjoin";
    this.antNeeded = 1;
  }

  @Override public List<Item> getConsequences() {
    if (antecedences.size() == antNeeded) {
      String[] itemForm = antecedences.get(0).getItemform();
      String treeName = itemForm[0];
      String node = itemForm[1];
      int l = Integer.parseInt(itemForm[6]);
      boolean obligatoryAdjoin = tag.getTree(treeName).isInOA(node);
      if (!obligatoryAdjoin && itemForm[2].equals("la")
        && itemForm[7].equals("0")) {
        consequences.add(new TagEarleyItem(treeName, node, "lb", l,
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

package chartparsing.tagrules;

import java.util.List;

import chartparsing.AbstractDynamicDeductionRule;
import common.Item;
import common.tag.Tag;
import common.tag.TagEarleyItem;

/** If the dot is at a node where adjunction is possible, predict the auxiliary
 * tree that can be adjoined into that node. */
public class TagEarleyPredictadjoinable extends AbstractDynamicDeductionRule {

  private final String auxtreename;
  private final Tag tag;

  /** Constructor takes an auxiliary tree for the items the rule shall derive,
   * also needs the grammar to retrieve information about the antecedence. */
  public TagEarleyPredictadjoinable(String auxtreename, Tag tag) {
    this.auxtreename = auxtreename;
    this.tag = tag;
    this.name = "predict adjoinable with " + auxtreename;
    this.antneeded = 1;
  }

  @Override public List<Item> getConsequences() {
    if (antecedences.size() == antneeded) {
      String[] itemform = antecedences.get(0).getItemform();
      String treename = itemform[0];
      String node = itemform[1];
      int l = Integer.parseInt(itemform[6]);
      boolean adjoinable = tag.isAdjoinable(auxtreename, treename, node);
      if (adjoinable && itemform[2].equals("la") && itemform[7].equals("0")) {
        consequences.add(new TagEarleyItem(auxtreename, "", "la", l,
          (Integer) null, null, l, false));
      }
    }
    return consequences;
  }

  @Override public String toString() {
    return "[ɣ,p,la,i,j,k,l,0]" + "\n______ " + auxtreename + " ∈ f_SA(ɣ,p)\n"
        + "[" + auxtreename + ",ε,la,l,-,-,l,0]";
  }

}

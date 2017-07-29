package chartparsing.tag;

import java.util.List;

import chartparsing.AbstractDynamicDeductionRule;
import chartparsing.DeductionItem;
import chartparsing.Item;
import common.tag.Tag;

/** If the dot is at a node where adjunction is possible, predict the auxiliary
 * tree that can be adjoined into that node. */
public class TagEarleyPredictAdjoinable extends AbstractDynamicDeductionRule {

  private final String auxTreeName;
  private final Tag tag;

  /** Constructor takes an auxiliary tree for the items the rule shall derive,
   * also needs the grammar to retrieve information about the antecedence. */
  public TagEarleyPredictAdjoinable(String auxTreeName, Tag tag) {
    this.auxTreeName = auxTreeName;
    this.tag = tag;
    this.name = "predict adjoinable with " + auxTreeName;
    this.antNeeded = 1;
  }

  @Override public List<Item> getConsequences() {
    if (antecedences.size() == antNeeded) {
      String[] itemForm = antecedences.get(0).getItemform();
      String treeName = itemForm[0];
      String node = itemForm[1];
      String l = itemForm[6];
      boolean adjoinable = tag.isAdjoinable(auxTreeName, treeName, node);
      if (adjoinable && itemForm[2].equals("la") && itemForm[7].equals("0")) {
        consequences
          .add(new DeductionItem(auxTreeName, "", "la", l, "-", "-", l, "0"));
      }
    }
    return consequences;
  }

  @Override public String toString() {
    return "[ɣ,p,la,i,j,k,l,0]" + "\n______ " + auxTreeName + " ∈ f_SA(ɣ,p)\n"
      + "[" + auxTreeName + ",ε,la,l,-,-,l,0]";
  }

}

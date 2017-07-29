package chartparsing.tag;

import java.util.List;

import chartparsing.AbstractDynamicDeductionRule;
import chartparsing.DeductionItem;
import chartparsing.Item;
import common.tag.Tag;

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
      String i = itemForm[3];
      String j = itemForm[4];
      String k = itemForm[5];
      String l = itemForm[6];
      String adj = itemForm[7];
      if (pos.equals("la") && adj.equals("0") && tag.getTree(treeName)
        .getNodeByGornAdress(node).getLabel().equals("")) {
        consequences
          .add(new DeductionItem(treeName, node, "ra", i, j, k, l, "0"));
      }
    }
    return consequences;
  }

  @Override public String toString() {
    return "[ɣ,p,la,i,j,k,l,0]" + "\n______ l(ɣ,p) = ε\n"
      + "[ɣ,p,ra,i,j,k,l,0]";
  }

}

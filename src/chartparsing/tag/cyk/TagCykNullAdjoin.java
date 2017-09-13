package chartparsing.tag.cyk;

import java.util.List;

import chartparsing.AbstractDynamicDeductionRule;
import chartparsing.DeductionItem;
import chartparsing.Item;
import common.tag.Tag;

/** Goes from bottom into top position without adjoining. */
public class TagCykNullAdjoin extends AbstractDynamicDeductionRule {

  private final Tag tag;

  /** Constructor needs the grammar to check if adjoins is obligatory. */
  public TagCykNullAdjoin(Tag tag) {
    this.tag = tag;
    this.name = "null-adjoin";
    this.antNeeded = 1;
  }

  @Override public List<Item> getConsequences() {
    if (antecedences.size() == antNeeded) {
      String[] itemForm = antecedences.get(0).getItemform();
      String treeName = itemForm[0];
      String node = itemForm[1];
      String nodeGorn;
      if (node.length() == 1) {
        nodeGorn = "";
      } else {
        nodeGorn = node.substring(0, node.length() - 1);
      }
      String i = itemForm[2];
      String f1 = itemForm[3];
      String f2 = itemForm[4];
      boolean obligatoryAdjoin = tag.getTree(treeName).isInOA(nodeGorn);
      String j = itemForm[5];
      if (node.endsWith("⊥") && !obligatoryAdjoin) {
        String newNode = node.substring(0, node.length() - 1) + "⊤";
        consequences.add(new DeductionItem(treeName, newNode, i, f1, f2, j));
      }
    }
    return consequences;
  }

  @Override public String toString() {
    return "[ɣ,p⊥,i,f1,f2,j]" + "\n______ f_OA(ɣ,p) = 0\n" + "[ɣ,p⊤,i,f1,f2,j]";
  }

}

package chartparsing.tag;

import java.util.List;

import chartparsing.AbstractDynamicDeductionRule;
import chartparsing.DeductionItem;
import chartparsing.Item;
import common.tag.Tag;

/** From a single-child node move up to the parent node. */
public class TagCykMoveUnary extends AbstractDynamicDeductionRule {

  private final Tag tag;

  /** Constructor needs the grammar to retrieve information about the
   * antecedences. */
  public TagCykMoveUnary(Tag tag) {
    this.tag = tag;
    this.name = "move-unary";
    this.antNeeded = 1;
  }

  @Override public List<Item> getConsequences() {
    if (antecedences.size() == antNeeded) {
      String[] itemForm = antecedences.get(0).getItemform();
      String treeName = itemForm[0];
      String node = itemForm[1];
      String i = itemForm[2];
      String f1 = itemForm[3];
      String f2 = itemForm[4];
      String j = itemForm[5];
      if (node.endsWith(".1⊤")) {
        String nodeSib = tag.getTree(treeName)
          .getNodeByGornAdress(node.substring(0, node.length() - 1))
          .getGornAddressOfPotentialRightSibling();
        if (tag.getTree(treeName).getNodeByGornAdress(nodeSib) == null) {
          String parentNode = node.substring(0, node.length() - 3) + "⊥";
          consequences
            .add(new DeductionItem(treeName, parentNode, i, f1, f2, j));
        }
      }
    }
    return consequences;
  }

  @Override public String toString() {
    return "[ɣ,(p.1)⊤,i,f1,f2,j]"
      + "\n______ node adress p.2 does not exist in ɣ\n" + "[ɣ,p⊥,i,f1,f2,j]";
  }

}

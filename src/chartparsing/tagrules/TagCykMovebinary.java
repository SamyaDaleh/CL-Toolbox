package chartparsing.tagrules;

import java.util.List;

import chartparsing.AbstractDynamicDeductionRule;
import common.Item;
import common.tag.TagCykItem;

/** From a two sibling nodes move up to the parent node. */
public class TagCykMovebinary extends AbstractDynamicDeductionRule {

  public TagCykMovebinary() {
    this.name = "move-binary";
    this.antneeded = 2;
  }

  @Override public List<Item> getConsequences() {
    if (antecedences.size() == antneeded) {
      String[] itemform1 = antecedences.get(0).getItemform();
      String[] itemform2 = antecedences.get(1).getItemform();
      String treename1 = itemform1[0];
      String treename2 = itemform2[0];
      String node1 = itemform1[1];
      String node2 = itemform2[1];
      int i = Integer.parseInt(itemform1[2]);
      int k2 = Integer.parseInt(itemform2[2]);
      Integer f1;
      Integer f2;
      try {
        f1 = Integer.parseInt(itemform1[3]);
        f2 = Integer.parseInt(itemform1[4]);
      } catch (NumberFormatException e) {
        f1 = null;
        f2 = null;
      }
      Integer f1b;
      Integer f2b;
      try {
        f1b = Integer.parseInt(itemform2[3]);
        f2b = Integer.parseInt(itemform2[4]);
      } catch (NumberFormatException e) {
        f1b = null;
        f2b = null;
      }
      int k1 = Integer.parseInt(itemform1[5]);
      int j = Integer.parseInt(itemform2[5]);
      boolean node1childofroot = node1.length() > 1
        && !node1.substring(1, node1.length() - 1).contains(".");
      boolean node2childofroot = node2.length() > 1
        && !node2.substring(1, node2.length() - 1).contains(".");
      if (node1.length() > 1 && node2.length() > 1
        && treename1.equals(treename2)
        && ((node1childofroot && node2childofroot) || (!node1childofroot
          && !node2childofroot && node1.substring(0, node1.length() - 3)
            .equals(node2.substring(0, node2.length() - 3))))) {
        if (node1.endsWith(".1⊤") && node2.endsWith(".2⊤") && k1 == k2) {
          String parentnode = node1.substring(0, node1.length() - 3) + "⊥";
          Integer f1new = (f1 == null) ? f1b : f1;
          Integer f2new = (f2 == null) ? f2b : f2;
          consequences
            .add(new TagCykItem(treename1, parentnode, i, f1new, f2new, j));
        } else if (node2.endsWith(".1⊤") && node1.endsWith(".2⊤") && j == i) {
          // the other way around
          String parentnode = node1.substring(0, node1.length() - 3) + "⊥";
          Integer f1new = (f1 == null) ? f1b : f1;
          Integer f2new = (f2 == null) ? f2b : f2;
          consequences
            .add(new TagCykItem(treename1, parentnode, k2, f1new, f2new, k1));
        }

      }
    }
    return consequences;
  }

  @Override public String toString() {
    StringBuilder representation = new StringBuilder();
    representation.append("[ɣ,(p.1)⊤,i,f1,f2,k] [ɣ,(p.2)⊤,k,f1',f2',j]");
    representation.append("\n______\n");
    representation.append("[ɣ,p⊥,i,f1⊕f1',f2⊕f2',j]");
    return representation.toString();
  }

}

package chartparsing.tag;

import java.util.List;

import chartparsing.AbstractDynamicDeductionRule;
import chartparsing.Item;

/** From a two sibling nodes move up to the parent node. */
public class TagCykMoveBinary extends AbstractDynamicDeductionRule {

  public TagCykMoveBinary() {
    this.name = "move-binary";
    this.antNeeded = 2;
  }

  @Override public List<Item> getConsequences() {
    if (antecedences.size() == antNeeded) {
      String[] itemForm1 = antecedences.get(0).getItemform();
      String[] itemForm2 = antecedences.get(1).getItemform();
      String treeName1 = itemForm1[0];
      String treeName2 = itemForm2[0];
      String node1 = itemForm1[1];
      String node2 = itemForm2[1];
      int i = Integer.parseInt(itemForm1[2]);
      int k2 = Integer.parseInt(itemForm2[2]);
      Integer f1;
      Integer f2;
      try {
        f1 = Integer.parseInt(itemForm1[3]);
        f2 = Integer.parseInt(itemForm1[4]);
      } catch (NumberFormatException e) {
        f1 = null;
        f2 = null;
      }
      Integer f1b;
      Integer f2b;
      try {
        f1b = Integer.parseInt(itemForm2[3]);
        f2b = Integer.parseInt(itemForm2[4]);
      } catch (NumberFormatException e) {
        f1b = null;
        f2b = null;
      }
      int k1 = Integer.parseInt(itemForm1[5]);
      int j = Integer.parseInt(itemForm2[5]);
      boolean node1ChildOfRoot = node1.length() > 1
        && !node1.substring(1, node1.length() - 1).contains(".");
      boolean node2childofroot = node2.length() > 1
        && !node2.substring(1, node2.length() - 1).contains(".");
      if (node1.length() > 1 && node2.length() > 1
        && treeName1.equals(treeName2)
        && ((node1ChildOfRoot && node2childofroot) || (!node1ChildOfRoot
          && !node2childofroot && node1.substring(0, node1.length() - 3)
            .equals(node2.substring(0, node2.length() - 3))))) {
        if (node1.endsWith(".1⊤") && node2.endsWith(".2⊤") && k1 == k2) {
          String parentNode = node1.substring(0, node1.length() - 3) + "⊥";
          Integer f1New = (f1 == null) ? f1b : f1;
          Integer f2New = (f2 == null) ? f2b : f2;
          consequences
            .add(new TagCykItem(treeName1, parentNode, i, f1New, f2New, j));
        } else if (node2.endsWith(".1⊤") && node1.endsWith(".2⊤") && j == i) {
          // the other way around
          String parentNode = node1.substring(0, node1.length() - 3) + "⊥";
          Integer f1New = (f1 == null) ? f1b : f1;
          Integer f2New = (f2 == null) ? f2b : f2;
          consequences
            .add(new TagCykItem(treeName1, parentNode, k2, f1New, f2New, k1));
        }

      }
    }
    return consequences;
  }

  @Override public String toString() {
    return "[ɣ,(p.1)⊤,i,f1,f2,k] [ɣ,(p.2)⊤,k,f1',f2',j]" + "\n______\n"
        + "[ɣ,p⊥,i,f1⊕f1',f2⊕f2',j]";
  }

}

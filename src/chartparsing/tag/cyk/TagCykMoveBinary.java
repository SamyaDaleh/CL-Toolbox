package chartparsing.tag.cyk;

import java.util.List;

import chartparsing.AbstractDynamicDeductionRule;
import chartparsing.DeductionItem;
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
      String i = itemForm1[2];
      String k2 = itemForm2[2];
      String f1 = itemForm1[3];
      String f2 = itemForm1[4];
      String f1b = itemForm2[3];
      String f2b = itemForm2[4];
      String k1 = itemForm1[5];
      String j = itemForm2[5];
      boolean node1ChildOfRoot = node1.length() > 1
        && !node1.substring(1, node1.length() - 1).contains(".");
      boolean node2childofroot = node2.length() > 1
        && !node2.substring(1, node2.length() - 1).contains(".");
      if (node1.length() > 1 && node2.length() > 1
        && treeName1.equals(treeName2)
        && ((node1ChildOfRoot && node2childofroot) || (!node1ChildOfRoot
          && !node2childofroot && node1.substring(0, node1.length() - 3)
            .equals(node2.substring(0, node2.length() - 3))))) {
        if (node1.endsWith(".1⊤") && node2.endsWith(".2⊤") && k1.equals(k2)) {
          String parentNode = node1.substring(0, node1.length() - 3) + "⊥";
          String f1New = (f1.equals("-")) ? f1b : f1;
          String f2New = (f2.equals("-")) ? f2b : f2;
          consequences
            .add(new DeductionItem(treeName1, parentNode, i, f1New, f2New, j));
        } else if (node2.endsWith(".1⊤") && node1.endsWith(".2⊤") && j.equals(i)) {
          // the other way around
          String parentNode = node1.substring(0, node1.length() - 3) + "⊥";
          String f1New = (f1.equals("-")) ? f1b : f1;
          String f2New = (f2.equals("-")) ? f2b : f2;
          consequences.add(
            new DeductionItem(treeName1, parentNode, k2, f1New, f2New, k1));
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

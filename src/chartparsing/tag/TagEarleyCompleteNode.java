package chartparsing.tag;

import java.util.List;

import chartparsing.AbstractDynamicDeductionRule;
import chartparsing.Item;
import common.tag.Tag;

/** If you have one item in a node la and another matching in the same node in
 * rb, you can put both together. */
public class TagEarleyCompleteNode extends AbstractDynamicDeductionRule {

  private final Tag tag;

  /** Constructor needs the grammar to retrieve information about the
   * antecedences. */
  public TagEarleyCompleteNode(Tag tag) {
    this.tag = tag;
    this.name = "complete node";
    this.antNeeded = 2;
  }

  @Override public List<Item> getConsequences() {
    if (antecedences.size() == antNeeded) {
      String[] itemForm1 = antecedences.get(0).getItemform();
      String treeName1 = itemForm1[0];
      String node1 = itemForm1[1];
      String pos1 = itemForm1[2];
      int f = Integer.parseInt(itemForm1[3]);
      String g = itemForm1[4];
      String h = itemForm1[5];
      int i1 = Integer.parseInt(itemForm1[6]);
      String adj1 = itemForm1[7];
      String[] itemForm2 = antecedences.get(1).getItemform();
      String treeName2 = itemForm2[0];
      String node2 = itemForm2[1];
      String pos2 = itemForm2[2];
      int i2 = Integer.parseInt(itemForm2[3]);
      String j = itemForm2[4];
      String k = itemForm2[5];
      int l = Integer.parseInt(itemForm2[6]);
      String adj2 = itemForm2[7];
      String label =
        tag.getTree(treeName1).getNodeByGornAdress(node1).getLabel();
      if (treeName1.equals(treeName2) && node1.equals(node2)
        && tag.isInNonterminals(label)) {
        if (pos1.equals("la") && pos2.equals("rb") && i1 == i2
          && adj1.equals("0")) {
          String f1 = (g.equals("-")) ? j : g;
          String f2 = (h.equals("-")) ? k : h;
          consequences.add(
            new TagEarleyItem(treeName1, node1, "ra", f, f1, f2, l, false));
        } else if (pos2.equals("la") && pos1.equals("rb") && l == f
          && adj2.equals("0")) {
          // the other way around
          String f1 = (g.equals("-")) ? j : g;
          String f2 = (h.equals("-")) ? k : h;
          consequences.add(
            new TagEarleyItem(treeName2, node2, "ra", i2, f1, f2, i1, false));
        }
      }
    }
    return consequences;
  }

  @Override public String toString() {
    return "[ɣ,p,la,f,g,h,i,0], [ɣ,p,rb,i,j,k,l,adj]" + "\n______ l(ɣ,p) ∈ N\n"
        + "[ɣ,p,ra,f,g⊕j,h⊕k,l,0]";
  }

}

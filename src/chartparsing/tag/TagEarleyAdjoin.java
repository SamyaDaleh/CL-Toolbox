package chartparsing.tag;

import java.util.List;

import chartparsing.AbstractDynamicDeductionRule;
import chartparsing.DeductionItem;
import chartparsing.Item;
import common.tag.Tag;

/** Combines an auxiliary tree with another tree to get a new item in which has
 * been adjoined. */
public class TagEarleyAdjoin extends AbstractDynamicDeductionRule {

  private final Tag tag;

  /** Constructor needs the grammar to retrieve information about the
   * antecedences. */
  public TagEarleyAdjoin(Tag tag) {
    this.tag = tag;
    this.name = "adjoin";
    this.antNeeded = 2;
  }

  @Override public List<Item> getConsequences() {
    if (antecedences.size() == antNeeded) {
      String[] itemForm1 = antecedences.get(0).getItemform();
      String treeName1 = itemForm1[0];
      String node1 = itemForm1[1];
      String pos1 = itemForm1[2];
      String i = itemForm1[3];
      String j1 = itemForm1[4];
      String k1 = itemForm1[5];
      String l = itemForm1[6];
      String adj1 = itemForm1[7];
      String[] itemForm2 = antecedences.get(1).getItemform();
      String treeName2 = itemForm2[0];
      String node2 = itemForm2[1];
      String pos2 = itemForm2[2];
      String j2 = itemForm2[3];
      String g = itemForm2[4];
      String h = itemForm2[5];
      String k2 = itemForm2[6];
      String adj2 = itemForm2[7];
      boolean adjoinable1 = tag.isAdjoinable(treeName1, treeName2, node2);
      boolean adjoinable2 = tag.isAdjoinable(treeName2, treeName1, node1);
      if (adj1.equals("0") && adj2.equals("0")) {
        if (adjoinable1 && node1.equals("") && pos1.equals("ra")
          && pos2.equals("rb") && j1.equals(j2)) {
          consequences
            .add(new DeductionItem(treeName2, node2, "rb", i, g, h, l, "1"));
          String node2name = node2.length() == 0 ? "ε" : node2;
          this.name =
            "adjoin " + treeName2 + "[" + node2name + "," + treeName1 + "]";
        } else if (adjoinable2 && node2.equals("") && pos2.equals("ra")
          && pos1.equals("rb") && g == i) {
          // the other way around
          String node1Name = node1.length() == 0 ? "ε" : node1;
          consequences.add(
            new DeductionItem(treeName1, node1, "rb", j2, j1, k1, k2, "1"));
          this.name =
            "adjoin " + treeName1 + "[" + node1Name + "," + treeName2 + "]";
        }
      }
    }
    return consequences;
  }

  @Override public String toString() {
    return "[β,ε,ra,i,j,k,l,0], [ɣ,p,rb,j,g,h,k,0]" + "\n______ β ∈ f_SA(ɣ,p)\n"
      + "[ɣ,p,rb,i,g,h,l,1]";
  }

}

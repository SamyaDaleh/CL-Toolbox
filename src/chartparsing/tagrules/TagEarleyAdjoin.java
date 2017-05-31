package chartparsing.tagrules;

import java.util.List;

import chartparsing.AbstractDynamicDeductionRule;
import common.Item;
import common.tag.Tag;
import common.tag.TagEarleyItem;

/** Combines an auxiliary tree with another tree to get a new item in which has
 * been adjoined. */
public class TagEarleyAdjoin extends AbstractDynamicDeductionRule {

  private final Tag tag;

  /** Constructor needs the grammar to retrieve information about the
   * antecedences. */
  public TagEarleyAdjoin(Tag tag) {
    this.tag = tag;
    this.name = "adjoin";
    this.antneeded = 2;
  }

  @Override public List<Item> getConsequences() {
    if (antecedences.size() == antneeded) {
      String[] itemform1 = antecedences.get(0).getItemform();
      String treename1 = itemform1[0];
      String node1 = itemform1[1];
      String pos1 = itemform1[2];
      int i = Integer.parseInt(itemform1[3]);
      Integer j1;
      Integer k1;
      try {
        j1 = Integer.parseInt(itemform1[4]);
        k1 = Integer.parseInt(itemform1[5]);
      } catch (NumberFormatException e) {
        j1 = null;
        k1 = null;
      }
      int l = Integer.parseInt(itemform1[6]);
      String adj1 = itemform1[7];
      String[] itemform2 = antecedences.get(1).getItemform();
      String treename2 = itemform2[0];
      String node2 = itemform2[1];
      String pos2 = itemform2[2];
      int j2 = Integer.parseInt(itemform2[3]);
      Integer g;
      Integer h;
      try {
        g = Integer.parseInt(itemform2[4]);
        h = Integer.parseInt(itemform2[5]);
      } catch (NumberFormatException e) {
        g = null;
        h = null;
      }
      int k2 = Integer.parseInt(itemform2[6]);
      String adj2 = itemform2[7];
      boolean adjoinable1 = tag.isAdjoinable(treename1, treename2, node2);
      boolean adjoinable2 = tag.isAdjoinable(treename2, treename1, node1);
      if (adj1.equals("0") && adj2.equals("0")) {
        if (adjoinable1 && node1.equals("") && pos1.equals("ra")
          && pos2.equals("rb") && j1 == j2) {
          consequences
            .add(new TagEarleyItem(treename2, node2, "rb", i, g, h, l, true));
          String node2name = node2.length() == 0 ? "ε" : node2;
          this.name =
            "adjoin " + treename2 + "[" + node2name + "," + treename1 + "]";
        } else if (adjoinable2 && node2.equals("") && pos2.equals("ra")
          && pos1.equals("rb") && g == i) {
          // the other way around
          String node1name = node1.length() == 0 ? "ε" : node1;
          consequences.add(
            new TagEarleyItem(treename1, node1, "rb", j2, j1, k1, k2, true));
          this.name =
            "adjoin " + treename1 + "[" + node1name + "," + treename2 + "]";
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

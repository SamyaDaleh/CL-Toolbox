package chartparsing.tag;

import java.util.List;

import chartparsing.AbstractDynamicDeductionRule;
import chartparsing.Item;
import common.tag.Tag;

/** If there is an auxiliary tree and another tree where the aux tree can adjoin
 * into, fill the foot of the aux tree with the span of the other tree. */
public class TagEarleyCompleteFoot extends AbstractDynamicDeductionRule {

  private final Tag tag;

  /** Constructor needs the grammar to retrieve information about the
   * antecedences. */
  public TagEarleyCompleteFoot(Tag tag) {
    this.tag = tag;
    this.name = "complete foot";
    this.antNeeded = 2;
  }

  @Override public List<Item> getConsequences() {
    if (antecedences.size() == antNeeded) {
      String[] itemForm1 = antecedences.get(0).getItemform();
      String treeName1 = itemForm1[0];
      String node1 = itemForm1[1];
      String pos1 = itemForm1[2];
      int i1 = Integer.parseInt(itemForm1[3]);
      String j = itemForm1[4];
      String k = itemForm1[5];
      int l = Integer.parseInt(itemForm1[6]);
      String adj1 = itemForm1[7];
      String[] itemForm2 = antecedences.get(1).getItemform();
      String treeName2 = itemForm2[0];
      String node2 = itemForm2[1];
      String pos2 = itemForm2[2];
      int i21 = Integer.parseInt(itemForm2[3]);
      String f12 = itemForm2[4];
      String f22 = itemForm2[5];
      int i22 = Integer.parseInt(itemForm2[6]);
      String adj2 = itemForm2[7];
      boolean adjoinable1 = tag.isAdjoinable(treeName2, treeName1, node1);
      boolean adjoinable2 = tag.isAdjoinable(treeName1, treeName2, node2);
      if (i1 == i21 && adj1.equals("0") && adj2.equals("0")) {
        if (adjoinable1 && pos1.equals("rb") && pos2.equals("lb") && i21 == i22
          && f12.equals("-") && f22.equals("-")) {
          consequences.add(
            new TagEarleyItem(treeName2, node2, "rb", i1, i1, l, l, false));
        } else if (adjoinable2 && pos2.equals("rb") && pos1.equals("lb")
          && i1 == l && j.equals("-") && k.equals("-")) {
          // the other way around
          consequences.add(new TagEarleyItem(treeName1, node1, "rb", i21, i21,
            i22, i22, false));
        }
      }
    }
    return consequences;
  }

  @Override public String toString() {
    return "[ɣ,p,rb,i,j,k,l,0], [β,pf,lb,i,-,-,i,0]"
        + "\n______ pf foot node address in β, β ∈ f_SA(ɣ,p)\n"
        + "[β,pf,rb,i,i,l,l,0]";
  }

}

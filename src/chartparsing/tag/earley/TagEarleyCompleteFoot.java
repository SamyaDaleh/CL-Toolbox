package chartparsing.tag.earley;

import chartparsing.AbstractDynamicDecutionRuleTwoAntecedences;
import chartparsing.DeductionItem;
import common.tag.Tag;

/**
 * If there is an auxiliary tree and another tree where the aux tree can adjoin
 * into, fill the foot of the aux tree with the span of the other tree.
 */
public class TagEarleyCompleteFoot
  extends AbstractDynamicDecutionRuleTwoAntecedences {

  private final Tag tag;

  /**
   * Constructor needs the grammar to retrieve information about the
   * antecedences.
   */
  public TagEarleyCompleteFoot(Tag tag) {
    this.tag = tag;
    this.name = "complete foot";
    this.antNeeded = 2;
  }

  protected void calculateConsequences(String[] itemForm1, String[] itemForm2) {
    String treeName1 = itemForm1[0];
    String node1 = itemForm1[1];
    String pos1 = itemForm1[2];
    String i1 = itemForm1[3];
    String l = itemForm1[6];
    String adj1 = itemForm1[7];
    String treeName2 = itemForm2[0];
    String node2 = itemForm2[1];
    String pos2 = itemForm2[2];
    String i21 = itemForm2[3];
    String f12 = itemForm2[4];
    String f22 = itemForm2[5];
    String i22 = itemForm2[6];
    String adj2 = itemForm2[7];
    boolean adjoinable1 = tag.isAdjoinable(treeName2, treeName1, node1);
    if (i1.equals(i21) && adj1.equals("0") && adj2.equals("0")) {
      if (adjoinable1 && pos1.equals("rb") && pos2.equals("lb")
        && i21.equals(i22) && f12.equals("-") && f22.equals("-")
        && tag.getAuxiliaryTree(treeName2).getFoot().getGornAddress()
          .equals(node2)) {
        consequences
          .add(new DeductionItem(treeName2, node2, "rb", i1, i1, l, l, "0"));
      }
    }
  }

  @Override public String toString() {
    return "[ɣ,p,rb,i,j,k,l,0], [β,pf,lb,i,-,-,i,0]"
      + "\n______ pf foot node address in β, β ∈ f_SA(ɣ,p)\n"
      + "[β,pf,rb,i,i,l,l,0]";
  }

}

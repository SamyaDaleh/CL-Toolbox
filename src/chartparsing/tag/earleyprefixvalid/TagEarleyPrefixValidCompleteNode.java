package chartparsing.tag.earleyprefixvalid;

import chartparsing.AbstractDynamicDecutionRuleTwoAntecedences;
import chartparsing.DeductionItem;
import common.tag.Tag;
import common.tag.Vertex;

public class TagEarleyPrefixValidCompleteNode extends
  AbstractDynamicDecutionRuleTwoAntecedences {

  private final Tag tag;

  public TagEarleyPrefixValidCompleteNode(Tag tag) {
    this.tag = tag;
    this.name = "complete node";
    this.antNeeded = 2;
  }

  protected void calculateConsequences(String[] itemForm1, String[] itemForm2) {
    String treeName1 = itemForm1[0];
    String node1 = itemForm1[1];
    String pos1 = itemForm1[2];
    String iGamma1 = itemForm1[3];
    String i1 = itemForm1[4];
    String j1 = itemForm1[5];
    String k1 = itemForm1[6];
    String l1 = itemForm1[7];
    String adj1 = itemForm1[8];
    String treeName2 = itemForm2[0];
    String node2 = itemForm2[1];
    String pos2 = itemForm2[2];
    String iGamma2 = itemForm2[3];
    String i2 = itemForm2[4];
    String j2 = itemForm2[5];
    String k2 = itemForm2[6];
    String l2 = itemForm2[7];
    Vertex p = tag.getTree(treeName1).getNodeByGornAdress(node1);
    if (treeName1.equals(treeName2) && node1.equals(node2) && pos1.equals("la")
      && pos2.equals("rb") && !iGamma1.equals("~") && !i1.equals("~")
      && !j1.equals("~") && !k1.equals("~") && !l1.equals("~")
      && adj1.equals("0") && iGamma2.equals("~") && !i2.equals("~")
      && !j2.equals("~") && !k2.equals("~") && !l2.equals("~")
      && tag.isInNonterminals(p.getLabel())) {
      String f1 = (j1.equals("-")) ? j2 : j1;
      String f2 = (k1.equals("-")) ? k2 : k1;
      consequences.add(new DeductionItem(treeName1, node1, "ra", iGamma1, i1,
        f1, f2, l2, "0"));
    }
  }

  @Override public String toString() {
    return "[ɣ,p,la,i_ɣ,f,g,h,i,0], [ɣ,p,rb,~,i,j,k,l,adj]"
      + "\n______ l(ɣ,p) ∈  N\n" + "[ɣ,p,ra,i_ɣ,f,g⊕j,h⊕k,l,0]";
  }
}

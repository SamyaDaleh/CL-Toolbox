package chartparsing.tag.earleyprefixvalid;

import java.util.List;

import chartparsing.AbstractDynamicDeductionRule;
import chartparsing.DeductionItem;
import chartparsing.DynamicDeductionRule;
import chartparsing.Item;
import common.tag.Tag;
import common.tag.Vertex;

public class TagEarleyPrefixValidSubstitute extends AbstractDynamicDeductionRule
  implements DynamicDeductionRule {

  private final Tag tag;

  public TagEarleyPrefixValidSubstitute(Tag tag) {
    this.tag = tag;
    this.name = "substitute";
    this.antNeeded = 2;
  }

  @Override public List<Item> getConsequences() {
    if (antecedences.size() == antNeeded) {
      String[] itemForm1 = antecedences.get(0).getItemform();
      String[] itemForm2 = antecedences.get(1).getItemform();
      calculateConsequences(itemForm1, itemForm2);
      calculateConsequences(itemForm2, itemForm1);
    }
    return consequences;
  }

  private void calculateConsequences(String[] itemForm1, String[] itemForm2) {
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
    String adj2 = itemForm2[8];
    Vertex p = tag.getTree(treeName1).getNodeByGornAdress(node1);
    if (pos1.equals("la") && iGamma1.equals("~") && i1.equals("~")
      && j1.equals("~") && k1.equals("~") && !l1.equals("~") && adj1.equals("0")
      && l1.equals(iGamma2) && iGamma2.equals(i2) && j2.equals("-")
      && k2.equals("-") && tag.getInitialTree(treeName2) != null
      && node2.equals("") && tag.isSubstitutionNode(p, treeName1)
      && p.getLabel().equals(tag.getInitialTree(treeName2).getRoot().getLabel())
      && pos2.equals("ra") && adj2.equals("0")) {
      consequences.add(
        new DeductionItem(treeName1, node1, "rb", "~", l1, "-", "-", l2, "0"));
      this.name =
        "substitute " + treeName1 + "[" + node1 + "," + treeName2 + "]";
    }
  }

  @Override public String toString() {
    return "[ɣ,p,la,~,~,~,~,i,0], [α,ε,ra,i,i,-,-,j,0]"
      + "\n______ α ∈ I, (ɣ,p) a substitution node, l(ɣ,p) = l(α,ε)\n"
      + "[ɣ,p,rb,~,i,-,-,j,0]";
  }

}

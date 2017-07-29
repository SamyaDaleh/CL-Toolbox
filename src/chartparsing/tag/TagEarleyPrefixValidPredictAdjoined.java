package chartparsing.tag;

import java.util.List;

import chartparsing.AbstractDynamicDeductionRule;
import chartparsing.DeductionItem;
import chartparsing.DynamicDeductionRule;
import chartparsing.Item;
import common.tag.Tag;

public class TagEarleyPrefixValidPredictAdjoined
  extends AbstractDynamicDeductionRule implements DynamicDeductionRule {

  private final Tag tag;

  public TagEarleyPrefixValidPredictAdjoined(Tag tag) {
    this.tag = tag;
    this.name = "predict adjoined";
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
    String j1 = itemForm1[5];
    String k1 = itemForm1[6];
    String m = itemForm1[7];
    String adj1 = itemForm1[8];
    String treeName2 = itemForm2[0];
    String node2 = itemForm2[1];
    String pos2 = itemForm2[2];
    String iGamma2 = itemForm2[3];
    String j2 = itemForm2[4];
    String g = itemForm2[5];
    String h = itemForm2[6];
    String k2 = itemForm2[7];
    String adj2 = itemForm2[8];
    boolean adjoinable1 = tag.isAdjoinable(treeName1, treeName2, node2);
    if (adj1.equals("0") && adj2.equals("0") && adjoinable1) {
      boolean isFootNode = tag.getAuxiliaryTree(treeName1).getFoot()
        .getGornAddress().equals(node1);
      if (isFootNode && pos1.equals("la") && pos2.equals("la") && j1.equals("-")
        && k1.equals("-") && iGamma1.equals(k2) && j2.equals("~")
        && g.equals("~") && h.equals("~")) {
        consequences.add(new DeductionItem(treeName2, node2, "lb", iGamma2, m,
          "-", "-", m, "0"));
        String node2name = node2.length() == 0 ? "ε" : node2;
        this.name = "predict adjoined " + treeName2 + "[" + node2name + ","
          + treeName1 + "]";
      }
    }
  }

  @Override public String toString() {
    return "[β,p_f,ra,i,j,k,l,0], [ɣ,p,rb,j,g,h,k,0]"
      + "\n______ β(p_f) foot node, β ∈ f_SA(ɣ,p)\n" + "[ɣ,p,rb,i,g,h,l,1]";
  }

}

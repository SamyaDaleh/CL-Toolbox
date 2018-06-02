package chartparsing.tag.earleyprefixvalid;

import java.util.List;

import chartparsing.AbstractDynamicDeductionRule;
import chartparsing.DeductionItem;
import chartparsing.Item;
import common.tag.Tag;

public class TagEarleyPrefixValidScanTerm extends AbstractDynamicDeductionRule {

  private final String[] wSplit;
  private final Tag tag;

  public TagEarleyPrefixValidScanTerm(String[] wSplit, Tag tag) {
    this.wSplit = wSplit;
    this.tag = tag;
    this.name = "scan term";
    this.antNeeded = 1;
  }

  @Override public List<Item> getConsequences() {
    if (antecedences.size() == antNeeded) {
      String[] itemForm = antecedences.get(0).getItemform();
      String treeName = itemForm[0];
      String node = itemForm[1];
      String pos = itemForm[2];
      String iGamma = itemForm[3];
      String i = itemForm[4];
      String j = itemForm[5];
      String k = itemForm[6];
      String l = itemForm[7];
      int lInt = Integer.parseInt(l);
      String adj = itemForm[8];
      if (lInt < wSplit.length && pos.equals("la") && adj.equals("0")
        && tag.getTree(treeName).getNodeByGornAdress(node).getLabel()
          .equals(wSplit[lInt])) {
        consequences.add(new DeductionItem(treeName, node, "ra", iGamma, i, j,
          k, String.valueOf(lInt + 1), "0"));
      }
    }
    return consequences;
  }

  @Override public String toString() {
    return "[ɣ,p,la,i_ɣ,i,j,k,l,0]" + "\n______ l(ɣ,p_ɣ . p) = w_l\n"
      + "[ɣ,p,ra,i_ɣ,i,j,k,l+1,0]";
  }

}

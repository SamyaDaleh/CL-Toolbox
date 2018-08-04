package chartparsing.tag.earleyprefixvalid;

import java.util.List;

import chartparsing.AbstractDynamicDeductionRule;
import chartparsing.DeductionItem;
import chartparsing.Item;
import common.tag.Tag;

public class TagEarleyPrefixValidMoveRight
  extends AbstractDynamicDeductionRule {

  private final Tag tag;

  public TagEarleyPrefixValidMoveRight(Tag tag) {
    this.tag = tag;
    this.name = "move right";
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
      String adj = itemForm[8];
      String siblingGorn = tag.getTree(treeName).getNodeByGornAdress(node)
        .getGornAddressOfPotentialRightSibling();
      if (pos.equals("ra") && !iGamma.equals("~") && !i.equals("~")
        && !j.equals("~") && !k.equals("~") && adj.equals("0")
        && tag.getTree(treeName).getNodeByGornAdress(siblingGorn) != null) {
        Item consequence = new DeductionItem(treeName, siblingGorn, "la",
          iGamma, i, j, k, l, "0");
        consequence.setTree(antecedences.get(0).getTree());
        consequences.add(consequence);
      }
    }
    return consequences;
  }

  @Override public String toString() {
    return "[ɣ,p,ra,i_ɣ,i,j,k,l,0]" + "\n______ ɣ(p+1) is defined\n"
      + "[ɣ,p+1,la,i_ɣ,i,j,k,l,0]";
  }

}

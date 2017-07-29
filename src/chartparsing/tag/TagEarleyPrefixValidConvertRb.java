package chartparsing.tag;

import java.util.List;

import chartparsing.AbstractDynamicDeductionRule;
import chartparsing.DeductionItem;
import chartparsing.DynamicDeductionRule;
import chartparsing.Item;

public class TagEarleyPrefixValidConvertRb extends AbstractDynamicDeductionRule
  implements DynamicDeductionRule {

  public TagEarleyPrefixValidConvertRb() {
    this.name = "convert rb";
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
      if (pos.equals("rb") && adj.equals("0") && iGamma.equals("~")
        && !j.equals("~") && !k.equals("~")) {
        consequences.add(
          new DeductionItem(treeName, node, "rb", "~", i, "~", "~", l, "0"));
      }
    }
    return consequences;
  }

  @Override public String toString() {
    return "[ɣ,p,lb,~,i,j,k,l,0]" + "\n______ \n" + "[ɣ,p.1,la,~,i,~,~,l,0]";
  }

}

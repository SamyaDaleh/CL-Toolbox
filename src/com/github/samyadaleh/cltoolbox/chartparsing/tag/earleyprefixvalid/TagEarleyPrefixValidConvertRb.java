package com.github.samyadaleh.cltoolbox.chartparsing.tag.earleyprefixvalid;

import java.util.List;

import com.github.samyadaleh.cltoolbox.chartparsing.AbstractDynamicDeductionRule;
import com.github.samyadaleh.cltoolbox.chartparsing.DeductionItem;
import com.github.samyadaleh.cltoolbox.chartparsing.Item;

public class TagEarleyPrefixValidConvertRb
  extends AbstractDynamicDeductionRule {

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
        Item consequence =
          new DeductionItem(treeName, node, "rb", "~", i, "~", "~", l, "0");
        consequence.setTrees(antecedences.get(0).getTrees());
        consequences.add(consequence);
      }
    }
    return consequences;
  }

  @Override public String toString() {
    return "[ɣ,p,lb,~,i,j,k,l,0]" + "\n______ \n" + "[ɣ,p.1,la,~,i,~,~,l,0]";
  }

}

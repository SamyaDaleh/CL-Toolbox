package com.github.samyadaleh.cltoolbox.chartparsing.tag.earleyprefixvalid;

import java.util.Arrays;
import java.util.List;

import com.github.samyadaleh.cltoolbox.chartparsing.AbstractDynamicDeductionRule;
import com.github.samyadaleh.cltoolbox.chartparsing.DeductionChartItem;
import com.github.samyadaleh.cltoolbox.chartparsing.ChartItemInterface;
import com.github.samyadaleh.cltoolbox.common.tag.Tag;
import com.github.samyadaleh.cltoolbox.common.tag.Tree;
import com.github.samyadaleh.cltoolbox.common.tag.Vertex;

public class TagEarleyPrefixValidCompleteFoot
  extends AbstractDynamicDeductionRule {

  private final Tag tag;

  public TagEarleyPrefixValidCompleteFoot(Tag tag) {
    this.tag = tag;
    this.name = "complete foot";
    this.antNeeded = 3;
  }

  @Override public List<ChartItemInterface> getConsequences() {
    if (antecedences.size() == antNeeded) {
      String[] itemForm1 = antecedences.get(0).getItemForm();
      String[] itemForm2 = antecedences.get(1).getItemForm();
      String[] itemForm3 = antecedences.get(2).getItemForm();
      calculateConsequences(itemForm1, itemForm2, itemForm3);
      calculateConsequences(itemForm1, itemForm3, itemForm2);
      calculateConsequences(itemForm2, itemForm1, itemForm3);
      calculateConsequences(itemForm2, itemForm3, itemForm1);
      calculateConsequences(itemForm3, itemForm1, itemForm2);
      calculateConsequences(itemForm3, itemForm2, itemForm1);
    }
    return consequences;
  }

  private void calculateConsequences(String[] itemForm1, String[] itemForm2,
    String[] itemForm3) {
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
    String treeName3 = itemForm3[0];
    String node3 = itemForm3[1];
    String pos3 = itemForm3[2];
    String iGamma3 = itemForm3[3];
    String i3 = itemForm3[4];
    String j3 = itemForm3[5];
    String k3 = itemForm3[6];
    String l3 = itemForm3[7];
    String adj3 = itemForm3[8];
    if (tag.getAuxiliaryTree(treeName2) != null) {
      Vertex pf = tag.getAuxiliaryTree(treeName2).getNodeByGornAdress(node2);
      if (pos1.equals("rb") && iGamma1.equals("~") && i1.equals(l2)
        && j1.equals("~") && k1.equals("~") && adj1.equals("0")
        && pf == tag.getAuxiliaryTree(treeName2).getFoot() && pos2.equals("la")
        && iGamma2.equals(l3) && !i2.equals("~") && j2.equals("-")
        && k2.equals("-") && adj2.equals("0") && treeName1.equals(treeName3)
        && node1.equals(node3) && pos3.equals("la") && iGamma3.equals("~")
        && i3.equals("~") && j3.equals("~") && k3.equals("~")
        && adj3.equals("0") && tag.isAdjoinable(treeName2, treeName1, node1)) {
        ChartItemInterface consequence =
          new DeductionChartItem(treeName2, node2, "rb", "~", i1, i1, l1, l1, "0");
        List<Tree> derivedTrees;
        if (Arrays.equals(itemForm2, antecedences.get(0).getItemForm())) {
          derivedTrees = antecedences.get(0).getTrees();
        } else if (Arrays.equals(itemForm2, antecedences.get(1).getItemForm())) {
          derivedTrees = antecedences.get(1).getTrees();
        } else {
          derivedTrees = antecedences.get(2).getTrees();
        }
        consequence.setTrees(derivedTrees);
        logItemGeneration(consequence);
        consequences.add(consequence);
      }
    }

  }

  @Override public String toString() {
    return "[ɣ,p,rb,~,i,~,~,l,0], [β,p_f,la,i_β,m,-,-,i,0], [ɣ,p,la,~,~,~,~,i_β,0]"
      + "\n______ β(p_f) foot node, β ∈  f_SA(ɣ,p)\n"
      + "[β,p_f,rb,~,m,i,l,l,0]";
  }

}

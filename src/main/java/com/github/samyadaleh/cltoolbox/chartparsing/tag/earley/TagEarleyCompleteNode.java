package com.github.samyadaleh.cltoolbox.chartparsing.tag.earley;

import com.github.samyadaleh.cltoolbox.chartparsing.dynamicdeductionrule.AbstractDynamicDecutionRuleTwoAntecedences;
import com.github.samyadaleh.cltoolbox.chartparsing.item.DeductionChartItem;
import com.github.samyadaleh.cltoolbox.chartparsing.item.ChartItemInterface;
import com.github.samyadaleh.cltoolbox.common.tag.Tag;

import static com.github.samyadaleh.cltoolbox.common.Constants.DEDUCTION_RULE_TAG_EARLEY_COMPLETENODE;

/**
 * If you have one item in a node la and another matching in the same node in
 * rb, you can put both together.
 */
public class TagEarleyCompleteNode
    extends AbstractDynamicDecutionRuleTwoAntecedences {

  private final Tag tag;

  /**
   * Constructor needs the grammar to retrieve information about the
   * antecedences.
   */
  public TagEarleyCompleteNode(Tag tag) {
    this.tag = tag;
    this.name = DEDUCTION_RULE_TAG_EARLEY_COMPLETENODE;
    this.antNeeded = 2;
  }

  protected void calculateConsequences(String[] itemForm1, String[] itemForm2) {
    String treeName1 = itemForm1[0];
    String node1 = itemForm1[1];
    String pos1 = itemForm1[2];
    String f = itemForm1[3];
    String g = itemForm1[4];
    String h = itemForm1[5];
    String i1 = itemForm1[6];
    String adj1 = itemForm1[7];
    String treeName2 = itemForm2[0];
    String node2 = itemForm2[1];
    String pos2 = itemForm2[2];
    String i2 = itemForm2[3];
    String j = itemForm2[4];
    String k = itemForm2[5];
    String l = itemForm2[6];
    String label = tag.getTree(treeName1).getNodeByGornAddress(node1).getLabel();
    if (treeName1.equals(treeName2) && node1.equals(node2) && tag
        .isInNonterminals(label)) {
      if (pos1.equals("la") && pos2.equals("rb") && i1.equals(i2) && adj1
          .equals("0")) {
        String f1 = (g.equals("-")) ? j : g;
        String f2 = (h.equals("-")) ? k : h;
        ChartItemInterface consequence =
            new DeductionChartItem(treeName1, node1, "ra", f, f1, f2, l, "0");
        consequence.setTrees(antecedences.get(0).getTrees());
        logItemGeneration(consequence);
        consequences.add(consequence);
      }
    }
  }

  @Override public String toString() {
    return "[ɣ,p,la,f,g,h,i,0], [ɣ,p,rb,i,j,k,l,adj]" + "\n______ l(ɣ,p) ∈ N\n"
        + "[ɣ,p,ra,f,g⊕j,h⊕k,l,0]";
  }

}

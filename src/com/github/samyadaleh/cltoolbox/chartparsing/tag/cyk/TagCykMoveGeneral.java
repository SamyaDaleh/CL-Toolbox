package com.github.samyadaleh.cltoolbox.chartparsing.tag.cyk;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import com.github.samyadaleh.cltoolbox.chartparsing.AbstractDynamicDeductionRule;
import com.github.samyadaleh.cltoolbox.chartparsing.ChartItemInterface;
import com.github.samyadaleh.cltoolbox.chartparsing.DeductionChartItem;
import com.github.samyadaleh.cltoolbox.common.tag.Tag;
import com.github.samyadaleh.cltoolbox.common.tag.Tree;

/**
 * From "antNeeded" number of sibling nodes moves up to the parent.
 */
public class TagCykMoveGeneral extends AbstractDynamicDeductionRule {

  private Tag tag;

  public TagCykMoveGeneral(Tag tag, int antNeeded) {
    this.tag = tag;
    this.antNeeded = antNeeded;
    this.name = "move-general";
  }

  @Override public List<ChartItemInterface> getConsequences()
    throws ParseException {
    if (antecedences.size() == antNeeded) {
      String treeName = antecedences.get(0).getItemform()[0];
      String nodeGorn = antecedences.get(0).getItemform()[1];
      if ("⊤".equals(nodeGorn) || "⊥".equals(nodeGorn)) {
        return consequences;
      }
      String parentGorn = nodeGorn.substring(0, nodeGorn.lastIndexOf('.'));
      Tree tree = tag.getTree(treeName);
      if (tree.getChildren(tree.getNodeByGornAdress(parentGorn))
        .size() != antNeeded) {
        return consequences;
      }
      List<Integer> children = new ArrayList<Integer>();
      String[] boundaries = new String[antNeeded * 2];
      String[] foot = new String[] {"-", "-"};
      int i = 0;
      for (ChartItemInterface antecedence : antecedences) {
        String thisNodeGorn = antecedence.getItemform()[1];
        if ("⊤".equals(thisNodeGorn) || "⊥".equals(thisNodeGorn)) {
          return consequences;
        }
        String thisParentGorn =
          thisNodeGorn.substring(0, thisNodeGorn.lastIndexOf('.'));
        if (antecedence.getItemform()[0].equals(treeName)
          && thisParentGorn.equals(parentGorn) && thisNodeGorn.endsWith("⊤")) {
          children.add(Integer.parseInt(thisNodeGorn.substring(
            thisNodeGorn.lastIndexOf('.') + 1, thisNodeGorn.length() - 1)));
          boundaries[i] = antecedence.getItemform()[2];
          boundaries[i + 1] = antecedence.getItemform()[5];
          if (!"-".equals(antecedence.getItemform()[3])) {
            foot[0] = antecedence.getItemform()[3];
            foot[1] = antecedence.getItemform()[4];
          }
          i += 2;
        } else {
          return consequences;
        }
      }
      int lastEntry = 0;
      for (i = 1; i <= antNeeded; i++) {
        int index = children.indexOf(i);
        if (index < 0 || children.get(index) != i || (i > 1
          && !boundaries[index * 2].equals(boundaries[lastEntry * 2 + 1]))) {
          return consequences;
        }
        lastEntry = index;
      }
      int firstEntry = children.indexOf(1);
      ChartItemInterface consequence = new DeductionChartItem(treeName,
        parentGorn + "⊥", boundaries[firstEntry * 2], foot[0], foot[1],
        boundaries[lastEntry * 2 + 1]);
      consequence.setTrees(antecedences.get(0).getTrees());
      consequences.add(consequence);
    }
    return consequences;
  }

  /**
   * @see java.lang.String#toString()
   */
  @Override public String toString() {
    StringBuilder repr = new StringBuilder();
    int i;
    StringBuilder bars = new StringBuilder();
    for (i = 1; i <= antNeeded; i++) {
      repr.append("[ɣ,(p." + i + ")⊤,i_" + i + ",f1" + bars.toString()
        + ",f2+bars.toString()+,i_" + (i + 1) + "] ");
      bars.append("'");
    }
    repr.append("\n____________\n")
      .append("[ɣ,p⊥,i_" + (i - 1) + ",f1⊕...⊕f1"
        + bars.substring(0, bars.length() - 1)
        + ",f2⊕...⊕f2\"+bars.substring(0, bars.length()-1)+\",i_" + i + "]");
    return repr.toString();
  }

}

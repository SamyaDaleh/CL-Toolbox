package com.github.samyadaleh.cltoolbox.chartparsing.tag.cyk;

import java.util.ArrayList;
import java.util.List;

import com.github.samyadaleh.cltoolbox.chartparsing.AbstractDynamicDeductionRule;
import com.github.samyadaleh.cltoolbox.chartparsing.DeductionChartItem;
import com.github.samyadaleh.cltoolbox.chartparsing.ChartItemInterface;
import com.github.samyadaleh.cltoolbox.common.tag.Tag;
import com.github.samyadaleh.cltoolbox.common.tag.Tree;

/**
 * Tries to substitute a given initial tree into the node of the tree it
 * remembers.
 */
public class TagCykSubstitute extends AbstractDynamicDeductionRule {

  private final Tag tag;
  private final String nodeGorn;
  private final String treeName;

  /** Remembers tree and node it can substitute in. */
  public TagCykSubstitute(String treeName, String nodeGorn, Tag tag) {
    this.tag = tag;
    this.treeName = treeName;
    this.nodeGorn = nodeGorn;
    this.name = "substitute in " + treeName + "(" + nodeGorn + ")";
    this.antNeeded = 1;
  }

  @Override public List<ChartItemInterface> getConsequences() {
    if (antecedences.size() == antNeeded) {
      String[] itemForm = antecedences.get(0).getItemForm();
      String treeName = itemForm[0];
      String node = itemForm[1];
      String i = itemForm[2];
      String j = itemForm[5];
      if (tag.getInitialTree(treeName) != null && node.equals("⊤")
        && tag.getInitialTree(treeName).getRoot().getLabel()
          .equals(tag.getTree(this.treeName).getNodeByGornAdress(nodeGorn)
            .getLabel())) {
        ChartItemInterface consequence = new DeductionChartItem(this.treeName,
          this.nodeGorn + "⊤", i, "-", "-", j);
        Tree derivedTreeBase = tag.getTree(this.treeName);
        List<Tree> derivedTrees = new ArrayList<>();
        for (Tree tree : antecedences.get(0).getTrees()) {
          derivedTrees.add(derivedTreeBase.substitute(this.nodeGorn, tree));
        }
        consequence.setTrees(derivedTrees);
        this.name = "substitute " + this.treeName + "[" + this.nodeGorn + ","
            + treeName + "]";
        logItemGeneration(consequence);
        consequences.add(consequence);
      }
    }
    return consequences;
  }

  @Override public String toString() {
    return "[α,ε⊤,i,-,-,j]" + "\n______ l(α,ε) = l(" + treeName + "," + nodeGorn
      + "), " + treeName + "(" + nodeGorn + ") a substitution node\n" + "["
      + treeName + "," + nodeGorn + "⊤,i,-,-,j]";
  }

}

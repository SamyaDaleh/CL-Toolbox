package com.github.samyadaleh.cltoolbox.chartparsing.tag.earley;

import java.util.ArrayList;
import java.util.List;

import com.github.samyadaleh.cltoolbox.chartparsing.dynamicdeductionrule.AbstractDynamicDeductionRule;
import com.github.samyadaleh.cltoolbox.chartparsing.item.DeductionChartItem;
import com.github.samyadaleh.cltoolbox.chartparsing.item.ChartItemInterface;
import com.github.samyadaleh.cltoolbox.common.TreeUtils;
import com.github.samyadaleh.cltoolbox.common.tag.Tag;
import com.github.samyadaleh.cltoolbox.common.tag.Tree;

import static com.github.samyadaleh.cltoolbox.common.Constants.DEDUCTION_RULE_TAG_EARLEY_SUBSTITUTE;
import static com.github.samyadaleh.cltoolbox.common.Constants.EPSILON;

/**
 * If a potential initial tree is complete, substitute it if possible.
 */
public class TagEarleySubstitute extends AbstractDynamicDeductionRule {

  private final String outTreeName;
  private final String outNode;
  private final Tag tag;

  /**
   * Constructor takes a tree and a node where maybe is, was, will be adjoined,
   * also needs the grammar to retrieve information about the antecedence.
   */
  public TagEarleySubstitute(String outTreeName, String outNode, Tag tag) {
    this.outTreeName = outTreeName;
    this.outNode = outNode;
    this.tag = tag;
    this.name =
        DEDUCTION_RULE_TAG_EARLEY_SUBSTITUTE + " " + outTreeName + "(" + outNode
            + ")";
    this.antNeeded = 1;
  }

  @Override public List<ChartItemInterface> getConsequences() {
    if (antecedences.size() == antNeeded) {
      String[] itemForm = antecedences.get(0).getItemForm();
      String treeName = itemForm[0];
      String node = itemForm[1];
      String pos = itemForm[2];
      String i = itemForm[3];
      String f1 = itemForm[4];
      String f2 = itemForm[5];
      String j = itemForm[6];
      String adj = itemForm[7];
      String iniTreeRootLabel = tag.getTree(treeName).getRoot().getLabel();
      String substNodeLabel =
          tag.getTree(outTreeName).getNodeByGornAddress(outNode).getLabel();
      if (tag.getInitialTree(treeName) != null && node.equals("") && f1
          .equals("-") && f2.equals("-") && adj.equals("0") && pos.equals("ra")
          && iniTreeRootLabel.equals(substNodeLabel)) {
        ChartItemInterface consequence =
            new DeductionChartItem(outTreeName, outNode, "rb", i, "-", "-", j,
                "0");
        Tree derivedTreeBase = tag.getTree(outTreeName);
        List<Tree> derivedTrees = new ArrayList<>();
        for (Tree tree : antecedences.get(0).getTrees()) {
          derivedTrees.add(
              TreeUtils.performLeftmostSubstitution(derivedTreeBase, tree));
        }
        // imagine a tree with 1 node where you would substitute into the root
        // ...
        String outNodeName = outNode.length() == 0 ? EPSILON : outNode;
        this.name =
            "substitute " + outTreeName + "[" + outNodeName + "," + treeName
                + "]";
        consequence.setTrees(derivedTrees);
        logItemGeneration(consequence);
        consequences.add(consequence);
      }
    }
    return consequences;
  }

  @Override public String toString() {
    return "[α,ε,ra,i,-,-,j,0]" + "\n______ " + outTreeName + "(" + outNode
        + ") a substitution node, α ∈ I, l(" + outTreeName + "," + outNode
        + ") = l(α,ε)\n" + "[" + outTreeName + "," + outNode + ",rb,i,-,-,j,0]";
  }

}

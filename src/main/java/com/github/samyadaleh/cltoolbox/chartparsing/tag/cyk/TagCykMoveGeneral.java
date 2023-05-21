package com.github.samyadaleh.cltoolbox.chartparsing.tag.cyk;

import com.github.samyadaleh.cltoolbox.chartparsing.dynamicdeductionrule.AbstractDynamicDeductionRule;
import com.github.samyadaleh.cltoolbox.chartparsing.item.ChartItemInterface;
import com.github.samyadaleh.cltoolbox.chartparsing.item.DeductionChartItem;
import com.github.samyadaleh.cltoolbox.common.TreeUtils;
import com.github.samyadaleh.cltoolbox.common.tag.Tag;
import com.github.samyadaleh.cltoolbox.common.tag.Tree;

import java.util.ArrayList;
import java.util.List;

import static com.github.samyadaleh.cltoolbox.common.Constants.DEDUCTION_RULE_TAG_MOVE_GENERAL;

/**
 * From "antNeeded" number of sibling nodes moves up to the parent.
 */
public class TagCykMoveGeneral extends AbstractDynamicDeductionRule {

  private final Tag tag;

  public TagCykMoveGeneral(Tag tag, int antNeeded) {
    this.tag = tag;
    this.antNeeded = antNeeded;
    this.name = DEDUCTION_RULE_TAG_MOVE_GENERAL;
  }

  @Override public List<ChartItemInterface> getConsequences() {
    if (antecedences.size() == antNeeded) {
      String treeName = antecedences.get(0).getItemForm()[0];
      String nodeGorn = antecedences.get(0).getItemForm()[1];
      if ("⊤".equals(nodeGorn) || "⊥".equals(nodeGorn)) {
        return consequences;
      }
      String parentGorn = nodeGorn.substring(0, nodeGorn.lastIndexOf('.'));
      Tree tree = tag.getTree(treeName);
      if (tree.getChildren(tree.getNodeByGornAddress(parentGorn))
        .size() != antNeeded) {
        return consequences;
      }
      List<Integer> children = new ArrayList<>();
      String[] boundaries = new String[antNeeded * 2];
      String[] foot = new String[] {"-", "-"};
      if (checkAllAntecedences(treeName, parentGorn, children, boundaries,
          foot))
        return consequences;
      int i;
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
      if (antecedences.size() == 1) {
        consequence.setTrees(antecedences.get(0).getTrees());
      } else {
        List<Tree[]> treeCombinations = getAllTreeCombinations();
        List<Tree> derivedTrees = new ArrayList<>();
        for (Tree[] combination : treeCombinations) {
          derivedTrees.add(TreeUtils.mergeTrees(combination));
        }
        consequence.setTrees(derivedTrees);
      }
      logItemGeneration(consequence);
      consequences.add(consequence);
    }
    return consequences;
  }

  public List<Tree[]> getAllTreeCombinations() {
    List<Tree[]> combinations = new ArrayList<>();
    getAllTreeCombinationsHelper(combinations, new ArrayList<>(),
        antecedences, 0);
    return combinations;
  }

  private void getAllTreeCombinationsHelper(
      List<Tree[]> combinations, List<Tree> currentCombination,
      List<ChartItemInterface> antecedences, int index) {
    if (index == antecedences.size()) {
      // We've selected a tree from each antecedent, so add the combination to the result list
      combinations.add(currentCombination.toArray(new Tree[0]));
    } else {
      List<Tree> trees = antecedences.get(index).getTrees();
      for (Tree tree : trees) {
        List<Tree> newCombination = new ArrayList<>(currentCombination);
        newCombination.add(tree);
        getAllTreeCombinationsHelper(combinations, newCombination,
            antecedences, index + 1);
      }
    }
  }


  private boolean checkAllAntecedences(String treeName, String parentGorn,
      List<Integer> children, String[] boundaries, String[] foot) {
    int i = 0;
    for (ChartItemInterface antecedence : antecedences) {
      String thisNodeGorn = antecedence.getItemForm()[1];
      if ("⊤".equals(thisNodeGorn) || "⊥".equals(thisNodeGorn)) {
        return true;
      }
      String thisParentGorn =
        thisNodeGorn.substring(0, thisNodeGorn.lastIndexOf('.'));
      if (antecedence.getItemForm()[0].equals(treeName)
        && thisParentGorn.equals(parentGorn) && thisNodeGorn.endsWith("⊤")) {
        children.add(Integer.parseInt(thisNodeGorn.substring(
          thisNodeGorn.lastIndexOf('.') + 1, thisNodeGorn.length() - 1)));
        boundaries[i] = antecedence.getItemForm()[2];
        boundaries[i + 1] = antecedence.getItemForm()[5];
        if (!"-".equals(antecedence.getItemForm()[3])) {
          foot[0] = antecedence.getItemForm()[3];
          foot[1] = antecedence.getItemForm()[4];
        }
        i += 2;
      } else {
        return true;
      }
    }
    return false;
  }

  /**
   * @see java.lang.String#toString()
   */
  @Override public String toString() {
    StringBuilder repr = new StringBuilder();
    int i;
    StringBuilder bars = new StringBuilder();
    for (i = 1; i <= antNeeded; i++) {
      repr.append("[ɣ,(p.").append(i).append(")⊤,i_").append(i).append(",f1")
          .append(bars).append(",f2+bars.toString()+,i_")
          .append(i + 1).append("] ");
      bars.append("'");
    }
    repr.append("\n____________\n").append("[ɣ,p⊥,i_").append(i - 1)
        .append(",f1⊕...⊕f1").append(bars.substring(0, bars.length() - 1))
        .append(",f2⊕...⊕f2\"+bars.substring(0, bars.length()-1)+\",i_")
        .append(i).append("]");
    return repr.toString();
  }

}

package com.github.samyadaleh.cltoolbox.chartparsing.cfg.shiftreduce;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.github.samyadaleh.cltoolbox.chartparsing.dynamicdeductionrule.AbstractDynamicDeductionRule;
import com.github.samyadaleh.cltoolbox.chartparsing.item.BottomUpChartItem;
import com.github.samyadaleh.cltoolbox.chartparsing.item.ChartItemInterface;
import com.github.samyadaleh.cltoolbox.chartparsing.item.Pair;
import com.github.samyadaleh.cltoolbox.common.ArrayUtils;
import com.github.samyadaleh.cltoolbox.common.TreeUtils;
import com.github.samyadaleh.cltoolbox.common.cfg.CfgProductionRule;
import com.github.samyadaleh.cltoolbox.common.tag.Tree;

import static com.github.samyadaleh.cltoolbox.common.Constants.DEDUCTION_RULE_CFG_SHIFTREDUCE_REDUCE;

/**
 * If the top o the stack matches the rhs of a rule, replace it with the lhs.
 */
public class CfgBottomUpReduce extends AbstractDynamicDeductionRule {

  private final CfgProductionRule rule;

  public CfgBottomUpReduce(CfgProductionRule rule) {
    this.rule = rule;
    this.antNeeded = 1;
    this.name = DEDUCTION_RULE_CFG_SHIFTREDUCE_REDUCE + " " + rule.toString();
  }

  @Override public List<ChartItemInterface> getConsequences() {
    if (antecedences.size() == this.antNeeded) {
      String[] itemForm = antecedences.get(0).getItemForm();
      String stack = itemForm[0];
      String[] stackSplit = stack.split(" ");
      String i = itemForm[1];
      String gamma =
          ArrayUtils.getStringHeadIfEndsWith(stackSplit, rule.getRhs());
      if (gamma != null) {
        BottomUpChartItem consequence;
        if (gamma.length() == 0) {
          consequence = new BottomUpChartItem(rule.getLhs(), i);
        } else {
          consequence = new BottomUpChartItem(gamma + " " + rule.getLhs(), i);
        }
        List<Pair<String, List<Tree>>> derivedTrees = new ArrayList<>(
            ((BottomUpChartItem) antecedences.get(0)).getStackState());
        try {
          Tree derivedTreeBase = new Tree(rule);
          if (derivedTrees.size() == 0) {
            List<Tree> derivedTreeBaseList = new ArrayList<>();
            derivedTreeBaseList.add(derivedTreeBase);
            derivedTrees.add(new Pair<>(rule.getLhs(), derivedTreeBaseList));
          } else {
            List<Pair<String, List<Tree>>> rhsTreesLists = new ArrayList<>();
            List<String> rhsSymbols = Arrays.asList(rule.getRhs());

            int rhsIndex = 0;
            for (Pair<String, List<Tree>> treePair : derivedTrees) {
              while (rhsIndex < rhsSymbols.size() && !treePair.getFirst().equals(rhsSymbols.get(rhsIndex))) {
                rhsIndex++; // Skip over non-matching symbols
              }
              if (rhsIndex < rhsSymbols.size() && treePair.getFirst().equals(rhsSymbols.get(rhsIndex))) {
                rhsTreesLists.add(treePair);
                rhsIndex++; // Move to the next symbol
              }
            }
            if (rhsTreesLists.size() > 0) {
              derivedTrees.subList(0, rhsTreesLists.size()).clear();
            }

            List<List<Tree>> treeCombinations = generateCombinations(rhsTreesLists);

            // Create a new tree for each combination and add it to the new stack state.
            List<Tree> newTrees = new ArrayList<>();
            for (List<Tree> treeCombination : treeCombinations) {
              Tree localTree = new Tree(rule);
              for (Tree tree : treeCombination) {
                localTree = TreeUtils.performLeftmostSubstitution(localTree, tree);
              }
              newTrees.add(localTree);
            }
            if (newTrees.size() > 0) {
              derivedTrees.add(0, new Pair<>(rule.getLhs(), newTrees));
            }
            else {
              List<Tree> derivedTreeBaseList = new ArrayList<>();
              derivedTreeBaseList.add(derivedTreeBase);
              derivedTrees.add(new Pair<>(rule.getLhs(), derivedTreeBaseList));
            }
          }
          consequence.setStackState(derivedTrees);
          logItemGeneration(consequence);
          consequences.add(consequence);
        } catch (ParseException e) {
          throw new RuntimeException(e);
        }
      }
    }
    return consequences;
  }

  public static List<List<Tree>> generateCombinations(List<Pair<String, List<Tree>>> pairs) {
    List<List<Tree>> combinations = new ArrayList<>();
    generateCombinationsHelper(pairs, combinations, 0, new ArrayList<>());
    return combinations;
  }

  private static void generateCombinationsHelper(
      List<Pair<String, List<Tree>>> pairs, List<List<Tree>> combinations,
      int depth, List<Tree> currentCombination) {
    if (depth == pairs.size()) {
      combinations.add(new ArrayList<>(currentCombination));
      return;
    }

    for (int i = 0; i < pairs.get(depth).getSecond().size(); i++) {
      currentCombination.add(pairs.get(depth).getSecond().get(i));
      generateCombinationsHelper(pairs, combinations, depth + 1,
          currentCombination);
      currentCombination.remove(currentCombination.size() - 1);
    }
  }



  @Override public String toString() {
    return "[Γ " + ArrayUtils.toString(rule.getRhs()) + ",i]" + "\n______"
        + rule.toString() + "\n" + "[Γ " + rule.getLhs() + ",i]";
  }

}

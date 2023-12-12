package com.github.samyadaleh.cltoolbox.chartparsing.cfg.shiftreduce;

import java.text.ParseException;
import java.util.*;

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
    if (antecedences.size() != this.antNeeded) {
      return consequences;
    }
    String[] itemForm = antecedences.get(0).getItemForm();
    String stack = itemForm[0];
    String[] stackSplit = stack.split(" ");
    String i = itemForm[1];
    String gamma =
        ArrayUtils.getStringHeadIfEndsWith(stackSplit, rule.getRhs());
    if (gamma == null) {
      return consequences;
    }
    BottomUpChartItem consequence = createConsequence(i, gamma);
    List<Pair<String, Map<Integer, List<Tree>>>> derivedTrees =
        duplicateStackState((
            (BottomUpChartItem) antecedences.get(0)).getStackState());
    try {
      Tree derivedTreeBase = new Tree(rule);
      if (derivedTrees.size() == 0) {
        List<Tree> derivedTreeBaseList = new ArrayList<>();
        derivedTreeBaseList.add(derivedTreeBase);
        int length = rule.getRhs()[0].equals("") ? 0 : rule.getRhs().length;
        addTreesForLengthToDerivedTrees(derivedTrees, derivedTreeBaseList,
            length);
      } else {
        RhsTreesListAndRequiredLength result =
            getRhsTreesListAndRequiredLength(derivedTrees);
        if (result.rhsTreesLists.size() > 0) {
          derivedTrees.subList(0, result.rhsTreesLists.size()).clear();
        }

        List<List<Tree>> treeCombinations = generateCombinations(
            result.rhsTreesLists, result.requiredLength).get(
            result.requiredLength);

        // Create a new tree for each combination and add it to the new stack state.
        List<Tree> newTrees = createNewTrees(treeCombinations);
        if (newTrees.size() > 0) {
          int length = result.terminalsBetween + result.requiredLength;
          addTreesForLengthToDerivedTrees(derivedTrees, newTrees, length);
        } else {
          handleDerivedTreeBaseList(derivedTrees, derivedTreeBase);
        }
      }
      consequence.setStackState(derivedTrees);
      logItemGeneration(consequence);
      consequences.add(consequence);
    } catch (ParseException e) {
      throw new RuntimeException(e);
    }
    return consequences;
  }

  private List<Pair<String, Map<Integer, List<Tree>>>> duplicateStackState(
      List<Pair<String, Map<Integer, List<Tree>>>> stackState) {
    List<Pair<String, Map<Integer, List<Tree>>>> duplicateStackState =
        new ArrayList<>();
    for (Pair<String, Map<Integer, List<Tree>>> pair : stackState) {
      Map<Integer, List<Tree>> mapCopy = pair.getSecond();
      Pair<String, Map<Integer, List<Tree>>> dupPair =
          new Pair<>(pair.getFirst(), mapCopy);
      duplicateStackState.add(dupPair);
    }
    return duplicateStackState;
  }

  private void handleDerivedTreeBaseList(
      List<Pair<String, Map<Integer, List<Tree>>>> derivedTrees,
      Tree derivedTreeBase) {
    List<Tree> derivedTreeBaseList = new ArrayList<>();
    derivedTreeBaseList.add(derivedTreeBase);
    int length = rule.getRhs()[0].equals("") ? 0 : rule.getRhs().length;
    addTreesForLengthToDerivedTrees(derivedTrees, derivedTreeBaseList, length);
  }

  /**
   * requiredLength sums up the lengths of the subtrees that will be combined in the rhs
   */
  private RhsTreesListAndRequiredLength getRhsTreesListAndRequiredLength(
      List<Pair<String, Map<Integer, List<Tree>>>> derivedTrees) {
    List<Pair<String, Map<Integer, List<Tree>>>> rhsTreesLists = new ArrayList<>();
    List<String> rhsSymbols = Arrays.asList(rule.getRhs());

    int rhsIndex = rhsSymbols.size() - 1;
    int terminalsBetween = 0;
    int requiredLength = 0;
    for (Pair<String, Map<Integer, List<Tree>>> treePair : derivedTrees) {
      while (rhsIndex >= 0
          && !treePair.getFirst().equals(rhsSymbols.get(rhsIndex))) {
        rhsIndex--; // Skip over non-matching symbols
        terminalsBetween++;
      }
      if (rhsIndex >= 0
          && treePair.getFirst().equals(rhsSymbols.get(rhsIndex))) {
        rhsTreesLists.add(treePair);
        requiredLength
            += treePair.getSecond().entrySet().iterator().next().getKey();
        rhsIndex--; // Move to the next symbol
      }
    }
    while (rhsIndex >= 0) {
      rhsIndex--; // Skip over non-matching symbols
      terminalsBetween++;
    }
    return new RhsTreesListAndRequiredLength(
        rhsTreesLists, terminalsBetween, requiredLength);
  }

  private static class RhsTreesListAndRequiredLength {
    public final List<Pair<String, Map<Integer, List<Tree>>>> rhsTreesLists;
    public final int terminalsBetween;
    public final int requiredLength;

    public RhsTreesListAndRequiredLength(
        List<Pair<String, Map<Integer, List<Tree>>>> rhsTreesLists,
        int terminalsBetween, int requiredLength) {
      this.rhsTreesLists = rhsTreesLists;
      this.terminalsBetween = terminalsBetween;
      this.requiredLength = requiredLength;
    }
  }

  private List<Tree> createNewTrees(List<List<Tree>> treeCombinations)
      throws ParseException {
    List<Tree> newTrees = new ArrayList<>();
    for (List<Tree> treeCombination : treeCombinations) {
      Tree localTree = new Tree(rule);
      for (Tree tree : treeCombination) {
        localTree = TreeUtils.performLeftmostSubstitution(localTree, tree);
      }
      if (!newTrees.contains(localTree)) {
        newTrees.add(localTree);
      }
    }
    return newTrees;
  }

  private void addTreesForLengthToDerivedTrees(
      List<Pair<String, Map<Integer, List<Tree>>>> derivedTrees,
      List<Tree> newTrees, int length) {
    Map<Integer, List<Tree>> derivedTreeBaseListMap = new LinkedHashMap<>();
    derivedTreeBaseListMap.put(length, newTrees);
    derivedTrees.add(0, new Pair<>(rule.getLhs(), derivedTreeBaseListMap));
  }

  private BottomUpChartItem createConsequence(String i, String gamma) {
    BottomUpChartItem consequence;
    if (gamma.length() == 0) {
      consequence = new BottomUpChartItem(rule.getLhs(), i);
    } else {
      consequence = new BottomUpChartItem(gamma + " " + rule.getLhs(), i);
    }
    return consequence;
  }

  private Map<Integer, List<List<Tree>>> generateCombinations(
      List<Pair<String, Map<Integer, List<Tree>>>> rhsTreesLists, int requiredLength) {
    Map<Integer, List<List<Tree>>> result = new HashMap<>();

    // Base case: If the list is empty, there is only one combination: an empty list.
    // But only add it if the required length is zero.
    if (rhsTreesLists.isEmpty()) {
      if (requiredLength == 0) {
        result.put(0, Collections.singletonList(new ArrayList<>()));
      }
      return result;
    }

    Pair<String, Map<Integer, List<Tree>>> firstPair = rhsTreesLists.get(0);
    List<Pair<String, Map<Integer, List<Tree>>>> restList
        = rhsTreesLists.subList(1, rhsTreesLists.size());

    // Go through all the lengths that are not greater than the required length
    for (Map.Entry<Integer, List<Tree>> entry : firstPair.getSecond().entrySet()) {
      int length = entry.getKey();
      if (length > requiredLength) {
        continue;
      }

      List<Tree> trees = entry.getValue();
      Map<Integer, List<List<Tree>>> subCombs
          = generateCombinations(restList, requiredLength - length);

      for (Tree tree : trees) {
        for (Map.Entry<Integer, List<List<Tree>>> subComb : subCombs.entrySet()) {
          int subCombLength = subComb.getKey();
          for (List<Tree> subCombList : subComb.getValue()) {
            List<Tree> newComb = new ArrayList<>();
            newComb.add(tree);
            newComb.addAll(subCombList);
            int newCombLength = length + subCombLength;

            // Only add the new combination to the result if its total length equals the required length
            if (newCombLength == requiredLength) {
              result.computeIfAbsent(newCombLength, k -> new ArrayList<>()).add(newComb);
            }
          }
        }
      }
    }

    return result;
  }


  @Override public String toString() {
    return "[Γ " + ArrayUtils.toString(rule.getRhs()) + ",i]" + "\n______"
        + rule + "\n" + "[Γ " + rule.getLhs() + ",i]";
  }

}

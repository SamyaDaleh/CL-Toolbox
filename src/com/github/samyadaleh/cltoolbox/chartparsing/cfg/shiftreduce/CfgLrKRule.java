package com.github.samyadaleh.cltoolbox.chartparsing.cfg.shiftreduce;

import com.github.samyadaleh.cltoolbox.chartparsing.AbstractDynamicDeductionRule;
import com.github.samyadaleh.cltoolbox.chartparsing.ChartItemInterface;
import com.github.samyadaleh.cltoolbox.chartparsing.DeductionChartItem;
import com.github.samyadaleh.cltoolbox.common.ArrayUtils;
import com.github.samyadaleh.cltoolbox.common.TreeUtils;
import com.github.samyadaleh.cltoolbox.common.cfg.CfgProductionRule;
import com.github.samyadaleh.cltoolbox.common.tag.Tree;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Implementation of a general rule for LR(k) parsing, depends on a deterministic
 * parse table
 */
public class CfgLrKRule extends AbstractDynamicDeductionRule {
  private final List<CfgProductionRule> rules;
  private final Map<String, String> parsTable;
  private final String[] wSplit;

  public CfgLrKRule(String[] wSplit, List<CfgProductionRule> rules,
      Map<String, String> parsTable) {
    this.wSplit = wSplit;
    this.rules = rules;
    this.parsTable = parsTable;
    this.antNeeded = 1;
    this.name = "LR(k) parse table lookup";
  }

  @Override public List<ChartItemInterface> getConsequences()
      throws ParseException {
    if (antNeeded == antecedences.size()) {
      String[] itemForm = antecedences.get(0).getItemForm();
      int i = Integer.parseInt(itemForm[1]);
      String stack = itemForm[0];
      String[] stackSplit = stack.split(" ");
      String state = stackSplit[stackSplit.length - 1];
      String[] tableKey;
      tableKey = new String[2];
      tableKey[0] = state.substring(1);
      int j = 1;
      for (String sym : ArrayUtils.getSubSequenceAsArray(wSplit, i, i + 1)) {
        tableKey[j] = sym;
        j++;
      }
      String key =
          ArrayUtils.getSubSequenceAsString(tableKey, 0, tableKey.length);
      if (parsTable.containsKey(key)) {
        String action = parsTable.get(key);
        if (action.startsWith("s")) {
          ChartItemInterface consequence = new DeductionChartItem(ArrayUtils
              .getSubSequenceAsString(stackSplit, 0, stackSplit.length) + " "
              + wSplit[i] + " q" + action.substring(1), String.valueOf(i + 1));
          consequence.setTrees(antecedences.get(0).getTrees());
          this.name = "shift " + wSplit[i];
          logItemGeneration(consequence);
          consequences.add(consequence);
        } else {
          log.error("Unexpected table entry " + action + " for " + state + ", "
              + wSplit[i]);
        }
      } else {
        String halfKey = state.substring(1) + " $";
        String action = parsTable.get(halfKey);
        if (action != null && action.startsWith("r")) {
          int ruleId = Integer.parseInt(action.substring(1));
          CfgProductionRule rule = rules.get(ruleId - 1);
          for (int l = 0; l < rule.getRhs().length; l++) {
            if (!rule.getRhs()[l].equals(stackSplit[stackSplit.length
                - (rule.getRhs().length - l) * 2])) {
              return consequences;
            }
          }
          this.name = "reduce " + rule.toString();
          StringBuilder newStack = new StringBuilder(ArrayUtils
              .getSubSequenceAsString(stackSplit, 0,
                  stackSplit.length - rule.getRhs().length * 2));
          newStack.append(" ").append(rule.getLhs());
          String[] lastTableKey = new String[] {
              stackSplit[stackSplit.length - rule.getRhs().length * 2
                  - 1].substring(1), rule.getLhs()};
          String lastKey = ArrayUtils
              .getSubSequenceAsString(lastTableKey, 0, lastTableKey.length);
          String newState = parsTable.get(lastKey);
          newStack.append(" q").append(newState);
          ChartItemInterface consequence =
              new DeductionChartItem(newStack.toString(), itemForm[1]);
          List<Tree> derivedTrees =
              new ArrayList<>(antecedences.get(0).getTrees());
          Tree derivedTreeBase = new Tree(rule);
          for (Tree tree : antecedences.get(0).getTrees()) {
            boolean found = false;
            for (String rhsSym : rule.getRhs()) {
              if (tree.getRoot().getLabel().equals(rhsSym)) {
                derivedTrees.remove(0);
                derivedTreeBase = TreeUtils
                    .performLeftmostSubstitution(derivedTreeBase, tree);
                found = true;
                break;
              }
            }
            if (!found) {
              break;
            }
          }
          derivedTrees.add(0, derivedTreeBase);
          this.name = "reduce " + rule.toString();
          consequence.setTrees(derivedTrees);
          logItemGeneration(consequence);
          consequences.add(consequence);
        } else if (action != null && action.equals("acc")) {
          return consequences;
        } else {
          log.error("Unexpected table entry " + action + " for " + halfKey);
        }
      }
    }
    return consequences;
  }

  // TODO implement toString()
}

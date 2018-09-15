package com.github.samyadaleh.cltoolbox.chartparsing;

import com.github.samyadaleh.cltoolbox.chartparsing.dynamicdeductionrule.DynamicDeductionRuleInterface;
import com.github.samyadaleh.cltoolbox.chartparsing.item.ChartItemInterface;
import com.github.samyadaleh.cltoolbox.chartparsing.item.ProbabilisticChartItemInterface;
import com.github.samyadaleh.cltoolbox.common.tag.Tree;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A deduction system that derives consequences from antecendence items and
 * tries to generate a goal item. Based on the slides from Laura Kallmeyer about
 * Parsing as Deduction
 * https://user.phil.hhu.de/~kallmeyer/Parsing/deduction.pdf
 */
public class Deduction {
  /**
   * All items derived in the process.
   */
  private List<ChartItemInterface> chart;
  /**
   * Items waiting to be used for further derivation.
   */
  private List<ChartItemInterface> agenda;
  /**
   * List of the same length of chart, elements at same indexes belong to each
   * other. Contains lists of lists of backpointers. One item can be derived in
   * different ways from different antecedence items.
   */
  private ArrayList<ArrayList<ArrayList<Integer>>> deductedFrom;
  /**
   * Indexes correspond to entries of chart and deductedfrom. Collects the names
   * of the rules that were applied to retrieve new items.
   */
  private ArrayList<ArrayList<String>> appliedRule;
  /**
   * When true print only items that lead to a goal.
   */
  private boolean successfulTrace = false;
  /**
   * Markers if items lead to goal
   */
  private boolean[] usefulItem;
  /**
   * Specify if new items shall replace same existing items in the chart. If
   * null, don't replace. If h, replace by items with higher value (like
   * probabilities). If l, replace by items with lower value (like weights). If
   * - don't replace and add new backpointers to the list, commonly used for
   * items without value.
   */
  private char replace = '-';
  /**
   * When checking the goal items this stores the trees retrieved from them,
   * representing the result of the syntactic analysis..
   */
  private List<Tree> derivedTrees;
  /**
   * When retrieving the derived tree for a probabilistic parse only return the
   * best one, store probability or weight here.
   */
  private Double pGoal;
  private static final Logger log = LogManager.getLogger();

  /**
   * Takes a parsing schema, generates items from axiom rules and applies rules
   * to the items until all items were used. Returns true if a goal item was
   * derived.
   */
  public boolean doParse(ParsingSchema schema, boolean success) {
    successfulTrace = success;
    chart = new ArrayList<>();
    agenda = new ArrayList<>();
    deductedFrom = new ArrayList<>();
    appliedRule = new ArrayList<>();
    if (schema == null)
      return false;
    for (StaticDeductionRule rule : schema.getAxioms()) {
      applyAxiomRule(rule);
    }
    while (!agenda.isEmpty()) {
      ChartItemInterface item = agenda.get(0);
      agenda.remove(0);
      for (DynamicDeductionRuleInterface rule : schema.getRules()) {
        applyRule(item, rule);
      }
    }
    boolean goalfound = false;
    usefulItem = new boolean[chart.size()];
    derivedTrees = new ArrayList<>();
    for (ChartItemInterface goal : schema.getGoals()) {
      if (checkForGoal(goal) >= 0) {
        goalfound = true;
      }
    }
    return goalfound;
  }

  /**
   * Prints the trace to the command line. If only the useful items shall be
   * retrieved, it checks all items if they lead to a goal. Returns the printed
   * chart data as string array with columns: Id, Item, Rules, Backpointers.
   */
  public String[][] printTrace() {
    markUsefulItems();
    ArrayList<String[]> chartData = new ArrayList<>();
    int iMaxWidth = 0;
    int chartMaxWidth = 0;
    int appliedRuleMaxWidth = 0;
    for (int i = 0; i < chart.size(); i++) {
      int iWidth = String.valueOf(i).length();
      int chartWidth = chart.get(i).toString().length();
      int appliedRuleWidth = rulesToString(appliedRule.get(i)).length();
      if (iWidth > iMaxWidth) {
        iMaxWidth = iWidth;
      }
      if (chartWidth > chartMaxWidth) {
        chartMaxWidth = chartWidth;
      }
      if (appliedRuleWidth > appliedRuleMaxWidth) {
        appliedRuleMaxWidth = appliedRuleWidth;
      }
    }
    if (successfulTrace) {
      for (int i = 0; i < chart.size(); i++) {
        if (!usefulItem[i]) {
          continue;
        }
        String[] line =
            prettyPrint(i, chart.get(i).toString(), appliedRule.get(i),
                deductedFrom.get(i), iMaxWidth + 3, chartMaxWidth + 3,
                appliedRuleMaxWidth + 3);
        chartData.add(line);
      }
    } else {
      for (int i = 0; i < chart.size(); i++) {
        String[] line =
            prettyPrint(i, chart.get(i).toString(), appliedRule.get(i),
                deductedFrom.get(i), iMaxWidth + 3, chartMaxWidth + 3,
                appliedRuleMaxWidth + 3);
        chartData.add(line);
      }
    }

    return chartData.toArray(new String[chartData.size()][]);
  }

  private void markUsefulItems() {
    if (!successfulTrace) {
      return;
    }
    boolean changed = true;
    while (changed) {
      changed = false;
      for (int i = chart.size() - 1; i >= 0; i--) {
        if (!usefulItem[i]) {
          continue;
        }
        ArrayList<Integer> pointers = getPointersAsArray(deductedFrom.get(i));
        for (int pointer : pointers) {
          if (usefulItem[pointer]) {
            continue;
          }
          usefulItem[pointer] = true;
          changed = true;
        }
      }
    }
  }

  /**
   * Returns the backpointers in this list of lists as plain list.
   */
  private static ArrayList<Integer> getPointersAsArray(
      ArrayList<ArrayList<Integer>> backpointers) {
    ArrayList<Integer> pointerList = new ArrayList<>();
    for (ArrayList<Integer> pointerTuple : backpointers) {
      pointerList.addAll(pointerTuple);
    }
    return pointerList;
  }

  /**
   * Takes a goal item and compares it with all items in the chart. Returns its
   * index if one was found.
   */
  private int checkForGoal(ChartItemInterface goal) {
    for (int i = 0; i < chart.size(); i++) {
      if (chart.get(i).equals(goal)) {
        usefulItem[i] = true;
        List<Tree> trees = chart.get(i).getTrees();
        if (trees != null) {
          switch (replace) {
          case 'h':
            if (pGoal == null) {
              pGoal = ((ProbabilisticChartItemInterface) chart.get(i))
                  .getProbability();
              derivedTrees = trees;
            } else {
              Double newP = ((ProbabilisticChartItemInterface) chart.get(i))
                  .getProbability();
              if (newP > pGoal) {
                pGoal = newP;
                derivedTrees = trees;
              }
            }
            break;
          case 'l':
            if (pGoal == null) {
              pGoal = ((ProbabilisticChartItemInterface) chart.get(i))
                  .getProbability();
              derivedTrees = trees;
            } else {
              Double newP = ((ProbabilisticChartItemInterface) chart.get(i))
                  .getProbability();
              if (newP < pGoal) {
                pGoal = newP;
                derivedTrees = trees;
              }
            }
            break;
          default:
            derivedTrees.addAll(trees);
          }
        }
        return i;
      }
    }
    return -1;
  }

  /**
   * Applies an axiom rule, that is a rule without antecedence items and adds
   * the consequence items to chart and agenda.
   */
  @SuppressWarnings("serial") private void applyAxiomRule(
      StaticDeductionRule rule) {
    for (ChartItemInterface item : rule.consequences) {
      if (chart.contains(item)) {
        continue;
      }
      chart.add(item);
      agenda.add(item);
      deductedFrom.add(new ArrayList<ArrayList<Integer>>() {
        {
          add(new ArrayList<>());
        }
      });
      appliedRule.add(new ArrayList<String>() {
        {
          add(rule.getName());
        }
      });
    }
  }

  /**
   * Tries to apply a deduction rule by using the passed item as one of the
   * antecendence items. Looks through the chart to find the other needed items
   * and adds new consequence items to chart and agenda if all antecedences were
   * found.
   */
  private void applyRule(ChartItemInterface item,
      DynamicDeductionRuleInterface rule) {
    int itemsNeeded = rule.getAntecedencesNeeded();
    if (chart.size() < itemsNeeded) {
      return;
    }
    List<List<ChartItemInterface>> startList = new ArrayList<>();
    startList.add(new ArrayList<>());
    startList.get(0).add(item);
    for (List<ChartItemInterface> tryAntecedences : antecedenceListGenerator(
        startList, 0, itemsNeeded - 1)) {
      rule.clearItems();
      rule.setAntecedences(tryAntecedences);
      List<ChartItemInterface> newItems = rule.getConsequences();
      if (newItems.size() > 0) {
        processNewItems(newItems, rule);
      }
    }
  }

  /**
   * Returns itemsNeeded items from the chart. All items appear only once per
   * list, no list is the permutation of another one.
   */
  private List<List<ChartItemInterface>> antecedenceListGenerator(
      List<List<ChartItemInterface>> oldList, int i, int itemsNeeded) {
    if (itemsNeeded == 0) {
      return oldList;
    }
    List<List<ChartItemInterface>> finalList = new ArrayList<>();
    for (int j = i; j <= chart.size() - itemsNeeded; j++) {
      if (!chart.get(j).equals(oldList.get(0).get(0))) {
        List<List<ChartItemInterface>> newList = new ArrayList<>();
        for (List<ChartItemInterface> subList : oldList) {
          newList.add(new ArrayList<>());
          newList.get(newList.size() - 1).addAll(subList);
          newList.get(newList.size() - 1).add(chart.get(j));
        }
        finalList
            .addAll(antecedenceListGenerator(newList, j + 1, itemsNeeded - 1));
      }
    }
    return finalList;
  }

  /**
   * Adds new items to chart and agenda if they are not in the chart yet.
   */
  private void processNewItems(List<ChartItemInterface> newItems,
      DynamicDeductionRuleInterface rule) {
    ArrayList<Integer> newItemsDeductedFrom = new ArrayList<>();
    for (ChartItemInterface itemToCheck : rule.getAntecedences()) {
      newItemsDeductedFrom.add(chart.indexOf(itemToCheck));
    }
    Collections.sort(newItemsDeductedFrom);
    for (ChartItemInterface newItem : newItems) {
      if (chart.contains(newItem)) {
        int oldId = chart.indexOf(newItem);
        switch (replace) {
        case '-':
          if (!deductedFrom.get(oldId).contains(newItemsDeductedFrom)) {
            appliedRule.get(oldId).add(rule.getName());
            deductedFrom.get(oldId).add(newItemsDeductedFrom);
            chart.get(oldId).getTrees().addAll(newItem.getTrees());
          }
          break;
        case 'h':
          Double oldValue = ((ProbabilisticChartItemInterface) chart.get(oldId))
              .getProbability();
          Double newValue =
              ((ProbabilisticChartItemInterface) newItem).getProbability();
          if (newValue > oldValue) {
            chart.set(oldId, newItem);
            appliedRule.get(oldId).set(0, rule.getName());
            deductedFrom.get(oldId).set(0, newItemsDeductedFrom);
          }
          break;
        case 'l':
          oldValue = ((ProbabilisticChartItemInterface) chart.get(oldId))
              .getProbability();
          newValue =
              ((ProbabilisticChartItemInterface) newItem).getProbability();
          if (newValue < oldValue) {
            chart.set(oldId, newItem);
            appliedRule.get(oldId).set(0, rule.getName());
            deductedFrom.get(oldId).set(0, newItemsDeductedFrom);
          }
          break;
        default:
          log.info("Unknown replace parameter " + replace + ", doing nothing.");
        }
      } else {
        chart.add(newItem);
        agenda.add(newItem);
        appliedRule.add(new ArrayList<>());
        appliedRule.get(appliedRule.size() - 1).add(rule.getName());
        deductedFrom.add(new ArrayList<>());
        deductedFrom.get(deductedFrom.size() - 1).add(newItemsDeductedFrom);
      }
    }
  }

  /**
   * Pretty-prints rows of the parsing process by filling up all columns up to a
   * specific length with spaces. Returns the data it prints as string array.
   */
  private static String[] prettyPrint(int i, String item,
      ArrayList<String> rules, ArrayList<ArrayList<Integer>> backpointers,
      int column1, int column2, int column3) {
    StringBuilder line = new StringBuilder();
    line.append(String.valueOf(i + 1));
    for (int i1 = 0; i1 < column1 - String.valueOf(i + 1).length(); i1++) {
      line.append(" ");
    }
    line.append(item);
    for (int i1 = 0; i1 < column2 - String.valueOf(item).length(); i1++) {
      line.append(" ");
    }
    String rulesRep = rulesToString(rules);
    line.append(rulesRep);
    for (int i1 = 0; i1 < column3 - String.valueOf(rulesRep).length(); i1++) {
      line.append(" ");
    }
    String backpointersRep = backpointersToString(backpointers);
    line.append(backpointersRep);
    log.info(line.toString());
    return new String[] {String.valueOf(i + 1), item, rulesRep,
        backpointersRep};
  }

  /**
   * Returns a string representation of a list of rules in a human friendly
   * form.
   */
  private static String rulesToString(ArrayList<String> rules) {
    if (rules.size() == 0)
      return "";
    StringBuilder builder = new StringBuilder();
    for (String rule : rules) {
      if (builder.length() > 0)
        builder.append(", ");
      builder.append(rule);
    }
    return builder.toString();
  }

  /**
   * Returns a string representation of a list of lists of backpointers in a
   * human friendly form.
   */
  private static String backpointersToString(
      ArrayList<ArrayList<Integer>> backpointers) {
    if (backpointers.size() == 0)
      return "";
    StringBuilder builder = new StringBuilder();
    for (ArrayList<Integer> pointertuple : backpointers) {
      if (builder.length() > 0)
        builder.append(", ");
      builder.append("{");
      for (int i = 0; i < pointertuple.size(); i++) {
        if (i > 0)
          builder.append(", ");
        builder.append(String.valueOf(pointertuple.get(i) + 1));
      }
      builder.append("}");
    }
    return builder.toString();
  }

  public void setReplace(char replace) {
    this.replace = replace;
  }

  public List<Tree> getDerivedTrees() {
    return this.derivedTrees;
  }
}

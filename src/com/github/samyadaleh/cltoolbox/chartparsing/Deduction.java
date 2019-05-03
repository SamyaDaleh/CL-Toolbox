package com.github.samyadaleh.cltoolbox.chartparsing;

import com.github.samyadaleh.cltoolbox.chartparsing.dynamicdeductionrule.DynamicDeductionRuleInterface;
import com.github.samyadaleh.cltoolbox.chartparsing.item.ChartItemInterface;
import com.github.samyadaleh.cltoolbox.chartparsing.item.ProbabilisticChartItemInterface;
import com.github.samyadaleh.cltoolbox.common.tag.Tree;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.ParseException;
import java.util.*;

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
  private List<List<List<Integer>>> deductedFrom;

  /**
   * Indexes correspond to entries of chart and deductedfrom. Collects the names
   * of the rules that were applied to retrieve new items.
   */
  private List<List<String>> appliedRule;
  /**
   * When true print only items that lead to a goal.
   */
  private boolean successfulTrace = false;

  /**
   * Markers if items lead to goal
   */
  private boolean[] usefulItem;

  /**
   * Markers if items are goal items.
   */
  private boolean[] goalItem;
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
  /**
   * Rules might get reapplied if trees of subsequent items change. Make it
   * global, so I don't have to pass them 10 steps down.
   */
  private Set<DynamicDeductionRuleInterface> deductionRules;
  private static final Logger log = LogManager.getLogger();

  /**
   * Takes a parsing schema, generates items from axiom rules and applies rules
   * to the items until all items were used. Returns true if a goal item was
   * derived.
   */
  public boolean doParse(ParsingSchema schema, boolean success)
      throws ParseException {
    return doParse(schema, success, Integer.MAX_VALUE);
  }

  /**
   * Takes a parsing schema, generates items from axiom rules and applies rules
   * to the items until all items were used. Returns true if a goal item was
   * derived. maxItemCount can be used to restrict the deduction process and is
   * intended for services to prevent excessive use.
   */
  public boolean doParse(ParsingSchema schema, boolean success,
      int maxItemCount) throws ParseException {
    successfulTrace = success;
    chart = new ArrayList<>();
    agenda = new ArrayList<>();
    deductedFrom = new ArrayList<>();
    appliedRule = new ArrayList<>();
    deductionRules = schema.getRules();
    if (schema == null)
      return false;
    for (StaticDeductionRule rule : schema.getAxioms()) {
      applyAxiomRule(rule);
    }
    while (!agenda.isEmpty()) {
      if (chart.size() > maxItemCount) {
        throw new ParseException("Chart size exceeds limit of " + maxItemCount
            + " items. Deduction process was stopped.", 1);
      }
      ChartItemInterface item = agenda.get(0);
      agenda.remove(0);
      for (DynamicDeductionRuleInterface rule : deductionRules) {
        applyRule(rule, item, null);
      }
    }
    boolean goalfound = false;
    usefulItem = new boolean[chart.size()];
    goalItem = new boolean[chart.size()];
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
        List<Integer> pointers = getPointersAsArray(deductedFrom.get(i));
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
  private static List<Integer> getPointersAsArray(
      List<List<Integer>> backpointers) {
    List<Integer> pointerList = new ArrayList<>();
    for (List<Integer> pointerTuple : backpointers) {
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
        goalItem[i] = true;
        List<Tree> trees = chart.get(i).getTrees();
        if (trees != null) {
          if (replace == 'h' || replace == 'l') {
            if (pGoal == null) {
              pGoal = ((ProbabilisticChartItemInterface) chart.get(i))
                  .getProbability();
              derivedTrees = trees;
            } else {
              Double newP = ((ProbabilisticChartItemInterface) chart.get(i))
                  .getProbability();
              if ((newP > pGoal && replace == 'h') || (newP < pGoal
                  && replace == 'l')) {
                pGoal = newP;
                derivedTrees = trees;
              }
            }
          } else {
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
      deductedFrom.add(new ArrayList<List<Integer>>() {
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
  private void applyRule(DynamicDeductionRuleInterface rule,
      ChartItemInterface item, ChartItemInterface triggerItem) {
    int itemsNeeded = rule.getAntecedencesNeeded();
    if (chart.size() < itemsNeeded) {
      return;
    }
    List<List<ChartItemInterface>> startList = new ArrayList<>();
    startList.add(new ArrayList<>());
    startList.get(0).add(item);
    for (List<ChartItemInterface> tryAntecedences : antecedenceListGenerator(
        startList, 0, itemsNeeded - 1)) {
      applyRule(rule, tryAntecedences, triggerItem);
    }
  }

  private void applyRule(DynamicDeductionRuleInterface rule,
      List<ChartItemInterface> antecedences, ChartItemInterface triggerItem) {
    rule.clearItems();
    rule.setAntecedences(antecedences);
    List<ChartItemInterface> newItems = rule.getConsequences();
    if (newItems.size() > 0) {
      processNewItems(newItems, rule, triggerItem);
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
      DynamicDeductionRuleInterface rule, ChartItemInterface triggerItem) {
    ArrayList<Integer> newItemsDeductedFrom = new ArrayList<>();
    for (ChartItemInterface itemToCheck : rule.getAntecedences()) {
      newItemsDeductedFrom.add(chart.indexOf(itemToCheck));
    }
    Collections.sort(newItemsDeductedFrom);
    if (newItems.contains(triggerItem)) {
      log.info("Stopped tree update, because " + triggerItem
          + " triggered an update on itself.");
      return;
    }
    for (ChartItemInterface newItem : newItems) {
      if (chart.contains(newItem)) {
        int oldId = chart.indexOf(newItem);
        List<Tree> oldTrees = new LinkedList<>(chart.get(oldId).getTrees());
        switch (replace) {
        case '-':
          if (!deductedFrom.get(oldId).contains(newItemsDeductedFrom)) {
            appliedRule.get(oldId).add(rule.getName());
            deductedFrom.get(oldId).add(newItemsDeductedFrom);
          }
          addNewTrees(chart.get(oldId).getTrees(), newItem.getTrees());
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
        List<Tree> newTrees = chart.get(oldId).getTrees();
        if (!equals(oldTrees, newTrees)) {
          if (triggerItem == null) {
            triggerTreeUpdate(oldId, newItem);
          } else {
            triggerTreeUpdate(oldId, triggerItem);
          }
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

  private void triggerTreeUpdate(int oldId, ChartItemInterface triggerItem) {
    for (int i = 0; i < deductedFrom.size(); i++) {
      List<List<Integer>> line = deductedFrom.get(i);
      for (int j = 0; j < line.size(); j++) {
        List<Integer> backPointerSet = line.get(j);
        for (int backPointer : backPointerSet) {
          if (oldId == backPointer) {
            List<ChartItemInterface> backPointerItems = new ArrayList<>();
            for (int bPointer : backPointerSet) {
              backPointerItems.add(chart.get(bPointer));
            }
            String usedRule = appliedRule.get(i).get(j);
            applyRule(usedRule, backPointerItems, triggerItem);
          }
        }
      }
    }
  }

  /**
   * Returns true if both lists contain the exact same trees.
   */
  private boolean equals(List<Tree> oldTrees, List<Tree> newTrees) {
    Set<Tree> oldSet = new HashSet<>(oldTrees);
    Set<Tree> newSet = new HashSet<>(newTrees);
    return oldSet.equals(newSet);
  }

  private void applyRule(String usedRule,
      List<ChartItemInterface> backPointerItems,
      ChartItemInterface triggerItem) {
    String ruleName = usedRule.split(" ")[0];
    for (DynamicDeductionRuleInterface rule : deductionRules) {
      String checkRuleName = rule.getName().split(" ")[0];
      if (ruleName.equals(checkRuleName)) {
        applyRule(rule, backPointerItems, triggerItem);
      }
    }
  }

  private void addNewTrees(List<Tree> treesOld, List<Tree> treesNew) {
    for (Tree tree1 : treesNew) {
      boolean found = false;
      for (Tree tree2 : treesOld) {
        if (tree1.equals(tree2)) {
          found = true;
          break;
        }
      }
      if (!found) {
        treesOld.add(tree1);
      }
    }
  }

  /**
   * Pretty-prints rows of the parsing process by filling up all columns up to a
   * specific length with spaces. Returns the data it prints as string array.
   */
  private static String[] prettyPrint(int i, String item, List<String> rules,
      List<List<Integer>> backpointers, int column1, int column2, int column3) {
    StringBuilder line = new StringBuilder();
    line.append((i + 1));
    for (int i1 = 0; i1 < column1 - String.valueOf(i + 1).length(); i1++) {
      line.append(" ");
    }
    line.append(item);
    for (int i1 = 0; i1 < column2 - String.valueOf(item).length(); i1++) {
      line.append(" ");
    }
    String rulesRep = rulesToString(rules);
    line.append(rulesRep);
    for (int i1 = 0; i1 < column3 - rulesRep.length(); i1++) {
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
  private static String rulesToString(List<String> rules) {
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
  private static String backpointersToString(List<List<Integer>> backpointers) {
    if (backpointers.size() == 0)
      return "";
    StringBuilder builder = new StringBuilder();
    for (List<Integer> pointertuple : backpointers) {
      if (builder.length() > 0)
        builder.append(", ");
      builder.append("{");
      for (int i = 0; i < pointertuple.size(); i++) {
        if (i > 0)
          builder.append(", ");
        builder.append((pointertuple.get(i) + 1));
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

  public List<ChartItemInterface> getChart() {
    return chart;
  }

  public List<List<List<Integer>>> getDeductedFrom() {
    return deductedFrom;
  }

  public List<List<String>> getAppliedRule() {
    return appliedRule;
  }

  public boolean[] getUsefulItem() {
    return usefulItem;
  }

  public boolean[] getGoalItem() {
    return goalItem;
  }
}

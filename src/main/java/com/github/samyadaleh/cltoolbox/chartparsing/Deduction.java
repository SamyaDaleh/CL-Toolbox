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
  /**
   * The highest amount of items the agenda contains at one point.
   */
  private int maxAgendaSize = 0;
  /**
   * Count how many tree updates were initially triggered.
   */
  private int treeUpdatesRoot = 0;
  /**
   * Count how many overall tree updates were triggered.
   */
  private int treeUpdatesAllLevels = 0;
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
    maxAgendaSize = 0;
    treeUpdatesRoot = 0;
    treeUpdatesAllLevels = 0;
    if (schema == null)
      return false;
    for (StaticDeductionRule rule : schema.getAxioms()) {
      applyAxiomRule(rule);
    }
    while (!agenda.isEmpty()) {
      if (agenda.size() > maxAgendaSize) {
        maxAgendaSize = agenda.size();
      }
      if (chart.size() > maxItemCount) {
        throw new ParseException("Chart size exceeds limit of " + maxItemCount
            + " items. Deduction process was stopped.", 1);
      }
      ChartItemInterface item = agenda.get(0);
      agenda.remove(0);
      for (DynamicDeductionRuleInterface rule : deductionRules) {
        applyRule(rule, item, new ArrayList<>());
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
    markUsefulItems();
    return goalfound;
  }

  /**
   * Prints the trace to the command line.
   */
  public void printTrace(String[][] trace) {
    int iMaxWidth = 0;
    int chartMaxWidth = 0;
    int appliedRuleMaxWidth = 0;
    for (String[] line : trace) {
      int iWidth = line[0].length();
      int chartWidth = line[1].length();
      int appliedRuleWidth = line[2].length();
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
    for (String[] line : trace) {
      prettyPrintLine(line, iMaxWidth + 3, chartMaxWidth + 3,
          appliedRuleMaxWidth + 3);
    }
  }

  /**
   * Prints the trace to the command line in a tex format.
   */
  public void printTraceLatex(String[][] trace) {
    log.info("\\begin{tabular}{l l l l}");
    log.info("Id & Item & Rules & BackPointers \\\\");
    log.info("\\hline");
    for (String[] line : trace) {
      String id = line[0];
      String item = line[1].replace("$", "\\$").replace("•", "\\textbullet{}")
          .replace("ε", "$\\epsilon$").replace("->", "$\\rightarrow$");
      String rules =
          line[2].replace("ε", "$\\epsilon$").replace("->", "$\\rightarrow$");
      String backPointers = line[3].replace("{", "\\{").replace("}", "\\}");
      log.info(
          id + " & " + item + " & " + rules + " & " + backPointers + " \\\\");
    }
    log.info("\\end{tabular}");
  }

  /**
   * Returns the printed chart data as string array with columns: Id, Item,
   * Rules, Backpointers.
   */
  public String[][] getTraceTable() {
    ArrayList<String[]> chartData = new ArrayList<>();
    if (successfulTrace) {
      for (int i = 0; i < chart.size(); i++) {
        if (!usefulItem[i]) {
          continue;
        }
        String[] line =
            getLineData(i, chart.get(i).toString(), appliedRule.get(i),
                deductedFrom.get(i));
        chartData.add(line);
      }
    } else {
      for (int i = 0; i < chart.size(); i++) {
        String[] line =
            getLineData(i, chart.get(i).toString(), appliedRule.get(i),
                deductedFrom.get(i));
        chartData.add(line);
      }
    }
    return chartData.toArray(new String[chartData.size()][]);
  }

  private void markUsefulItems() {
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
      ChartItemInterface item, List<ChartItemInterface> triggerItems) {
    int itemsNeeded = rule.getAntecedencesNeeded();
    if (chart.size() < itemsNeeded) {
      return;
    }
    List<List<ChartItemInterface>> startList = new ArrayList<>();
    startList.add(new ArrayList<>());
    startList.get(0).add(item);
    for (List<ChartItemInterface> tryAntecedences : antecedenceListGenerator(
        startList, 0, itemsNeeded - 1)) {
      applyRule(rule, tryAntecedences, triggerItems);
    }
  }

  private void applyRule(DynamicDeductionRuleInterface rule,
      List<ChartItemInterface> antecedences, List<ChartItemInterface> triggerItems) {
    rule.clearItems();
    rule.setAntecedences(antecedences);
    List<ChartItemInterface> newItems = rule.getConsequences();
    if (newItems.size() > 0) {
      processNewItems(newItems, rule, triggerItems);
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
      DynamicDeductionRuleInterface rule, List<ChartItemInterface> triggerItems) {
    ArrayList<Integer> newItemsDeductedFrom = new ArrayList<>();
    for (ChartItemInterface itemToCheck : rule.getAntecedences()) {
      newItemsDeductedFrom.add(chart.indexOf(itemToCheck));
    }
    Collections.sort(newItemsDeductedFrom);
    if (triggerItems.containsAll(newItems)) {
      log.info("Stopped tree update, because all items triggered an update on "
          + "themselves.");
      return;
    }
    for (ChartItemInterface newItem : newItems) {
      if (triggerItems.contains(newItem)) {
        log.info("Stopped tree update, because all items triggered an update on "
            + "themselves.");
      }
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
          ArrayList<ChartItemInterface> newTriggerItems =
              new ArrayList<>(triggerItems);
          if (triggerItems.size() == 0) {
            treeUpdatesRoot++;
          }
          treeUpdatesAllLevels++;
          newTriggerItems.add(newItem);
          triggerTreeUpdate(oldId, newTriggerItems);
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

  private void triggerTreeUpdate(int oldId,
      List<ChartItemInterface> triggerItems) {
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
            applyRule(usedRule, backPointerItems, triggerItems);
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
      List<ChartItemInterface> triggerItems) {
    String ruleName = usedRule.split(" ")[0];
    for (DynamicDeductionRuleInterface rule : deductionRules) {
      String checkRuleName = rule.getName().split(" ")[0];
      if (ruleName.equals(checkRuleName)) {
        applyRule(rule, backPointerItems, triggerItems);
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
   * specific length with spaces..
   */
  private static void prettyPrintLine(String[] chartLine, int column1,
      int column2, int column3) {
    StringBuilder line = new StringBuilder();
    line.append(chartLine[0]);
    for (int i1 = 0; i1 < column1 - chartLine[0].length(); i1++) {
      line.append(" ");
    }
    String item = chartLine[1];
    line.append(item);
    for (int i1 = 0; i1 < column2 - String.valueOf(item).length(); i1++) {
      line.append(" ");
    }
    String rulesRep = chartLine[2];
    line.append(rulesRep);
    for (int i1 = 0; i1 < column3 - rulesRep.length(); i1++) {
      line.append(" ");
    }
    line.append(chartLine[3]);
    log.info(line.toString());
  }

  /**
   * Returns the data as string array.
   */
  private static String[] getLineData(int i, String item, List<String> rules,
      List<List<Integer>> backpointers) {
    String rulesRep = rulesToString(rules);
    String backpointersRep = backpointersToString(backpointers);
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

  /**
   * Prints the graph of computations in a format so that it can be rendered by
   * the latex package tikz-dependency. Visualizes the workings of the
   * algorithms.
   */
  public static String printLatexGraph(String[][] data, String algorithm,
      String w) {
    StringBuilder graph = new StringBuilder();
    graph.append("\\begin{dependency}\n"
        + "   \\begin{deptext}[column sep=1em]\n"
        + "      ");
    int wLength = 0;
    graph.append("0");
    if (!w.equals("")) {
      String[] wSplit = w.split(" ");
      wLength = wSplit.length;
      for (int i = 0; i < wSplit.length; i++) {
        graph.append(" \\& ").append(wSplit[i]).append(" \\& ").append(i + 1);
      }
    }
    graph.append(" \\\\\n").append("   \\end{deptext}\n");
    Map<String, Map<String,StringBuilder>> graphData = new HashMap<>();
    switch (algorithm) {
    case "cfg-topdown":
      generateCfgTopDownComputationGraph(data, wLength, graphData);
      break;
    case "cfg-shiftreduce":
      generateCfgShiftReduceComputationGraph(data, graphData);
      break;
    case "cfg-cyk":
    case "cfg-leftcorner-chart":
      generateCfgCykComputationGraph(data, graphData);
      break;
    case "cfg-earley":
      generateCfgEarleyComputationGraph(data, graphData);
      break;
    default:
      log.info("I don't know how to generate computation graphs for algorithm "
          + algorithm + ".");
      return null;
    }
    for (Map.Entry<String, Map<String, StringBuilder>> entryA : graphData
        .entrySet()) {
      for (Map.Entry<String, StringBuilder> entryB : entryA.getValue()
          .entrySet()) {
        graph.append("   \\depedge");
        if (entryA.getKey().equals(entryB.getKey())) {
          graph.append("[edge height=3ex]");
        }
        graph.append("{").append(entryA.getKey()).append("}{")
            .append(entryB.getKey()).append("}{")
            .append(entryB.getValue().toString()).append("}\n");
      }
    }
    graph.append("\\end{dependency}");
    return graph.toString();
  }

  private static void generateCfgEarleyComputationGraph(String[][] data,
      Map<String, Map<String, StringBuilder>> graphData) {
    for (String[] datum : data) {
      String itemForm = datum[1];
      int comma1Pos = itemForm.indexOf(",");
      int comma2Pos = itemForm.indexOf(",", comma1Pos + 1);
      String index1 = itemForm.substring(comma1Pos + 1, comma2Pos);
      String index2 =
          itemForm.substring(comma2Pos + 1, itemForm.length() - 1);
      String graphIndex1 = String.valueOf(Integer.parseInt(index1) * 2 + 1);
      String graphIndex2 = String.valueOf(Integer.parseInt(index2) * 2 + 1);
      if (!graphData.containsKey(graphIndex1)) {
        graphData.put(graphIndex1, new HashMap<>());
      }
      Map<String, StringBuilder> subGraphData = graphData.get(graphIndex1);
      if (!subGraphData.containsKey(graphIndex2)) {
        subGraphData.put(graphIndex2, new StringBuilder());
      }
      if (subGraphData.get(graphIndex2).length() > 0) {
        subGraphData.get(graphIndex2).append(", ");
      }
      subGraphData.get(graphIndex2).append(datum[0]);
    }
  }

  private static void generateCfgCykComputationGraph(String[][] data,
      Map<String, Map<String, StringBuilder>> graphData) {
    for (String[] datum : data) {
      String itemForm = datum[1];
      int comma1Pos = itemForm.indexOf(",");
      int comma2Pos = itemForm.indexOf(",", comma1Pos+1);
      String indexStart = itemForm.substring(comma1Pos + 1, comma2Pos);
      String indexLength = itemForm.substring(comma2Pos+1, itemForm.length()-1);
      String lowGraphIndex = String.valueOf(Integer.parseInt(indexStart)*2+1);
      if (!graphData.containsKey(lowGraphIndex)) {
        graphData.put(lowGraphIndex, new HashMap<>());
      }
      Map<String, StringBuilder> subGraphData = graphData.get(lowGraphIndex);
      String highGraphIndex = String.valueOf(
          (Integer.parseInt(indexStart) + Integer.parseInt(indexLength))*2+1);
      if (!subGraphData.containsKey(highGraphIndex)) {
        subGraphData.put(highGraphIndex, new StringBuilder());
      }
      if (subGraphData.get(highGraphIndex).length() > 0) {
        subGraphData.get(highGraphIndex).append(", ");
      }
      subGraphData.get(highGraphIndex).append(datum[0]);
    }
  }

  private static void generateCfgShiftReduceComputationGraph(String[][] data,
      Map<String, Map<String, StringBuilder>> graphData) {
    String minGraphIndex = "1";
    for (String[] datum : data) {
      String itemForm = datum[1];
      int commaPos = itemForm.indexOf(",");
      String index = itemForm.substring(commaPos + 1, itemForm.length() - 1);
      String graphIndex = String.valueOf(Integer.parseInt(index) * 2 + 1);
      if (!graphData.containsKey(minGraphIndex)) {
        graphData.put(minGraphIndex, new HashMap<>());
      }
      Map<String, StringBuilder> subGraphData = graphData.get(minGraphIndex);
      if (!subGraphData.containsKey(graphIndex)) {
        subGraphData.put(graphIndex, new StringBuilder());
      }
      if (subGraphData.get(graphIndex).length() > 0) {
        subGraphData.get(graphIndex).append(", ");
      }
      subGraphData.get(graphIndex).append(datum[0]);
    }
  }

  static void generateCfgTopDownComputationGraph(String[][] data,
      int wLength, Map<String, Map<String, StringBuilder>> graphData) {
    String graphMaxIndex = String.valueOf(wLength * 2 + 1);
    for (String[] datum : data) {
      String itemForm = datum[1];
      int commaPos = itemForm.indexOf(",");
      String index = itemForm.substring(commaPos + 1, itemForm.length() - 1);
      String graphIndex = String.valueOf(Integer.parseInt(index) * 2 + 1);
      if (!graphData.containsKey(graphIndex)) {
        graphData.put(graphIndex, new HashMap<>());
      }
      Map<String, StringBuilder> subGraphData = graphData.get(graphIndex);
      if (!subGraphData.containsKey(graphMaxIndex)) {
        subGraphData.put(graphMaxIndex, new StringBuilder());
      }
      if (subGraphData.get(graphMaxIndex).length() > 0) {
        subGraphData.get(graphMaxIndex).append(", ");
      }
      subGraphData.get(graphMaxIndex).append(datum[0]);
    }
  }

  public void setReplace(char replace) {
    this.replace = replace;
  }

  public List<Tree> getDerivedTrees() {
    Set<Tree> treeSet = new HashSet<>(this.derivedTrees);
    return new ArrayList<>(treeSet);
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

  public int getMaxAgendaSize() {
    return maxAgendaSize;
  }

  public void setMaxAgendaSize(int maxAgendaSize) {
    this.maxAgendaSize = maxAgendaSize;
  }

  public int getTreeUpdatesRoot() {
    return treeUpdatesRoot;
  }

  public void setTreeUpdatesRoot(int treeUpdatesRoot) {
    this.treeUpdatesRoot = treeUpdatesRoot;
  }

  public int getTreeUpdatesAllLevels() {
    return treeUpdatesAllLevels;
  }

  public void setTreeUpdatesAllLevels(int treeUpdatesAllLevels) {
    this.treeUpdatesAllLevels = treeUpdatesAllLevels;
  }
}

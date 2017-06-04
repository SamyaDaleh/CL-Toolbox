package chartparsing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import common.Item;

/** A deduction system that derives consequences from antecendence items and
 * tries to generate a goal item. Based on the slides from Laura Kallmeyer about
 * Parsing as Deduction
 * https://user.phil.hhu.de/~kallmeyer/Parsing/deduction.pdf */
public class Deduction {
  /** All items derived in the process. */
  private List<Item> chart;
  /** Items waiting to be used for further derivation. */
  private List<Item> agenda;
  /** List of the same length of chart, elements at same indexes belong to each
   * other. Contains lists of lists of backpointers. One item can be derived in
   * different ways from different antecedence items. */
  private ArrayList<ArrayList<ArrayList<Integer>>> deductedfrom;
  /** Indexes correspond to entries of chart and deductedfrom. Collects the
   * names of the rules that were applied to retrieve new items. */
  private ArrayList<ArrayList<String>> appliedRule;
  /** When true print only items that lead to a goal. */
  private boolean successfultrace = false;
  /** Markers if items lead to goal */
  private boolean[] usefulitem;

  /** Takes a parsing schema, generates items from axiom rules and applies rules
   * to the items until all items were used. Returns true if a goal item was
   * derived. */
  public boolean doParse(ParsingSchema schema, boolean success) {
    successfultrace = success;
    chart = new LinkedList<Item>();
    agenda = new LinkedList<Item>();
    deductedfrom = new ArrayList<ArrayList<ArrayList<Integer>>>();
    appliedRule = new ArrayList<ArrayList<String>>();
    if (schema == null)
      return false;
    for (StaticDeductionRule rule : schema.getAxioms()) {
      applyAxiomRule(rule);
    }
    while (!agenda.isEmpty()) {
      Item item = agenda.get(0);
      agenda.remove(0);
      for (DynamicDeductionRule rule : schema.getRules()) {
        applyRule(item, rule);
      }
    }
    boolean goalfound = false;
    usefulitem = new boolean[chart.size()];
    for (Item goal : schema.getGoals()) {
      if (checkForGoal(goal) >= 0)
        goalfound = true;
    }
    return goalfound;
  }

  /** Prints the trace to the command line. If only the useful items shall be
   * retrieved, it checks all items if they lead to a goal. 
   * Returns the printed chart data as string array with columns: Id, Item, Rules, Backpointers. */
  public String[][] printTrace() {
    if (successfultrace) {
      boolean changed = true;
      while (changed) {
        changed = false;
        for (int i = chart.size() - 1; i >= 0; i--) {
          if (usefulitem[i]) {
            ArrayList<Integer> pointers =
              getPointersAsArray(deductedfrom.get(i));
            for (int pointer : pointers) {
              if (!usefulitem[pointer]) {
                usefulitem[pointer] = true;
                changed = true;
              }
            }
          }
        }
      }
    }
    ArrayList<String[]> chartdata = new ArrayList<String[]>();
    if (successfultrace) {
      for (int i = 0; i < chart.size(); i++) {
        if (usefulitem[i]) {
          String[] line = prettyprint(i, chart.get(i).toString(),
            appliedRule.get(i), deductedfrom.get(i));
          chartdata.add(line);
        }
      }
    } else {
      for (int i = 0; i < chart.size(); i++) {
        String[] line = prettyprint(i, chart.get(i).toString(),
          appliedRule.get(i), deductedfrom.get(i));
        chartdata.add(line);
      }
    }
   
     return chartdata.toArray(new String[chartdata.size()][]);
  }
  

  /** Returns the backpointers in this list of lists as plain list. */
  private static ArrayList<Integer>
    getPointersAsArray(ArrayList<ArrayList<Integer>> backpointers) {
    ArrayList<Integer> pointerlist = new ArrayList<Integer>();
    for (ArrayList<Integer> pointertuple : backpointers) {
      pointerlist.addAll(pointertuple);
    }
    return pointerlist;
  }

  /** Takes a goal item and compares it with all items in the chart. Returns its
   * index if one was found. */
  private int checkForGoal(Item goal) {
    for (int i = 0; i < chart.size(); i++) {
      if (chart.get(i).equals(goal)) {
        usefulitem[i] = true;
        return i;
      }
    }
    return -1;
  }

  /** Applies an axiom rule, that is a rule without antecedence items and adds
   * the consequence items to chart and agenda. */
  private void applyAxiomRule(StaticDeductionRule rule) {
    for (Item item : rule.consequences) {
      if (!chart.contains(item)) {
        chart.add(item);
        agenda.add(item);
        deductedfrom.add(new ArrayList<ArrayList<Integer>>());
        deductedfrom.get(deductedfrom.size() - 1).add(new ArrayList<Integer>());
        appliedRule.add(new ArrayList<String>());
        appliedRule.get(appliedRule.size() - 1).add(rule.getName());
      }
    }
  }

  /** Tries to apply a deduction rule by using the passed item as one of the
   * antecendence items. Looks through the chart to find the other needed items
   * and adds new consequence items to chart and agenda if all antecedences were
   * found. */
  private void applyRule(Item item, DynamicDeductionRule rule) {
    int itemsneeded = rule.getAntecedencesNeeded();
    rule.clearItems();
    // TODO how can I make the depth dynamic?
    // By a recursive call to a function that counts down the number of items
    // it shall append to a list
    List<Item> newitems;
    if (itemsneeded == 1) {
      rule.addAntecedence(item);
      newitems = rule.getConsequences();
      if (newitems.size() > 0) {
        processNewItems(newitems, rule);
      }
    } else if (itemsneeded == 2) {
      for (int i = 0; i < chart.size(); i++) {
        rule.clearItems();
        rule.addAntecedence(item);
        rule.addAntecedence(chart.get(i));
        newitems = rule.getConsequences();
        if (newitems.size() > 0) {
          processNewItems(newitems, rule);
        }
      }
    } else if (itemsneeded == 3) {
      for (int i = 0; i < chart.size(); i++) {
        for (int j = 0; j < chart.size(); j++) {
          rule.clearItems();
          rule.addAntecedence(item);
          rule.addAntecedence(chart.get(i));
          rule.addAntecedence(chart.get(j));
          newitems = rule.getConsequences();
          if (newitems.size() > 0) {
            processNewItems(newitems, rule);
          }
        }
      }
    }
  }

  private void processNewItems(List<Item> newitems,
    DynamicDeductionRule rule) {

    ArrayList<Integer> newitemsdeductedfrom = new ArrayList<Integer>();
    for (Item itemtocheck : rule.getAntecedences()) {
      newitemsdeductedfrom.add(chart.indexOf(itemtocheck));
    }
    Collections.sort(newitemsdeductedfrom);
    for (Item newitem : newitems) {
      if (!chart.contains(newitem)) {
        chart.add(newitem);
        agenda.add(newitem);
        appliedRule.add(new ArrayList<String>());
        appliedRule.get(appliedRule.size() - 1).add(rule.getName());
        deductedfrom.add(new ArrayList<ArrayList<Integer>>());
        deductedfrom.get(deductedfrom.size() - 1).add(newitemsdeductedfrom);
      } else {
        // same set of backpointers must not exist yet.
        // backpointers are always in the same order, that's why this works.
        int oldid = chart.indexOf(newitem);
        if (!deductedfrom.get(oldid).contains(newitemsdeductedfrom)) {
          appliedRule.get(oldid).add(rule.getName());
          deductedfrom.get(oldid).add(newitemsdeductedfrom);
        }
      }
    }
  }

  private static final int column1 = 5;
  private static final int column2 = 25;
  private static final int column3 = 20;

  /** Pretty-prints rows of the parsing process by filling up all columns up to
   * a specific length with spaces. Returns the data it prints as string
   * array. */
  private static String[] prettyprint(int i, String item,
    ArrayList<String> rules, ArrayList<ArrayList<Integer>> backpointers) {
    StringBuilder line = new StringBuilder();
    line.append(String.valueOf(i + 1));
    for (int i1 = 0; i1 < column1 - String.valueOf(i + 1).length(); i1++) {
      line.append(" ");
    }
    line.append(item);
    for (int i1 = 0; i1 < column2 - String.valueOf(item).length(); i1++) {
      line.append(" ");
    }
    String rulesrep = rulesToString(rules);
    line.append(rulesrep);
    for (int i1 = 0; i1 < column3 - String.valueOf(rulesrep).length(); i1++) {
      line.append(" ");
    }
    String backpointersrep = backpointersToString(backpointers);
    line.append(backpointersrep);
    System.out.println(line.toString());
    return new String[] {String.valueOf(i + 1), item, rulesrep,
      backpointersrep};
  }

  /** Returns a string representation of a list of rules in a human friendly
   * form. */
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

  /** Returns a string representation of a list of lists of backpointers in a
   * human friendly form. */
  private static String
    backpointersToString(ArrayList<ArrayList<Integer>> backpointers) {
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
  
  public  List<Item> getChart() {
    return this.chart;
  }
  
  public ArrayList<ArrayList<ArrayList<Integer>>> getBackpointers(){
    return this.deductedfrom;
  }
  
  public ArrayList<ArrayList<String>> getAppliedRules(){
    return this.appliedRule;
  }
}

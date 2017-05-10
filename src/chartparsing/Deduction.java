package chartparsing;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import common.Item;
import gui.ParsingTraceTable;

/** A deduction system that derives consequences from antecendence items and
 * tries to generate a goal item. Based on the slides from Laura Kallmeyer about
 * Parsing as Deduction
 * https://user.phil.hhu.de/~kallmeyer/Parsing/deduction.pdf */
public class Deduction {
  /** All items derived in the process. */
  static List<Item> chart;
  /** Items waiting to be used for further derivation. */
  static List<Item> agenda;
  /** List of the same length of chart, elements at same indexes belong to each
   * other. Contains lists of lists of backpointers. One item can be derived in
   * different ways from different antecedence items. */
  static ArrayList<ArrayList<ArrayList<Integer>>> deductedfrom;
  /** Indexes correspond to entries of chart and deductedfrom. Collects the
   * names of the rules that were applied to retrieve new items. */
  static ArrayList<ArrayList<String>> appliedRule;
  /** When true print only items that lead to a goal. */
  static boolean successfultrace = false;
  /** Markers if items lead to goal */
  static boolean[] usefulitem;

  /** Takes a parsing schema, generates items from axiom rules and applies rules
   * to the items until all items were used. Returns true if a goal item was
   * derived. */
  public static boolean doParse(ParsingSchema schema, boolean success) {
    if (schema == null)
      return false;
    successfultrace = success;
    chart = new LinkedList<Item>();
    agenda = new LinkedList<Item>();
    deductedfrom = new ArrayList<ArrayList<ArrayList<Integer>>>();
    appliedRule = new ArrayList<ArrayList<String>>();
    for (DeductionRule rule : schema.getRules()) {
      if (rule.getAntecedences().isEmpty()) {
        applyAxiomRule(rule);
      }
    }
    while (!agenda.isEmpty()) {
      Item item = agenda.get(0);
      agenda.remove(0);
      for (DeductionRule rule : schema.getRules()) {
        if (!rule.getAntecedences().isEmpty()) {
          applyRule(item, rule);
        }
      }
    }
    boolean goalfound = false;
    usefulitem = new boolean[chart.size()];
    for (Item goal : schema.getGoals()) {
      if (checkForGoal(goal) >= 0)
        goalfound = true;
    }
    printTrace();
    return goalfound;
  }

  /** Prints the trace to the command line. If only the useful items shall be
   * retrieved, it checks all items if they lead to a goal. */
  private static void printTrace() {
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
    ParsingTraceTable.displayTrace(
      chartdata.toArray(new String[chartdata.size()][]),
      new String[] {"Id", "Item", "Rules", "Backpointers"});
  }

  /** Returns the backpointers in this list of lists as plain list. */
  private static ArrayList<Integer>
    getPointersAsArray(ArrayList<ArrayList<Integer>> backpointers) {
    ArrayList<Integer> pointerlist = new ArrayList<Integer>();
    for (ArrayList<Integer> pointertuple : backpointers) {
      for (int i = 0; i < pointertuple.size(); i++) {
        pointerlist.add(pointertuple.get(i));
      }
    }
    return pointerlist;
  }

  /** Takes a goal item and compares it with all items in the chart. Returns its
   * index if one was found. */
  private static int checkForGoal(Item goal) {
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
  private static void applyAxiomRule(DeductionRule rule) {
    for (Item item : rule.consequences) {
      if (!chart.contains(item)) {
        chart.add(item);
        agenda.add(item);
        deductedfrom.add(new ArrayList<ArrayList<Integer>>());
        deductedfrom.get(deductedfrom.size() - 1).add(new ArrayList<Integer>());
        appliedRule.add(new ArrayList<String>());
        appliedRule.get(appliedRule.size() - 1).add(rule.getName());
      } else {
        int oldid = chart.indexOf(item);
        appliedRule.get(oldid).add(rule.getName());
        deductedfrom.get(oldid).add(new ArrayList<Integer>());
      }
    }
  }

  /** Tries to apply a deduction rule by using the passed item as one of the
   * antecendence items. Looks through the chart to find the other needed items
   * and adds new consequence items to chart and agenda if all antecedences were
   * found. */
  private static void applyRule(Item item, DeductionRule rule) {
    Set<Item> itemstofind = rule.getAntecedences();
    if (contains(itemstofind, item)) {
      itemstofind.remove(item);
    } else
      return;
    ArrayList<Integer> newitemsdeductedfrom = new ArrayList<Integer>();
    for (Object itemtocheck : itemstofind.toArray()) {
      if (!chart.contains((Item) itemtocheck))
        return;
      newitemsdeductedfrom.add(chart.indexOf(itemtocheck));
    }
    for (Item newitem : rule.consequences) {
      if (!chart.contains(newitem)) {
        chart.add(newitem);
        agenda.add(newitem);
        appliedRule.add(new ArrayList<String>());
        appliedRule.get(appliedRule.size() - 1).add(rule.getName());
        deductedfrom.add(new ArrayList<ArrayList<Integer>>());
        deductedfrom.get(deductedfrom.size() - 1).add(newitemsdeductedfrom);
      } else {
        int oldid = chart.indexOf(newitem);
        appliedRule.get(oldid).add(rule.getName());
        deductedfrom.get(oldid).add(newitemsdeductedfrom);
      }
    }
  }

  /** Returns true if item is an element of the set. Default contains() from Set
   * doesn't work in this case. */
  private static boolean contains(Set<Item> itemstofind, Item item) {
    List<Item> itemslist = new ArrayList<Item>(itemstofind);
    if (itemslist.contains(item))
      return true;
    else
      return false;
  }

  static int column1 = 5;
  static int column2 = 25;
  static int column3 = 20;

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
    return new String[] {String.valueOf(i + 1), item.toString(), rulesrep,
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
}

/* Based on the slides from Laura Kallmeyer about Parsing as Deduction
 * https://user.phil.hhu.de/~kallmeyer/Parsing/deduction.pdf */

package chartparsing;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import common.Item;

public class Deduction {
  static List<Item> chart;
  static List<Item> agenda;
  static List<Integer> deductedfrom;

  public static boolean doParse(ParsingSchema schema) {
    if (schema == null)
      return false;
    chart = new LinkedList<Item>();
    agenda = new LinkedList<Item>();
    deductedfrom = new LinkedList<Integer>();
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
    for (Item goal : schema.getGoals()) {
      if (checkForGoal(goal))
        return true;
    }
    return false;
  }

  private static boolean checkForGoal(Item goal) {
    for (Item item : chart) {
      if (item.equals(goal))
        return true;
    }
    return false;
  }

  private static void applyAxiomRule(DeductionRule rule) {
    for (Item item : rule.consequences) {
      if (!chart.contains(item)) {
        chart.add(item);
        agenda.add(item);
        prettyprint((chart.indexOf(item) + 1),item.toString(),rule.getName(), "");
      }
    }
  }

  private static void applyRule(Item item, DeductionRule rule) {
    Set<Item> itemstofind = rule.getAntecedences();
    StringBuilder derivedfrom = new StringBuilder();
    if (contains(itemstofind, item)) {
      itemstofind.remove(item);
    } else
      return;
    for (Object itemtocheck : itemstofind.toArray()) {
      if (!chart.contains((Item) itemtocheck))
        return;
      if (derivedfrom.length() > 0)
        derivedfrom.append(", ");
      derivedfrom.append(chart.indexOf(itemtocheck) + 1);
    }
    for (Item newitem : rule.consequences) {
      if (!chart.contains(newitem)) {
        chart.add(newitem);
        agenda.add(newitem);
        prettyprint((chart.indexOf(newitem) +1), newitem.toString(), rule.getName(), derivedfrom.toString());
      }
    }
  }

  private static boolean contains(Set<Item> itemstofind, Item item) {
    List<Item> itemslist = new ArrayList<Item>(itemstofind);
    if (itemslist.contains(item))
      return true;
    else
      return false;
  }

  static int column1 = 5;
  static int column2 = 20;
  static int column3 = 20;
  
  private static void prettyprint(int i, String string, String name, String string2) {
    StringBuilder line = new StringBuilder();
    line.append(String.valueOf(i));
    for (int i1 = 0; i1 < column1-String.valueOf(i).length(); i1++) {
      line.append(" ");
    }
    line.append(string);
    for (int i1 = 0; i1 < column2-String.valueOf(string).length(); i1++) {
      line.append(" ");
    }
    line.append(name);
    for (int i1 = 0; i1 < column3-String.valueOf(name).length(); i1++) {
      line.append(" ");
    }
    line.append(string2);
    System.out.println(line.toString());
  }
}

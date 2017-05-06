/*
 * Based on the slides from Laura Kallmeyer about Parsing as Deduction
 * https://user.phil.hhu.de/~kallmeyer/Parsing/deduction.pdf
 */

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
	
	public static boolean doParse(ParsingScheme scheme){	
		chart = new LinkedList<Item>();
		agenda = new LinkedList<Item>();
		deductedfrom = new LinkedList<Integer>();
		for (DeductionRule rule : scheme.getRules()) {
			if (rule.getAntecedences().isEmpty()) {
				applyAxiomRule(rule);
			}
		}
		while (!agenda.isEmpty()) {
			Item item = agenda.get(0);
			agenda.remove(0);
			for (DeductionRule rule : scheme.getRules()) {
				if (!rule.getAntecedences().isEmpty()) {
					applyRule(item, rule);
				}
			}
		}
		for (Item goal : scheme.getGoals()) {
		if (checkForGoal(goal)) return true;
		}
		return false;
	}
  private static boolean checkForGoal(Item goal) {
		for (Item item : chart) {
			if (item.equals(goal)) return true;
	  }
		return false;
	}
private static void applyAxiomRule(DeductionRule rule) {
	  for (Item item : rule.consequences) {
		if (!chart.contains(item)) {
		  chart.add(item);
		  agenda.add(item);
		  System.out.println((chart.indexOf(item)+1) + " | " + item.toString() + " | " + rule.getName() + " | "); //DEBUG
		}
	  }
  }
  private static void applyRule(Item item, DeductionRule rule) {
	  Set<Item> itemstofind = rule.getAntecedences();
	  StringBuilder derivedfrom = new StringBuilder();
	  if (contains(itemstofind,item)) {
		  itemstofind.remove(item);
	  } else return;
	  for (Object itemtocheck : itemstofind.toArray()) {
		  if (!chart.contains((Item) itemtocheck)) return;
		  if (derivedfrom.length() > 0 ) derivedfrom.append(", ");
		  derivedfrom.append(chart.indexOf(itemtocheck)+1);
	  }
	  for (Item newitem : rule.consequences) {
		if (!chart.contains(newitem)) {
		  chart.add(newitem);
		  agenda.add(newitem);
		  System.out.println((chart.indexOf(newitem)+1) + " | " + newitem.toString() + " | " + rule.getName() + " | " + derivedfrom.toString()); //DEBUG
		}
	  }
  }
  
  private static boolean contains(Set<Item> itemstofind, Item item) {
	List<Item> itemslist = new ArrayList<Item>(itemstofind);
	if(itemslist.contains(item)) return true;
	else return false;
}
}

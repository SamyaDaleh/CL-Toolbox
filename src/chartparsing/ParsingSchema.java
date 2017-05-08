package chartparsing;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import common.Item;

/** Representation of a parsing schema, based on slides from Laura Kallmeyer.
 * Consists of a set of deduction rules and goal items that have to be derived
 * with help of the rules. */
public class ParsingSchema {
  Set<DeductionRule> rules = new HashSet<DeductionRule>();
  List<Item> goal = new LinkedList<Item>();

  public void addRule(DeductionRule rule) {
    rules.add(rule);
  }

  public void addGoal(Item item) {
    this.goal.add(item);
  }

  public Set<DeductionRule> getRules() {
    return this.rules;
  }

  public List<Item> getGoals() {
    return this.goal;
  }
}

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
  Set<StaticDeductionRule> axioms = new HashSet<StaticDeductionRule>();
  Set<DynamicDeductionRule> rules = new HashSet<DynamicDeductionRule>();
  List<Item> goal = new LinkedList<Item>();
  
  public void addAxiom(StaticDeductionRule rule) {
    axioms.add(rule);
  }

  public void addRule(DynamicDeductionRule rule) {
    rules.add(rule);
  }

  public void addGoal(Item item) {
    this.goal.add(item);
  }

  public Set<DynamicDeductionRule> getRules() {
    return this.rules;
  }

  public Set<StaticDeductionRule> getAxioms() {
    return this.axioms;
  }

  public List<Item> getGoals() {
    return this.goal;
  }
}

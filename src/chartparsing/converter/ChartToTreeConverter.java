package chartparsing.converter;

import common.tag.Tag;
import common.tag.Tree;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import chartparsing.Deduction;
import common.Item;
import common.cfg.CfgProductionRule;

/** Collection of functions that take a chart and a list of goal items and
 * return one tree that represents the successful parse. */
public class ChartToTreeConverter {

  /** Returns the first possible tree that spans the whole input string. */
  public static Tree tagToDerivatedTree(Deduction deduction, List<Item> goals,
    Tag tag) {
    Tree derivationtree = null;
    for (Item goal : goals) {
      for (int i = 0; i < deduction.getChart().size(); i++) {
        if (deduction.getChart().get(i).equals(goal)) {
          ArrayList<String> steps =
            retrieveSteps(i, deduction.getAppliedRules(),
              deduction.getBackpointers(), new String[] {"subst", "adjoin"});
          for (int j = steps.size() - 1; j >= 0; j--) {
            derivationtree = applyStep(tag, derivationtree, steps, j);
          }
          return derivationtree;
        }
      }
    }
    return null;
  }

  public static Tree cfgToDerivatedTree(Deduction deduction, List<Item> goals,
    String algorithm) throws ParseException {
    Tree derivationtree = null;
    for (Item goal : goals) {
      for (int i = 0; i < deduction.getChart().size(); i++) {
        if (deduction.getChart().get(i).equals(goal)) {
          ArrayList<String> steps = null;
          if (algorithm.equals("topdown") || algorithm.equals("earley")) {
            steps = retrieveSteps(i, deduction.getAppliedRules(),
              deduction.getBackpointers(), new String[] {"predict"});
          } else if (algorithm.equals("shiftreduce")) {
            steps = retrieveSteps(i, deduction.getAppliedRules(),
              deduction.getBackpointers(), new String[] {"reduce"});
          }
          if (algorithm.equals("earley")) {
            derivationtree =
              new Tree(new CfgProductionRule(goal.getItemform()[0].substring(0,
                goal.getItemform()[0].length() - 2)));
          }
          if (algorithm.equals("topdown")) {
            for (int j = steps.size() - 1; j >= 0; j--) {
              String step = steps.get(j);
              if (j == steps.size() - 1) {
                derivationtree = new Tree(
                  new CfgProductionRule(step.substring(step.indexOf(" ") + 1)));
              } else {
                derivationtree = applyStep(derivationtree, step, false);
              }
            }
          } else if (algorithm.equals("earley")
            || algorithm.equals("shiftreduce")) {
            for (int j = 0; j < steps.size(); j++) {
              String step = steps.get(j);
              if (algorithm.equals("earley")) {
                derivationtree = applyStep(derivationtree, step, false);
              } else if (algorithm.equals("shiftreduce")) {
                if (j == 0) {
                  derivationtree = new Tree(new CfgProductionRule(
                    step.substring(step.indexOf(" ") + 1)));
                } else {
                  derivationtree = applyStep(derivationtree, step, true);
                }
              }
            }
          }
          return derivationtree;
        }
      }
    }
    return null;
  }

  /** Applies one derivation step in a CFG parsing process. */
  private static Tree applyStep(Tree derivationtree, String step,
    boolean rightmost) throws ParseException {
    Tree newruletree =
      new Tree(new CfgProductionRule(step.substring(step.indexOf(" ") + 1)));
    String dertreestring = derivationtree.toString();
    String lhs = step.substring(step.indexOf(" ") + 1,
      step.indexOf(" ", step.indexOf(" ", step.indexOf(" ") + 1)));
    if (rightmost) {
      derivationtree = new Tree(
        dertreestring.substring(0, dertreestring.lastIndexOf("(" + lhs + " )"))
          + newruletree.toString() + dertreestring.substring(
            dertreestring.lastIndexOf("(" + lhs + " )") + lhs.length() + 3));
    } else {
      derivationtree = new Tree(
        dertreestring.substring(0, dertreestring.indexOf("(" + lhs + " )"))
          + newruletree.toString() + dertreestring.substring(
            dertreestring.indexOf("(" + lhs + " )") + lhs.length() + 3));
    }
    return derivationtree;
  }

  /** Applies a single derivation step, either creating a new tree with
   * adjunction or substitution if it is the first step or using the previous
   * one. */
  private static Tree applyStep(Tag tag, Tree derivationtree,
    ArrayList<String> steps, int j) {
    String step = steps.get(j);
    String treename1 = step.substring(step.indexOf(" ") + 1, step.indexOf("["));
    String node1 = step.substring(step.indexOf("[") + 1, step.indexOf(","));
    if (node1.equals("ε")) {
      node1 = "";
    }
    String treename2 = step.substring(step.indexOf(",") + 1, step.indexOf("]"));
    if (step.startsWith("adjoin")) {
      if (j == steps.size() - 1) {
        derivationtree =
          tag.getTree(treename1).adjoin(node1, tag.getAuxiliaryTree(treename2));
      } else {
        derivationtree = tag.getTree(treename1).adjoin(node1, derivationtree);
      }
    } else if (step.startsWith("subst")) {
      if (j == steps.size() - 1) {
        derivationtree = tag.getTree(treename1).substitute(node1,
          tag.getInitialTree(treename2));
      } else {
        derivationtree =
          tag.getTree(treename1).substitute(node1, derivationtree);
      }
    }
    return derivationtree;
  }

  /** Retrieves a list of adjoin and substitute steps that lead to the goal
   * item. */
  private static ArrayList<String> retrieveSteps(int i,
    ArrayList<ArrayList<String>> appliedRules,
    ArrayList<ArrayList<ArrayList<Integer>>> backPointers, String[] prefixes) {
    ArrayList<String> steps = new ArrayList<String>();
    ArrayList<Integer> idagenda = new ArrayList<Integer>();
    ArrayList<Integer> allids = new ArrayList<Integer>();
    idagenda.add(i);
    while (idagenda.size() > 0) {
      int currentid = idagenda.get(0);
      idagenda.remove(0);
      for (String prefix : prefixes) {
        if (appliedRules.get(currentid).get(0).startsWith(prefix)) {
          steps.add(appliedRules.get(currentid).get(0));
        }
      }
      for (Integer pointer : backPointers.get(currentid).get(0)) {
        if (!allids.contains(pointer)) {
          idagenda.add(pointer);
          allids.add(pointer);
        }
      }
    }
    return steps;
  }
}

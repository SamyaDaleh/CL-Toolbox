package chartparsing.converter;

import common.tag.Tag;
import common.tag.Tree;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import chartparsing.Deduction;
import chartparsing.Item;
import common.cfg.CfgProductionRule;
import common.lcfrs.Clause;
import common.lcfrs.Predicate;

/** Collection of functions that take a chart and a list of goal items and
 * return one tree that represents the successful parse. */
public class ChartToTreeConverter {

  /** Returns the first possible tree that spans the whole input string. */
  public static Tree tagToDerivatedTree(Deduction deduction, List<Item> goals,
    Tag tag) {
    Tree derivatedTree = null;
    for (Item goal : goals) {
      for (int i = 0; i < deduction.getChart().size(); i++) {
        if (deduction.getChart().get(i).equals(goal)) {
          ArrayList<String> steps =
            retrieveSteps(i, deduction, new String[] {"subst", "adjoin"});
          for (int j = steps.size() - 1; j >= 0; j--) {
            derivatedTree = applyStep(tag, derivatedTree, steps, j);
          }
          return derivatedTree;
        }
      }
    }
    return null;
  }

  public static Tree srcgToDerivatedTree(Deduction deduction, List<Item> goals,
    String algorithm) throws ParseException {
    Tree derivatedTree = null;
    for (Item goal : goals) {
      for (int i = 0; i < deduction.getChart().size(); i++) {
        if (deduction.getChart().get(i).equals(goal)) {
          ArrayList<Item> items = null;
          if (algorithm.equals("earley")) {
            items = retrieveItems(i, deduction,
              new String[] {"complete", "scan"}, true);
          } else if (algorithm.equals("cyk")
            || algorithm.equals("cyk-extended")) {
            items = retrieveItems(i, deduction,
              new String[] {"complete", "scan"}, false);
          }
          if (algorithm.equals("earley")) {
            for (int j = 0; j < items.size(); j++) {
              Item item = items.get(j);
              String clauseString = item.getItemform()[0];
              if (j == 0) {
                derivatedTree =
                  new Tree(getCfgRuleRepresentationOfSrcgClauseString(item));
              } else {
                String derivatedTreeString = derivatedTree.toString();
                String itemTreeString;
                if (item.getItemform()[0].endsWith("ε")) {
                  itemTreeString = new Tree(
                    getCfgRuleRepresentationOfSrcgEpsilonClauseString(item))
                      .toString();
                } else {
                  itemTreeString =
                    new Tree(getCfgRuleRepresentationOfSrcgClauseString(item))
                      .toString();
                }
                String lhsNt =
                  clauseString.substring(0, clauseString.indexOf('('));
                derivatedTree = new Tree(derivatedTreeString.substring(0,
                  derivatedTreeString.lastIndexOf("(" + lhsNt + " )"))
                  + itemTreeString
                  + derivatedTreeString.substring(
                    derivatedTreeString.lastIndexOf("(" + lhsNt + " )")
                      + lhsNt.length() + 3));
              }
            }
            return derivatedTree;
          }
        }
      }
    }
    return null;
  }

  private static CfgProductionRule
    getCfgRuleRepresentationOfSrcgEpsilonClauseString(Item item)
      throws ParseException {
    Clause clause = new Clause(item.getItemform()[0]);
    StringBuilder extractedRule = new StringBuilder();
    extractedRule.append(clause.getLhs().getNonterminal()).append(" ->");
    String[] lhsSyms = clause.getLhs().getSymbolsAsPlainArray();
    for (int i = 0; i < lhsSyms.length; i++) {
        extractedRule.append(' ')
          .append(lhsSyms[i]).append('<');
        extractedRule.append(item.getItemform()[i * 2 + 4]);
        extractedRule.append('>');
    }
    return new CfgProductionRule(extractedRule.toString());
  }

  private static CfgProductionRule getCfgRuleRepresentationOfSrcgClauseString(
    Item item) throws ParseException {
    String srcgClauseString = item.getItemform()[0];
    Clause clause = new Clause(srcgClauseString);
    StringBuilder extractedRule = new StringBuilder();
    extractedRule.append(clause.getLhs().getNonterminal()).append(" ->");
    int terminalsInLhs = 0;
    for (String symbol : clause.getLhs().getSymbolsAsPlainArray()) {
      if (!symbolIsVariable(clause, symbol)) {
        terminalsInLhs++;
      }
    }
    String[] lhsSymbols = clause.getLhs().getSymbolsAsPlainArray();
    int i = 0;
    for (int terminalsProcessed = 0; terminalsProcessed < terminalsInLhs
      / 2; i++) {
      String symbol = lhsSymbols[i];
      boolean found = symbolIsVariable(clause, symbol);
      if (!found) {
        terminalsProcessed++;
        extractedRule.append(" ").append(symbol).append('<')
          .append(item.getItemform()[i * 2 + 4]).append('>');
      }
    }
    for (Predicate rhs : clause.getRhs()) {
      extractedRule.append(" ").append(rhs.getNonterminal());
    }
    for (; i < lhsSymbols.length; i++) {
      String symbol = lhsSymbols[i];
      boolean found = symbolIsVariable(clause, symbol);
      if (!found) {
        extractedRule.append(" ").append(symbol).append('<')
          .append(item.getItemform()[i * 2 + 4]).append('>');
      }
    }
    return new CfgProductionRule(extractedRule.toString());
  }

  private static boolean symbolIsVariable(Clause clause, String symbol) {
    boolean found = false;
    for (Predicate rhsPred : clause.getRhs()) {
      int[] indices = rhsPred.find(symbol);
      if (indices[0] >= 0) {
        found = true;
        break;
      }
    }
    return found;
  }

  public static Tree cfgToDerivatedTree(Deduction deduction, List<Item> goals,
    String algorithm) throws ParseException {
    Tree derivatedTree = null;
    for (Item goal : goals) {
      for (int i = 0; i < deduction.getChart().size(); i++) {
        if (deduction.getChart().get(i).equals(goal)) {
          ArrayList<String> steps =
            getDerivationStepsForAlgorithm(deduction, algorithm, i);
          if (algorithm.equals("earley")) {
            derivatedTree = new Tree(new CfgProductionRule(goal.getItemform()[0]
              .substring(0, goal.getItemform()[0].length() - 2)));
          }
          derivatedTree =
            getTreeDerivedFromCfgSteps(algorithm, derivatedTree, steps);
          return derivatedTree;
        }
      }
    }
    return null;
  }

  private static ArrayList<String> getDerivationStepsForAlgorithm(
    Deduction deduction, String algorithm, int i) {
    ArrayList<String> steps = null;
    switch (algorithm) {
    case "topdown":
    case "earley":
      steps = retrieveSteps(i, deduction, new String[] {"predict"});
      break;
    case "shiftreduce":
      steps = retrieveSteps(i, deduction, new String[] {"reduce"});
      break;
    case "unger":
      steps = retrieveSteps(i, deduction, new String[] {"complete"});
      break;
    default:
      System.out.println("Unknown algorithm: " + algorithm
        + ", can not retrieve derivated tree.");
    }
    return steps;
  }

  private static Tree getTreeDerivedFromCfgSteps(String algorithm,
    Tree derivatedTree, ArrayList<String> steps) throws ParseException {
    if (algorithm.equals("topdown")) {
      for (int j = steps.size() - 1; j >= 0; j--) {
        String step = steps.get(j);
        if (j == steps.size() - 1) {
          derivatedTree = new Tree(
            new CfgProductionRule(step.substring(step.indexOf(" ") + 1)));
        } else {
          derivatedTree = applyStep(derivatedTree, step, false);
        }
      }
    } else if (algorithm.equals("earley") || algorithm.equals("shiftreduce")
      || algorithm.equals("unger")) {
      for (int j = 0; j < steps.size(); j++) {
        String step = steps.get(j);
        switch (algorithm) {
        case "earley":
          derivatedTree = applyStep(derivatedTree, step, false);
          break;
        case "unger":
          if (j == 0) {
            derivatedTree = new Tree(
              new CfgProductionRule(step.substring(step.indexOf(" ") + 1)));
          } else {
            derivatedTree = applyStep(derivatedTree, step, false);
          }
          break;
        case "shiftreduce":
          if (j == 0) {
            derivatedTree = new Tree(
              new CfgProductionRule(step.substring(step.indexOf(" ") + 1)));
          } else {
            derivatedTree = applyStep(derivatedTree, step, true);
          }
          break;
        default:
          System.out.println("Unknown algorithm: " + algorithm
            + ", can not retrieve derivated tree.");
        }
      }
    }
    return derivatedTree;
  }

  /** Applies one derivation step in a CFG parsing process. */
  private static Tree applyStep(Tree derivatedTree, String step,
    boolean rightmost) throws ParseException {
    Tree newRuleTree =
      new Tree(new CfgProductionRule(step.substring(step.indexOf(" ") + 1)));
    String derTreeString = derivatedTree.toString();
    String lhs = step.substring(step.indexOf(" ") + 1,
      step.indexOf(" ", step.indexOf(" ", step.indexOf(" ") + 1)));
    if (rightmost) {
      derivatedTree = new Tree(
        derTreeString.substring(0, derTreeString.lastIndexOf("(" + lhs + " )"))
          + newRuleTree.toString() + derTreeString.substring(
            derTreeString.lastIndexOf("(" + lhs + " )") + lhs.length() + 3));
    } else {
      derivatedTree = new Tree(
        derTreeString.substring(0, derTreeString.indexOf("(" + lhs + " )"))
          + newRuleTree.toString() + derTreeString.substring(
            derTreeString.indexOf("(" + lhs + " )") + lhs.length() + 3));
    }
    return derivatedTree;
  }

  /** Applies a single derivation step, either creating a new tree with
   * adjunction or substitution if it is the first step or using the previous
   * one. */
  private static Tree applyStep(Tag tag, Tree derivatedTree,
    ArrayList<String> steps, int j) {
    String step = steps.get(j);
    String treeName1 = step.substring(step.indexOf(" ") + 1, step.indexOf("["));
    String node1 = step.substring(step.indexOf("[") + 1, step.indexOf(","));
    if (node1.equals("ε")) {
      node1 = "";
    }
    String treeName2 = step.substring(step.indexOf(",") + 1, step.indexOf("]"));
    if (step.startsWith("adjoin")) {
      if (j == steps.size() - 1) {
        derivatedTree =
          tag.getTree(treeName1).adjoin(node1, tag.getAuxiliaryTree(treeName2));
      } else {
        derivatedTree = tag.getTree(treeName1).adjoin(node1, derivatedTree);
      }
    } else if (step.startsWith("subst")) {
      if (j == steps.size() - 1) {
        derivatedTree = tag.getTree(treeName1).substitute(node1,
          tag.getInitialTree(treeName2));
      } else {
        derivatedTree = tag.getTree(treeName1).substitute(node1, derivatedTree);
      }
    }
    return derivatedTree;
  }

  /** Retrieves a list of steps starting with one of the given prefixes that
   * lead to the goal item. */
  private static ArrayList<String> retrieveSteps(int i, Deduction deduction,
    String[] prefixes) {
    ArrayList<String> steps = new ArrayList<String>();
    ArrayList<Integer> idAgenda = new ArrayList<Integer>();
    ArrayList<Integer> allIds = new ArrayList<Integer>();
    idAgenda.add(i);
    while (idAgenda.size() > 0) {
      int currentId = idAgenda.get(0);
      idAgenda.remove(0);
      for (String prefix : prefixes) {
        if (deduction.getAppliedRules().get(currentId).get(0)
          .startsWith(prefix)) {
          steps.add(deduction.getAppliedRules().get(currentId).get(0));
        }
      }
      for (Integer pointer : deduction.getBackpointers().get(currentId)
        .get(0)) {
        if (!allIds.contains(pointer)) {
          idAgenda.add(pointer);
          allIds.add(pointer);
        }
      }
    }
    return steps;
  }

  /** Retrieves a list of items that lead to the goal item and where the rules
   * start with the given prefix. */
  private static ArrayList<Item> retrieveItems(int i, Deduction deduction,
    String[] prefixes, boolean dotEnd) {
    ArrayList<Item> items = new ArrayList<Item>();
    ArrayList<Integer> idAgenda = new ArrayList<Integer>();
    ArrayList<Integer> allIds = new ArrayList<Integer>();
    idAgenda.add(i);
    while (idAgenda.size() > 0) {
      int currentid = idAgenda.get(0);
      idAgenda.remove(0);
      for (String prefix : prefixes) {
        if (deduction.getAppliedRules().get(currentid).get(0)
          .startsWith(prefix)) {
          if (dotEnd) {
            String itemRepresentation =
              deduction.getChart().get(currentid).toString();
            if (itemRepresentation.contains(" •)")) {
              items.add(deduction.getChart().get(currentid));
            }
          } else {
            items.add(deduction.getChart().get(currentid));
          }
        }
      }
      for (Integer pointer : deduction.getBackpointers().get(currentid)
        .get(0)) {
        if (!allIds.contains(pointer)) {
          idAgenda.add(pointer);
          allIds.add(pointer);
        }
      }
    }
    return items;
  }
}

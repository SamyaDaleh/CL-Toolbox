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

/**
 * Collection of functions that take a chart and a list of goal items and return
 * one tree that represents the successful parse.
 */
public class ChartToTreeConverter {

  /** Returns the first possible tree that spans the whole input string. */
  public static List<Tree> tagToDerivatedTree(Deduction deduction,
    List<Item> goals, Tag tag) {
    List<Tree> derivatedTrees = new ArrayList<Tree>();
    for (Item goal : goals) {
      for (int i = 0; i < deduction.getChart().size(); i++) {
        if (deduction.getChart().get(i).equals(goal)) {
          List<List<String>> stepss =
            retrieveSteps(i, deduction, new String[] {"subst", "adjoin"});
          for (int k = 0; k < stepss.size(); k++) {
            List<String> steps = stepss.get(k);
            Tree derivatedTree = null;
            for (int j = steps.size() - 1; j >= 0; j--) {
              derivatedTree = applyStep(tag, derivatedTree, steps, j);
            }
            if (derivatedTree != null) {
              derivatedTrees.add(derivatedTree);
            }
          }
        }
      }
    }
    return removeDuplicates(derivatedTrees);
  }

  public static List<Tree> srcgToDerivatedTree(Deduction deduction,
    List<Item> goals, String algorithm) throws ParseException {
    List<Tree> derivatedTrees = new ArrayList<Tree>();
    for (Item goal : goals) {
      for (int i = 0; i < deduction.getChart().size(); i++) {
        if (deduction.getChart().get(i).equals(goal)) {
          List<List<Item>> itemss = null;
          if (algorithm.equals("earley")) {
            itemss = retrieveItems(i, deduction,
              new String[] {"complete", "scan"}, true);
          } else if (algorithm.equals("cyk")
            || algorithm.equals("cyk-extended")) {
            itemss = retrieveItems(i, deduction,
              new String[] {"complete", "scan"}, false);
          }
          if (algorithm.equals("earley")) {
            for (int k = 0; k < itemss.size(); k++) {
              Tree derivatedTree = null;
              List<Item> items = itemss.get(k);
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
              derivatedTrees.add(derivatedTree);
            }
          }
        }
      }
    }
    return removeDuplicates(derivatedTrees);
  }

  private static CfgProductionRule
    getCfgRuleRepresentationOfSrcgEpsilonClauseString(Item item)
      throws ParseException {
    Clause clause = new Clause(item.getItemform()[0]);
    StringBuilder extractedRule = new StringBuilder();
    extractedRule.append(clause.getLhs().getNonterminal()).append(" ->");
    String[] lhsSyms = clause.getLhs().getSymbolsAsPlainArray();
    for (int i = 0; i < lhsSyms.length; i++) {
      extractedRule.append(' ').append(lhsSyms[i]).append('<');
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

  public static List<Tree> cfgToDerivatedTree(Deduction deduction,
    List<Item> goals, String algorithm) throws ParseException {
    List<Tree> derivatedTrees = new ArrayList<Tree>();
    for (Item goal : goals) {
      for (int i = 0; i < deduction.getChart().size(); i++) {
        if (deduction.getChart().get(i).equals(goal)) {
          List<List<String>> stepss =
            getDerivationStepsForAlgorithm(deduction, algorithm, i);
          Tree initialDerivatedTree = null;
          if (algorithm.equals("earley")) {
            initialDerivatedTree =
              new Tree(new CfgProductionRule(goal.getItemform()[0].substring(0,
                goal.getItemform()[0].length() - 2)));
          }
          for (int k = 0; k < stepss.size(); k++) {
            List<String> steps = stepss.get(k);
            Tree derivatedTree = getTreeDerivedFromCfgSteps(algorithm,
              initialDerivatedTree, steps);
            derivatedTrees.add(derivatedTree);
          }
        }
      }
    }
    return removeDuplicates(derivatedTrees);
  }

  private static List<List<String>> getDerivationStepsForAlgorithm(
    Deduction deduction, String algorithm, int i) {
    List<List<String>> steps = new ArrayList<List<String>>();
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
    Tree derivatedTree, List<String> steps) throws ParseException {
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

  /**
   * Applies a single derivation step, either creating a new tree with
   * adjunction or substitution if it is the first step or using the previous
   * one.
   */
  private static Tree applyStep(Tag tag, Tree derivatedTree, List<String> steps,
    int j) {
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

  /**
   * Retrieves a list of lists of steps starting with one of the given prefixes
   * that lead to the goal item.
   */
  private static List<List<String>> retrieveSteps(int i, Deduction deduction,
    String[] prefixes) {
    List<List<String>> stepss = new ArrayList<List<String>>();
    List<List<Integer>> idAgendas = new ArrayList<List<Integer>>();
    List<List<Integer>> allIdss = new ArrayList<List<Integer>>();
    stepss.add(new ArrayList<String>());
    idAgendas.add(new ArrayList<Integer>());
    allIdss.add(new ArrayList<Integer>());
    idAgendas.get(0).add(i);
    for (int j = 0; j < idAgendas.size(); j++) {
      List<Integer> idAgenda = idAgendas.get(j);
      List<String> steps = stepss.get(j);
      List<Integer> allIds = allIdss.get(j);
      while (idAgenda.size() > 0) {
        int currentId = idAgenda.get(0);
        idAgenda.remove(0);
        int setPos = 0;
        List<Integer> idAgendaCopy = new ArrayList<Integer>();
        idAgendaCopy.addAll(idAgenda);
        List<String> stepsCopy = new ArrayList<String>();
        stepsCopy.addAll(steps);
        List<Integer> allIdsCopy = new ArrayList<Integer>();
        allIdsCopy.addAll(allIds);
        handleAppliedRulesAndBackPointersForPos(deduction, prefixes, idAgenda,
          steps, allIds, currentId, setPos);
        if (deduction.getAppliedRules().get(currentId).size() > 1) {
          for (int k = 1; k < deduction.getAppliedRules().get(currentId)
            .size(); k++) {
            List<Integer> newIdAgenda = new ArrayList<Integer>();
            newIdAgenda.addAll(idAgendaCopy);
            List<String> newSteps = new ArrayList<String>();
            newSteps.addAll(stepsCopy);
            List<Integer> newAllIds = new ArrayList<Integer>();
            newAllIds.addAll(allIdsCopy);
            handleAppliedRulesAndBackPointersForPos(deduction, prefixes,
              newIdAgenda, newSteps, newAllIds, currentId, k);
            stepss.add(newSteps);
            idAgendas.add(newIdAgenda);
            allIdss.add(newAllIds);
          }
        }
      }
    }
    return stepss;
  }

  private static void handleAppliedRulesAndBackPointersForPos(
    Deduction deduction, String[] prefixes, List<Integer> idAgenda,
    List<String> steps, List<Integer> allIds, int currentId, int setPos) {
    for (String prefix : prefixes) {
      if (deduction.getAppliedRules().get(currentId).get(setPos)
        .startsWith(prefix)) {
        steps.add(deduction.getAppliedRules().get(currentId).get(setPos));
      }
    }
    for (Integer pointer : deduction.getBackpointers().get(currentId)
      .get(setPos)) {
      if (!allIds.contains(pointer)) {
        idAgenda.add(pointer);
        allIds.add(pointer);
      }
    }
  }

  /**
   * Retrieves a list of lists of items that lead to the goal item and where the
   * rules start with the given prefix.
   */
  private static List<List<Item>> retrieveItems(int i, Deduction deduction,
    String[] prefixes, boolean dotEnd) {
    List<List<Item>> itemss = new ArrayList<List<Item>>();
    List<List<Integer>> idAgendas = new ArrayList<List<Integer>>();
    List<List<Integer>> allIdss = new ArrayList<List<Integer>>();
    itemss.add(new ArrayList<Item>());
    allIdss.add(new ArrayList<Integer>());
    idAgendas.add(new ArrayList<Integer>());
    idAgendas.get(0).add(i);
    for (int j = 0; j < idAgendas.size(); j++) {
      List<Item> items = itemss.get(j);
      List<Integer> idAgenda = idAgendas.get(j);
      List<Integer> allIds = allIdss.get(j);
      while (idAgenda.size() > 0) {
        int currentId = idAgenda.get(0);
        idAgenda.remove(0);
        int setPos = 0;
        List<Item> itemsCopy = new ArrayList<Item>();
        itemsCopy.addAll(items);
        List<Integer> idAgendaCopy = new ArrayList<Integer>();
        idAgendaCopy.addAll(idAgenda);
        List<Integer> allIdsCopy = new ArrayList<Integer>();
        allIdsCopy.addAll(allIds);
        handleAppliedRulesAndBackPointersForPos(deduction, prefixes, dotEnd,
          items, idAgenda, allIds, currentId, setPos);
        if (deduction.getBackpointers().get(currentId).size() > 1) {
          for (int k = 1; k < deduction.getAppliedRules().get(currentId)
            .size(); k++) {
            List<Integer> newIdAgenda = new ArrayList<Integer>();
            newIdAgenda.addAll(idAgendaCopy);
            List<Item> newItems = new ArrayList<Item>();
            newItems.addAll(itemsCopy);
            List<Integer> newAllIds = new ArrayList<Integer>();
            newAllIds.addAll(allIdsCopy);
            handleAppliedRulesAndBackPointersForPos(deduction, prefixes, dotEnd,
              newItems, newIdAgenda, newAllIds, currentId, setPos);
            itemss.add(newItems);
            idAgendas.add(newIdAgenda);
            allIdss.add(newAllIds);
          }
        }
      }
    }
    return itemss;
  }

  private static void handleAppliedRulesAndBackPointersForPos(
    Deduction deduction, String[] prefixes, boolean dotEnd, List<Item> items,
    List<Integer> idAgenda, List<Integer> allIds, int currentid, int setPos) {
    for (String prefix : prefixes) {
      if (deduction.getAppliedRules().get(currentid).get(setPos)
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
      .get(setPos)) {
      if (!allIds.contains(pointer)) {
        idAgenda.add(pointer);
        allIds.add(pointer);
      }
    }
  }

  /**
   * Removes duplicates based on their string representation.
   */
  private static List<Tree> removeDuplicates(List<Tree> derivatedTrees) {
    for (int i = derivatedTrees.size() - 1; i >= 0; i--) {
      for (int j = 0; j < i; j++) {
        if (derivatedTrees.get(i).toString()
          .equals(derivatedTrees.get(j).toString())) {
          derivatedTrees.remove(i);
          break;
        }
      }
    }
    return derivatedTrees;
  }
}

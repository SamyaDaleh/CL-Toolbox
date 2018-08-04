package chartparsing.cfg.unger;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import chartparsing.AbstractDynamicDeductionRule;
import chartparsing.DeductionItem;
import chartparsing.Item;
import common.TreeUtils;
import common.cfg.Cfg;
import common.cfg.CfgProductionRule;
import common.tag.Tree;

/** Predict all possible separations of the rhs of a rule. */
public class CfgUngerPredict extends AbstractDynamicDeductionRule {

  private final CfgProductionRule rule;
  private final Cfg cfg;

  public CfgUngerPredict(CfgProductionRule rule, Cfg cfg) {
    this.rule = rule;
    this.antNeeded = 1;
    this.name = "predict " + rule.toString();
    this.cfg = cfg;
  }

  @Override public List<Item> getConsequences() throws ParseException {
    if (antecedences.size() == this.antNeeded) {
      String[] itemForm = antecedences.get(0).getItemform();
      String from = itemForm[1];
      String to = itemForm[2];
      int fromInt = Integer.parseInt(from);
      int toInt = Integer.parseInt(to);
      if (itemForm[0].substring(1).equals(rule.getLhs())) {
        Tree derivedTree = antecedences.get(0).getTree();
        if (derivedTree == null) {
          derivedTree = new Tree(rule);
        } else {
          derivedTree =
            TreeUtils.performLeftmostSubstitution(derivedTree, new Tree(rule));
        }
        if (rule.getRhs().length == 1) {
          Item consequence =
            new DeductionItem("•" + rule.getRhs()[0], from, to);
          consequence.setTree(derivedTree);
          consequences.add(consequence);
        } else {
          for (ArrayList<Integer> sequence : getAllSeparations(fromInt, toInt,
            rule.getRhs().length - 1)) {
            boolean skipSequence = false;
            if (cfg.terminalsContain(rule.getRhs()[0])
              && sequence.get(0) - fromInt > 1) {
              continue;
            }
            for (int i = 1; i < sequence.size() - 1; i++) {
              if (cfg.terminalsContain(rule.getRhs()[i])
                && sequence.get(i) - sequence.get(i - 1) > 1) {
                skipSequence = true;
                break;
              }
            }
            if (skipSequence) {
              continue;
            }
            if (cfg.terminalsContain(rule.getRhs()[rule.getRhs().length - 1])
              && toInt - sequence.get(sequence.size() - 1) > 1) {
              continue;
            }
            Item consequence = new DeductionItem("•" + rule.getRhs()[0], from,
              String.valueOf(sequence.get(0)));
            consequence.setTree(derivedTree);
            consequences.add(consequence);
            for (int i = 1; i < sequence.size(); i++) {
              consequence = new DeductionItem("•" + rule.getRhs()[i],
                String.valueOf(sequence.get(i - 1)),
                String.valueOf(sequence.get(i)));
              consequence.setTree(derivedTree);
              consequences.add(consequence);
            }
            consequence =
              new DeductionItem("•" + rule.getRhs()[rule.getRhs().length - 1],
                String.valueOf(sequence.get(sequence.size() - 1)), to);
            consequence.setTree(derivedTree);
            consequences.add(consequence);
          }
        }
      }
    }
    return consequences;
  }

  /**
   * Returns all sequences of length integers between (exclusive) from and to,
   * where every integer is greater than the previous one.
   */
  private ArrayList<ArrayList<Integer>> getAllSeparations(int from, int to,
    int length) {
    if (length == 1) {
      ArrayList<ArrayList<Integer>> newList =
        new ArrayList<ArrayList<Integer>>();
      for (int i = from + 1; i < to; i++) {
        newList.add(new ArrayList<Integer>());
        newList.get(newList.size() - 1).add(i);
      }
      return newList;
    } else {
      ArrayList<ArrayList<Integer>> finalList =
        new ArrayList<ArrayList<Integer>>();
      for (int i = from + 1; i <= to - length; i++) {
        for (ArrayList<Integer> subSequence : getAllSeparations(i, to,
          length - 1)) {
          finalList.add(new ArrayList<Integer>());
          finalList.get(finalList.size() - 1).add(i);
          finalList.get(finalList.size() - 1).addAll(subSequence);
        }
      }
      return finalList;
    }
  }

  @Override public String toString() {
    return "[•A, i_0, i_k]" + "\n______" + rule.toString() + "\n"
      + "[•A_1, i_0, i_1], ... , [•A_k,i_(k-1), i_k]";
  }

}

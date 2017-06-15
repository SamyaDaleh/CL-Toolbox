package chartparsing.cfg;

import java.util.ArrayList;
import java.util.List;

import chartparsing.AbstractDynamicDeductionRule;
import chartparsing.Item;
import common.cfg.Cfg;
import common.cfg.CfgProductionRule;

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

  @Override public List<Item> getConsequences() {
    if (antecedences.size() == this.antNeeded) {
      String[] itemForm = antecedences.get(0).getItemform();
      int from = Integer.parseInt(itemForm[1]);
      int to = Integer.parseInt(itemForm[2]);
      if (itemForm[0].substring(1).equals(rule.getLhs())) {
        if (rule.getRhs().length == 1) {
          consequences.add(new CfgItem("•" + rule.getRhs()[0],from,to));
        } else {
          for (ArrayList<Integer> sequence : getAllSeparations(from, to,
            rule.getRhs().length - 1)) {
            boolean skipSequence = false;
            if (cfg.terminalsContain(rule.getRhs()[0])
              && sequence.get(0) - from > 1) {
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
              && to - sequence.get(sequence.size() - 1) > 1) {
              continue;
            }
            consequences
              .add(new CfgItem("•" + rule.getRhs()[0], from, sequence.get(0)));
            for (int i = 1; i < sequence.size(); i++) {
              consequences.add(new CfgItem("•" + rule.getRhs()[i],
                sequence.get(i - 1), sequence.get(i)));
            }
            consequences
              .add(new CfgItem("•" + rule.getRhs()[rule.getRhs().length - 1],
                sequence.get(sequence.size() - 1), to));
          }
        }
      }
    }
    return consequences;
  }

  /** Returns all sequences of length integers between (exclusive) from and to,
   * where every integer is greater than the previous one. */
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

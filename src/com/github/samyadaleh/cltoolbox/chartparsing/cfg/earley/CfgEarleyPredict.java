package com.github.samyadaleh.cltoolbox.chartparsing.cfg.earley;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import com.github.samyadaleh.cltoolbox.chartparsing.dynamicdeductionrule.AbstractDynamicDeductionRule;
import com.github.samyadaleh.cltoolbox.chartparsing.item.DeductionChartItem;
import com.github.samyadaleh.cltoolbox.chartparsing.item.ChartItemInterface;
import com.github.samyadaleh.cltoolbox.common.ArrayUtils;
import com.github.samyadaleh.cltoolbox.common.TreeUtils;
import com.github.samyadaleh.cltoolbox.common.cfg.CfgProductionRule;
import com.github.samyadaleh.cltoolbox.common.tag.Tree;

/**
 * If the next symbol after the dot is a nonterminal, for a rule with that
 * symbol as lhs predict a new item.
 */
public class CfgEarleyPredict extends AbstractDynamicDeductionRule {

  private final CfgProductionRule rule;

  public CfgEarleyPredict(CfgProductionRule rule) {
    this.rule = rule;
    this.name = "predict " + rule.toString();
    this.antNeeded = 1;
  }

  @Override public List<ChartItemInterface> getConsequences() {
    if (antecedences.size() == antNeeded) {
      String[] itemForm = antecedences.get(0).getItemForm();
      String stack = itemForm[0];
      String[] stackSplit = stack.split(" ");
      int j = Integer.parseInt(itemForm[2]);

      for (String stackSymbol : stackSplit) {
        if (stackSymbol.startsWith("•") && stackSymbol.substring(1)
            .equals(rule.getLhs())) {
          String newStack;
          if (rule.getRhs()[0].equals("")) {
            newStack = rule.getLhs() + " -> •";
          } else {
            newStack =
                rule.getLhs() + " -> " + "•" + String.join(" ", rule.getRhs());
          }
          ChartItemInterface consequence =
              new DeductionChartItem(newStack, String.valueOf(j),
                  String.valueOf(j));
          try {
            Tree derivedTreeBase = new Tree(rule);
            List<Tree> derivedTrees = new ArrayList<>();
            for (Tree tree : antecedences.get(0).getTrees()) {
              try {
                derivedTrees.add(TreeUtils
                    .performLeftmostSubstitution(tree, derivedTreeBase));
              } catch (StringIndexOutOfBoundsException e) {
                log.debug(e.getMessage(), e);
              }
            }
            consequence.setTrees(derivedTrees);
            logItemGeneration(consequence);
            consequences.add(consequence);
            break;
          } catch (ParseException e) {
            throw new RuntimeException(e);
          }
        }
      }
    }
    return consequences;
  }

  @Override public String toString() {
    return "[A -> α •" + rule.getLhs() + "β,i,j]" + "\n______ " + rule
        .toString() + "\n" + "[" + rule.getLhs() + " -> •" + ArrayUtils
        .toString(rule.getRhs()) + ",j,j]";
  }

}

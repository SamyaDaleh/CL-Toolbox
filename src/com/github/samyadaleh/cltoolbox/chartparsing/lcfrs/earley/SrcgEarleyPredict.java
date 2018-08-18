package com.github.samyadaleh.cltoolbox.chartparsing.lcfrs.earley;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import com.github.samyadaleh.cltoolbox.chartparsing.AbstractDynamicDeductionRule;
import com.github.samyadaleh.cltoolbox.chartparsing.ChartItemInterface;
import com.github.samyadaleh.cltoolbox.common.TreeUtils;
import com.github.samyadaleh.cltoolbox.common.lcfrs.Clause;
import com.github.samyadaleh.cltoolbox.common.lcfrs.Predicate;
import com.github.samyadaleh.cltoolbox.common.lcfrs.RangeVector;
import com.github.samyadaleh.cltoolbox.common.tag.Tree;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Whenever our dot is left of a variable that is the first argument of some rhs
 * predicate B, we predict new B-rules.
 */
public class SrcgEarleyPredict extends AbstractDynamicDeductionRule {
  private static final Logger log = LogManager.getLogger();

  private final Clause outClause;

  public SrcgEarleyPredict(Clause outclause) {
    this.outClause = outclause;
    this.name = "predict " + outclause.toString();
    this.antNeeded = 1;
  }

  @Override public List<ChartItemInterface> getConsequences()
    throws ParseException {
    if (antecedences.size() == antNeeded) {
      String[] itemForm = antecedences.get(0).getItemForm();
      String clause = itemForm[0];
      if (itemForm[0].contains("->")) {
        Clause clauseParsed;
        try {
          clauseParsed = new Clause(clause);
        } catch (ParseException e) {
          log.error(e.getMessage(),e);
          return this.consequences;
        }
        String pos = itemForm[1];
        int posInt = Integer.parseInt(pos);
        String i = itemForm[2];
        int iInt = Integer.parseInt(i);
        String j = itemForm[3];
        int jInt = Integer.parseInt(j);
        if (clauseParsed.getLhs().ifSymExists(iInt, jInt)) {
          String mayV = clauseParsed.getLhsSymAt(iInt, jInt);
          for (Predicate rhsPred : clauseParsed.getRhs()) {
            if (rhsPred.getSymAt(1, 0).equals(mayV) && rhsPred.getNonterminal()
              .equals(outClause.getLhs().getNonterminal())) {
              ChartItemInterface consequence = new SrcgEarleyActiveItem(
                outClause.toString(), posInt, 1, 0, new RangeVector(
                  outClause.getLhs().getSymbolsAsPlainArray().length));
              List<Tree> derivedTrees = new ArrayList<>();
              Tree derivedTreeBase = TreeUtils.getTreeOfSrcgClause(outClause);
              for (Tree tree : antecedences.get(0).getTrees()) {
                try {
                  derivedTrees.add(TreeUtils.performLeftmostSubstitution(tree,
                    derivedTreeBase));
                } catch (IndexOutOfBoundsException e) {
                  // several items with different trees can lead to the same
                  // item with "predict". It collects all the trees, but they
                  // can not always be applied in all derivations downstream.
                }
              }
              consequence.setTrees(derivedTrees);
              logItemGeneration(consequence);
              consequences.add(consequence);
            }
          }
        }
      }
    }
    return this.consequences;
  }

  @Override public String toString() {
    return "[A(φ) -> ... B(X,...)...,pos,<i,j>,ρ_A]" + "\n______ φ(i,j) = X\n"
      + "[" + outClause.toString() + ",pos,<1,0>,ρ_init']";
  }

}

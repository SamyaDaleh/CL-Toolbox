package com.github.samyadaleh.cltoolbox.chartparsing.lcfrs.cyk;

import com.github.samyadaleh.cltoolbox.chartparsing.dynamicdeductionrule.AbstractDynamicDecutionRuleTwoAntecedences;
import com.github.samyadaleh.cltoolbox.chartparsing.item.ChartItemInterface;
import com.github.samyadaleh.cltoolbox.chartparsing.lcfrs.SrcgDeductionUtils;
import com.github.samyadaleh.cltoolbox.common.TreeUtils;
import com.github.samyadaleh.cltoolbox.common.lcfrs.Clause;
import com.github.samyadaleh.cltoolbox.common.tag.Tree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Similar to the binary complete rule in CYK for CFG. If there is a clause and
 * the vectors of two items that represent the rhs match, combine them to a new
 * item that represents the lhs with span over both.
 */
public class SrcgCykBinary extends AbstractDynamicDecutionRuleTwoAntecedences {

  private final Clause clause;
  private final String[] wSplit;

  public SrcgCykBinary(Clause clause, String[] wSplit) {
    this.name = "complete " + clause.toString();
    this.antNeeded = 2;
    this.clause = clause;
    this.wSplit = wSplit;
  }

  protected void calculateConsequences(String[] itemForm2, String[] itemForm1) {
    String nt1 = itemForm1[0];
    String nt2 = itemForm2[0];
    if (nt2.equals(clause.getRhs().get(0).getNonterminal()) && nt1
        .equals(clause.getRhs().get(1).getNonterminal())) {
      ArrayList<Integer> overallRanges = new ArrayList<>();
      boolean looksGood =
          isVectorRangesMatch(itemForm2, itemForm1, overallRanges);
      if (looksGood && overallRanges.size() > 0) {
        @SuppressWarnings("unchecked") List<Integer> newVector =
            (List<Integer>) SrcgDeductionUtils
                .getRangesForArguments(overallRanges, clause.getLhs());
        ChartItemInterface consequence =
            new SrcgCykItem(clause.getLhs().getNonterminal(), newVector);
        List<Tree> derivedTrees =
            calculateDerivatedTrees(itemForm1, overallRanges);
        consequence.setTrees(derivedTrees);
        consequences.add(consequence);
        logItemGeneration(consequence);
        this.name = "complete " + clause.toString();
      }
    }
  }

  private boolean isVectorRangesMatch(String[] itemForm2, String[] itemForm1,
      ArrayList<Integer> overallRanges) {
    boolean looksGood = true;
    for (String[] argument : clause.getLhs().getSymbols()) {
      ArrayList<String> vectorRanges = new ArrayList<>();
      for (String element : argument) {
        addVectorRangeForElement(itemForm2, itemForm1, vectorRanges, element);
      }
      int i = (vectorRanges.lastIndexOf("?") + 1) / 2;
      int prevnum = Integer.parseInt(vectorRanges.get(i * 2));
      for (; i > 0; --i) {
        vectorRanges.set(i * 2, String.valueOf(prevnum - 1));
        vectorRanges.set(i * 2 + 1, String.valueOf(prevnum));
        if (prevnum == 0 || !wSplit[prevnum - 1].equals(argument[i])) {
          looksGood = false;
        }
        prevnum--;
      }
      if (looksGood) {
        i = 1;
        for (; i * 2 < vectorRanges.size(); i++) {
          prevnum = Integer.parseInt(vectorRanges.get(i * 2 - 1));
          if (vectorRanges.get(i * 2).equals("?")) {
            vectorRanges.set(i * 2, String.valueOf(prevnum));
            vectorRanges.set(i * 2 + 1, String.valueOf(prevnum + 1));
            if (!wSplit[prevnum].equals(argument[i])) {
              looksGood = false;
            }
          } else if (!vectorRanges.get(i * 2)
              .equals(vectorRanges.get(i * 2 - 1))) {
            looksGood = false;
          }
        }
        for (String elem : vectorRanges) {
          overallRanges.add(Integer.parseInt(elem));
        }
      }
    }
    return looksGood;
  }

  private List<Tree> calculateDerivatedTrees(String[] itemForm1,
      ArrayList<Integer> overallRanges) {
    List<Tree> derivedTrees = new ArrayList<>();
    Tree derivedTreeBase = TreeUtils.getTreeOfSrcgClause(clause, overallRanges);
    if (Arrays.equals(itemForm1, antecedences.get(0).getItemForm())) {
      for (Tree tree1 : antecedences.get(0).getTrees()) {
        for (Tree tree2 : antecedences.get(1).getTrees()) {
          assert derivedTreeBase != null;
          derivedTreeBase =
              TreeUtils.performLeftmostSubstitution(derivedTreeBase, tree1);
          assert derivedTreeBase != null;
          derivedTreeBase =
              TreeUtils.performLeftmostSubstitution(derivedTreeBase, tree2);
        }
      }
    } else {
      for (Tree tree1 : antecedences.get(0).getTrees()) {
        for (Tree tree2 : antecedences.get(1).getTrees()) {
          assert derivedTreeBase != null;
          derivedTreeBase =
              TreeUtils.performLeftmostSubstitution(derivedTreeBase, tree2);
          assert derivedTreeBase != null;
          derivedTreeBase =
              TreeUtils.performLeftmostSubstitution(derivedTreeBase, tree1);
        }
      }
    }
    derivedTrees.add(derivedTreeBase);
    return derivedTrees;
  }

  private void addVectorRangeForElement(String[] itemForm2, String[] itemForm1,
      ArrayList<String> vectorRanges, String element) {
    int[] indices = clause.getRhs().get(0).find(element);
    if (indices[0] == -1) {
      indices = clause.getRhs().get(1).find(element);
      if (indices[0] == -1) {
        vectorRanges.add("?");
        vectorRanges.add("?");
      } else {
        vectorRanges.add(itemForm1[(indices[0] - 1) * 2 + 1]);
        vectorRanges.add(itemForm1[(indices[0] - 1) * 2 + 2]);
      }
    } else {
      vectorRanges.add(itemForm2[(indices[0] - 1) * 2 + 1]);
      vectorRanges.add(itemForm2[(indices[0] - 1) * 2 + 2]);
    }
  }

  @Override public String toString() {
    return "[" + clause.getRhs().get(0).toString() + ",ρ_B], [" + clause
        .getRhs().get(1).toString() + ",ρ_C]" + "\n______ \n" + "[" + clause
        .getLhs().toString() + ",ρ_A]";
  }

}

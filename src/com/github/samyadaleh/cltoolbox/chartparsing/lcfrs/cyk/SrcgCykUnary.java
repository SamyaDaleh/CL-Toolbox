package com.github.samyadaleh.cltoolbox.chartparsing.lcfrs.cyk;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import com.github.samyadaleh.cltoolbox.chartparsing.AbstractDynamicDeductionRule;
import com.github.samyadaleh.cltoolbox.chartparsing.ChartItemInterface;
import com.github.samyadaleh.cltoolbox.chartparsing.lcfrs.SrcgDeductionUtils;
import com.github.samyadaleh.cltoolbox.common.TreeUtils;
import com.github.samyadaleh.cltoolbox.common.lcfrs.Clause;
import com.github.samyadaleh.cltoolbox.common.tag.Tree;

/**
 * Similar to the Unary rule in extended CYK for CFG. If there is a chain rule
 * and an item for the rhs, get an lhs item with the same span.
 */
public class SrcgCykUnary extends AbstractDynamicDeductionRule {

  private final Clause clause;
  private final String[] wSplit;

  public SrcgCykUnary(Clause clause, String[] wSplit) {
    this.name = "unary " + clause.toString();
    this.antNeeded = 1;
    this.clause = clause;
    this.wSplit = wSplit;
  }

  @SuppressWarnings("unchecked") @Override public List<ChartItemInterface> getConsequences()
    throws ParseException {
    if (antecedences.size() == antNeeded) {
      String[] itemForm = antecedences.get(0).getItemform();
      String nt = itemForm[0];

      if (nt.equals(clause.getRhs().get(0).getNonterminal())) {
        ArrayList<Integer> overallRanges = new ArrayList<Integer>();
        for (String[] argument : clause.getLhs().getSymbols()) {
          ArrayList<String> vectorRanges = new ArrayList<String>();
          for (String element : argument) {
            addVectorRangeForElement(itemForm, vectorRanges, element);
          }
          int i = 0;
          for (; i * 2 < vectorRanges.size(); i++) {
            if (!vectorRanges.get(i * 2).equals("?")) {
              break;
            }
          }
          int prevNum = Integer.parseInt(vectorRanges.get(i * 2));
          while (i > 0) {
            i--;
            vectorRanges.set(i * 2, String.valueOf(prevNum - 1));
            vectorRanges.set(i * 2 + 1, String.valueOf(prevNum));
            if (prevNum == 0 || !wSplit[prevNum - 1].equals(argument[i])) {
              return this.consequences;
            }
            prevNum--;
          }
          i = 1;
          for (; i * 2 < vectorRanges.size(); i++) {
            prevNum = Integer.parseInt(vectorRanges.get(i * 2 - 1));
            if (vectorRanges.get(i * 2).equals("?")) {
              vectorRanges.set(i * 2, String.valueOf(prevNum));
              vectorRanges.set(i * 2 + 1, String.valueOf(prevNum + 1));
              if (!wSplit[prevNum].equals(argument[i])) {
                return this.consequences;
              }
            } else if (!vectorRanges.get(i * 2)
              .equals(vectorRanges.get(i * 2 - 1))) {
              return this.consequences;
            }
          }
          for (String elem : vectorRanges) {
            overallRanges.add(Integer.parseInt(elem));
          }
          // Can't handle something like B(a,X), I would need to create all
          // possible ranges for that and return a new item for each
          // Why would you need that anyway? Just put an a in the string where
          // you want it.
        }
        if (overallRanges.size() > 0) {
          List<Integer> newVector = (List<Integer>) SrcgDeductionUtils
            .getRangesForArguments(overallRanges, clause.getLhs());
          Tree derivedTreeBase = new Tree(
            TreeUtils.getCfgRuleRepresentationOfSrcgClause(clause, overallRanges));
          List<Tree> derivedTrees = new ArrayList<Tree>();
          for (Tree tree : antecedences.get(0).getTrees()) {
            derivedTrees.add(
              TreeUtils.performLeftmostSubstitution(derivedTreeBase, tree));
          }
          ChartItemInterface consequence =
            new SrcgCykItem(clause.getLhs().getNonterminal(), newVector);
          consequence.setTrees(derivedTrees);
          consequences.add(consequence);
          this.name = "complete " + clause.toString();
        }
      }

    }
    return this.consequences;
  }

  private void addVectorRangeForElement(String[] itemForm,
    ArrayList<String> vectorRanges, String element) {
    int[] indices = clause.getRhs().get(0).find(element);
    if (indices[0] == -1) {
      vectorRanges.add("?");
      vectorRanges.add("?");
    } else {
      vectorRanges.add(itemForm[(indices[0] - 1) * 2 + 1]);
      vectorRanges.add(itemForm[(indices[0] - 1) * 2 + 2]);
    }
  }

  @Override public String toString() {
    return "[" + clause.getRhs().get(0).toString() + ",ρ]]" + "\n______ \n"
      + "[" + clause.getLhs().toString() + ",ρ]";
  }
}

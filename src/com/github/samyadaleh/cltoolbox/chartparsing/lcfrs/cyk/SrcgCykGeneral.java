package com.github.samyadaleh.cltoolbox.chartparsing.lcfrs.cyk;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import com.github.samyadaleh.cltoolbox.chartparsing.AbstractDynamicDeductionRule;
import com.github.samyadaleh.cltoolbox.chartparsing.ChartItemInterface;
import com.github.samyadaleh.cltoolbox.chartparsing.lcfrs.SrcgDeductionUtils;
import com.github.samyadaleh.cltoolbox.common.TreeUtils;
import com.github.samyadaleh.cltoolbox.common.lcfrs.Clause;
import com.github.samyadaleh.cltoolbox.common.lcfrs.Predicate;
import com.github.samyadaleh.cltoolbox.common.tag.Tree;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Similar to the general complete rule in CYK for CFG. If there is a clause and
 * the vectors of all items that represent the rhs match, combine them to a new
 * item that represents the lhs with span over all: // example clause: S^1(X V Y
 * W) -> A^11(X,Y) B^11(V,W) // items: [A^11, (<0,1>, <2,3>)] [B^11, (<1,2>,
 * <3,4>)] // generate: [S^1, (0,4)]
 */
public class SrcgCykGeneral extends AbstractDynamicDeductionRule {
  private static final Logger log = LogManager.getLogger();

  private final Clause clause;
  private final String[] wSplit;
  private final int rangesNeeded;

  public SrcgCykGeneral(Clause clause, String[] wSplit) {
    this.name = "complete " + clause.toString();
    this.antNeeded = clause.getRhs().size();
    this.clause = clause;
    this.wSplit = wSplit;
    this.rangesNeeded = clause.getLhs().getSymbolsAsPlainArray().length;
  }

  @SuppressWarnings("unchecked") @Override public List<ChartItemInterface>
    getConsequences() throws ParseException {
    if (antecedences.size() == antNeeded) {
      Predicate lhs = clause.getLhs();
      List<List<String>> solutionRangesList =
        matchLhsWithAntecedencesReturnSolutionRanges(lhs);
      for (List<String> solutionRangeVector : solutionRangesList) {
        if (solutionRangeVector.size() < this.rangesNeeded * 2) {
          continue;
        }
        ArrayList<Integer> rangeOverElements = new ArrayList<>();
        boolean vectorsMatch = true;
        for (int o = 0; o < lhs.getDim(); o++) {
          String lastEnd = null;
          for (int p = 0; p < lhs.getArgumentByIndex(o + 1).length; p++) {
            int absPos = lhs.getAbsolutePos(o + 1, p);
            if (p > 0) {
              if (!lastEnd.equals(solutionRangeVector.get(absPos * 2))) {
                vectorsMatch = false;
                break;
              }
            }
            lastEnd = solutionRangeVector.get(absPos * 2 + 1);
            rangeOverElements
              .add(Integer.parseInt(solutionRangeVector.get(absPos * 2)));
            rangeOverElements
              .add(Integer.parseInt(solutionRangeVector.get(absPos * 2 + 1)));
          }
          if (!vectorsMatch) {
            break;
          }
        }
        if (!vectorsMatch) {
          continue;
        }
        List<Integer> rangeOverArguments = (List<Integer>) SrcgDeductionUtils
          .getRangesForArguments(rangeOverElements, clause.getLhs());
        if (rangesAreOverlapping(rangeOverArguments)) {
          continue;
        }
        List<Tree> derivedTrees = new ArrayList<>();
        derivedTrees
          .add(TreeUtils.getTreeOfSrcgClause(clause, rangeOverElements));
        for (Predicate rhsPred : clause.getRhs()) {
          int[] indices = lhs.find(rhsPred.getArgumentByIndex(1)[0]);
          int pos = lhs.getAbsolutePos(indices[0], indices[1]);
          String rangeStart = rangeOverElements.get(pos * 2).toString();
          for (ChartItemInterface item : antecedences) {
            if (item.getItemForm()[1].equals(rangeStart)) {
              List<Tree> derivedTreesNew = new ArrayList<>();
              for (Tree tree1 : derivedTrees) {
                for (Tree tree2 : item.getTrees()) {
                  derivedTreesNew
                    .add(TreeUtils.performLeftmostSubstitution(tree1, tree2));
                }
              }
              derivedTrees = derivedTreesNew;
              break;
            }
          }
        }
        ChartItemInterface consequence =
          new SrcgCykItem(clause.getLhs().getNonterminal(), rangeOverArguments);
        consequence.setTrees(derivedTrees);
        logItemGeneration(consequence);
        consequences.add(consequence);
      }
    }
    return this.consequences;
  }

  private List<List<String>>
    matchLhsWithAntecedencesReturnSolutionRanges(Predicate lhs) {
    List<List<String>> solutionRangesList = new ArrayList<>();
    for (int i = 0; i < lhs.getDim(); i++) {
      for (int j = 0; j < lhs.getArgumentByIndex(i + 1).length; j++) {
        String mayV = lhs.getSymAt(i + 1, j);
        String nt = getNonterminalOfPredicateContainingSymbol(mayV, clause);
        if (nt == null) {
          addTerminalVectors(solutionRangesList, i, mayV);
          continue;
        }
        List<List<String>> newsolutionRangesList = new ArrayList<>();
        for (ChartItemInterface item : antecedences) {
          if (!nt.equals(item.getItemForm()[0])) {
            continue;
          }
          if (i == 0 && j == 0) {
            handleFirstSymbol(solutionRangesList, mayV, item);
          } else {
            handleSubsequentSymbol(solutionRangesList, mayV,
              newsolutionRangesList, item);
          }
        }
        if (!newsolutionRangesList.isEmpty()) {
          solutionRangesList = newsolutionRangesList;
        }
      }
    }
    return solutionRangesList;
  }

  private void handleSubsequentSymbol(List<List<String>> solutionRangesList,
    String mayV, List<List<String>> newsolutionRangesList,
    ChartItemInterface item) {
    for (Predicate rhsPred : clause.getRhs()) {
      int[] indices = rhsPred.find(mayV);
      if (indices[0] == -1) {
        continue;
      }
      int absPos = rhsPred.getAbsolutePos(indices[0], indices[1]);
      for (List<String> solutionRangeVector : solutionRangesList) {
        List<String> newList = new ArrayList<>(solutionRangeVector);
        newList.add(item.getItemForm()[absPos * 2 + 1]);
        newList.add(item.getItemForm()[absPos * 2 + 2]);
        newsolutionRangesList.add(newList);
      }
    }
  }

  private void handleFirstSymbol(List<List<String>> solutionRangesList,
    String mayV, ChartItemInterface item) {
    for (Predicate rhsPred : clause.getRhs()) {
      int[] indices = rhsPred.find(mayV);
      if (indices[0] == -1) {
        continue;
      }
      int absPos = rhsPred.getAbsolutePos(indices[0], indices[1]);
      solutionRangesList.add(new ArrayList<>());
      solutionRangesList.get(solutionRangesList.size() - 1)
        .add(item.getItemForm()[absPos * 2 + 1]);
      solutionRangesList.get(solutionRangesList.size() - 1)
        .add(item.getItemForm()[absPos * 2 + 2]);
      break;
    }
  }

  private boolean rangesAreOverlapping(List<Integer> rangeOverArguments) {
    for (int i = 0; i * 2 < rangeOverArguments.size(); i++) {
      Integer v1 = rangeOverArguments.get(i * 2);
      Integer v2 = rangeOverArguments.get(i * 2 + 1);
      for (int j = i + 1; j * 2 < rangeOverArguments.size(); j++) {
        Integer v3 = rangeOverArguments.get(j * 2);
        Integer v4 = rangeOverArguments.get(j * 2 + 1);
        if (!(v1 <= v2 && v2 <= v3 && v3 <= v4)
          && !(v1 >= v2 && v2 >= v3 && v3 >= v4)) {
          return true;
        }
      }
    }
    return false;
  }

  private void addTerminalVectors(List<List<String>> solutionRangesList, int i,
    String mayV) {
    for (int m = 0; m < wSplit.length; m++) {
      if (wSplit[m].equals(mayV)) {
        if (i == 0) {
          solutionRangesList.add(new ArrayList<>());
          solutionRangesList.get(solutionRangesList.size() - 1)
            .add(String.valueOf(m));
          solutionRangesList.get(solutionRangesList.size() - 1)
            .add(String.valueOf(m + 1));
        } else {
          for (List<String> solutionRangeVector : solutionRangesList) {
            solutionRangeVector.add(String.valueOf(m));
            solutionRangeVector.add(String.valueOf(m + 1));
          }
        }
      }
    }
  }

  /**
   * Finds the given symbol in the rhs and returns the nonterminal it belongs
   * to. If it is not found, it is probably a terminal and null is returned.
   */
  private String getNonterminalOfPredicateContainingSymbol(String symAt,
    Clause clause) {
    for (Predicate rhsPred : clause.getRhs()) {
      for (String rhsPredElem : rhsPred.getSymbolsAsPlainArray()) {
        if (rhsPredElem.equals(symAt)) {
          return rhsPred.getNonterminal();
        }
      }
    }
    return null;
  }

  @Override public String toString() {
    StringBuilder stringRep = new StringBuilder();
    for (int i = 0; i < clause.getRhs().size(); i++) {
      if (i > 0) {
        stringRep.append(", ");
      }
      stringRep.append('[').append(clause.getRhs().get(i).toString())
        .append(",ρ_").append(i + 1).append(']');
    }
    stringRep.append("\n______ \n[").append(clause.getLhs().toString())
      .append(",ρ]");
    return stringRep.toString();
  }

}

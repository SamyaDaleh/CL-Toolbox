package chartparsing.lcfrs.cyk;

import java.util.ArrayList;
import java.util.List;

import chartparsing.AbstractDynamicDeductionRule;
import chartparsing.Item;
import chartparsing.lcfrs.SrcgDeductionUtils;
import common.lcfrs.Clause;
import common.lcfrs.Predicate;

/** Similar to the general complete rule in CYK for CFG. If there is a clause
 * and the vectors of all items that represent the rhs match, combine them to a
 * new item that represents the lhs with span over all: // example clause: S^1(X
 * V Y W) -> A^11(X,Y) B^11(V,W) // items: [A^11, (<0,1>, <2,3>)] [B^11, (<1,2>,
 * <3,4>)] // generate: [S^1, (0,4)] */
public class SrcgCykGeneral extends AbstractDynamicDeductionRule {

  private final Clause clause;
  private final String[] wSplit;

  public SrcgCykGeneral(Clause clause, String[] wSplit) {
    this.name = "complete";
    this.antNeeded = clause.getRhs().size();
    this.clause = clause;
    this.wSplit = wSplit;
  }

  @SuppressWarnings("unchecked") @Override public List<Item> getConsequences() {
    if (antecedences.size() == antNeeded) {
      Predicate lhs = clause.getLhs();
      List<List<String>> solutionRangesList = new ArrayList<List<String>>();
      for (int i = 0; i < lhs.getDim(); i++) {
        for (int j = 0; j < lhs.getArgumentByIndex(i + 1).length; j++) {
          String mayV = lhs.getSymAt(i + 1, j);
          String nt = getNonterminalOfPredicateContainingSymbol(mayV, clause);
          if (nt == null) {
            addTerminalVectors(solutionRangesList, i, mayV);
            continue;
          }
          for (Item item : antecedences) {
            if (!nt.equals(item.getItemform()[0])) {
              continue;
            }
            if (i == 0) {
              for (Predicate rhsPred : clause.getRhs()) {
                int[] indices = rhsPred.find(mayV);
                if (indices[0] == -1) {
                  continue;
                }
                int absPos = rhsPred.getAbsolutePos(indices[0], indices[1]);
                solutionRangesList.add(new ArrayList<String>());
                solutionRangesList.get(solutionRangesList.size() - 1)
                  .add(item.getItemform()[absPos * 2 + 1]);
                solutionRangesList.get(solutionRangesList.size() - 1)
                  .add(item.getItemform()[absPos * 2 + 2]);
                break;
              }
            } else {
              for (Predicate rhsPred : clause.getRhs()) {
                int[] indices = rhsPred.find(mayV);
                if (indices[0] == -1) {
                  continue;
                }
                int absPos = rhsPred.getAbsolutePos(indices[0], indices[1]);
                for (List<String> solutionRangeVector : solutionRangesList) {
                  solutionRangeVector.add(item.getItemform()[absPos * 2 + 1]);
                  solutionRangeVector.add(item.getItemform()[absPos * 2 + 2]);
                }
              }
            }
          }
        }
      }
      for (List<String> solutionRangeVector : solutionRangesList) {
        if (solutionRangeVector.size() < this.antNeeded * 2) {
          continue;
        }
        ArrayList<Integer> rangeOverElements = new ArrayList<Integer>();
        boolean vectorsMatch = true;
        for (int o = 0; o < lhs.getDim(); o++) {
          String lastEnd = null;
          for (int p = 0; p < lhs.getArgumentByIndex(o + 1).length; p++) {
            if (p > 0) {
              if (!lastEnd.equals(lhs.getSymAt(o + 1, p))) {
                vectorsMatch = false;
                break;
              }
            }
            int absPos = lhs.getAbsolutePos(o + 1, p);
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
        consequences.add(new SrcgCykItem(clause.getLhs().getNonterminal(),
          rangeOverArguments));
      }
    }
    return this.consequences;
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
          solutionRangesList.add(new ArrayList<String>());
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

  /** Finds the given symbol in the rhs and returns the nonterminal it belongs
   * to. If it is not found, it is probably a terminal and null is returned. */
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

  @Override public String toString() { // TODO update or remove, it's wrong
    return "[" + clause.getRhs().get(0).toString() + ",ρ_B], ["
      + clause.getRhs().get(1).toString() + ",ρ_C]" + "\n______ \n" + "["
      + clause.getLhs().toString() + ",ρ_A]";
  }

}

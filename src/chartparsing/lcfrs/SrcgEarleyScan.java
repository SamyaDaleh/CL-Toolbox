package chartparsing.lcfrs;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import chartparsing.AbstractDynamicDeductionRule;
import chartparsing.Item;
import common.lcfrs.Clause;

/** Whenever the next symbol after the dot is the next terminal in the input, we
 * can scan it. */
public class SrcgEarleyScan extends AbstractDynamicDeductionRule {

  private final String[] wSplit;

  /** Remembers the input string to compare it with the next symbol to scan. */
  public SrcgEarleyScan(String[] wSplit) {
    this.wSplit = wSplit;
    this.name = "scan";
    this.antNeeded = 1;
  }

  @Override public List<Item> getConsequences() {
    if (antecedences.size() == antNeeded) {
      String[] itemForm = antecedences.get(0).getItemform();
      String clause = itemForm[0];
      if (itemForm[0].contains("->")) {
        Clause clauseParsed;
        try {
          clauseParsed = new Clause(clause);
        } catch (ParseException e) {
          e.printStackTrace();
          return this.consequences;
        }
        String pos = itemForm[1];
        int posInt = Integer.parseInt(pos);
        String i = itemForm[2];
        int iInt = Integer.parseInt(i);
        String j = itemForm[3];
        int jInt = Integer.parseInt(j);
        int place = clauseParsed.getLhs().getAbsolutePos(iInt, jInt);

        if (clauseParsed.getLhs().ifSymExists(iInt, jInt)
          && posInt < wSplit.length
          && clauseParsed.getLhsSymAt(iInt, jInt).equals(wSplit[posInt])) {
          ArrayList<String> newVector = new ArrayList<String>();
          for (int k = 0; k * 2 + 5 < itemForm.length; k++) {
            newVector.add(itemForm[2 * k + 4]);
            newVector.add(itemForm[2 * k + 5]);
          }
          newVector.set(place * 2, pos);
          newVector.set(place * 2 + 1, String.valueOf(posInt + 1));
          consequences.add(new SrcgEarleyActiveItem(clause, posInt + 1, iInt,
            jInt + 1, newVector));
        }
      }
    }
    return this.consequences;
  }

  @Override public String toString() {
    return "[A(φ) -> Φ,pos,<i,j>,ρ]" + "\n______ φ(i,j) = w_pos\n"
      + "[A(φ) -> Φ,pos,<i,j+1>,ρ']";
  }
}

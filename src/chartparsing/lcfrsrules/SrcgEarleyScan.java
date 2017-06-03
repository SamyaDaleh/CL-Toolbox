package chartparsing.lcfrsrules;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import chartparsing.AbstractDynamicDeductionRule;
import common.Item;
import common.lcfrs.Clause;
import common.lcfrs.SrcgEarleyActiveItem;

/** Whenever the next symbol after the dot is the next terminal in the input, we
 * can scan it. */
public class SrcgEarleyScan extends AbstractDynamicDeductionRule {

  private final String[] wsplit;

  /** Remembers the input string to compare it with the next symbol to scan. */
  public SrcgEarleyScan(String[] wsplit) {
    this.wsplit = wsplit;
    this.name = "scan";
    this.antneeded = 1;
  }

  @Override public List<Item> getConsequences() {
    if (antecedences.size() == antneeded) {
      String[] itemform = antecedences.get(0).getItemform();
      String clause = itemform[0];
      if (itemform[0].contains("->")) {
        Clause clauseparsed;
        try {
          clauseparsed = new Clause(clause);
        } catch (ParseException e) {
          e.printStackTrace();
          return this.consequences;
        }
        String pos = itemform[1];
        int posint = Integer.parseInt(pos);
        String i = itemform[2];
        int iint = Integer.parseInt(i);
        String j = itemform[3];
        int jint = Integer.parseInt(j);
        int place = clauseparsed.getLhs().getAbsolutePos(iint, jint);

        if (clauseparsed.getLhs().ifSymExists(iint, jint)
          && clauseparsed.getLhsSymAt(iint, jint).equals(wsplit[posint])) {
          ArrayList<String> newvector = new ArrayList<String>();
          for (int k = 0; k * 2 + 5 < itemform.length; k++) {
            newvector.add(itemform[2 * k + 4]);
            newvector.add(itemform[2 * k + 5]);
          }
          newvector.set(place*2, pos);
          newvector.set(place*2 + 1, String.valueOf(posint + 1));
          consequences.add(new SrcgEarleyActiveItem(clause, posint + 1, iint,
            jint + 1, newvector.toArray(new String[newvector.size()])));
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

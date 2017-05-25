package chartparsing.lcfrsrules;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import chartparsing.DynamicDeductionRule;
import common.Item;
import common.lcfrs.Clause;
import common.lcfrs.SrcgEarleyActiveItem;

/** Whenever the next symbol after the dot is the next terminal in the input, we
 * can scan it. */
public class SrcgEarleyScan implements DynamicDeductionRule {

  private List<Item> antecedences = new LinkedList<Item>();
  private List<Item> consequences = new LinkedList<Item>();
  private String name = "Scan";

  private String[] wsplit;

  private int antneeded = 1;

  /** Remembers the input string to compare it with the next symbol to scan. */
  public SrcgEarleyScan(String[] wsplit) {
    this.wsplit = wsplit;
  }

  @Override public void addAntecedence(Item item) {
    this.antecedences.add(item);
  }

  @Override public List<Item> getAntecedences() {
    return this.antecedences;
  }

  @Override public void setAntecedences(List<Item> antecedences) {
    this.antecedences = antecedences;
  }

  @Override public List<Item> getConsequences() {
    if (antecedences.size() == antneeded) {
      String[] itemform = antecedences.get(0).getItemform();
      String clause = itemform[0];
      if (itemform[0].contains("->")) {
        Clause clauseparsed = new Clause(clause);
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

  @Override public String getName() {
    return this.name;
  }

  @Override public int getAntecedencesNeeded() {
    return this.antneeded;
  }

  @Override public void clearItems() {
    this.antecedences = new LinkedList<Item>();
    this.consequences = new LinkedList<Item>();
  }

  @Override public String toString() {
    StringBuilder representation = new StringBuilder();
    representation.append("[A(φ) -> Φ,pos,<i,j>,ρ]");
    representation.append("\n______ φ(i,j) = w_pos\n");
    representation.append("[A(φ) -> Φ,pos,<i,j+1>,ρ']");
    return representation.toString();
  }
}

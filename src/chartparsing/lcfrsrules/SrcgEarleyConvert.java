package chartparsing.lcfrsrules;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import chartparsing.DynamicDeductionRule;
import common.Item;
import common.lcfrs.Clause;
import common.lcfrs.Predicate;
import common.lcfrs.SrcgEarleyPassiveItem;

/** Whenever we arrive at the end of the last argument, we convert the item into
 * a passive one. */
public class SrcgEarleyConvert implements DynamicDeductionRule {

  private List<Item> antecedences = new LinkedList<Item>();
  private List<Item> consequences = new LinkedList<Item>();
  private String name = "Convert";

  private int antneeded = 1;

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
        String i = itemform[2];
        int iint = Integer.parseInt(i);
        String j = itemform[3];
        int jint = Integer.parseInt(j);
        if (clauseparsed.getLhs().getDim() == iint
          && clauseparsed.getLhs().getArgumentByIndex(iint).length == jint) {

          ArrayList<String> newvector = new ArrayList<String>();
          Predicate lhs = clauseparsed.getLhs();
          for (int k = 0; k < lhs.getDim(); k++) {
            newvector.add(itemform[4 + lhs.getAbsolutePos(k + 1, 0)*2]);
            if (lhs.ifSymExists(k + 2, 0)) {
              newvector.add(itemform[4 + lhs.getAbsolutePos(k + 2, 0)*2 - 1]);
            } else {
              newvector.add(itemform[itemform.length - 1]);
            }
          }
          consequences
            .add(new SrcgEarleyPassiveItem(clauseparsed.getLhsNonterminal(),
              newvector.toArray(new String[newvector.size()])));
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
    representation.append("[B(ψ) -> Ψ,pos,<i,j>,ρ_B]");
    representation.append("\n______ |ψ(i)| = j, |ψ| = i, ρ_B(Ψ) = ρ\n");
    representation.append("[B,ρ]");
    return representation.toString();
  }

}

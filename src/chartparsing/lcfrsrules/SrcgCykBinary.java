package chartparsing.lcfrsrules;

import java.util.ArrayList;
import java.util.List;

import chartparsing.AbstractDynamicDeductionRule;
import common.Item;
import common.lcfrs.Clause;
import common.lcfrs.SrcgCykItem;

/** Similar to the binary complete rule in CYK for CFG. If there is a clause and
 * the vectors of two items that represent the rhs match, combine them to a new
 * item that represents the lhs with span over both. */
public class SrcgCykBinary extends AbstractDynamicDeductionRule {

  private final Clause clause;
  private final String[] wsplit;

  public SrcgCykBinary(Clause clause, String[] wsplit) {
    this.name = "complete";
    this.antneeded = 2;
    this.clause = clause;
    this.wsplit = wsplit;
  }

  @Override public List<Item> getConsequences() {
    if (antecedences.size() == antneeded) {
      String[] itemform1 = antecedences.get(0).getItemform();
      String[] itemform2 = antecedences.get(1).getItemform();
      calculateConsequences(itemform1, itemform2);
      calculateConsequences(itemform2, itemform1);
    }
    return this.consequences;
  }

  private void calculateConsequences(String[] itemform2, String[] itemform1) {
    String nt1 = itemform1[0];
    String nt2 = itemform2[0];
    if (nt2.equals(clause.getRhs().get(0).getNonterminal())
      && nt1.equals(clause.getRhs().get(1).getNonterminal())) {
      boolean looksgood = true;
      ArrayList<Integer> overallranges = new ArrayList<Integer>();
      for (String[] argument : clause.getLhs().getSymbols()) {
        ArrayList<String> vectorranges = new ArrayList<String>();
        for (String element : argument) {
          int[] indices = clause.getRhs().get(0).find(element);
          if (indices[0] == -1) {
            indices = clause.getRhs().get(1).find(element);
            if (indices[0] == -1) {
              vectorranges.add("?");
              vectorranges.add("?");
            } else {
              vectorranges.add(itemform1[(indices[0] - 1) * 2 + 1]);
              vectorranges.add(itemform1[(indices[0] - 1) * 2 + 2]);
            }
          } else {
            vectorranges.add(itemform2[(indices[0] - 1) * 2 + 1]);
            vectorranges.add(itemform2[(indices[0] - 1) * 2 + 2]);
          }
        }
        int i = 0;
        for (; i * 2 < vectorranges.size(); i++) {
          if (!vectorranges.get(i * 2).equals("?")) {
            break;
          }
        }
        int prevnum = Integer.parseInt(vectorranges.get(i * 2));
        while (i > 0) {
          i--;
          vectorranges.set(i * 2, String.valueOf(prevnum - 1));
          vectorranges.set(i * 2 + 1, String.valueOf(prevnum));
          if (prevnum == 0 || !wsplit[prevnum - 1].equals(argument[i])) {
            looksgood = false;
          }
          prevnum--;
        }
        i = 1;
        for (; i * 2 < vectorranges.size(); i++) {
          prevnum = Integer.parseInt(vectorranges.get(i * 2 - 1));
          if (vectorranges.get(i * 2).equals("?")) {
            vectorranges.set(i * 2, String.valueOf(prevnum));
            vectorranges.set(i * 2 + 1, String.valueOf(prevnum + 1));
            if (!wsplit[prevnum].equals(argument[i])) {
              looksgood = false;
            }
          } else if (!vectorranges.get(i * 2)
            .equals(vectorranges.get(i * 2 - 1))) {
            looksgood = false;
          }
        }
        for (String elem : vectorranges) {
          overallranges.add(Integer.parseInt(elem));
        }

      }
      if (looksgood && overallranges.size() > 0) {
        Integer[] newvector = SrcgDeductionUtils.getRangesForArguments(
          overallranges.toArray(new Integer[overallranges.size()]),
          clause.getLhs());
        consequences
          .add(new SrcgCykItem(clause.getLhs().getNonterminal(), newvector));
        this.name = "complete " + clause.toString();
      }
    }
  }

  @Override public String toString() {
    return "[" + clause.getRhs().get(0).toString() + ",ρ_B], ["
      + clause.getRhs().get(1).toString() + ",ρ_C]" + "\n______ \n" + "["
      + clause.getLhs().toString() + ",ρ_A]";
  }

}

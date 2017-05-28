package chartparsing.lcfrsrules;

import java.util.ArrayList;
import java.util.List;

import chartparsing.AbstractDynamicDeductionRule;
import common.Item;
import common.lcfrs.Clause;
import common.lcfrs.SrcgCykItem;

/** Similar to the Unary rule in extended CYK for CFG. If there is a chain rule
 * and an item for the rhs, get an lhs item with the same span. */
public class SrcgCykUnary extends AbstractDynamicDeductionRule {

  private final Clause clause;
  private final String[] wsplit;

  public SrcgCykUnary(Clause clause, String[] wsplit) {
    this.name = "Unary";
    this.antneeded = 1;
    this.clause = clause;
    this.wsplit = wsplit;
  }

  @Override public List<Item> getConsequences() {
    if (antecedences.size() == antneeded) {
      String[] itemform = antecedences.get(0).getItemform();
      String nt = itemform[0];
      // rest is vector

      if (nt.equals(clause.getRhs().get(0).getNonterminal())) {
        ArrayList<Integer> overallranges = new ArrayList<Integer>();
        for (String[] argument : clause.getLhs().getSymbols()) {
          ArrayList<String> vectorranges = new ArrayList<String>();
          for (String element : argument) {
            int[] indices = clause.getRhs().get(0).find(element);
            if (indices[0] == -1) {
              vectorranges.add("?");
              vectorranges.add("?");
            } else {
              vectorranges.add(itemform[(indices[0] - 1) * 2 + 1]);
              vectorranges.add(itemform[(indices[0] - 1) * 2 + 2]);
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
              return this.consequences;
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
                return this.consequences;
              }
            } else if (!vectorranges.get(i * 2)
              .equals(vectorranges.get(i * 2 - 1))) {
              return this.consequences;
            }
          }
          for (String elem : vectorranges) {
            overallranges.add(Integer.parseInt(elem));
          }
          // Can't handle something like B(a,X), I would need to create all
          // possible ranges for that and return a new item for each
          // Why would you need that anyway? Just put an a in the string where
          // you want it.
        }
        if (overallranges.size() > 0) {
          Integer[] newvector = SrcgDeductionUtils.getRangesForArguments(
            overallranges.toArray(new Integer[overallranges.size()]),
            clause.getLhs());
          consequences
            .add(new SrcgCykItem(clause.getLhs().getNonterminal(), newvector));
        }
      }

    }
    return this.consequences;
  }

  @Override public String toString() {
    return "[" + clause.getRhs().get(0).toString() + ",ρ]]" + "\n______ \n"
      + "[" + clause.getLhs().toString() + ",ρ]";
  }
}

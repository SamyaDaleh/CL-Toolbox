package chartparsing.lcfrsrules;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import chartparsing.AbstractDynamicDeductionRule;
import common.Item;
import common.lcfrs.Clause;
import common.lcfrs.Predicate;
import common.lcfrs.SrcgEarleyPassiveItem;

/** Whenever we arrive at the end of the last argument, we convert the item into
 * a passive one. */
public class SrcgEarleyConvert extends AbstractDynamicDeductionRule {

  public SrcgEarleyConvert() {
    this.name = "Convert";
    this.antneeded = 1;
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

          ArrayList<String> rangesforelements = new ArrayList<String>();
          rangesforelements
              .addAll(Arrays.asList(itemform).subList(4, itemform.length));

          Predicate lhs = clauseparsed.getLhs();
          String[] newvector = SrcgDeductionUtils.getRangesForArguments(
            rangesforelements.toArray(new String[rangesforelements.size()]),
            lhs);

          consequences.add(new SrcgEarleyPassiveItem(
            clauseparsed.getLhsNonterminal(), newvector));
        }
      }
    }
    return this.consequences;
  }

  @Override public String toString() {
    return "[B(ψ) -> Ψ,pos,<i,j>,ρ_B]"
      + "\n______ |ψ(i)| = j, |ψ| = i, ρ_B(Ψ) = ρ\n" + "[B,ρ]";
  }

}

package com.github.samyadaleh.cltoolbox.chartparsing.lcfrs.earley;

import com.github.samyadaleh.cltoolbox.chartparsing.dynamicdeductionrule.AbstractDynamicDeductionRule;
import com.github.samyadaleh.cltoolbox.chartparsing.item.ChartItemInterface;
import com.github.samyadaleh.cltoolbox.chartparsing.lcfrs.SrcgDeductionUtils;
import com.github.samyadaleh.cltoolbox.common.lcfrs.Clause;
import com.github.samyadaleh.cltoolbox.common.lcfrs.Predicate;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Whenever we arrive at the end of the last argument, we convert the item into
 * a passive one.
 */
public class SrcgEarleyConvert extends AbstractDynamicDeductionRule {

  public SrcgEarleyConvert() {
    this.name = "convert";
    this.antNeeded = 1;
  }

  @SuppressWarnings("unchecked") @Override
  public List<ChartItemInterface> getConsequences() throws ParseException {
    if (antecedences.size() == antNeeded) {
      String[] itemForm = antecedences.get(0).getItemForm();
      String clause = itemForm[0];
      if (itemForm[0].contains("->")) {
        Clause clauseParsed;
        clauseParsed = new Clause(clause);
        int iInt = Integer.parseInt(itemForm[2]);
        int jInt = Integer.parseInt(itemForm[3]);
        if (clauseParsed.getLhs().getDim() == iInt
            && clauseParsed.getLhs().getArgumentByIndex(iInt).length == jInt) {
          ArrayList<String> rangesForElements = new ArrayList<>(
              Arrays.asList(itemForm).subList(4, itemForm.length));
          Predicate lhs = clauseParsed.getLhs();
          List<String> newVector = (List<String>) SrcgDeductionUtils
              .getRangesForArguments(rangesForElements, lhs);
          ChartItemInterface consequence =
              new SrcgEarleyPassiveItem(clauseParsed.getLhs().getNonterminal(),
                  newVector);
          consequence.setTrees(antecedences.get(0).getTrees());
          logItemGeneration(consequence);
          consequences.add(consequence);
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

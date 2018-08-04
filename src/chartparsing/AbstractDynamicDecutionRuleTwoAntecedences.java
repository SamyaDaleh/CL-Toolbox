package chartparsing;

import java.text.ParseException;
import java.util.List;

public abstract class AbstractDynamicDecutionRuleTwoAntecedences
  extends AbstractDynamicDeductionRule {

  @Override public List<Item> getConsequences() throws ParseException {
    if (antecedences.size() == antNeeded) {
      String[] itemForm1 = antecedences.get(0).getItemform();
      String[] itemForm2 = antecedences.get(1).getItemform();
      calculateConsequences(itemForm1, itemForm2);
      calculateConsequences(itemForm2, itemForm1);
    }
    return consequences;
  }

  protected abstract void calculateConsequences(String[] itemForm1,
    String[] itemForm2) throws ParseException;

}

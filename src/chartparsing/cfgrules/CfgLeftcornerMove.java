package chartparsing.cfgrules;

import java.util.LinkedList;
import java.util.List;

import chartparsing.DynamicDeductionRule;
import common.ArrayUtils;
import common.Item;
import common.cfg.CfgDollarItem;

/** If the end of a rhs is encountered, move the topmost nonterminal from the
 * stack of lhs to the stack of completed items. */
public class CfgLeftcornerMove implements DynamicDeductionRule {

  private List<Item> antecedences = new LinkedList<Item>();
  private List<Item> consequences = new LinkedList<Item>();
  private String name = "move";

  private String[] nonterminals;

  private int antneeded = 1;

  public CfgLeftcornerMove(String[] nonterminals) {
    this.nonterminals = nonterminals;
  }

  @Override public void addAntecedence(Item item) {
    antecedences.add(item);
  }

  @Override public List<Item> getAntecedences() {
    return antecedences;
  }

  @Override public void setAntecedences(List<Item> antecedences) {
    this.antecedences = antecedences;
  }

  @Override public List<Item> getConsequences() {
    if (antecedences.size() == antneeded) {
      String[] itemform = antecedences.get(0).getItemform();
      String stackcompl = itemform[0];
      String stackpred = itemform[1];
      String[] stackpredsplit = stackpred.split(" ");
      String stacklhs = itemform[2];
      String[] stacklhssplit = stacklhs.split(" ");

      if (stackpredsplit[0].equals("$")) {
        for (String nt : nonterminals) {
          if (stacklhssplit[0].equals(nt)) {
            String newcompl;
            if (stackcompl.length() == 0) {
              newcompl = nt;
            } else {
              newcompl = nt + " " + stackcompl;
            }
            String newpred = ArrayUtils.getSubSequenceAsString(stackpredsplit,
              1, stackpredsplit.length);
            String newlhs = ArrayUtils.getSubSequenceAsString(stacklhssplit, 1,
              stacklhssplit.length);;
            consequences.add(new CfgDollarItem(newcompl, newpred, newlhs));
            break;
          }
        }
      }
    }
    return consequences;
  }

  @Override public String getName() {
    return this.name;
  }

  @Override public int getAntecedencesNeeded() {
    return this.antneeded;
  }

  @Override public String toString() {
    StringBuilder representation = new StringBuilder();
    representation.append("[α,$β,Aɣ]");
    representation.append("\n______ A ∈ N\n");
    representation.append("[Aα,β,ɣ]");
    return representation.toString();
  }

  @Override public void clearItems() {
    antecedences = new LinkedList<Item>();
    consequences = new LinkedList<Item>();
  }

}

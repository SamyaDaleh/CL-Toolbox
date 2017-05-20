package chartparsing.cfgrules;

import java.util.LinkedList;
import java.util.List;

import chartparsing.DynamicDeductionRule;
import common.ArrayUtils;
import common.Item;
import common.cfg.CfgDottedItem;

/** If in one item a dot is before a nonterminal and the other item is a rule
 * with that nonterminal as lhs and the dot at the end, move the dot over the
 * nonterminal. */
public class CfgEarleyComplete implements DynamicDeductionRule {

  private List<Item> antecedences = new LinkedList<Item>();
  private List<Item> consequences = new LinkedList<Item>();
  private String name = "complete";

  private int antneeded = 2;

  @Override public void addAntecedence(Item item) {
    antecedences.add(item);
  }

  @Override public void addConsequence(Item item) {
    consequences.add(item);
  }

  @Override public List<Item> getAntecedences() {
    return antecedences;
  }

  @Override public void setAntecedences(List<Item> antecedences) {
    this.antecedences = antecedences;
  }

  @Override public List<Item> getConsequences() {
    if (antecedences.size() == antneeded) {
      String[] itemform1 = antecedences.get(0).getItemform();
      String stack1 = itemform1[0];
      String[] stacksplit1 = stack1.split(" ");
      int i1 = Integer.parseInt(itemform1[1]);
      int j1 = Integer.parseInt(itemform1[2]);
      String[] itemform2 = antecedences.get(1).getItemform();
      String stack2 = itemform2[0];
      String[] stacksplit2 = stack2.split(" ");
      int j2 = Integer.parseInt(itemform2[1]);
      int k2 = Integer.parseInt(itemform2[2]);

      if (j1 == j2 && stack2.endsWith("•")) {
        for (int l = 0; l < stacksplit1.length; l++) {
          if (stacksplit1[l].startsWith("•") && stacksplit1[l]
            .substring(1, stacksplit1[l].length()).equals(stacksplit2[0])) {
            String newstack;

            if (l == stacksplit1.length - 1) {
              newstack = ArrayUtils.getSubSequenceAsString(stacksplit1, 0, l)
                + " " + stacksplit2[0] + " •";
            } else {
              newstack =
                ArrayUtils.getSubSequenceAsString(stacksplit1, 0, l) + " "
                  + stacksplit2[0] + " •" + ArrayUtils.getSubSequenceAsString(
                    stacksplit1, l + 1, stacksplit1.length);
            }

            consequences.add(new CfgDottedItem(newstack, i1, k2));
            break;
          }
        }
      } else if (k2 == i1 && stack1.endsWith("•")) {
        // the other way around
        for (int l = 0; l < stacksplit2.length; l++) {
          if (stacksplit2[l].startsWith("•") && stacksplit2[l]
            .substring(1, stacksplit2[l].length()).equals(stacksplit1[0])) {
            String newstack;

            if (l == stacksplit2.length - 1) {
              newstack = ArrayUtils.getSubSequenceAsString(stacksplit2, 0, l)
                + " " + stacksplit1[0] + " •";
            } else {
              newstack =
                ArrayUtils.getSubSequenceAsString(stacksplit2, 0, l) + " "
                  + stacksplit1[0] + " •" + ArrayUtils.getSubSequenceAsString(
                    stacksplit2, l + 1, stacksplit2.length);
            }

            consequences.add(new CfgDottedItem(newstack, j2, j1));
            break;
          }
        }
      }
    }
    return consequences;
  }

  @Override public void setConsequences(List<Item> consequences) {
    // ignore
  }

  @Override public String getName() {
    return this.name;
  }

  @Override public int getAntecedencesNeeded() {
    return this.antneeded;
  }

  @Override public String toString() {
    StringBuilder representation = new StringBuilder();
    representation.append("[A -> α •B β,i,j] [B -> ɣ •,j,k]");
    representation.append("\n______\n");
    representation.append("[A -> α B •β,i,k]");
    return representation.toString();
  }

  @Override public void clearItems() {
    antecedences = new LinkedList<Item>();
    consequences = new LinkedList<Item>();
  }

}

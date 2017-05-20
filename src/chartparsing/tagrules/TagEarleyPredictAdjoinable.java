package chartparsing.tagrules;

import java.util.LinkedList;
import java.util.List;

import chartparsing.DynamicDeductionRule;
import common.Item;
import common.tag.Tag;
import common.tag.TagEarleyItem;

/** If the dot is at a node where adjunction is possible, predict the auxiliary
 * tree that can be adjoined into that node. */
public class TagEarleyPredictadjoinable implements DynamicDeductionRule {

  private List<Item> antecedences = new LinkedList<Item>();
  private List<Item> consequences = new LinkedList<Item>();
  private String name = null;

  private String auxtreename = null;
  private Tag tag = null;

  private int antneeded = 1;

  /** Constructor takes an auxiliary tree for the items the rule shall derive,
   * also needs the grammar to retrieve information about the antecedence. */
  public TagEarleyPredictadjoinable(String auxtreename, Tag tag) {
    this.auxtreename = auxtreename;
    this.tag = tag;
    this.name = "predict adjoinable with " + auxtreename;
  }

  @Override public void addAntecedence(Item item) {
    this.antecedences.add(item);
  }

  @Override public void addConsequence(Item item) {
    this.consequences.add(item);
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
      String treename = itemform[0];
      String node = itemform[1];
      int l = Integer.parseInt(itemform[6]);
      boolean adjoinable = tag.isAdjoinable(auxtreename, treename, node);
      if (adjoinable && itemform[2].equals("la") && itemform[7].equals("0")) {
        consequences.add(new TagEarleyItem(auxtreename, "", "la", l,
          (Integer) null, null, l, false));
      }
    }
    return consequences;
  }

  @Override public void setConsequences(List<Item> consequences) {
    // ignore
  }

  @Override public String getName() {
    return name;
  }

  @Override public int getAntecedencesNeeded() {
    return antneeded;
  }

  @Override public void clearItems() {
    antecedences = new LinkedList<Item>();
    consequences = new LinkedList<Item>();
  }

  @Override public String toString() {
    StringBuilder representation = new StringBuilder();
    representation.append("[ɣ,p,la,i,j,k,l,0]");
    representation.append("\n______ " + auxtreename + " ∈ f_SA(ɣ,p)\n");
    representation.append("[" + auxtreename + ",ε,la,l,-,-,l,0]");
    return representation.toString();
  }

}

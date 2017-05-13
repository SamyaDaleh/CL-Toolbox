package chartparsing.tagrules;

import java.util.LinkedList;
import java.util.List;

import chartparsing.DynamicDeductionRule;
import common.Item;
import common.tag.Tag;
import common.tag.TagEarleyItem;
import common.tag.Vertex;

/** If in a node is substitution possible, predict the new tree that can be
 * substituted there. */
public class TagEarleyPredictsubst implements DynamicDeductionRule {

  List<Item> antecedences = new LinkedList<Item>();
  List<Item> consequences = new LinkedList<Item>();
  String name = null;

  String initreename = null;
  Tag tag = null;

  int antneeded = 1;

  /** Constructor takes an auxiliary tree for the items the rule shall derive,
   * also needs the grammar to retrieve information about the antecedence. */
  public TagEarleyPredictsubst(String auxtreename, Tag tag) {
    this.initreename = auxtreename;
    this.tag = tag;
    this.name = "predict subst";
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
      String pos = itemform[2];
      int i1 = Integer.parseInt(itemform[3]);
      String f1 = itemform[4];
      String f2 = itemform[5];
      int i2 = Integer.parseInt(itemform[6]);
      String adj = itemform[7];
      Vertex p = tag.getTree(treename).getNodeByGornAdress(node);
      boolean substnode = tag.isSubstitutionNode(p, treename);
      if (substnode && pos.equals("lb") && i1 == i2 && f1.equals("-")
        && f2.equals("-") && adj.equals("0")) {
        consequences.add(new TagEarleyItem(initreename, "", "la", i1,
          (Integer) null, null, i1, false));
      }
    }
    return consequences;
  }

  @Override public void setConsequences(List<Item> consequences) {
    // ignore
  }

  @Override public void setName(String name) {
    this.name = name;
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

}

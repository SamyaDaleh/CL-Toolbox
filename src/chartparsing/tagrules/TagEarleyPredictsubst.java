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

  private List<Item> antecedences = new LinkedList<Item>();
  private List<Item> consequences = new LinkedList<Item>();
  private String name = null;

  private String initreename = null;
  private Tag tag = null;

  private int antneeded = 1;

  /** Constructor takes an auxiliary tree for the items the rule shall derive,
   * also needs the grammar to retrieve information about the antecedence. */
  public TagEarleyPredictsubst(String auxtreename, Tag tag) {
    this.initreename = auxtreename;
    this.tag = tag;
    this.name = "predict substitution of " + auxtreename;
  }

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
    representation.append("[ɣ,p,lb,i,-,-,i,0]");
    representation.append("\n______ ɣ(p) a substitution node, " + initreename
      + " ∈ I, l(ɣ,p) = l(" + initreename + ",ε)\n");
    representation.append("[" + initreename + ",ε,la,i,-,-,i,0]");
    return representation.toString();
  }

}

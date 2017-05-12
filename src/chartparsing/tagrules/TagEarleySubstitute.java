package chartparsing.tagrules;

import java.util.LinkedList;
import java.util.List;

import chartparsing.DynamicDeductionRule;
import common.Item;
import common.tag.Tag;
import common.tag.TagEarleyItem;
import common.tag.Vertex;

/** If a potential initial tree is complete, substitute it if possible. */
public class TagEarleySubstitute implements DynamicDeductionRule {

  List<Item> antecedences = new LinkedList<Item>();
  List<Item> consequences = new LinkedList<Item>();
  String name = null;

  String outtreename = null;
  String outnode = null;
  Tag tag = null;

  int antneeded = 1;

  /** Constructor takes a tree and a node where maybe is, was, will be adjoined,
   * also needs the grammar to retrieve information about the antecedence. */
  public TagEarleySubstitute(String outtreename, String outnode, Tag tag) {
    this.outtreename = outtreename;
    this.outnode = outnode;
    this.tag = tag;
    this.name = "predict adjoined";
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
      String pos = itemform[2];
      int i = Integer.parseInt(itemform[3]);
      String f1 = itemform[4];
      String f2 = itemform[5];
      int j = Integer.parseInt(itemform[6]);
      String adj = itemform[7];
      Vertex p = tag.getTree(outtreename).getNodeByGornAdress(outnode);
      boolean substnode = tag.isSubstitutionNode(p, outtreename);
      if (substnode && f1.equals("null") && f2.equals("null") && adj.equals("0") && pos.equals("ra")) {
        consequences.add(new TagEarleyItem(outtreename, outnode, "rb", i,
          (Integer) null, null, j, false));
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

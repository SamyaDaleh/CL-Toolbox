package chartparsing.tagrules;

import java.util.LinkedList;
import java.util.List;

import chartparsing.DynamicDeductionRule;
import common.Item;
import common.tag.Tag;
import common.tag.TagEarleyItem;

/** If a potential initial tree is complete, substitute it if possible. */
public class TagEarleySubstitute implements DynamicDeductionRule {

  private List<Item> antecedences = new LinkedList<Item>();
  private List<Item> consequences = new LinkedList<Item>();
  private String name = null;

  private String outtreename = null;
  private String outnode = null;
  private Tag tag = null;

  private int antneeded = 1;

  /** Constructor takes a tree and a node where maybe is, was, will be adjoined,
   * also needs the grammar to retrieve information about the antecedence. */
  public TagEarleySubstitute(String outtreename, String outnode, Tag tag) {
    this.outtreename = outtreename;
    this.outnode = outnode;
    this.tag = tag;
    this.name = "substitute in " + outtreename + "(" + outnode + ")";
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
      int i = Integer.parseInt(itemform[3]);
      String f1 = itemform[4];
      String f2 = itemform[5];
      int j = Integer.parseInt(itemform[6]);
      String adj = itemform[7];
      if (tag.getInitialTree(treename) != null && node.equals("")
        && f1.equals("-") && f2.equals("-") && adj.equals("0")
        && pos.equals("ra")) {
        consequences.add(new TagEarleyItem(outtreename, outnode, "rb", i,
          (Integer) null, null, j, false));
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
    representation.append("[α,ε,ra,i,-,-,j,0]");
    representation.append("\n______ " + outtreename + "(" + outnode
      + ") a substitution node, α ∈ I, l(" + outtreename + "," + outnode
      + ") = l(α,ε)\n");
    representation.append("[" + outtreename + "," + outnode + ",rb,i,-,-,j,0]");
    return representation.toString();
  }

}

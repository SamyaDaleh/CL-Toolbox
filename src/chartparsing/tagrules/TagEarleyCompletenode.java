package chartparsing.tagrules;

import java.util.LinkedList;
import java.util.List;

import chartparsing.DynamicDeductionRule;
import common.Item;
import common.tag.Tag;
import common.tag.TagEarleyItem;

/** If you have one item in a node la and another matching in the same node in
 * rb, you can put both together. */
public class TagEarleyCompletenode implements DynamicDeductionRule {

  private List<Item> antecedences = new LinkedList<Item>();
  private List<Item> consequences = new LinkedList<Item>();
  private String name = "complete node";

  private Tag tag = null;

  private int antneeded = 2;

  /** Constructor needs the grammar to retrieve information about the
   * antecedences. */
  public TagEarleyCompletenode(Tag tag) {
    this.tag = tag;
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
      String[] itemform1 = antecedences.get(0).getItemform();
      String treename1 = itemform1[0];
      String node1 = itemform1[1];
      String pos1 = itemform1[2];
      int f = Integer.parseInt(itemform1[3]);
      String g = itemform1[4];
      String h = itemform1[5];
      int i1 = Integer.parseInt(itemform1[6]);
      String adj1 = itemform1[7];
      String[] itemform2 = antecedences.get(1).getItemform();
      String treename2 = itemform2[0];
      String node2 = itemform2[1];
      String pos2 = itemform2[2];
      int i2 = Integer.parseInt(itemform2[3]);
      String j = itemform2[4];
      String k = itemform2[5];
      int l = Integer.parseInt(itemform2[6]);
      String adj2 = itemform2[7];
      String label =
        tag.getTree(treename1).getNodeByGornAdress(node1).getLabel();
      if (treename1.equals(treename2) && node1.equals(node2)
        && tag.isInNonterminals(label)) {
        if (pos1.equals("la") && pos2.equals("rb") && i1 == i2
          && adj1.equals("0")) {
          String f1 = (g.equals("-")) ? j : g;
          String f2 = (h.equals("-")) ? k : h;
          consequences.add(
            new TagEarleyItem(treename1, node1, "ra", f, f1, f2, l, false));
        } else if (pos2.equals("la") && pos1.equals("rb") && l == f
          && adj2.equals("0")) {
          // the other way around
          String f1 = (g.equals("-")) ? j : g;
          String f2 = (h.equals("-")) ? k : h;
          consequences.add(
            new TagEarleyItem(treename2, node2, "ra", i2, f1, f2, i1, false));
        }
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
    representation.append("[ɣ,p,la,f,g,h,i,0], [ɣ,p,rb,i,j,k,l,adj]");
    representation.append("\n______ l(ɣ,p) ∈ N\n");
    representation.append("[ɣ,p,ra,f,g⊕j,h⊕k,l,0]");
    return representation.toString();
  }

}

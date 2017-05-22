package chartparsing.tagrules;

import java.util.LinkedList;
import java.util.List;

import chartparsing.DynamicDeductionRule;
import common.Item;
import common.tag.Tag;
import common.tag.TagEarleyItem;

/** If the node's label is the next input symbol, consume it. */
public class TagEarleyScanterm implements DynamicDeductionRule {

  private List<Item> antecedences = new LinkedList<Item>();
  private List<Item> consequences = new LinkedList<Item>();
  private String name = "scan term";

  private String[] wsplit = null;
  private Tag tag = null;

  private int antneeded = 1;

  /** Constructor takes the input string to compare with the tree labels, also
   * needs the grammar to retrieve information about the antecedence. */
  public TagEarleyScanterm(String[] wsplit, Tag tag) {
    this.wsplit = wsplit;
    this.tag = tag;
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
      int i = Integer.parseInt(itemform[3]);
      Integer j;
      Integer k;
      try {
        j = Integer.parseInt(itemform[4]);
        k = Integer.parseInt(itemform[5]);
      } catch (NumberFormatException e) {
        j = null;
        k = null;
      }
      int l = Integer.parseInt(itemform[6]);
      String adj = itemform[7];
      if (l < wsplit.length && pos.equals("la") && adj.equals("0")
        && tag.getTree(treename).getNodeByGornAdress(node).getLabel()
          .equals(wsplit[l])) {
        consequences.add(new TagEarleyItem(treename, node, "ra", i, (Integer) j,
          k, l + 1, false));
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
    representation.append("\n______ l(ɣ,p) = w_l\n");
    representation.append("[ɣ,p,ra,i,j,k,l+1,0]");
    return representation.toString();
  }

}
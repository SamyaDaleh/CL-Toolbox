package chartparsing.tagrules;

import java.util.LinkedList;
import java.util.List;

import chartparsing.DynamicDeductionRule;
import common.Item;
import common.tag.Tag;
import common.tag.TagCykItem;

/** From a single-child node move up to the parent node. */
public class TagCykMoveunary implements DynamicDeductionRule {

  List<Item> antecedences = new LinkedList<Item>();
  List<Item> consequences = new LinkedList<Item>();
  String name = null;

  Tag tag = null;

  int antneeded = 1;

  /** Constructor needs the grammar to retrieve information about the
   * antecedences. */
  public TagCykMoveunary(Tag tag) {
    this.tag = tag;
    this.name = "move-unary";
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
      int i = Integer.parseInt(itemform[2]);
      Integer f1;
      Integer f2;
      try {
        f1 = Integer.parseInt(itemform[3]);
        f2 = Integer.parseInt(itemform[4]);
      } catch (NumberFormatException e) {
        f1 = null;
        f2 = null;
      }
      int j = Integer.parseInt(itemform[5]);
      if (node.endsWith(".1⊤")) {
        String nodesib = tag.getTree(treename)
          .getNodeByGornAdress(node.substring(0, node.length() - 1))
          .getGornAddressOfPotentialRightSibling();
        if (tag.getTree(treename).getNodeByGornAdress(nodesib) == null) {
          String parentnode = node.substring(0, node.length() - 3) + "⊥";
          consequences.add(new TagCykItem(treename, parentnode, i, f1, f2, j));
        }
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

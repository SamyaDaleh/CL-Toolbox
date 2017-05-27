package chartparsing.tagrules;

import java.util.List;

import chartparsing.AbstractDynamicDeductionRule;
import common.Item;
import common.tag.Tag;
import common.tag.TagCykItem;

/** Goes from bottom into top position without adjoining. */
public class TagCykNulladjoin extends AbstractDynamicDeductionRule {
  
  private final Tag tag;

  /** Constructor needs the grammar to check if adjoins is obligatory. */
  public TagCykNulladjoin(Tag tag) {
    this.tag = tag;
    this.name = "null-adjoin";
    this.antneeded = 1;
  }

  @Override public List<Item> getConsequences() {
    if (antecedences.size() == antneeded) {
      String[] itemform = antecedences.get(0).getItemform();
      String treename = itemform[0];
      String node = itemform[1];
      String nodegorn;
      if (node.length() == 1) {
        nodegorn = "";
      } else {
        nodegorn = node.substring(0, node.length() - 1);
      }
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
      boolean obligatoryadjoin = tag.getTree(treename).isInOA(nodegorn);
      int j = Integer.parseInt(itemform[5]);
      if (node.endsWith("⊥") && !obligatoryadjoin) {
        String newnode = node.substring(0, node.length() - 1) + "⊤";
        consequences.add(new TagCykItem(treename, newnode, i, f1, f2, j));
      }
    }
    return consequences;
  }

  @Override public String toString() {
    return "[ɣ,p⊥,i,f1,f2,j]" + "\n______ f_OA(ɣ,p) = 0\n" + "[ɣ,p⊤,i,f1,f2,j]";
  }

}

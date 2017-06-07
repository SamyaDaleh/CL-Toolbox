package chartparsing.tagrules;

import java.util.List;

import chartparsing.AbstractDynamicDeductionRule;
import common.Item;
import common.tag.Tag;
import common.tag.TagCykItem;

/** Adjoin an auxiliary tree into an appropriate node in any other tree. */
public class TagCykAdjoin extends AbstractDynamicDeductionRule {

  private final Tag tag;

  /** Rule needs grammar to check if adjoin is possible. */
  public TagCykAdjoin(Tag tag) {
    this.tag = tag;
    this.name = "adjoin";
    this.antNeeded = 2;
  }

  @Override public List<Item> getConsequences() {
    if (antecedences.size() == antNeeded) {
      String[] itemForm1 = antecedences.get(0).getItemform();
      String[] itemForm2 = antecedences.get(1).getItemform();
      String treeName1 = itemForm1[0];
      String treeName2 = itemForm2[0];
      String node1 = itemForm1[1];
      String node2 = itemForm2[1];
      int i = Integer.parseInt(itemForm1[2]);
      int f12 = Integer.parseInt(itemForm2[2]);
      Integer f11;
      Integer f21;
      try {
        f11 = Integer.parseInt(itemForm1[3]);
        f21 = Integer.parseInt(itemForm1[4]);
      } catch (NumberFormatException e) {
        f11 = null;
        f21 = null;
      }
      Integer f1b;
      Integer f2b;
      try {
        f1b = Integer.parseInt(itemForm2[3]);
        f2b = Integer.parseInt(itemForm2[4]);
      } catch (NumberFormatException e) {
        f1b = null;
        f2b = null;
      }
      int j = Integer.parseInt(itemForm1[5]);
      int f22 = Integer.parseInt(itemForm2[5]);
      if (f11 != null && f11 == f12 && f21 != null && f21 == f22
        && tag.isAdjoinable(treeName1, treeName2,
          node2.substring(0, node2.length() - 1))
        && node1.equals("⊤") && node2.endsWith("⊥")) {
        consequences.add(new TagCykItem(treeName2,
          node2.substring(0, node2.length() - 1) + "⊤", i, f1b, f2b, j));
        String node2Name =
          (node2.length() > 1) ? node2.substring(0, node2.length() - 1) : "ε";
        this.name =
          "adjoin " + treeName2 + "[" + node2Name + "," + treeName1 + "]";
      } else if (f1b != null && f1b == i && f2b != null && f2b == j
        && tag.isAdjoinable(treeName2, treeName1,
          node1.substring(0, node1.length() - 1))
        && node2.equals("⊤") && node1.endsWith("⊥")) {
        // the other way around
        String node1Name =
          (node1.length() > 1) ? node1.substring(0, node1.length() - 1) : "ε";
        this.name =
          "adjoin " + treeName1 + "[" + node1Name + "," + treeName2 + "]";
        consequences.add(new TagCykItem(treeName1,
          node1.substring(0, node1.length() - 1) + "⊤", f12, f11, f21, f22));
      }

    }
    return consequences;
  }

  @Override public String toString() {
    return "[β,ε⊤,i,f1,f2,j] [ɣ,p⊥,f1,f1',f2',f2]" + "\n______ β ∈ f_SA(ɣ,p)\n"
      + "[ɣ,p⊤,i,f1',f2',j]";
  }

}

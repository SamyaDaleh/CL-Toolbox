package chartparsing.tagrules;

import java.util.List;

import chartparsing.AbstractDynamicDeductionRule;
import common.Item;
import common.tag.Tag;
import common.tag.TagEarleyItem;
import common.tag.Vertex;

/** If in a node is substitution possible, predict the new tree that can be
 * substituted there. */
public class TagEarleyPredictsubst extends AbstractDynamicDeductionRule {
  
  private final String initreename;
  private final Tag tag;

  /** Constructor takes an auxiliary tree for the items the rule shall derive,
   * also needs the grammar to retrieve information about the antecedence. */
  public TagEarleyPredictsubst(String auxtreename, Tag tag) {
    this.initreename = auxtreename;
    this.tag = tag;
    this.name = "predict substitution of " + auxtreename;
    this.antneeded = 1;
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

  @Override public String toString() {
    return "[ɣ,p,lb,i,-,-,i,0]" + "\n______ ɣ(p) a substitution node, "
        + initreename + " ∈ I, l(ɣ,p) = l(" + initreename + ",ε)\n" + "["
        + initreename + ",ε,la,i,-,-,i,0]";
  }

}

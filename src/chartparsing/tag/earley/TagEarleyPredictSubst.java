package chartparsing.tag.earley;

import java.util.List;

import chartparsing.AbstractDynamicDeductionRule;
import chartparsing.DeductionItem;
import chartparsing.Item;
import common.tag.Tag;
import common.tag.Vertex;

/**
 * If in a node is substitution possible, predict the new tree that can be
 * substituted there.
 */
public class TagEarleyPredictSubst extends AbstractDynamicDeductionRule {

  private final String iniTreeName;
  private final Tag tag;

  /**
   * Constructor takes an auxiliary tree for the items the rule shall derive,
   * also needs the grammar to retrieve information about the antecedence.
   */
  public TagEarleyPredictSubst(String auxTreeName, Tag tag) {
    this.iniTreeName = auxTreeName;
    this.tag = tag;
    this.name = "predict substitution of " + auxTreeName;
    this.antNeeded = 1;
  }

  @Override public List<Item> getConsequences() {
    if (antecedences.size() == antNeeded) {
      String[] itemForm = antecedences.get(0).getItemform();
      String treeName = itemForm[0];
      String node = itemForm[1];
      String pos = itemForm[2];
      String i1 = itemForm[3];
      String f1 = itemForm[4];
      String f2 = itemForm[5];
      String i2 = itemForm[6];
      String adj = itemForm[7];
      Vertex p = tag.getTree(treeName).getNodeByGornAdress(node);
      String substNodeLabel = p.getLabel();
      String iniTreeRootLabel =
        tag.getInitialTree(iniTreeName).getRoot().getLabel();
      boolean substNode = tag.isSubstitutionNode(p, treeName);
      if (substNode && pos.equals("lb") && i1.equals(i2) && f1.equals("-")
        && f2.equals("-") && adj.equals("0")
        && substNodeLabel.equals(iniTreeRootLabel)) {
        Item consequence =
          new DeductionItem(iniTreeName, "", "la", i1, "-", "-", i1, "0");
        consequence.setTree(tag.getInitialTree(iniTreeName));
        consequences.add(consequence);
      }
    }
    return consequences;
  }

  @Override public String toString() {
    return "[ɣ,p,lb,i,-,-,i,0]" + "\n______ ɣ(p) a substitution node, "
      + iniTreeName + " ∈ I, l(ɣ,p) = l(" + iniTreeName + ",ε)\n" + "["
      + iniTreeName + ",ε,la,i,-,-,i,0]";
  }

}

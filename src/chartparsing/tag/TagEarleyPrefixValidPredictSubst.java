package chartparsing.tag;

import java.util.List;

import chartparsing.AbstractDynamicDeductionRule;
import chartparsing.DynamicDeductionRule;
import chartparsing.Item;
import common.tag.Tag;
import common.tag.Vertex;

public class TagEarleyPrefixValidPredictSubst
  extends AbstractDynamicDeductionRule implements DynamicDeductionRule {

  private final Tag tag;
  private final String iniTreeName;

  public TagEarleyPrefixValidPredictSubst(String iniTreeName, Tag tag) {
    this.tag = tag;
    this.name = "predict substituted";
    this.iniTreeName = iniTreeName;
    this.antNeeded = 1;
  }

  @Override public List<Item> getConsequences() {
    if (antecedences.size() == antNeeded) {
      String[] itemForm = antecedences.get(0).getItemform();
      String treeName = itemForm[0];
      String node = itemForm[1];
      String pos = itemForm[2];
      String iGamma = itemForm[3];
      String i = itemForm[4];
      String j = itemForm[5];
      String k = itemForm[6];
      String l = itemForm[7];
      String adj = itemForm[8];
      Vertex p = tag.getTree(treeName).getNodeByGornAdress(node);
      if (pos.equals("la") && adj.equals("0") && iGamma.equals("~")
        && i.equals("~") && j.equals("~") && k.equals("~") && !l.equals("~")
        && tag.isSubstitutionNode(p, treeName)
        && p.getLabel().equals(tag.getTree(iniTreeName).getRoot().getLabel())) {
        consequences.add(new TagEarleyPrefixValidItem(iniTreeName, "", "la", l,
          l, "-", "-", l, false));
      }
    }
    return consequences;
  }

  @Override public String toString() {
    return "[ɣ,p,la,~,~,~,~,l,0]" + "\n______ " + iniTreeName
      + " ∈ I, ɣ(p) substitution node, l(ɣ,p) = l(\" + iniTreeName + \",ε)\n"
      + "[" + iniTreeName + ",ε,la,l,l,-,-,l,0]";
  }

}

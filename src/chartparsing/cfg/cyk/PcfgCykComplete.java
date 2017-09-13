package chartparsing.cfg.cyk;

import java.util.ArrayList;
import java.util.List;

import chartparsing.DynamicDeductionRule;
import chartparsing.Item;
import chartparsing.PItem;
import common.cfg.PcfgProductionRule;

/** Similar to the complete rule for CYK, but used for a PCFG and with weights
 * for probabilistic CYK parsing. */
public class PcfgCykComplete implements DynamicDeductionRule {

  private List<PItem> antecedences = new ArrayList<PItem>();
  private List<PItem> consequences = new ArrayList<PItem>();
  private String name = null;

  private final PcfgProductionRule pRule;

  private final int antneeded = 2;

  public PcfgCykComplete(PcfgProductionRule pRule) {
    this.pRule = pRule;
    this.name = "complete " + pRule.toString();
  }

  @Override public void addAntecedence(Item item) {
    this.antecedences.add((PItem) item);
  }

  @Override public List<Item> getAntecedences() {
    List<Item> outantecedences = new ArrayList<Item>();
    outantecedences.addAll(this.antecedences);
    return outantecedences;
  }

  @Override public void setAntecedences(List<Item> antecedences) {
    List<PItem> inAntecedences = new ArrayList<PItem>();
    for (Item item : antecedences) {
      inAntecedences.add((PItem) item);
    }
    this.antecedences = inAntecedences;
  }

  @Override public List<Item> getConsequences() {
    if (antecedences.size() == antneeded) {
      String[] itemForm1 = antecedences.get(0).getItemform();
      String[] itemForm2 = antecedences.get(1).getItemform();
      calculateConsequences(itemForm1, itemForm2);
      calculateConsequences(itemForm2, itemForm1);
    }
    List<Item> outcon = new ArrayList<Item>();
    outcon.addAll(this.consequences);
    return outcon;
  }

  private void calculateConsequences(String[] itemForm1, String[] itemForm2) {
    String nt1 = itemForm1[0];
    String i1 = itemForm1[1];
    int i1Int = Integer.parseInt(i1);
    String j1 = itemForm1[2];
    int j1Int = Integer.parseInt(j1);
    Double x1 = antecedences.get(0).getProbability();

    String nt2 = itemForm2[0];
    String i2 = itemForm2[1];
    int i2Int = Integer.parseInt(i2);
    String j2 = itemForm2[2];
    int j2Int = Integer.parseInt(j2);
    Double x2 = antecedences.get(1).getProbability();

    if (nt1.equals(pRule.getRhs()[0]) && nt2.equals(pRule.getRhs()[1])
      && j1Int == i2Int) {
      this.consequences.add(new PcfgCykItem(x1 + x2 + -Math.log(pRule.getP()),
        pRule.getLhs(), i1Int, j2Int));
    }
  }

  @Override public String getName() {
    return this.name;
  }

  @Override public int getAntecedencesNeeded() {
    return this.antneeded;
  }

  @Override public String toString() {
    return "x1 : [" + pRule.getRhs()[0] + ", i, j], x2 : [" + pRule.getRhs()[1]
      + ", j, k]" + "\n______ \n" + "x1 + x2 + |log("
      + String.valueOf(pRule.getP()) + ")| : [" + pRule.getLhs() + ", i, k]";
  }

  @Override public void clearItems() {
    antecedences = new ArrayList<PItem>();
    consequences = new ArrayList<PItem>();
  }

}

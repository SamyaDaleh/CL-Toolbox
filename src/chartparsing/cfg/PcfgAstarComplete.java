package chartparsing.cfg;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import chartparsing.DynamicDeductionRule;
import chartparsing.Item;
import chartparsing.PItem;
import common.cfg.PcfgProductionRule;

/** Similar to the complete rule for CYK, but used for a PCFG and with weights
 * for astar parsing. */
public class PcfgAstarComplete implements DynamicDeductionRule {

  private List<PItem> antecedences = new LinkedList<PItem>();
  private List<PItem> consequences = new LinkedList<PItem>();
  private String name = null;

  private final PcfgProductionRule pRule;
  private final Map<String, Double> outsides;

  private final int n;

  private final int antneeded = 2;

  public PcfgAstarComplete(PcfgProductionRule pRule,
    Map<String, Double> outsides, int n) {
    this.n = n;
    this.pRule = pRule;
    this.outsides = outsides;
    this.name = "complete " + pRule.toString();
  }

  @Override public void addAntecedence(Item item) {
    this.antecedences.add((PItem) item);
  }

  @Override public List<Item> getAntecedences() {
    List<Item> outantecedences = new LinkedList<Item>();
    outantecedences.addAll(this.antecedences);
    return outantecedences;
  }

  @Override public void setAntecedences(List<Item> antecedences) {
    List<PItem> inAntecedences = new LinkedList<PItem>();
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
    List<Item> outcon = new LinkedList<Item>();
    outcon.addAll(this.consequences);
    return outcon;
  }

  private void calculateConsequences(String[] itemForm1, String[] itemForm2) {
    String nt1 = itemForm1[0];
    String i1 = itemForm1[1];
    int i1Int = Integer.parseInt(i1);
    String j1 = itemForm1[2];
    int j1Int = Integer.parseInt(j1);
    Double w1 = antecedences.get(0).getProbability();

    String outKey1 = SxCalc.getOutsideKey(nt1, i1Int, j1Int - i1Int, n - j1Int);
    if (!outsides.containsKey(outKey1)) {
      return;
    }
    Double x1 = w1 - outsides
      .get(SxCalc.getOutsideKey(nt1, i1Int, j1Int - i1Int, n - j1Int));

    String nt2 = itemForm2[0];
    String i2 = itemForm2[1];
    int i2Int = Integer.parseInt(i2);
    String j2 = itemForm2[2];
    int j2Int = Integer.parseInt(j2);
    Double w2 = antecedences.get(1).getProbability();

    String outkey2 = SxCalc.getOutsideKey(nt2, i2Int, j2Int - i2Int, n - j2Int);
    if (!outsides.containsKey(outkey2)) {
      return;
    }
    Double x2 = w2 - outsides
      .get(SxCalc.getOutsideKey(nt2, i2Int, j2Int - i2Int, n - j2Int));

    if (nt1.equals(pRule.getRhs()[0]) && nt2.equals(pRule.getRhs()[1])
      && j1Int == i2Int) {
      String outKey3 =
        SxCalc.getOutsideKey(pRule.getLhs(), i1Int, j2Int - i1Int, n - j2Int);
      if (!outsides.containsKey(outKey3)) {
        return;
      }
      Double newOutP = outsides.get(outKey3);
      this.consequences.add(new PcfgAstarItem(x1 + x2 + -Math.log(pRule.getP()),
        newOutP, pRule.getLhs(), i1Int, j2Int));
    }
  }

  @Override public String getName() {
    return this.name;
  }

  @Override public int getAntecedencesNeeded() {
    return this.antneeded;
  }

  @Override public String toString() {
    return "x1 + out(" + pRule.getRhs()[0] + ", i, j - i, n - j) : ["
      + pRule.getRhs()[0] + ",i,j], x2 + out(" + pRule.getRhs()[1]
      + ", j, k - j, n - k) : [" + pRule.getRhs()[1] + ", j, k]" + "\n______ \n"
      + "x1 + x2 + |log(" + String.valueOf(pRule.getP()) + ")| + out("
      + pRule.getLhs() + ", i, k - i, n - k) : [" + pRule.getLhs() + ", i, k]";
  }

  @Override public void clearItems() {
    antecedences = new LinkedList<PItem>();
    consequences = new LinkedList<PItem>();
  }

}

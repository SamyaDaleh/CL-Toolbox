package chartparsing.cfgrules;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import astar.SxCalc;
import chartparsing.DynamicDeductionRule;
import common.Item;
import common.PItem;
import common.cfg.PcfgAstarItem;
import common.cfg.PcfgProductionRule;

/** Similar to the complete rule for CYK, but used for a PCFG and with weights
 * for astar parsing. */
public class PcfgAstarComplete implements DynamicDeductionRule {

  private List<PItem> antecedences = new LinkedList<PItem>();
  private List<PItem> consequences = new LinkedList<PItem>();
  private String name = null;

  PcfgProductionRule prule;
  Map<String, Double> outsides;

  int n;

  private int antneeded = 2;

  public PcfgAstarComplete(PcfgProductionRule prule,
    Map<String, Double> outsides, int n) {
    this.n = n;
    this.prule = prule;
    this.outsides = outsides;
    this.name = "Complete " + prule.toString();
  }

  @Override public void addAntecedence(Item item) {
    this.antecedences.add((PItem) item);
  }

  @Override public List<Item> getAntecedences() {
    List<Item> outantecedences = new LinkedList<Item>();
    for (PItem pitem : this.antecedences) {
      outantecedences.add(pitem);
    }
    return outantecedences;
  }

  @Override public void setAntecedences(List<Item> antecedences) {
    List<PItem> inantecedences = new LinkedList<PItem>();
    for (Item item : antecedences) {
      inantecedences.add((PItem) item);
    }
  }

  @Override public List<Item> getConsequences() {
    if (antecedences.size() == antneeded) {
      String[] itemform1 = antecedences.get(0).getItemform();
      String nt1 = itemform1[0];
      String i1 = itemform1[1];
      int i1int = Integer.parseInt(i1);
      String j1 = itemform1[2];
      int j1int = Integer.parseInt(j1);
      Double w1 = antecedences.get(0).getProbability();

      String outkey1 =
        SxCalc.getOutsideKey(nt1, i1int, j1int - i1int, n - j1int);
      if (!outsides.containsKey(outkey1)) {
        return new LinkedList<Item>();
      }
      Double x1 = w1 - outsides
        .get(SxCalc.getOutsideKey(nt1, i1int, j1int - i1int, n - j1int));

      String[] itemform2 = antecedences.get(1).getItemform();
      String nt2 = itemform2[0];
      String i2 = itemform2[1];
      int i2int = Integer.parseInt(i2);
      String j2 = itemform2[2];
      int j2int = Integer.parseInt(j2);
      Double w2 = antecedences.get(1).getProbability();

      String outkey2 =
        SxCalc.getOutsideKey(nt2, i2int, j2int - i2int, n - j2int);
      if (!outsides.containsKey(outkey2)) {
        return new LinkedList<Item>();
      }
      Double x2 = w2 - outsides
        .get(SxCalc.getOutsideKey(nt2, i2int, j2int - i2int, n - j2int));

      if (nt1.equals(prule.getRhs()[0]) && nt2.equals(prule.getRhs()[1])
        && j1int == i2int) {
        String outkey3 =
          SxCalc.getOutsideKey(prule.getLhs(), i1int, j2int - i1int, n - j2int);
        if (!outsides.containsKey(outkey3)) {
          return new LinkedList<Item>();
        }
        Double newoutw = outsides.get(outkey3);
        this.consequences
          .add(new PcfgAstarItem(x1 + x2 + -Math.log(prule.getP()), newoutw,
            prule.getLhs(), i1int, j2int));

      } else if (nt2.equals(prule.getRhs()[0]) && nt1.equals(prule.getRhs()[1])
        && j2int == i1int) {
        String outkey3 =
          SxCalc.getOutsideKey(prule.getLhs(), i2int, j1int - i2int, n - j1int);
        if (!outsides.containsKey(outkey3)) {
          return new LinkedList<Item>();
        }
        Double newoutw = outsides.get(outkey3);
        this.consequences
          .add(new PcfgAstarItem(x1 + x2 + -Math.log(prule.getP()), newoutw,
            prule.getLhs(), i2int, j1int));

      }
    }
    List<Item> outcon = new LinkedList<Item>();
    for (PItem pitem : this.consequences) {
      outcon.add(pitem);
    }
    return outcon;
  }

  @Override public String getName() {
    return this.name;
  }

  @Override public int getAntecedencesNeeded() {
    return this.antneeded;
  }

  @Override public String toString() {
    StringBuilder representation = new StringBuilder();
    representation
      .append("x1 + out(" + prule.getRhs()[0] + ", i, j - i, n - j) : ["
        + prule.getRhs()[0] + ",i,j], x2 + out(" + prule.getRhs()[1]
        + ", j, k - j, n - k) : [" + prule.getRhs()[1] + ", j, k]");
    representation.append("\n______ \n");
    representation.append("x1 + x2 + |log(" + String.valueOf(prule.getP())
      + ")| + out(" + prule.getLhs() + ", i, k - i, n - k) : [" + prule.getLhs()
      + ", i, k]");
    return representation.toString();
  }

  @Override public void clearItems() {
    antecedences = new LinkedList<PItem>();
    consequences = new LinkedList<PItem>();
  }

}

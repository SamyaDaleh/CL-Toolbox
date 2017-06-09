package chartparsing.converter;

import java.util.Map;

import astar.SxCalc;
import chartparsing.DynamicDeductionRule;
import chartparsing.ParsingSchema;
import chartparsing.StaticDeductionRule;
import chartparsing.cfgrules.PcfgAstarComplete;
import common.cfg.Cfg;
import common.cfg.Pcfg;
import common.cfg.PcfgAstarItem;
import common.cfg.PcfgProductionRule;

/** Creates parsing schemes for probabilistic parsing of CFGs. */
public class PcfgToDeductionRulesConverter {

  /** Converts a probabilistic CFG to a schema for a star parsing, which is
   * similar to CYK but with weights. */
  public static ParsingSchema pcfgToAstarRules(Pcfg pcfg, String w) {
    if (!(new Cfg(pcfg)).isInChomskyNormalForm()) {
      System.out.println(
        "PCFG must be in Chomsky Normal Form to apply this kind of astar parsing.");
    }
    String[] wSplit = w.split(" ");
    ParsingSchema schema = new ParsingSchema();

    Map<String, Double> insides = SxCalc.getInsides(pcfg, wSplit.length);
    Map<String, Double> outsides =
      SxCalc.getOutsides(insides, wSplit.length, pcfg);

    for (PcfgProductionRule pRule : pcfg.getProductionRules()) {
      if (pRule.getRhs().length == 1) {
        for (int i = 0; i < wSplit.length; i++) {
          if (pRule.getRhs()[0].equals(wSplit[i])) {
            StaticDeductionRule scan = new StaticDeductionRule();
            Double rulew = -Math.log(pRule.getP());
            Double outw = outsides.get(SxCalc.getOutsideKey(pRule.getLhs(), i,
              1, wSplit.length - 1 - i));
            scan.addConsequence(
              new PcfgAstarItem(rulew, outw, pRule.getLhs(), i, i + 1));
            scan.setName("Scan");
            schema.addAxiom(scan);
          }
        }
      } else {
        DynamicDeductionRule complete =
          new PcfgAstarComplete(pRule, outsides, wSplit.length);
        schema.addRule(complete);
      }
    }
    schema
      .addGoal(new PcfgAstarItem(0, 0, pcfg.getStartSymbol(), 0, wSplit.length));

    return schema;
  }

}

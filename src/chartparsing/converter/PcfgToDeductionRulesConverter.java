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

  /** Mapper to make the converting functions easier accessable. */
  public static ParsingSchema pcfgToParsingSchema(Pcfg pcfg, String w,
    String schema) {
    switch (schema) {
    case "astar":
      return pcfgToAstarRules(pcfg, w);
    default:
      return null;
    }
  }

  /** Converts a probabilistic CFG to a schema for a star parsing, which is
   * similar to CYK but with weights. */
  static ParsingSchema pcfgToAstarRules(Pcfg pcfg, String w) {
    if (!(new Cfg(pcfg)).isInChomskyNormalForm()) {
      System.out.println(
        "PCFG must be in Chomsky Normal Form to apply this kind of astar parsing.");
    }
    String[] wsplit = w.split(" ");
    ParsingSchema schema = new ParsingSchema();

    Map<String, Double> insides = SxCalc.getInsides(pcfg, wsplit.length);
    Map<String, Double> outsides =
      SxCalc.getOutsides(insides, wsplit.length, pcfg);

    for (PcfgProductionRule prule : pcfg.getProductionrules()) {
      if (prule.getRhs().length == 1) {
        for (int i = 0; i < wsplit.length; i++) {
          if (prule.getRhs()[0].equals(wsplit[i])) {
            StaticDeductionRule scan = new StaticDeductionRule();
            Double rulew = -Math.log(prule.getP());
            Double outw = outsides.get(SxCalc.getOutsideKey(prule.getLhs(), i,
              1, wsplit.length - 1 - i));
            scan.addConsequence(
              new PcfgAstarItem(rulew, outw, prule.getLhs(), i, i + 1));
            scan.setName("Scan");
            schema.addAxiom(scan);
          }
        }
      } else {
        DynamicDeductionRule complete =
          new PcfgAstarComplete(prule, outsides, wsplit.length);
        schema.addRule(complete);
      }
    }
    schema
      .addGoal(new PcfgAstarItem(0, 0, pcfg.getStartsymbol(), 0, wsplit.length));

    return schema;
  }

}

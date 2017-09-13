package chartparsing.converter;

import java.util.Map;

import chartparsing.DynamicDeductionRule;
import chartparsing.ParsingSchema;
import chartparsing.StaticDeductionRule;
import chartparsing.cfg.cyk.PcfgAstarComplete;
import chartparsing.cfg.cyk.PcfgAstarItem;
import chartparsing.cfg.cyk.PcfgCykComplete;
import chartparsing.cfg.cyk.PcfgCykItem;
import chartparsing.cfg.cyk.SxCalc;
import common.cfg.Cfg;
import common.cfg.Pcfg;
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
          if (!pRule.getRhs()[0].equals(wSplit[i])) {
            continue;
          }
          StaticDeductionRule scan = new StaticDeductionRule();
          Double rulew = -Math.log(pRule.getP());
          Double outw = outsides.get(
            SxCalc.getOutsideKey(pRule.getLhs(), i, 1, wSplit.length - 1 - i));
          scan.addConsequence(
            new PcfgAstarItem(rulew, outw, pRule.getLhs(), i, i + 1));
          scan.setName("scan " + pRule.toString());
          schema.addAxiom(scan);
        }
      } else {
        DynamicDeductionRule complete =
          new PcfgAstarComplete(pRule, outsides, wSplit.length);
        schema.addRule(complete);
      }
    }
    schema.addGoal(
      new PcfgAstarItem(0, 0, pcfg.getStartSymbol(), 0, wSplit.length));
    return schema;
  }

  /** Converts a probabilistic CFG to a schema for CYK parsing, which is similar
   * to CYK but with weights. */
  public static ParsingSchema pcfgToCykRules(Pcfg pcfg, String w) {
    if (!(new Cfg(pcfg)).isInChomskyNormalForm()) {
      System.out.println(
        "PCFG must be in Chomsky Normal Form to apply this kind of cyk parsing.");
    }
    String[] wSplit = w.split(" ");
    ParsingSchema schema = new ParsingSchema();

    for (PcfgProductionRule pRule : pcfg.getProductionRules()) {
      if (pRule.getRhs().length == 1) {
        for (int i = 0; i < wSplit.length; i++) {
          if (!pRule.getRhs()[0].equals(wSplit[i])) {
            continue;
          }
          StaticDeductionRule scan = new StaticDeductionRule();
          Double rulep = -Math.log(pRule.getP());
          scan.addConsequence(new PcfgCykItem(rulep, pRule.getLhs(), i, i + 1));
          scan.setName("scan " + pRule.toString());
          schema.addAxiom(scan);
        }
      } else {
        DynamicDeductionRule complete = new PcfgCykComplete(pRule);
        schema.addRule(complete);
      }
    }
    schema.addGoal(new PcfgCykItem(0, pcfg.getStartSymbol(), 0, wSplit.length));
    return schema;
  }

}

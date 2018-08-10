package com.github.samyadaleh.cltoolbox.chartparsing.converter;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.github.samyadaleh.cltoolbox.chartparsing.DynamicDeductionRule;
import com.github.samyadaleh.cltoolbox.chartparsing.PItem;
import com.github.samyadaleh.cltoolbox.chartparsing.ParsingSchema;
import com.github.samyadaleh.cltoolbox.chartparsing.StaticDeductionRule;
import com.github.samyadaleh.cltoolbox.chartparsing.cfg.cyk.PcfgAstarComplete;
import com.github.samyadaleh.cltoolbox.chartparsing.cfg.cyk.PcfgAstarItem;
import com.github.samyadaleh.cltoolbox.chartparsing.cfg.cyk.PcfgCykComplete;
import com.github.samyadaleh.cltoolbox.chartparsing.cfg.cyk.PcfgCykItem;
import com.github.samyadaleh.cltoolbox.chartparsing.cfg.cyk.SxCalc;
import com.github.samyadaleh.cltoolbox.common.cfg.Cfg;
import com.github.samyadaleh.cltoolbox.common.cfg.CfgProductionRule;
import com.github.samyadaleh.cltoolbox.common.cfg.Pcfg;
import com.github.samyadaleh.cltoolbox.common.cfg.PcfgProductionRule;
import com.github.samyadaleh.cltoolbox.common.tag.Tree;

/** Creates parsing schemes for probabilistic parsing of CFGs. */
public class PcfgToDeductionRulesConverter {

  /**
   * Converts a probabilistic CFG to a schema for a star parsing, which is
   * similar to CYK but with weights.
   */
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

  /**
   * Converts a probabilistic CFG to a schema for CYK parsing, which is similar
   * to CYK but with weights.
   * @throws ParseException
   */
  public static ParsingSchema pcfgToCykRules(Pcfg pcfg, String w)
    throws ParseException {
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
          PItem consequence = new PcfgCykItem(rulep, pRule.getLhs(), i, i + 1);
          List<Tree> derivedTrees = new ArrayList<Tree>();
          derivedTrees.add(
            new Tree(new CfgProductionRule(pRule.getLhs(), pRule.getRhs())));
          consequence.setTrees(derivedTrees);
          scan.addConsequence(consequence);
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

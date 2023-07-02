package com.github.samyadaleh.cltoolbox.chartparsing.converter.pcfg;

import com.github.samyadaleh.cltoolbox.chartparsing.dynamicdeductionrule.DynamicDeductionRuleInterface;
import com.github.samyadaleh.cltoolbox.chartparsing.ParsingSchema;
import com.github.samyadaleh.cltoolbox.chartparsing.item.ProbabilisticChartItemInterface;
import com.github.samyadaleh.cltoolbox.chartparsing.StaticDeductionRule;
import com.github.samyadaleh.cltoolbox.chartparsing.cfg.cyk.astar.PcfgAstarComplete;
import com.github.samyadaleh.cltoolbox.chartparsing.cfg.cyk.astar.PcfgAstarItem;
import com.github.samyadaleh.cltoolbox.chartparsing.cfg.cyk.astar.SxCalc;
import com.github.samyadaleh.cltoolbox.common.cfg.Cfg;
import com.github.samyadaleh.cltoolbox.common.cfg.CfgProductionRule;
import com.github.samyadaleh.cltoolbox.common.cfg.Pcfg;
import com.github.samyadaleh.cltoolbox.common.cfg.PcfgProductionRule;
import com.github.samyadaleh.cltoolbox.common.tag.Tree;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.github.samyadaleh.cltoolbox.common.Constants.DEDUCTION_RULE_PCFG_CYK_AXIOM;

public class PcfgToAstarRulesConverter {

  /**
   * Converts a probabilistic CFG to a schema for a star parsing, which is
   * similar to CYK but with weights.
   */
  public static ParsingSchema pcfgToAstarRules(Pcfg pcfg, String w)
      throws ParseException {
    if (!(new Cfg(pcfg)).isInChomskyNormalForm()) {
      throw new ParseException(
          "PCFG must be in Chomsky Normal Form to apply this kind of astar parsing.",
          1);
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
          double rulew = -Math.log(pRule.getP());
          Double outw = outsides.get(SxCalc
              .getOutsideKey(pRule.getLhs(), i, 1, wSplit.length - 1 - i));
          ProbabilisticChartItemInterface conequence =
              new PcfgAstarItem(rulew, outw, pRule.getLhs(), i, i + 1);
          List<Tree> derivedTrees = new ArrayList<>();
          derivedTrees.add(
              new Tree(new CfgProductionRule(pRule.getLhs(), pRule.getRhs())));
          conequence.setTrees(derivedTrees);
          scan.addConsequence(conequence);
          scan.setName(DEDUCTION_RULE_PCFG_CYK_AXIOM + " " + pRule.toString());
          schema.addAxiom(scan);
        }
      } else {
        DynamicDeductionRuleInterface complete =
            new PcfgAstarComplete(pRule, outsides, wSplit.length);
        schema.addRule(complete);
      }
    }
    schema.addGoal(
        new PcfgAstarItem(0, 0, pcfg.getStartSymbol(), 0, wSplit.length));
    return schema;
  }
}

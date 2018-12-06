package com.github.samyadaleh.cltoolbox.chartparsing.converter.pcfg;

import com.github.samyadaleh.cltoolbox.chartparsing.dynamicdeductionrule.DynamicDeductionRuleInterface;
import com.github.samyadaleh.cltoolbox.chartparsing.ParsingSchema;
import com.github.samyadaleh.cltoolbox.chartparsing.item.ProbabilisticChartItemInterface;
import com.github.samyadaleh.cltoolbox.chartparsing.StaticDeductionRule;
import com.github.samyadaleh.cltoolbox.chartparsing.cfg.cyk.PcfgCykComplete;
import com.github.samyadaleh.cltoolbox.chartparsing.cfg.cyk.PcfgCykItem;
import com.github.samyadaleh.cltoolbox.common.cfg.Cfg;
import com.github.samyadaleh.cltoolbox.common.cfg.CfgProductionRule;
import com.github.samyadaleh.cltoolbox.common.cfg.Pcfg;
import com.github.samyadaleh.cltoolbox.common.cfg.PcfgProductionRule;
import com.github.samyadaleh.cltoolbox.common.tag.Tree;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import static com.github.samyadaleh.cltoolbox.common.Constants.DEDUCTION_RULE_PCFG_CYK_AXIOM;

public class PcfgToCykRulesConverter {
  private static final Logger log = LogManager.getLogger();

  /**
   * Converts a probabilistic CFG to a schema for CYK parsing, which is similar
   * to CYK but with weights.
   */
  public static ParsingSchema pcfgToCykRules(Pcfg pcfg, String w)
      throws ParseException {
    if (!(new Cfg(pcfg)).isInChomskyNormalForm()) {
      log.info(
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
          ProbabilisticChartItemInterface consequence =
              new PcfgCykItem(rulep, pRule.getLhs(), i, i + 1);
          List<Tree> derivedTrees = new ArrayList<>();
          derivedTrees.add(
              new Tree(new CfgProductionRule(pRule.getLhs(), pRule.getRhs())));
          consequence.setTrees(derivedTrees);
          scan.addConsequence(consequence);
          scan.setName(DEDUCTION_RULE_PCFG_CYK_AXIOM + " " + pRule.toString());
          schema.addAxiom(scan);
        }
      } else {
        DynamicDeductionRuleInterface complete = new PcfgCykComplete(pRule);
        schema.addRule(complete);
      }
    }
    schema.addGoal(new PcfgCykItem(0, pcfg.getStartSymbol(), 0, wSplit.length));
    return schema;
  }
}

package com.github.samyadaleh.cltoolbox.chartparsing.converter.cfg;

import com.github.samyadaleh.cltoolbox.chartparsing.item.DeductionChartItem;
import com.github.samyadaleh.cltoolbox.chartparsing.dynamicdeductionrule.DynamicDeductionRuleInterface;
import com.github.samyadaleh.cltoolbox.chartparsing.ParsingSchema;
import com.github.samyadaleh.cltoolbox.chartparsing.StaticDeductionRule;
import com.github.samyadaleh.cltoolbox.chartparsing.cfg.topdown.CfgTopDownPredict;
import com.github.samyadaleh.cltoolbox.chartparsing.cfg.topdown.CfgTopDownScan;
import com.github.samyadaleh.cltoolbox.common.cfg.Cfg;
import com.github.samyadaleh.cltoolbox.common.cfg.CfgProductionRule;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static com.github.samyadaleh.cltoolbox.common.Constants.DEDUCTION_RULE_CFG_TOPDOWN_AXIOM;

public class CfgToTopDownRulesConverter {
  private static final Logger log = LogManager.getLogger();

  /**
   * Converts a cfg to a parsing scheme for Topdown parsing. Based on
   * https://user.phil.hhu.de/~kallmeyer/Parsing/deduction.pdf
   */
  public static ParsingSchema cfgToTopDownRules(Cfg cfg, String w) {
    if (cfg.hasLeftRecursion()) {
      log.info("CFG must not contain left recursion for TopDown parsing.");
      return null;
    }
    String[] wSplit = w.split(" ");
    ParsingSchema schema = new ParsingSchema();
    DynamicDeductionRuleInterface scan = new CfgTopDownScan(wSplit);
    schema.addRule(scan);

    for (CfgProductionRule rule : cfg.getProductionRules()) {
      DynamicDeductionRuleInterface predict = new CfgTopDownPredict(rule);
      schema.addRule(predict);
    }

    StaticDeductionRule axiom = new StaticDeductionRule();
    axiom.addConsequence(new DeductionChartItem(cfg.getStartSymbol(), "0"));
    axiom.setName(DEDUCTION_RULE_CFG_TOPDOWN_AXIOM);
    schema.addAxiom(axiom);
    schema.addGoal(new DeductionChartItem("", String.valueOf(wSplit.length)));
    return schema;
  }
}

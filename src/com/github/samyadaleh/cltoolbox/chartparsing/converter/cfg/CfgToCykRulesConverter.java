package com.github.samyadaleh.cltoolbox.chartparsing.converter.cfg;

import com.github.samyadaleh.cltoolbox.chartparsing.*;
import com.github.samyadaleh.cltoolbox.chartparsing.cfg.cyk.CfgCykComplete;
import com.github.samyadaleh.cltoolbox.chartparsing.cfg.cyk.CfgCykCompleteGeneral;
import com.github.samyadaleh.cltoolbox.chartparsing.cfg.cyk.CfgCykCompleteUnary;
import com.github.samyadaleh.cltoolbox.common.cfg.Cfg;
import com.github.samyadaleh.cltoolbox.common.cfg.CfgProductionRule;
import com.github.samyadaleh.cltoolbox.common.tag.Tree;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class CfgToCykRulesConverter {
  private static final Logger log = LogManager.getLogger();

  /**
   * Converts grammar into rules for CYK parsing for CNF.
   */
  public static ParsingSchema cfgToCykRules(Cfg cfg, String w)
      throws ParseException {
    if (!cfg.isInChomskyNormalForm()) {
      log.info("Grammar has to be in Chomsky Normal Form.");
      return null;
    }
    String[] wSplit = w.split(" ");
    ParsingSchema schema = new ParsingSchema();

    for (CfgProductionRule rule : cfg.getProductionRules()) {
      if (rule.getRhs().length == 1) {
        addCykScanRules(wSplit, schema, rule);
      } else {
        DynamicDeductionRuleInterface complete = new CfgCykComplete(rule);
        schema.addRule(complete);
      }
    }
    schema.addGoal(new DeductionChartItem(cfg.getStartSymbol(), "0",
        String.valueOf(wSplit.length)));
    return schema;
  }

  /**
   * Like CYK parsing, but with an additional deduction rule for chain rules,
   * hence grammar needs only to be in Canonical Two Form. Source: Giogio Satta,
   * ESSLLI 2013
   */
  public static ParsingSchema cfgToCykExtendedRules(Cfg cfg, String w)
      throws ParseException {
    if (!cfg.isInCanonicalTwoForm()) {
      log.info("Grammar has to be in Canonical Two Form.");
      return null;
    }
    String[] wSplit = w.split(" ");
    ParsingSchema schema = new ParsingSchema();

    for (CfgProductionRule rule : cfg.getProductionRules()) {
      if (rule.getRhs().length == 1) {
        if (cfg.terminalsContain(rule.getRhs()[0])) {
          addCykScanRules(wSplit, schema, rule);
        } else {
          DynamicDeductionRuleInterface complete =
              new CfgCykCompleteUnary(rule);
          schema.addRule(complete);
        }
      } else {
        DynamicDeductionRuleInterface complete = new CfgCykComplete(rule);
        schema.addRule(complete);
      }
    }
    schema.addGoal(new DeductionChartItem(cfg.getStartSymbol(), "0",
        String.valueOf(wSplit.length)));
    return schema;
  }

  private static void addCykScanRules(String[] wSplit, ParsingSchema schema,
      CfgProductionRule rule) throws ParseException {
    for (int i = 0; i < wSplit.length; i++) {
      if (wSplit[i].equals(rule.getRhs()[0])) {
        StaticDeductionRule scan = new StaticDeductionRule();
        ChartItemInterface consequence =
            new DeductionChartItem(rule.getLhs(), String.valueOf(i), "1");
        List<Tree> derivedTrees = new ArrayList<>();
        derivedTrees.add(new Tree(rule));
        consequence.setTrees(derivedTrees);
        scan.addConsequence(consequence);
        scan.setName("scan " + rule.toString());
        schema.addAxiom(scan);
      }
    }
  }

  public static ParsingSchema cfgToCykGeneralRules(Cfg cfg, String w) {
    String[] wSplit = w.split(" ");
    ParsingSchema schema = new ParsingSchema();

    for (int i = 0; i < wSplit.length; i++) {
      StaticDeductionRule scan = new StaticDeductionRule();
      scan.addConsequence(
          new DeductionChartItem(wSplit[i], String.valueOf(i), "1"));
      scan.setName("scan " + wSplit[i]);
      schema.addAxiom(scan);
      StaticDeductionRule scanEps = new StaticDeductionRule();
      scanEps
          .addConsequence(new DeductionChartItem("", String.valueOf(i), "0"));
      scanEps.setName("scan ε");
      schema.addAxiom(scanEps);
    }
    StaticDeductionRule scanEps = new StaticDeductionRule();
    scanEps.addConsequence(
        new DeductionChartItem("", String.valueOf(wSplit.length), "0"));
    scanEps.setName("scan ε");
    schema.addAxiom(scanEps);

    for (CfgProductionRule rule : cfg.getProductionRules()) {
      DynamicDeductionRuleInterface complete = new CfgCykCompleteGeneral(rule);
      schema.addRule(complete);
    }
    schema.addGoal(new DeductionChartItem(cfg.getStartSymbol(), "0",
        String.valueOf(wSplit.length)));
    return schema;
  }
}

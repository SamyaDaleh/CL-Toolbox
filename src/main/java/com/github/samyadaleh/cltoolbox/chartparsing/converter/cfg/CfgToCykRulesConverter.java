package com.github.samyadaleh.cltoolbox.chartparsing.converter.cfg;

import com.github.samyadaleh.cltoolbox.chartparsing.ParsingSchema;
import com.github.samyadaleh.cltoolbox.chartparsing.StaticDeductionRule;
import com.github.samyadaleh.cltoolbox.chartparsing.cfg.cyk.CfgCykComplete;
import com.github.samyadaleh.cltoolbox.chartparsing.cfg.cyk.CfgCykCompleteGeneral;
import com.github.samyadaleh.cltoolbox.chartparsing.cfg.cyk.CfgCykCompleteUnary;
import com.github.samyadaleh.cltoolbox.chartparsing.dynamicdeductionrule.DynamicDeductionRuleInterface;
import com.github.samyadaleh.cltoolbox.chartparsing.item.ChartItemInterface;
import com.github.samyadaleh.cltoolbox.chartparsing.item.DeductionChartItem;
import com.github.samyadaleh.cltoolbox.common.cfg.Cfg;
import com.github.samyadaleh.cltoolbox.common.cfg.CfgProductionRule;
import com.github.samyadaleh.cltoolbox.common.tag.Tree;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import static com.github.samyadaleh.cltoolbox.common.Constants.DEDUCTION_RULE_CFG_CYK_AXIOM;
import static com.github.samyadaleh.cltoolbox.common.Constants.DEDUCTION_RULE_CFG_CYK_AXIOM_EPSILON;

public class CfgToCykRulesConverter {

  /**
   * Converts grammar into rules for CYK parsing for CNF.
   */
  public static ParsingSchema cfgToCykRules(Cfg cfg, String w)
      throws ParseException {
    if (!cfg.isInChomskyNormalForm()) {
      throw new ParseException("Grammar has to be in Chomsky Normal Form.", 1);
    }
    String[] wSplit = w.split(" ");
    int wLength = wSplit[0].equals("") || wSplit[0].equals("ε")
        ? 0 : wSplit.length;
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
        String.valueOf(wLength)));
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
      throw new ParseException("Grammar has to be in Canonical Two Form.", 1);
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
        String itemLength = wSplit[i].equals("") || wSplit[i].equals("ε")
            ? "0" : "1";
        ChartItemInterface consequence =
            new DeductionChartItem(rule.getLhs(), String.valueOf(i),
                itemLength);
        List<Tree> derivedTrees = new ArrayList<>();
        derivedTrees.add(new Tree(rule));
        consequence.setTrees(derivedTrees);
        scan.addConsequence(consequence);
        scan.setName(DEDUCTION_RULE_CFG_CYK_AXIOM + " " + rule.toString());
        schema.addAxiom(scan);
      }
    }
  }

  public static ParsingSchema cfgToCykGeneralRules(Cfg cfg, String w) {
    String[] wSplit = w.split(" ");
    ParsingSchema schema = new ParsingSchema();
    if (!"".equals(wSplit[0])) {
      for (int i = 0; i < wSplit.length; i++) {
        StaticDeductionRule scan = new StaticDeductionRule();
        scan.addConsequence(
            new DeductionChartItem(wSplit[i], String.valueOf(i), "1"));
        scan.setName(DEDUCTION_RULE_CFG_CYK_AXIOM + " " + wSplit[i]);
        schema.addAxiom(scan);
        StaticDeductionRule scanEps = new StaticDeductionRule();
        scanEps
            .addConsequence(new DeductionChartItem("", String.valueOf(i), "0"));
        scanEps.setName(DEDUCTION_RULE_CFG_CYK_AXIOM_EPSILON);
        schema.addAxiom(scanEps);
      }
      StaticDeductionRule scanEps = new StaticDeductionRule();
      scanEps.addConsequence(
          new DeductionChartItem("", String.valueOf(wSplit.length), "0"));
      scanEps.setName(DEDUCTION_RULE_CFG_CYK_AXIOM_EPSILON);
      schema.addAxiom(scanEps);
    } else {
      StaticDeductionRule scanEps = new StaticDeductionRule();
      scanEps.addConsequence(new DeductionChartItem("", "0", "0"));
      scanEps.setName(DEDUCTION_RULE_CFG_CYK_AXIOM_EPSILON);
      schema.addAxiom(scanEps);
    }

    for (CfgProductionRule rule : cfg.getProductionRules()) {
      DynamicDeductionRuleInterface complete = new CfgCykCompleteGeneral(rule);
      schema.addRule(complete);
    }
    if (w.length() == 0) {
      schema.addGoal(new DeductionChartItem(cfg.getStartSymbol(), "0", "0"));
    } else {
      schema.addGoal(new DeductionChartItem(cfg.getStartSymbol(), "0",
          String.valueOf(wSplit.length)));
    }
    return schema;
  }
}

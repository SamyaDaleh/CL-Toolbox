package com.github.samyadaleh.cltoolbox.chartparsing.converter.lcfrs;

import com.github.samyadaleh.cltoolbox.chartparsing.item.ChartItemInterface;
import com.github.samyadaleh.cltoolbox.chartparsing.dynamicdeductionrule.DynamicDeductionRuleInterface;
import com.github.samyadaleh.cltoolbox.chartparsing.ParsingSchema;
import com.github.samyadaleh.cltoolbox.chartparsing.StaticDeductionRule;
import com.github.samyadaleh.cltoolbox.chartparsing.lcfrs.earley.*;
import com.github.samyadaleh.cltoolbox.common.TreeUtils;
import com.github.samyadaleh.cltoolbox.common.lcfrs.Clause;
import com.github.samyadaleh.cltoolbox.common.lcfrs.RangeVector;
import com.github.samyadaleh.cltoolbox.common.lcfrs.Srcg;
import com.github.samyadaleh.cltoolbox.common.tag.Tree;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class LcfrsToEarleyRulesConverter {
  private static final Logger log = LogManager.getLogger();

  public static ParsingSchema srcgToEarleyRules(Srcg srcg, String w) {
    if (srcg.hasEpsilonProductions()) {
      log.info(
          "sRCG is not allowed to have epsilon productions for this Earley algorithm.");
      return null;
    }
    if (!srcg.isOrdered()) {
      log.info("sRCG must be ordered for this Earley algorithm.");
      return null;
    }
    String[] wsplit = w.split(" ");
    ParsingSchema schema = new ParsingSchema();

    for (Clause clause : srcg.getClauses()) {
      DynamicDeductionRuleInterface predict = new SrcgEarleyPredict(clause);
      schema.addRule(predict);
      if (!clause.getLhs().getNonterminal().equals(srcg.getStartSymbol())) {
        continue;
      }
      StaticDeductionRule initialize = new StaticDeductionRule();
      ChartItemInterface consequence =
          new SrcgEarleyActiveItem(clause.toString(), 0, 1, 0,
              new RangeVector(clause.getLhs().getSymbolsAsPlainArray().length));
      List<Tree> derivedTrees = new ArrayList<>();
      derivedTrees.add(TreeUtils.getTreeOfSrcgClause(clause));
      consequence.setTrees(derivedTrees);
      initialize.addConsequence(consequence);
      initialize.setName("initialize");
      schema.addAxiom(initialize);
      schema.addGoal(
          new SrcgEarleyActiveItem(clause.toString(), wsplit.length, 1,
              clause.getLhs().getSymbolsAsPlainArray().length, new RangeVector(
              clause.getLhs().getSymbolsAsPlainArray().length)));
    }
    DynamicDeductionRuleInterface scan = new SrcgEarleyScan(wsplit);
    schema.addRule(scan);
    DynamicDeductionRuleInterface suspend =
        new SrcgEarleySuspend(srcg.getVariables());
    schema.addRule(suspend);
    DynamicDeductionRuleInterface convert = new SrcgEarleyConvert();
    schema.addRule(convert);
    DynamicDeductionRuleInterface complete = new SrcgEarleyComplete();
    schema.addRule(complete);
    DynamicDeductionRuleInterface resume =
        new SrcgEarleyResume(srcg.getVariables());
    schema.addRule(resume);
    return schema;
  }
}

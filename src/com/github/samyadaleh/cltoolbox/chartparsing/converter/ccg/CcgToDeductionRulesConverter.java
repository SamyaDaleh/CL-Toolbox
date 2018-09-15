package com.github.samyadaleh.cltoolbox.chartparsing.converter.ccg;

import com.github.samyadaleh.cltoolbox.chartparsing.ParsingSchema;
import com.github.samyadaleh.cltoolbox.chartparsing.StaticDeductionRule;
import com.github.samyadaleh.cltoolbox.chartparsing.ccg.*;
import com.github.samyadaleh.cltoolbox.chartparsing.item.DeductionChartItem;
import com.github.samyadaleh.cltoolbox.common.ccg.Ccg;
import com.github.samyadaleh.cltoolbox.common.tag.Tree;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.ParseException;

/**
 * Class that contains the methods to convert CCGs into Parsing Schemas.
 */
public class CcgToDeductionRulesConverter {
  private static final Logger log = LogManager.getLogger();

  /**
   * Returns a Parsing Schema for a CCG deductive system.
   */
  public static ParsingSchema ccgToDeductionRules(Ccg ccg, String w) {
    String[] wSplit = w.split(" ");
    ParsingSchema schema = new ParsingSchema();
    for (int i = 0; i < wSplit.length; i++) {
      for (String cat : ccg.getLexicon().get(wSplit[i])) {
        StaticDeductionRule axiom = new StaticDeductionRule();
        DeductionChartItem consequence =
            new DeductionChartItem(cat, String.valueOf(i),
                String.valueOf(i + 1));
        try {
          consequence.getTrees().add(new Tree(
              "( " + cat.replace('(', '[').replace(')', ']') + " " + wSplit[i]
                  + ")"));
          axiom.addConsequence(consequence);
          axiom.setName("axiom");
          schema.addAxiom(axiom);
        } catch (ParseException e) {
          log.error(e.getMessage(), e);
        }
      }
    }
    schema.addRule(new CcgForwardApplication());
    schema.addRule(new CcgBackwardApplication());
    schema.addRule(new CcgForwardComposition1());
    schema.addRule(new CcgForwardComposition2());
    schema.addRule(new CcgBackwardComposition1());
    schema.addRule(new CcgBackwardComposition2());
    schema.addGoal(
        new DeductionChartItem("S", "0", String.valueOf(wSplit.length)));
    return schema;
  }
}

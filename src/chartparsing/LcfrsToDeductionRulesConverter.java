package chartparsing;

import chartparsing.lcfrsrules.SrcgEarleyConvert;
import chartparsing.lcfrsrules.SrcgEarleyPredict;
import chartparsing.lcfrsrules.SrcgEarleyResume;
import chartparsing.lcfrsrules.SrcgEarleyScan;
import chartparsing.lcfrsrules.SrcgEarleySuspend;
import common.lcfrs.Clause;
import common.lcfrs.RangeVector;
import common.lcfrs.Srcg;
import common.lcfrs.SrcgEarleyActiveItem;

public class LcfrsToDeductionRulesConverter {

  public static ParsingSchema SrcgToParsingSchema(Srcg srcg, String w,
    String schema) {
    switch (schema) {
    case "earley":
      return LcfrsToEarleyRules(srcg, w);
    default:
      return null;
    }
  }

  private static ParsingSchema LcfrsToEarleyRules(Srcg srcg, String w) {
    // TODO if not ordered or not epsilon free, return note and null
    // TODO Auto-generated method stub
    String[] wsplit = w.split(" ");
    ParsingSchema schema = new ParsingSchema();

    for (Clause clause : srcg.getClauses()) {
      DynamicDeductionRule predict = new SrcgEarleyPredict(clause);
      schema.addRule(predict);
      if (clause.getLhsNonterminal().equals(srcg.getStartSymbol())) {
        StaticDeductionRule initialize = new StaticDeductionRule();
        int lhsdim = clause.getLhsDim();
        initialize.addAntecedence(new SrcgEarleyActiveItem(clause.setDotAt(0),
          0, 1, 0, new RangeVector(lhsdim)));
        schema.addAxiom(initialize);
        schema.addGoal(new SrcgEarleyActiveItem(clause.setDotAt(lhsdim),
          wsplit.length, 1, lhsdim, new RangeVector(lhsdim)));
      }
    }
    DynamicDeductionRule scan = new SrcgEarleyScan();
    schema.addRule(scan);
    DynamicDeductionRule suspend = new SrcgEarleySuspend();
    schema.addRule(suspend);
    DynamicDeductionRule convert = new SrcgEarleyConvert();
    schema.addRule(convert);
    DynamicDeductionRule resume = new SrcgEarleyResume();
    schema.addRule(resume);

    return null;
  }
}

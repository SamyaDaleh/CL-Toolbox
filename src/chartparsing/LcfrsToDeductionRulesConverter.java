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

/** Generates different parsing schemes. Based on the slides from Laura
 * Kallmeyer about Parsing as Deduction. */
public class LcfrsToDeductionRulesConverter {

  /** Instead of calling the respective function this method works as entry
   * point for all of them. Takes a srcg, an input string w and a string
   * specifying which parsing algorithm shall be applied. Returns the respective
   * parsing scheme. */
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
    String[] wsplit = w.split(" ");
    ParsingSchema schema = new ParsingSchema();

    for (Clause clause : srcg.getClauses()) {
      DynamicDeductionRule predict = new SrcgEarleyPredict(clause);
      schema.addRule(predict);
      if (clause.getLhsNonterminal().equals(srcg.getStartSymbol())) {
        StaticDeductionRule initialize = new StaticDeductionRule();
        int lhsdim = clause.getLhs().getDim();
        initialize.addConsequence(new SrcgEarleyActiveItem(clause.toString(), 0,
          1, 0, new RangeVector(lhsdim)));
        initialize.setName("Initialize");
        schema.addAxiom(initialize);
        schema.addGoal(
          new SrcgEarleyActiveItem(clause.toString(), wsplit.length, 1, lhsdim,
            new RangeVector(clause.getLhs().getSymbolsAsPlainArray().length)));
      }
    }
    DynamicDeductionRule scan = new SrcgEarleyScan(wsplit);
    schema.addRule(scan);
    DynamicDeductionRule suspend = new SrcgEarleySuspend();
    schema.addRule(suspend);
    DynamicDeductionRule convert = new SrcgEarleyConvert();
    schema.addRule(convert);
    DynamicDeductionRule resume = new SrcgEarleyResume();
    schema.addRule(resume);

    return schema;
  }
}

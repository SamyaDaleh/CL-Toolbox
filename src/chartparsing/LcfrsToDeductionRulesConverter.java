package chartparsing;

import common.lcfrs.Srcg;

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
    // TODO Auto-generated method stub
    return null;
  }
}
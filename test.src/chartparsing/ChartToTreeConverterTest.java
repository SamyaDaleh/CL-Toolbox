package chartparsing;

import static org.junit.Assert.assertEquals;

import java.text.ParseException;

import org.junit.Test;

import common.tag.Tag;
import common.tag.Tree;

public class ChartToTreeConverterTest {

  private static Tag gentag() throws ParseException {
    Tag g = new Tag();
    g.setNonterminals(new String[] {"S", "T"});
    g.setTerminals(new String[] {"a", "b", "c"});
    g.setStartsymbol("S");
    g.addInitialTree("α1", "(S T b)");
    g.addInitialTree("α2", "(T c)");
    g.addAuxiliaryTree("β", "(T a T*)");
    return g;
  }

  @Test public void testTagCykToDerivatedTree() throws ParseException {
    String w2 = "a a c b";
    ParsingSchema schema =
      TagToDeductionRulesConverter.TagToParsingSchema(gentag(), w2, "cyk");
    Deduction deduction = new Deduction();
    deduction.doParse(schema, false);
    Tree derivatedTree = ChartToTreeConverter.TagCykToDerivatedTree(
      deduction.getChart(), schema.getGoals(), deduction.getAppliedRules(),
      deduction.getBackpointers(), gentag());
    assertEquals("(S (T (a )(T (a )(T (c ))))(b ))", derivatedTree.toString());
  }
}

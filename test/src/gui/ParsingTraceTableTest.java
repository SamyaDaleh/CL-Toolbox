package gui;

import static org.junit.Assert.assertTrue;

import java.text.ParseException;

import org.junit.Test;

import chartparsing.Deduction;
import chartparsing.ParsingSchema;
import chartparsing.converter.TagToDeductionRulesConverter;
import common.tag.Tag;

public class ParsingTraceTableTest {
  private static Tag gentag() throws ParseException {
    Tag g = new Tag();
    g.setNonterminals(new String[] {"S", "T"});
    g.setTerminals(new String[] {"a", "b", "c"});
    g.setStartSymbol("S");
    g.addInitialTree("α1", "(S T b)");
    g.addInitialTree("α2", "(T c)");
    g.addAuxiliaryTree("β", "(T a T*)");
    return g;
  }
  
  @Test public void testParsingTraceTable() throws ParseException {
    String w2 = "a c b";
    ParsingSchema schema =
        TagToDeductionRulesConverter.tagToEarleyRules(gentag(), w2);
    Deduction deduction = new Deduction();
    deduction.doParse(schema, false);
    String[][] data = deduction.printTrace();
    new ParsingTraceTable(data,
      new String[] {"Id", "Item", "Rules", "Backpointers"}, gentag());
    assertTrue(true);
  }
}

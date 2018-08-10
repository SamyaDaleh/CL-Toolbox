package com.github.samyadaleh.cltoolbox.gui;

import static org.junit.Assert.assertTrue;

import java.text.ParseException;

import org.junit.Test;

import com.github.samyadaleh.cltoolbox.chartparsing.Deduction;
import com.github.samyadaleh.cltoolbox.chartparsing.ParsingSchema;
import com.github.samyadaleh.cltoolbox.chartparsing.converter.TagToDeductionRulesConverter;
import com.github.samyadaleh.cltoolbox.common.tag.Tag;
import com.github.samyadaleh.cltoolbox.gui.ParsingTraceTable;

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

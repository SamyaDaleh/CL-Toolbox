package com.github.samyadaleh.cltoolbox.gui;

import static com.github.samyadaleh.cltoolbox.common.TestGrammarLibrary.anCBTag;
import static org.junit.Assert.assertTrue;

import java.text.ParseException;

import com.github.samyadaleh.cltoolbox.chartparsing.converter.tag.TagToEarleyRulesConverter;
import org.junit.Ignore;
import org.junit.Test;

import com.github.samyadaleh.cltoolbox.chartparsing.Deduction;
import com.github.samyadaleh.cltoolbox.chartparsing.ParsingSchema;

public class ParsingTraceTableTest {

  @Ignore public void testParsingTraceTable() throws ParseException {
    String w2 = "a c b";
    ParsingSchema schema =
        TagToEarleyRulesConverter.tagToEarleyRules(anCBTag(), w2);
    Deduction deduction = new Deduction();
    deduction.doParse(schema, false);
    String[][] data = deduction.printTrace();
    new ParsingTraceTable(data,
        new String[] {"Id", "Item", "Rules", "Backpointers"}, anCBTag());
    assertTrue(true);
  }
}

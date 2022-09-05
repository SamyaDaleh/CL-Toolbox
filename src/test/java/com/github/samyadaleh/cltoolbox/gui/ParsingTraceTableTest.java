package com.github.samyadaleh.cltoolbox.gui;

import com.github.samyadaleh.cltoolbox.chartparsing.Deduction;
import com.github.samyadaleh.cltoolbox.chartparsing.ParsingSchema;
import com.github.samyadaleh.cltoolbox.chartparsing.converter.tag.TagToEarleyRulesConverter;
import com.github.samyadaleh.cltoolbox.common.GrammarLoader;
import com.github.samyadaleh.cltoolbox.common.tag.Tag;
import org.junit.Ignore;

import java.io.FileNotFoundException;
import java.text.ParseException;

import static org.junit.Assert.assertTrue;

public class ParsingTraceTableTest {

  @Ignore public void testParsingTraceTable()
      throws ParseException, FileNotFoundException {
    String w2 = "a c b";
    Tag tag = GrammarLoader.readTag("ancb.tag");
    ParsingSchema schema =
        TagToEarleyRulesConverter.tagToEarleyRules(tag, w2);
    Deduction deduction = new Deduction();
    deduction.doParse(schema, false);
    String[][] data = deduction.getTraceTable();
    deduction.printTrace(data);
    new ParsingTraceTable(data,
        new String[] {"Id", "Item", "Rules", "Backpointers"}, tag);
    assertTrue(true);
  }
}

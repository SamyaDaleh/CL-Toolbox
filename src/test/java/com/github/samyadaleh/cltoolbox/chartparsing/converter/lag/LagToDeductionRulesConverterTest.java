package com.github.samyadaleh.cltoolbox.chartparsing.converter.lag;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class LagToDeductionRulesConverterTest {

  @Test public void testSwapCategories() {
    List<List<String[]>> categories = new ArrayList<>();
    List<String[]> word1Categories = new ArrayList<>();
    word1Categories.add(new String[]{"b", "c"});
    word1Categories.add(new String[]{"a"});
    categories.add(word1Categories);
    List<String[]> word2Categories = new ArrayList<>();
    word2Categories.add(new String[]{"b"});
    categories.add(word2Categories);
    List<String[]> word3Categories = new ArrayList<>();
    word3Categories.add(new String[]{"c"});
    categories.add(word3Categories);
    List<String[][]> swappedCategories =
        LagToDeductionRulesConverter.swapInitialCategories(categories);
    assertEquals("b", swappedCategories.get(0)[0][0]);
    assertEquals("c", swappedCategories.get(0)[0][1]);
    assertEquals("b", swappedCategories.get(0)[1][0]);
    assertEquals("c", swappedCategories.get(0)[2][0]);
    assertEquals("a", swappedCategories.get(1)[0][0]);
  }
}

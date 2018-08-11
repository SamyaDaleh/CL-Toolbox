package com.github.samyadaleh.cltoolbox.common;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.github.samyadaleh.cltoolbox.chartparsing.DeductionChartItem;
import com.github.samyadaleh.cltoolbox.chartparsing.ChartItemInterface;

public class ArrayUtilsTest {

  @Test public void testItemEquals() {
    ChartItemInterface item1 = new DeductionChartItem("a", ".1", "la", "1", "?", "?", "3", "0");
    ChartItemInterface item2 = new DeductionChartItem("a", ".1", "la", "1", "-", "-", "3");
    ChartItemInterface item3 = new DeductionChartItem("a", ".1", "la", "1", "-", "-", "3", "0");
    assertTrue(!item1.equals(item2));
    assertTrue(item1.equals(item3));
  }

  @Test public void testListContainsItem() {
    ChartItemInterface item1 = new DeductionChartItem("a", ".1", "la", "1", "?", "?", "3", "0");
    ChartItemInterface item2 = new DeductionChartItem("a", ".1", "la", "1", "-", "-", "3", "0");

    List<ChartItemInterface> testchart = new ArrayList<ChartItemInterface>();
    testchart.add(item2);
    assertTrue(testchart.contains(item1));
    assertTrue(testchart.indexOf(item1) == 0);
  }

}

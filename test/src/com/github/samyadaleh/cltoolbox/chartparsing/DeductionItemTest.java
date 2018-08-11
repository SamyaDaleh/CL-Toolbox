package com.github.samyadaleh.cltoolbox.chartparsing;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import com.github.samyadaleh.cltoolbox.chartparsing.DeductionChartItem;
import com.github.samyadaleh.cltoolbox.chartparsing.ChartItemInterface;

public class DeductionItemTest {
  @Test public void testItemOperations() {
    ChartItemInterface item1 = new DeductionChartItem("S", "0");
    ChartItemInterface item2 = new DeductionChartItem("S", "0");

    assertTrue(item1.equals(item2));
    // test set contains item
    Set<ChartItemInterface> itemset = new HashSet<ChartItemInterface>();
    itemset.add(item1);

    List<ChartItemInterface> itemssetlist = new ArrayList<ChartItemInterface>(itemset);
    assertTrue(itemssetlist.contains(item2));
    // test list contains Item
    List<ChartItemInterface> itemlist = new ArrayList<ChartItemInterface>();
    itemlist.add(item1);
    assertTrue(itemlist.contains(item2));
  }
}

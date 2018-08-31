package com.github.samyadaleh.cltoolbox.chartparsing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import com.github.samyadaleh.cltoolbox.chartparsing.item.DeductionChartItem;
import com.github.samyadaleh.cltoolbox.chartparsing.item.ChartItemInterface;

public class DeductionItemTest {
  @Test public void testItemOperations() {
    ChartItemInterface item1 = new DeductionChartItem("S", "0");
    ChartItemInterface item2 = new DeductionChartItem("S", "0");

    assertEquals(item1, item2);
    // test set contains item
    Set<ChartItemInterface> itemset = new HashSet<>();
    itemset.add(item1);

    List<ChartItemInterface> itemssetlist = new ArrayList<>(itemset);
    assertTrue(itemssetlist.contains(item2));
    // test list contains Item
    List<ChartItemInterface> itemlist = new ArrayList<>();
    itemlist.add(item1);
    assertTrue(itemlist.contains(item2));
  }
}

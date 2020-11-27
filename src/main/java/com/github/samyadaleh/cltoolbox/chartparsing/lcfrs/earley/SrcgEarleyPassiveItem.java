package com.github.samyadaleh.cltoolbox.chartparsing.lcfrs.earley;

import java.util.List;

import com.github.samyadaleh.cltoolbox.chartparsing.item.AbstractChartItem;
import com.github.samyadaleh.cltoolbox.chartparsing.item.ChartItemInterface;

/** An item we get if we have fully seen an active item. It is used to move the
 * dot further in active items. */
class SrcgEarleyPassiveItem extends AbstractChartItem implements ChartItemInterface {

  SrcgEarleyPassiveItem(String nt,
    List<String> newVector) {
    this.itemForm = new String[newVector.size() + 1];
    this.itemForm[0] = nt;
    int i = 1;
    for (String entry : newVector) {
      itemForm[i] = entry;
      i++;
    }
  }

  @Override public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("[").append(itemForm[0]);
    for (int i = 0; i*2+2 <itemForm.length; i++){
        builder.append(", ");
      builder.append("<").append(itemForm[i * 2 + 1]).append(",")
          .append(itemForm[i * 2 + 2]).append(">");
    }
    builder.append("]");
    return builder.toString();
  }

}

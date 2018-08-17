package com.github.samyadaleh.cltoolbox.chartparsing;

import java.util.ArrayList;
import java.util.List;

import com.github.samyadaleh.cltoolbox.common.ArrayUtils;
import com.github.samyadaleh.cltoolbox.common.tag.Tree;

/** Abstract class that holds the functions commonly used by all items. */
public abstract class AbstractChartItem implements ChartItemInterface {

  protected String[] itemForm;
  private List<Tree> trees = new ArrayList<>();

  @Override public String[] getItemForm() {
    return this.itemForm;
  }

  @Override public boolean equals(Object o) {
    if (o instanceof ChartItemInterface) {
      return ArrayUtils
          .match(this.itemForm, ((ChartItemInterface) o).getItemForm());
    }
    return false;
  }

  @Override public int hashCode() {
    int hashCode = 0;
    int i = 2;
    for (String item : this.itemForm) {
      for (char chara : item.toCharArray()) {
        int value = (int) chara;
        hashCode += value * i;
        i += 2;
      }
    }
    return hashCode;
  }

  @Override public String toString() {
    return ArrayUtils.toString(this.itemForm);
  }

  @Override public List<Tree> getTrees() {
    return this.trees;
  }

  @Override public void setTrees(List<Tree> trees) {
    this.trees = trees;
  }

}

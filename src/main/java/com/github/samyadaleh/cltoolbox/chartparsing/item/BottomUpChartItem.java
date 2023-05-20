package com.github.samyadaleh.cltoolbox.chartparsing.item;

import com.github.samyadaleh.cltoolbox.common.tag.Tree;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BottomUpChartItem extends DeductionChartItem {
  private List<Pair<String, Map<Integer, List<Tree>>>> stackState;

  public BottomUpChartItem(String... itemForm) {
    this.itemForm = itemForm;
    this.stackState = new ArrayList<>();
  }

  @Override
  public List<Tree> getTrees() {
    return stackState.stream()
        .flatMap(pair -> pair.getSecond().values().stream())
        .flatMap(List::stream)
        .collect(Collectors.toList());
  }

  @Override
  public void setTrees(List<Tree> trees) {
    // Since trees are associated with stack symbols,
    // we may need to throw an error or a warning here.
    throw new UnsupportedOperationException("Cannot set trees directly. Use the stackState.");
  }

  public List<Pair<String, Map<Integer, List<Tree>>>> getStackState() {
    return stackState;
  }

  public void setStackState(List<Pair<String, Map<Integer, List<Tree>>>> stackState) {
    this.stackState = stackState;
  }
}

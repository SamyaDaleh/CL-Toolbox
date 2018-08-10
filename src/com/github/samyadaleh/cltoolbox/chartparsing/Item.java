package com.github.samyadaleh.cltoolbox.chartparsing;

import java.util.List;

import com.github.samyadaleh.cltoolbox.common.tag.Tree;

/** Interface for all items that are used by the deduction system that can be
 * used as antecedences or consequences of rules and can be derived. */
public interface Item {

  String[] getItemform();
  
  List<Tree> getTrees();
  void setTrees(List<Tree> trees);
}

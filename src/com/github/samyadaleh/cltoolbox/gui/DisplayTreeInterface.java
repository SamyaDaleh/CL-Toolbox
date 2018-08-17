package com.github.samyadaleh.cltoolbox.gui;

import java.util.Map;

import com.github.samyadaleh.cltoolbox.common.tag.Tree;

interface DisplayTreeInterface {
  
  Tree getTree();
  String[] getItemForm();
  Map<String, Integer[]> getNodesDrawn();
  
  void drawText(String label, int x, int y);
  void drawLine(int x1, int y1, int x2, int y2);
  int getWidth();
  int getHeight();
  void clearRect(int width, int height);
}

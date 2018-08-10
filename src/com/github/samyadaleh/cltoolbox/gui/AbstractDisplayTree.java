package com.github.samyadaleh.cltoolbox.gui;

import java.util.ArrayList;
import java.util.List;

import com.github.samyadaleh.cltoolbox.common.tag.Vertex;

abstract class AbstractDisplayTree {

  private static String createTreeLabel(DisplayTreeInterface dti, Vertex p) {
    StringBuilder label = new StringBuilder();
    label.append(p.getLabel());
    if (dti.getTree().isInOA(p.getGornAddress())) {
      label.append("_OA");
    }
    if (dti.getTree().isInNA(p.getGornAddress())) {
      label.append("_NA");
    }
    if (dti.getTree().getFoot() != null && dti.getTree().getFoot().equals(p)) {
      label.append("*");
    }
    return label.toString();
  }

  private static void drawFootIndices(DisplayTreeInterface dti, int height,
    int nodeX, String label) {
    int halfLabelWidth = label.length() * 10 / 2;
    if (dti.getItemForm().length == 6) {
      dti.drawText(dti.getItemForm()[3], nodeX - halfLabelWidth - 10, height);
      dti.drawText(dti.getItemForm()[4], nodeX + halfLabelWidth + 10, height);
    } else if (dti.getItemForm().length == 8) {
      dti.drawText(dti.getItemForm()[4], nodeX - halfLabelWidth - 10, height);
      dti.drawText(dti.getItemForm()[5], nodeX + halfLabelWidth + 10, height);
    } else if (dti.getItemForm().length == 9) {
      dti.drawText(dti.getItemForm()[5], nodeX - halfLabelWidth - 10, height);
      dti.drawText(dti.getItemForm()[6], nodeX + halfLabelWidth + 10, height);
    }
  }

  private static void drawTagEarleyDot(DisplayTreeInterface dti, int height,
    int nodeX, String label) {
    int halfLabelWidth = label.length() * 8 / 2;
    switch (dti.getItemForm()[2]) {
    case "la":
      dti.drawText("•", nodeX - halfLabelWidth, height - 5);
      break;
    case "lb":
      dti.drawText("•", nodeX - halfLabelWidth, height + 8);
      break;
    case "rb":
      dti.drawText("•", nodeX + halfLabelWidth, height + 8);
      break;
    case "ra":
      dti.drawText("•", nodeX + halfLabelWidth, height - 5);
      break;
    default:
      System.out.println("Unknown pos: " + dti.getItemForm()[2]);
    }
  }

  private static void drawTagCykDot(DisplayTreeInterface dti, int height,
    int nodeX) {
    char pos = dti.getItemForm()[1].charAt(dti.getItemForm()[1].length() - 1);
    switch (pos) {
    case '⊤':
      dti.drawText("•", nodeX, height - 8);
      break;
    case '⊥':
      dti.drawText("•", nodeX, height + 8);
      break;
    default:
      System.out.println("Unknown pos: " + pos);
    }
  }

  /** Draws the root of a subtree in the middle, divides its space by the number
   * of its children's width, triggers to draw the children. */
  private static void drawSubTree(DisplayTreeInterface dti, Vertex p,
    int height, int widthFrom, int widthDelta) {
    if (dti.getTree().getLeafGorns().contains(p.getGornAddress())) {
      return;
    }
    int nodeX = widthFrom + widthDelta / 2;
    String label = createTreeLabel(dti, p);
    if (dti.getTree().getFoot() != null && dti.getTree().getFoot().equals(p)) {
      drawFootIndices(dti, height, nodeX, label);
    }
    dti.drawText(label, nodeX, height);
    switch (dti.getItemForm().length) {
    case 0:
      break;
    case 6:
      String gorn =
        dti.getItemForm()[1].substring(0, dti.getItemForm()[1].length() - 1);
      if (p.getGornAddress().equals(gorn)
        || (p.getGornAddress().equals("") && gorn.equals("ε"))) {
        drawTagCykDot(dti, height, nodeX);
      }
      break;
    case 8:
    case 9:
      if (p.getGornAddress().equals(dti.getItemForm()[1])
        || (p.getGornAddress().equals("")
          && dti.getItemForm()[1].equals("ε"))) {
        drawTagEarleyDot(dti, height, nodeX, label);
      }
      break;
    default:
      System.err.println(
        "Unexpected item length " + String.valueOf(dti.getItemForm().length));
    }
    dti.getNodesDrawn().put(p.getGornAddress(), new Integer[] {nodeX, height});
    if (!p.getGornAddress().equals("")) {
      Integer[] xyParent = dti.getNodesDrawn().get(p.getGornAddressOfParent());
      dti.drawLine(nodeX, height - 10, xyParent[0], xyParent[1] + 10);
    }
    int widthSum = 0;
    List<Vertex> children = dti.getTree().getChildren(p);
    ArrayList<Integer> widths = new ArrayList<Integer>();
    for (Vertex child : children) {
      int width = dti.getTree().getWidthBelowNodeInNodes(child);
      if (width == 0) {
        widthSum += 1;
        widths.add(1);
      } else {
        widthSum += width;
        widths.add(width);
      }
    }
    callDrawingOfChildren(dti, height, widthFrom, widthDelta, widthSum,
      children, widths);
  }

  private static void callDrawingOfChildren(DisplayTreeInterface dti,
    int height, int widthFrom, int widthDelta, int widthSum,
    List<Vertex> children, ArrayList<Integer> widths) {
    int drawWidth = widthFrom;
    for (int i = 0; i < children.size(); i++) {
      if (widthSum > 0) {
        int newWidthDelta = widthDelta * widths.get(i) / widthSum;
        drawSubTree(dti, children.get(i),
          height + dti.getHeight() / dti.getTree().getHeight(), drawWidth,
          newWidthDelta);
        drawWidth += newWidthDelta;
      } else {
        drawSubTree(dti, children.get(i),
          height + dti.getHeight() / dti.getHeight(), drawWidth,
          widthDelta / children.size());
        drawWidth += widthDelta / children.size();
      }
    }
  }

  static void paint(DisplayTreeInterface dti) {
    dti.clearRect(0, 0, dti.getWidth(), dti.getHeight());
    switch (dti.getItemForm().length) {
    case 0:
      break;
    case 6:
      dti.drawText(dti.getItemForm()[2], 30, 60);
      dti.drawText(dti.getItemForm()[5], dti.getWidth() - 30, 60);
      break;
    case 8:
      dti.drawText(dti.getItemForm()[3], 30, 60);
      dti.drawText(dti.getItemForm()[6], dti.getWidth() - 30, 60);
      break;
    case 9:
      dti.drawText(dti.getItemForm()[4], 30, 60);
      dti.drawText(dti.getItemForm()[7], dti.getWidth() - 30, 60);
      break;
    default:
      System.err.println(
        "Unexpected item length " + String.valueOf(dti.getItemForm().length));
    }
    drawSubTree(dti, dti.getTree().getNodeByGornAdress(""), 60, 0,
      dti.getWidth());
    int overallLeafLabelWidth = 2;
    for (String gorn : dti.getTree().getLeafGorns()) {
      overallLeafLabelWidth +=
        dti.getTree().getNodeByGornAdress(gorn).getLabel().length() + 1;
    }
    int labelWidthUsed = 2;
    for (int i = 0; i < dti.getTree().getLeafOrder().size(); i++) {
      int index = dti.getTree().getLeafOrder().indexOf(String.valueOf(i));
      Vertex p = dti.getTree()
        .getNodeByGornAdress(dti.getTree().getLeafGorns().get(index));
      int nodex = labelWidthUsed * dti.getWidth() / overallLeafLabelWidth;
      labelWidthUsed += p.getLabel().length() + 1;
      int height = dti.getHeight() - 50;
      dti.drawText(p.getLabel(), nodex, height);
      Integer[] xyParent = dti.getNodesDrawn().get(p.getGornAddressOfParent());
      dti.drawLine(nodex, height - 10, xyParent[0], xyParent[1] + 10);
    }
  }
}

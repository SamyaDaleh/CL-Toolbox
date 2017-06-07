package gui;

import java.awt.Graphics;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.*;

import common.tag.Tree;
import common.tag.Vertex;

public class DisplayTree extends JFrame {

  private static final long serialVersionUID = -9123591819196303915L;
  private Tree tree;
  private Map<String, Integer[]> nodesDrawn;
  private String[] itemForm;

  /** Called with a tree in bracket format as argument, retrieves the depth by
   * brackets to estimate needed windows size. */
  public DisplayTree(String[] args) throws ParseException {
    super();
    this.setLocation(100, 500);

    int currentDepth = 0;
    int maxDepth = 0;
    for (int i = 0; i < args[0].length(); i++) {
      if (args[0].charAt(i) == '(') {
        currentDepth++;
        if (currentDepth > maxDepth) {
          maxDepth = currentDepth;
        }
      } else if (args[0].charAt(i) == ')') {
        currentDepth--;
      }
    }
    this.setSize(80 * maxDepth, 80 * maxDepth);
    this.setVisible(true);
    
    setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    this.tree = new Tree(args[0]);
    if (args.length > 1) {
      this.itemForm = args[1].substring(1, args[1].length() - 1).split(",");
    } else {
      itemForm = new String[] {};
    }
    nodesDrawn = new HashMap<String, Integer[]>();
  }

  /** Initiates the drawing of the tree. */
  public void paint(Graphics g) {
    g.clearRect(0, 0, this.getWidth(), this.getHeight());
    if (itemForm.length == 6) {
      g.drawString(itemForm[2], 30, 60);
      g.drawString(itemForm[5], this.getWidth() - 30, 60);
    } else if (itemForm.length == 8) {
      g.drawString(itemForm[3], 30, 60);
      g.drawString(itemForm[6], this.getWidth() - 30, 60);
    }
    drawSubTree(g, tree.getNodeByGornAdress(""), 60, 0, this.getWidth());
    for (int i = 0; i < tree.getLeafOrder().size(); i++) {
      int index = tree.getLeafOrder().indexOf(String.valueOf(i));
      Vertex p = tree.getNodeByGornAdress(tree.getLeafGorns().get(index));
      int nodex = (i + 1) * this.getWidth() / (tree.getLeafOrder().size() + 1);
      int height = this.getHeight() - 50;
      g.drawString(p.getLabel(), nodex, height);
      Integer[] xyParent = nodesDrawn.get(p.getGornAddressOfParent());
      g.drawLine(nodex, height - 10, xyParent[0], xyParent[1] + 10);
    }
  }

  /** Draws the root of a subtree in the middle, divides its space by the number
   * of its children's width, triggers to draw the children. */
  private void drawSubTree(Graphics g, Vertex p, int height, int widthFrom,
    int widthDelta) {
    if (tree.getLeafGorns().contains(p.getGornAddress())) {
      return;
    }
    int nodeX = widthFrom + widthDelta / 2;
    StringBuilder label = new StringBuilder();
    label.append(p.getLabel());
    if (tree.isInOA(p.getGornAddress())) {
      label.append("_OA");
    }
    if (tree.isInNA(p.getGornAddress())) {
      label.append("_NA");
    }
    if (tree.getFoot() != null && tree.getFoot().equals(p)) {
      label.append("*");
      int halfLabelWidth = label.length() * 10 / 2;
      if (itemForm.length == 6) {
        g.drawString(itemForm[3], nodeX-halfLabelWidth-10, height);
        g.drawString(itemForm[4], nodeX+halfLabelWidth+10, height);
      } else if (itemForm.length == 8) {
        g.drawString(itemForm[4], nodeX-halfLabelWidth-10, height);
        g.drawString(itemForm[5], nodeX+halfLabelWidth+10, height);
      }
    }
    g.drawString(label.toString(), nodeX, height);
    if (itemForm.length == 6) {
      char pos = itemForm[1].charAt(itemForm[1].length() - 1);
      String gorn = itemForm[1].substring(0, itemForm[1].length()-1);
      if (p.getGornAddress().equals(gorn)
          || (p.getGornAddress().equals("") && gorn.equals("ε"))) {
        switch (pos) {
        case '⊤':
          g.drawString("•", nodeX, height - 8);
          break;
        case '⊥':
          g.drawString("•", nodeX, height + 8);
          break;
        }
      }
    } else if (itemForm.length == 8) {
      if (p.getGornAddress().equals(itemForm[1])
        || (p.getGornAddress().equals("") && itemForm[1].equals("ε"))) {
        int halfLabelWidth = label.length() * 8 / 2;
        switch (itemForm[2]) {
        case "la":
          g.drawString("•", nodeX - halfLabelWidth, height - 5);
          break;
        case "lb":
          g.drawString("•", nodeX - halfLabelWidth, height + 8);
          break;
        case "rb":
          g.drawString("•", nodeX + halfLabelWidth, height + 8);
          break;
        case "ra":
          g.drawString("•", nodeX + halfLabelWidth, height - 5);
          break;
        }
      }
    }
    nodesDrawn.put(p.getGornAddress(), new Integer[] {nodeX, height});
    if (!p.getGornAddress().equals("")) {
      Integer[] xyParent = nodesDrawn.get(p.getGornAddressOfParent());
      g.drawLine(nodeX, height - 10, xyParent[0], xyParent[1] + 10);
    }
    List<Vertex> children = tree.getChildren(p);
    int widthSum = 0;
    ArrayList<Integer> widths = new ArrayList<Integer>();
    for (Vertex child : children) {
      int width = tree.getWidthBelowNode(child);
      if (width == 0) {
        widthSum += 1;
        widths.add(1);
      } else {
        widthSum += width;
        widths.add(width);
      }
    }

    int drawWidth = widthFrom;
    for (int i = 0; i < children.size(); i++) {
      if (widthSum > 0) {
        int newWidthDelta = widthDelta * widths.get(i) / widthSum;
        drawSubTree(g, children.get(i),
          height + this.getHeight() / tree.getHeight(), drawWidth,
          newWidthDelta);
        drawWidth += newWidthDelta;
      } else {
        drawSubTree(g, children.get(i),
          height + this.getHeight() / tree.getHeight(), drawWidth,
          widthDelta / children.size());
        drawWidth += widthDelta / children.size();
      }
    }
  }
}

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
  private Map<String, Integer[]> nodesdrawn;

  /** Constructor that sets up the tree and the node map, instantiates the
   * closing functionality. */
  private DisplayTree(String treestring) throws ParseException {
    super();
    setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    this.tree = new Tree(treestring);
    nodesdrawn = new HashMap<String, Integer[]>();
  }

  /** Initiates the drawing of the tree. */
  public void paint(Graphics g) {
    g.clearRect(0, 0, this.getWidth(), this.getHeight());
    drawSubTree(g, tree.getNodeByGornAdress(""), 60, 0, this.getWidth());
  }

  /** Draws the root of a subtree in the middle, divides its space by the number
   * of its children's width, triggers to drw the children. */
  private void drawSubTree(Graphics g, Vertex p, int height, int widthfrom,
    int widthdelta) {
    StringBuilder label = new StringBuilder();
    label.append(p.getLabel());
    if (tree.isInOA(p.getGornaddress())) {
      label.append("_OA");
    }
    if (tree.isInNA(p.getGornaddress())) {
      label.append("_NA");
    }
    if (tree.getFoot() != null && tree.getFoot().equals(p)) {
      label.append("*");
    }
    g.drawString(label.toString(), widthfrom + widthdelta / 2, height);
    nodesdrawn.put(p.getGornaddress(),
      new Integer[] {widthfrom + widthdelta / 2, height});
    if (!p.getGornaddress().equals("")) {
      Integer[] xyparent = nodesdrawn.get(p.getGornAddressOfParent());
      g.drawLine(widthfrom + widthdelta / 2, height - 10, xyparent[0],
        xyparent[1] + 10);
    }
    List<Vertex> children = tree.getChildren(p);
    int widthsum = 0;
    ArrayList<Integer> widths = new ArrayList<Integer>();
    for (Vertex child : children) {
      int width = tree.getWidthBelowNode(child);
      if (width == 0) {
        widthsum += 1;
        widths.add(1);
      } else {
        widthsum += width;
        widths.add(width);
      }
    }

    int drawwidth = widthfrom;
    for (int i = 0; i < children.size(); i++) {
      if (widthsum > 0) {
        int newwidthdelta = widthdelta * widths.get(i) / widthsum;
        drawSubTree(g, children.get(i),
          height + this.getHeight() / tree.getHeight(), drawwidth,
          newwidthdelta);
        drawwidth += newwidthdelta;
      } else {
        drawSubTree(g, children.get(i),
          height + this.getHeight() / tree.getHeight(), drawwidth,
          widthdelta / children.size());
        drawwidth += widthdelta / children.size();
      }
    }
  }

  /** Called with a tree in bracket format as argument, retrieves the depth by
   * brackets to estimate needed windows size. */
  public static void main(String[] args) throws ParseException {
    DisplayTree dt = new DisplayTree(args[0]);
    int currentdepth = 0;
    int maxdepth = 0;
    for (int i = 0; i < args[0].length(); i++) {
      if (args[0].charAt(i) == '(') {
        currentdepth++;
        if (currentdepth > maxdepth) {
          maxdepth = currentdepth;
        }
      } else if (args[0].charAt(i) == ')') {
        currentdepth--;
      }
    }
    dt.setSize(80 * maxdepth, 80 * maxdepth);
    dt.setVisible(true);
  }
}

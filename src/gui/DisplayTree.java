package gui;

import java.awt.Graphics;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.*;

import common.tag.Tree;
import common.tag.Vertex;

public class DisplayTree extends JFrame {

  private static final long serialVersionUID = -9123591819196303915L;
  private Tree tree;
  Map<String, Integer[]> nodesdrawn;

  private DisplayTree(String treestring) throws ParseException {
    super();
    setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    this.tree = new Tree(treestring);
    nodesdrawn = new HashMap<String, Integer[]>();
  }

  public void paint(Graphics g) {
    drawSubTree(g, tree.getNodeByGornAdress(""), 60, 0, this.getWidth());
  }

  /** Draws the root of a subtree in the middle, divides its space by the number
   * of its children's width, triggers to drw the children. */
  private void drawSubTree(Graphics g, Vertex p, int height, int widthfrom,
    int widthdelta) {
    String label = p.getLabel() + (tree.isInOA(p.getGornaddress()) ? "_OA" : "")
      + (tree.isInNA(p.getGornaddress()) ? "_NA" : "")
      + (tree.getFoot().equals(p) ? "*" : "");
    g.drawString(label, widthfrom + widthdelta / 2, height);
    nodesdrawn.put(p.getGornaddress(),
      new Integer[] {widthfrom + widthdelta / 2, height});
    if (!p.getGornaddress().equals("")) {
      Integer[] xyparent = nodesdrawn.get(p.getGornAddressOfParent());
      g.drawLine(widthfrom + widthdelta / 2, height - 10, xyparent[0],
        xyparent[1] + 10);
    }
    List<Vertex> children = tree.getChildren(p);
    int drawwidth = widthfrom;
    for (Vertex child : children) {
      drawSubTree(g, child, height + this.getHeight() / tree.getHeight(),
        drawwidth, widthdelta / children.size());
      drawwidth += widthdelta / children.size();
    }
  }

  public static void main(String[] args) throws ParseException {
    DisplayTree dt = new DisplayTree(args[0]);
    // could count maxdepth in brackets to determine height.
    dt.setSize(400, 400);
    dt.setVisible(true);
  }
}

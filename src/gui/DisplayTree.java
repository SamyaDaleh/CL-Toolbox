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
  private String[] itemform;

  /** Called with a tree in bracket format as argument, retrieves the depth by
   * brackets to estimate needed windows size. */
  public DisplayTree(String[] args) throws ParseException {
    super();
    this.setLocation(100, 500);

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
    this.setSize(80 * maxdepth, 80 * maxdepth);
    this.setVisible(true);
    
    setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    this.tree = new Tree(args[0]);
    if (args.length > 1) {
      this.itemform = args[1].substring(1, args[1].length() - 1).split(",");
    } else {
      itemform = new String[] {};
    }
    nodesdrawn = new HashMap<String, Integer[]>();
  }

  /** Initiates the drawing of the tree. */
  public void paint(Graphics g) {
    g.clearRect(0, 0, this.getWidth(), this.getHeight());
    if (itemform.length == 6) {
      g.drawString(itemform[2], 30, 60);
      g.drawString(itemform[5], this.getWidth() - 30, 60);
    } else if (itemform.length == 8) {
      g.drawString(itemform[3], 30, 60);
      g.drawString(itemform[6], this.getWidth() - 30, 60);
    }
    drawSubTree(g, tree.getNodeByGornAdress(""), 60, 0, this.getWidth());
  }

  /** Draws the root of a subtree in the middle, divides its space by the number
   * of its children's width, triggers to drw the children. */
  private void drawSubTree(Graphics g, Vertex p, int height, int widthfrom,
    int widthdelta) {
    int nodex = widthfrom + widthdelta / 2;
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
      int halflabelwidth = label.length() * 10 / 2;
      if (itemform.length == 6) {
        g.drawString(itemform[3], nodex-halflabelwidth-10, height);
        g.drawString(itemform[4], nodex+halflabelwidth+10, height);
      } else if (itemform.length == 8) {
        g.drawString(itemform[4], nodex-halflabelwidth-10, height);
        g.drawString(itemform[5], nodex+halflabelwidth+10, height);
      }
    }
    g.drawString(label.toString(), nodex, height);
    if (itemform.length == 6) {
      char pos = itemform[1].charAt(itemform[1].length() - 1);
      String gorn = itemform[1].substring(0, itemform[1].length()-1);
      if (p.getGornaddress().equals(gorn)
          || (p.getGornaddress().equals("") && gorn.equals("ε"))) {
        switch (pos) {
        case '⊤':
          g.drawString("•", nodex, height - 8);
          break;
        case '⊥':
          g.drawString("•", nodex, height + 8);
          break;
        }
      }
    } else if (itemform.length == 8) {
      if (p.getGornaddress().equals(itemform[1])
        || (p.getGornaddress().equals("") && itemform[1].equals("ε"))) {
        int halflabelwidth = label.length() * 8 / 2;
        switch (itemform[2]) {
        case "la":
          g.drawString("•", nodex - halflabelwidth, height - 5);
          break;
        case "lb":
          g.drawString("•", nodex - halflabelwidth, height + 8);
          break;
        case "rb":
          g.drawString("•", nodex + halflabelwidth, height + 8);
          break;
        case "ra":
          g.drawString("•", nodex + halflabelwidth, height - 5);
          break;
        }
      }
    }
    // TODO if gorn address of node is the same as in item, get dot position and
    // draw dot
    nodesdrawn.put(p.getGornaddress(), new Integer[] {nodex, height});
    if (!p.getGornaddress().equals("")) {
      Integer[] xyparent = nodesdrawn.get(p.getGornAddressOfParent());
      g.drawLine(nodex, height - 10, xyparent[0], xyparent[1] + 10);
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
}

package gui;

import java.awt.Graphics;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;

import common.tag.Tree;
import common.tag.Vertex;

public class DisplayTree extends JFrame {
  
  private static final long serialVersionUID = -9123591819196303915L;
  Tree tree;

  public DisplayTree(String treestring) throws ParseException {
    super();
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    this.tree = new Tree(treestring);
  }

  public void paint(Graphics g) {
    Map<String, Integer[]> nodesdrawn = new HashMap<String, Integer[]>();
    int height = tree.getHeight();
    int drawheight = 60;
    int drawwidth = 0;
    for (int i = 1; i <= height; i++) {
      int width = tree.getWidthInLayer(i);
      drawwidth = 400 * 1 / (width + 1);
      for (Vertex p : tree.getVertexes()) {
        if (p.getGornaddress().split("[.]").length == i) {
          String label =
            p.getLabel() + (tree.isInOA(p.getGornaddress()) ? "_OA" : "")
              + (tree.isInNA(p.getGornaddress()) ? "_NA" : "")
              + (tree.getFoot().equals(p) ? "*" : "");
          g.drawString(label, drawwidth, drawheight);
          nodesdrawn.put(p.getGornaddress(), new Integer[]{drawwidth, drawheight});
          if (i > 1) {
            Integer[] xyparent = nodesdrawn.get(p.getGornAddressOfParent());
            g.drawLine(drawwidth, drawheight - 10, xyparent[0],
              xyparent[1] + 10);
          }
          drawwidth += 400 / width;
        }
      }
      drawheight += 400 / height;
    }
  }

  public static void main(String[] args) throws ParseException {
    DisplayTree dt = new DisplayTree(args[0]);
    dt.setSize(400, 400);
    dt.setVisible(true);
  }
}
